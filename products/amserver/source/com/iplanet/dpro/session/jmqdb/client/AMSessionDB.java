/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: AMSessionDB.java,v 1.2 2007-11-14 00:21:07 manish_rustagi Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.iplanet.dpro.session.jmqdb.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import java.io.PrintWriter;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import com.iplanet.dpro.session.jmqdb.ConnectionFactoryProvider;
import com.iplanet.dpro.session.jmqdb.ConnectionFactoryProviderFactory;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

public class AMSessionDB implements Runnable {
    
    static AMSessionDB dbs;
    /* Operations */
    static boolean debug = true;
    
    static public final String READ = "READ";
    static public final String WRITE = "WRITE";
    static public final String DELETE = "DELETE";
    static public final String DELETEBYDATE = "DELETEBYDATE";
    static public final String SHUTDOWN = "SHUTDOWN";
    static public final String GET_SESSION_COUNT =
        "GET_SESSION_COUNT";
    static public final String NOT_FOUND = "notfound";
    static public final String OP_STATUS = "opstatus";
    
    /* JMQ Queue/Topic names */
    static public final String DBREQUEST = "AM_DBREQUEST";
    static public final String DBRESPONSE = "AM_DRESPONSE";
    static public final String DBNODESTATUS = "AM_DBNODE_STATUS";

    /* JMQ Message attrs */
    static public final String SESSIONID = "SessionId";
    static public final String EXPIRYDATE = "ExpiryDate";
    static public final String DATA = "Data";
    static public final String RANDOM = "RANDOM";

    /* JMQ Properties */
    static public final String ID = "ID";


    // Private data members
    private String _id;

    TopicConnectionFactory tFactory = null;
    TopicConnection tConn = null;
    TopicSession tSession = null;
    TopicSession tNodeSubSession = null;
    TopicSession tNodePubSession = null;
    Topic reqTopic = null;
    Topic resTopic = null;
    TopicSubscriber reqSub = null;
    TopicPublisher resPub = null;

    // Used for the determination of master BDB node
    Topic dbNodeStatusSubTopic = null;
    Topic dbNodeStatusPubTopic = null;
    TopicSubscriber dbNodeStatusSub = null;
    TopicPublisher dbNodeStatusPub = null;    
    private static long localStartTime;
    private static boolean isMasterNode = false;
    private static long localNodeID;
    private static Map serverStatusMap = new HashMap();
    private Thread nodeStatusSender;
    private Thread nodeStatusReceiver;
    private static long nodeUpdateInterval 
        = 5000; // 5 seconds in milli-seconds
    private static long nodeUpdateGraceperiod 
        = 1000; // 1 second in milli-seconds

    private SessionDataAccessor da;

    // Encapsulates the database environment.
    private static SessionDBEnv sessionDbEnv = new SessionDBEnv();
    private static File sessDbEnvPath = null;

    /* Config data - TODO : move to properties/CLI options */
    private int MAX_RESPONSE_QUEUES = 1;

    private String databaseFileName = "amsessions.db";

    private int flags = 0; /* Db.DB_AUTO_COMMIT */
    
    private String userName = "guest";
    private String userPassword = "guest";
    private int cacheSize = 32;  /* 32 mb has the best overall performance */
    private String clusterAddress = null;
    private String dbDirectory = "sessiondb";
    private int numCleanSessions = 1000;
    private boolean verbose = false;
    private long statsInterval = 60 * 1000; //60 seconds
    private boolean statsEnabled = false;
    private boolean deleteDatabase = true;
    
    private static Map arguments = new HashMap();
    private static final int INVALID = 0;
    private static final int USER_NAME = 1;
    private static final int PASSWORD = 2;
    private static final int PASSWORD_FILE = 3;
    private static final int CACHE_SIZE = 4;
    private static final int DIRECTORY = 5;
    private static final int CLUSTER_ADDRESS = 6;
    private static final int NUM_CLEAN_SESSIONS = 7;
    private static final int DELETE_DATABASE = 8;
    private static final int VERBOSE = 9;
    private static final int STATS_INTERVAL = 10;
    private static final int HELP = 11;
    private static final int VERSION = 12;
    private static final int NODE_STATUS_UPDATE_INTERVAL = 13;
    
    private static ResourceBundle bundle = null;
    private static final String RESOURCE_BUNDLE = "amSessionDB";
    
    private static boolean isServerUp = false;
    private int sleepTime = 60 * 1000; // 1 min in miillisec
    
    private static int readCount = 0;
    private static int writeCount = 0;
    private static int deleteCount = 0;
    private static int totalTrans = 0;
    
    // Session Constraints
    private static int scReadCount = 0;
    private static final int SESSION_VALID = 1;
    
    private static PrintWriter statsWriter = null;
    
    private Thread processThread;
    
    static {
        arguments.put("--username", new Integer(USER_NAME));
        arguments.put("-u", new Integer(USER_NAME));
        arguments.put("--password", new Integer(PASSWORD));
        arguments.put("-w", new Integer(PASSWORD));
        arguments.put("--passwordfile", new Integer(PASSWORD_FILE));
        arguments.put("-f", new Integer(PASSWORD_FILE));
        arguments.put("--cachesize", new Integer(CACHE_SIZE));
        arguments.put("-c", new Integer(CACHE_SIZE));
        arguments.put("--dbdirectory", new Integer(DIRECTORY));
        arguments.put("-b", new Integer(DIRECTORY));
        arguments.put("--clusteraddress", new Integer(CLUSTER_ADDRESS));
        arguments.put("-a", new Integer(CLUSTER_ADDRESS));
        arguments.put("--numcleansessions", new Integer(NUM_CLEAN_SESSIONS));
        arguments.put("-s", new Integer(NUM_CLEAN_SESSIONS));
        arguments.put("--deletedatabase", new Integer(DELETE_DATABASE));
        arguments.put("-r", new Integer(DELETE_DATABASE));
        arguments.put("--verbose", new Integer(VERBOSE));
        arguments.put("-v", new Integer(VERBOSE));
        arguments.put("--statsInterval", new Integer(STATS_INTERVAL));
        arguments.put("-i", new Integer(STATS_INTERVAL));
        arguments.put("--help", new Integer(HELP));
        arguments.put("-h", new Integer(HELP));
        arguments.put("--version", new Integer(VERSION));
        arguments.put("-n", new Integer(VERSION));
        arguments.put("--nodestatusupdateinterval", 
                      new Integer(NODE_STATUS_UPDATE_INTERVAL));
        arguments.put("-p", 
                      new Integer(NODE_STATUS_UPDATE_INTERVAL));
        try {
            bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, 
                     Locale.getDefault());
        } catch (MissingResourceException mre) {
            System.err.println("Cannot get the resource bundle.");
            System.exit(1);
        }
        
        statsWriter = new PrintWriter(System.out);

    }    
        

    public AMSessionDB(String id) throws Exception {
        
        _id = id;
    }
    
    private void initDB() throws Exception {
        try {
            sessionDbEnv.setup(sessDbEnvPath, // path to the environment home
                               false);        // is this environment read-only?
            // Open the data accessor. This is used to retrieve
            // persistent objects.
            da = new SessionDataAccessor(sessionDbEnv.getEntityStore());
        } catch (DatabaseException dbe) {
            // Exception handling goes here
            System.err.println("Error in creating session data accessor");
            System.err.println(dbe.getMessage());     
            if(verbose) {
                dbe.printStackTrace();
            }
        } 
    }
       
    private void initJMQ () throws Exception {

        ConnectionFactoryProvider provider = ConnectionFactoryProviderFactory
                .getProvider();
        tFactory = provider.newTopicConnectionFactory(clusterAddress,
                true, true, userName, userPassword);

        tConn = tFactory.createTopicConnection();
        tSession = tConn.createTopicSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        reqTopic = tSession.createTopic(DBREQUEST);
        resTopic = tSession.createTopic(DBRESPONSE);

        reqSub = tSession.createSubscriber(reqTopic);
        resPub = tSession.createPublisher(resTopic);
        
        tNodeSubSession = tConn.createTopicSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        tNodePubSession = tConn.createTopicSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        
        dbNodeStatusSubTopic = tNodeSubSession.createTopic(DBNODESTATUS);
        dbNodeStatusSub =
            tNodeSubSession.createSubscriber(dbNodeStatusSubTopic);
        dbNodeStatusPubTopic = tNodePubSession.createTopic(DBNODESTATUS);
        dbNodeStatusPub =
            tNodePubSession.createPublisher(dbNodeStatusPubTopic);

        tConn.start();
        /*
         * reseting the StartTime for MasterDBNodeChecker
         * in case of Broker restart/connection failure
         */
        isMasterNode = false;
        localStartTime = System.currentTimeMillis();
        isServerUp = true;
    }
    
    private void initialize(String args[]) throws Exception {
        parseCommandLine(args);
        System.out.println(bundle.getString("initializing"));
        initDB();
        initJMQ();
        initMasterDBNodeChecker();
        processThread = new Thread(this);        
        processThread.setName(_id);
        processThread.start();
        System.out.println(bundle.getString("startsuccess"));
        
    }
    
    private void initMasterDBNodeChecker() {
        localNodeID = (new SecureRandom()).nextLong();
        localStartTime = System.currentTimeMillis();
        nodeStatusSender = new Thread(new NodeStatusSender());        
        nodeStatusSender.start();
        nodeStatusReceiver = new Thread(new NodeStatusReceiver());
        nodeStatusReceiver.start();
        if(verbose) {
            System.out.println(bundle.getString("waitfornodechecking"));
        }      
        try {
            // Wait until the NodeInfo of all the peer BDB nodes to 
            // be received by the local server. 
            Thread.sleep(nodeUpdateInterval+nodeUpdateGraceperiod);
        } catch (Exception e) {
            System.err.println(e.getMessage());     
            if(verbose) {
                e.printStackTrace();
            }         
        }     
        determineMasterDBNode();
    }
    
    private void Shutdown() {
        try {
            sessionDbEnv.close();
        } catch (Exception e) {
            System.out.println("e.getMessage");
        }
    }

    public int process() throws Exception 
    {
        BytesMessage message = (BytesMessage) reqSub.receive();
                
        String id = message.getStringProperty(ID);
        String op = message.getStringProperty("op");
        
        showAllSessionRecords();
        
        if (op.indexOf(READ) >= 0) {
            if(verbose) {
                System.out.println(bundle.getString("readmsgrecv"));
            }
	    if(statsEnabled) {
                readCount++;
            }
            String sid = getLenString(message);
            long random = message.readLong();
            
            System.out.println(">>>>>>>>>>>>>> Read by SID : " + sid);

            SessionRecord sessionRec = null;
            try {
                sessionRec = da.sessionBySID.get(sid);
            } catch (DatabaseException ex) {
                ex.printStackTrace();
                System.out.println("READ exc: " + ex);
            }
            
            if (sessionRec != null) { 
                System.out.println(">>>>>>>>>>>>>> Found record !");
            } else {
                System.out.println(">>>>>>>>>>>>>> Not found record !");
            }
            
            if (sessionRec != null) { 
                BytesMessage resmsg = 
                    (BytesMessage) tSession.createBytesMessage();
                resmsg.setStringProperty(ID, id);
	        resmsg.writeLong(random);

                byte blob[] = sessionRec.getSessionBlob();
                resmsg.writeLong(blob.length);
                resmsg.writeBytes(blob);
                resPub.publish(resmsg);
            } else if(isMasterNode) {
                BytesMessage resmsg = 
                    (BytesMessage) tSession.createBytesMessage();
                resmsg.setStringProperty(ID, id);
                resmsg.setStringProperty(OP_STATUS, NOT_FOUND);
                resmsg.writeLong(random);
                resPub.publish(resmsg);
            }
        } else if (op.indexOf(WRITE) >= 0) {
            
            if(verbose) {
                System.out.println(bundle.getString("writemsgrecv"));
            }    
	    if(statsEnabled) {
                writeCount++;
            }
            String sid = getLenString(message);
            long expdate = message.readLong();
            byte[] uuid = getLenBytes(message);
            byte[] masterSID = getLenBytes(message);
            int state = message.readInt();
            byte[] stuff = getLenBytes(message);
            
            System.out.println(">>>>>>>>>>>>>> Write by SID : " + sid);

            SessionRecord record = new SessionRecord();
            record.setSID(sid);
            record.setExpDate(expdate);
            record.setUUID(new String(uuid, "utf8"));
            record.setMasterSID(new String(masterSID, "utf8"));
            record.setSessionState(state);
            record.setSessionBlob(stuff);

            da.sessionBySID.put(record);
        } else if (op.indexOf(DELETEBYDATE) >= 0) {
            if(verbose) {
                System.out.println(bundle.getString("datemsgrecv"));
            }    
            long expDate = message.readLong();
            System.out.println(">>>>>>>>>>>>>> Delete by Date : " + expDate);
            deleteByDate(expDate, numCleanSessions); 
        } else if (op.indexOf(DELETE) >= 0) {
            if(verbose) {
                System.out.println(bundle.getString("deletemsgrecv"));
            }
	    if(statsEnabled) {
                deleteCount++;
            }
            String sid = getLenString(message);
            System.out.println(">>>>>>>>>>>>>> Delete by SID : " + sid);
            Transaction txn = sessionDbEnv.getEnv().beginTransaction(null, null);
            try {
                da.sessionBySID.delete(txn, sid);
                txn.commit();
            } catch (Exception e) {
                txn.abort();
                System.out.println("Aborted txn: " + e.toString());
                e.printStackTrace();
            }             
        } else if (op.indexOf(SHUTDOWN) >= 0) {
            Shutdown();
            return(1);
        } else if (op.indexOf(GET_SESSION_COUNT) >= 0) {

            if(verbose) {
                System.out.println(bundle.getString("getsessioncount"));
            }    
	    if(statsEnabled) {
                scReadCount++;
            }
            getSessionsByUUID(message, id);
        } 
        return 0;
    }

    public void getSessionsByUUID(BytesMessage message,
                                  String id) 
        throws Exception {
        
        if (!isMasterNode) {
            if (verbose) {
                System.out.println(bundle.getString("notmasterdbnode"));
            }
            return;
        }

        String uuid = getLenString(message);
        long random = message.readLong();
        int nsessions = 0;
        Vector sessions = new Vector();
        EntityCursor<SessionRecord> records = null;
        
        try {
            // Use the SessionRecord uuid secondary key to retrieve
            // these objects.
            records = da.sessionByUUID.subIndex(uuid).entities();
            for (SessionRecord record : records) {
                // only the "valid" non-expired sessions with the 
                // right uuid will be counted
                long currentTime = 
                    System.currentTimeMillis()/1000;
                Long expdate = record.getExpDate();
                if ((record.getSessionState() == SESSION_VALID) &&
                    (currentTime < expdate.longValue())) {
                    nsessions++;
                    SessionExpTimeInfo info = 
                        new SessionExpTimeInfo();
                    info.masterSIDLen = record.getMasterSID().length();
                    info.masterSID = record.getMasterSID().getBytes("utf8");
                    info.expTime = expdate;
                    sessions.add(info);
                }
            }
        } catch (Exception e) {
            if (verbose) {
                e.printStackTrace();
            }
        } finally {
            records.close();
        }
        // construct a response message which contains the
        // session count
        BytesMessage resmsg 
            = (BytesMessage) tSession.createBytesMessage();
        resmsg.setStringProperty(ID, id);
        resmsg.writeLong(random);
        resmsg.writeInt(nsessions);
        for (int i=0;i<sessions.size();i++) {
            SessionExpTimeInfo info =
                (SessionExpTimeInfo)sessions.get(i);            
            resmsg.writeInt(info.masterSIDLen);
            resmsg.writeBytes(info.masterSID);
            resmsg.writeLong(info.expTime);
        }
        resPub.publish(resmsg);                    
    }
     
    private class SessionExpTimeInfo {
        int masterSIDLen;
        byte[] masterSID;
        long expTime;
    }
    
    public void deleteByDate(long expTime, int cleanCount) 
    throws Exception {
        EntityCursor<SessionRecord> records = 
            da.sessionByExpDate.entities();
        Transaction txn = null;
        
        for (SessionRecord record : records) {
            Long expdate = record.getExpDate();
            if (expdate.longValue() <= expTime) {
                try {
                    txn = sessionDbEnv.getEnv().beginTransaction(null, null);
                    da.sessionByExpDate.delete(txn, expdate);
                    txn.commit();
                } catch (Exception e) {
                    txn.abort();
                    System.out.println("Aborted txn: " + e.toString());
                    e.printStackTrace();
                }             
                
                System.out.println(">>>>> Delete the record has " + expdate);
            } else {
                break;
            }
        }
        records.close();
    }

    // Displays all the session records in the store
    private void showAllSessionRecords() 
        throws DatabaseException {

        // Get a cursor that will walk every
        // inventory object in the store.
        EntityCursor<SessionRecord> records =
            da.sessionByExpDate.entities();

        try { 
            for (SessionRecord record : records) {
                displaySessionRecord(record);
            }
        } catch(DatabaseException de){
            System.err.println(de.getMessage());     
            if(verbose) {
                de.printStackTrace();
            }        	
        } finally {
            records.close();
        }
    }
    
    private void displaySessionRecord(SessionRecord theSession)
        throws DatabaseException {

        assert theSession != null;
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("SID :\t " + theSession.getSID());
        System.out.println("Expiration Date :\t " + theSession.getExpDate());
        System.out.println("Master SessionID :\t " + theSession.getMasterSID());
        System.out.println("Session State :\t " + theSession.getSessionState());
        System.out.println("UUID :\t " + theSession.getUUID());
    }
    
    String getLenString(Message msg) throws Exception
    {
            BytesMessage message = (BytesMessage) msg;
            long sidlen = message.readLong();
            byte[] sidbytes  = new byte[(int) sidlen];
            message.readBytes(sidbytes);
            return(new String(sidbytes, "utf8"));
    }
    byte[] getLenBytes(Message msg) throws Exception
    {
            BytesMessage message = (BytesMessage) msg;
            long sidlen = message.readLong();
            byte[] sidbytes  = new byte[(int) sidlen];
            message.readBytes(sidbytes);
            return(sidbytes);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        long epoch = startTime;
        totalTrans = 0;
        while (true) {
            try {
                if (isServerUp) {
                    int ret = process();
                    totalTrans++;

                    if (statsEnabled && 
                            ((System.currentTimeMillis() - startTime) >= statsInterval)) {

                        printStats(); 
                        startTime = System.currentTimeMillis();
                        totalTrans = 0;
                        readCount = 0;
                        writeCount = 0;
                        deleteCount = 0;
                        scReadCount = 0;
                    }
                } else {
                    /*
                     * When server is down this thread runs with a sleep
                     * interval of 1 minute and cleans sessions 5 times the
                     * numCleanSessions value from the Database.
                     */
                    long curTime = System.currentTimeMillis()/1000;
                    int cleanCount = numCleanSessions * 5;
                    deleteByDate(curTime, cleanCount);
                    Thread.sleep(sleepTime);
                    System.out.println(bundle.getString("reconnecttobroker"));
                    initJMQ();
                    System.out.println(bundle.getString("reconnectsuccessfull"));
                }
            } catch (Exception ex) {
                isServerUp = false;
                System.err.println(bundle.getString("brokerdown"));
                if(verbose) {
                    ex.printStackTrace();
                }    
            } catch (Throwable t) {
                if(verbose) {
                     t.printStackTrace();
                }     
            }
        }

    }
    // TODO move out to seperate class?
    private void sunSpecificConfig(TopicConnectionFactory tFactory) 
                 throws Exception
    {
        ConnectionFactory cf = (com.sun.messaging.ConnectionFactory) tFactory;
        
        cf.setProperty(ConnectionConfiguration.imqAddressList,
                clusterAddress);
        cf.setProperty(ConnectionConfiguration.imqAddressListBehavior,
                "RANDOM");
        cf.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");
        cf.setProperty(ConnectionConfiguration.imqConnectionFlowLimitEnabled,
                "true");
        cf.setProperty(ConnectionConfiguration.imqDefaultUsername,
                userName);
        cf.setProperty(ConnectionConfiguration.imqDefaultPassword,
                userPassword);
    }

    static public void main(String args[]) 
    {
        
        try {
             dbs = new AMSessionDB("AMSessionDB");
             dbs.initialize(args);
             
             AMDBShutdown shutDownHook = new AMDBShutdown();
             Runtime.getRuntime().addShutdownHook(shutDownHook);
            
        } catch (Exception ex) {
            System.out.println("Exception main()");
            ex.printStackTrace();
            System.exit(1);
            
        }
        
    }
    
    static class AMDBShutdown extends Thread {
        public void run() {
            dbs.Shutdown();
        }
    }
    
   private void printCommandError(String errorMessage, String command) {
        System.err.println(bundle.getString(errorMessage) + " " + command);
        System.err.println(bundle.getString("usage"));
        System.exit(1);
   }
   
   private void printUsage() {
       System.err.println(bundle.getString("usage"));
       System.exit(1);
   }
   
   private void printStats() {
       
       
       statsWriter.println(bundle.getString("printingstats"));
       statsWriter.println(bundle.getString("totalreq") + " " +
            totalTrans);
       statsWriter.println(bundle.getString("totalread") + " " +
               readCount);
       statsWriter.println(bundle.getString("totalwrite") + " " +
               writeCount);
       statsWriter.println(bundle.getString("totaldelete") + " " +
               deleteCount);
       statsWriter.println(bundle.getString("totalreadsessioncount") 
                           + " " + scReadCount);
       statsWriter.flush();
       
   }
    
    private void parseCommandLine(String[] argv) throws Exception {
        if (!validateArguments(argv, bundle)) {
            printUsage();
        }

        for (int i = 0; i < argv.length; i++) {
            int opt = getToken(argv[i]);
            switch (opt) {

            case USER_NAME:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                userName = argv[i];
                if (getToken(userName.toLowerCase()) != INVALID) {
                    printCommandError("nousername", argv[i-1]);                    
                }
                break;
            case PASSWORD:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                userPassword = argv[i];
                if (getToken(userPassword.toLowerCase()) != INVALID) {
                    printCommandError("nopassword", argv[i-1]);                    
                    printCommandError("nopassword", argv[i-1]);                    
                    printCommandError("nopassword", argv[i-1]);                    
                }
                break;    
            case PASSWORD_FILE:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                String passwordfile = argv[i];
                if ((getToken(passwordfile.toLowerCase()) != INVALID)) {
                        printCommandError("nopasswordfile",  argv[i-1]);                    
                }
                
                String pwd = SFOCryptUtil.decrypt(SFOCryptUtil.DEFAULT_PBE_PWD,
                        AMSFOPassword.readEncPasswordFromFile(passwordfile));

                if (pwd == null) {
                    printCommandError("nopwdinfile",  argv[i]); 
                }

                userPassword = pwd.trim();
                break;
            case CACHE_SIZE:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }

                String cache = argv[i];
                if (getToken(cache) != INVALID) {
                    printCommandError("nocachesize",  argv[i-1]);                    
                }
                try {
                    cacheSize = Integer.parseInt(cache);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i-1]);
                    
                }
                break;
            case DIRECTORY:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                dbDirectory = argv[i];
                if (getToken(dbDirectory.toLowerCase()) != INVALID) {
                    printCommandError("nodbdirectory",  argv[i-1]);
                }
                
                sessDbEnvPath = new File(dbDirectory);
                break;
            case CLUSTER_ADDRESS:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                clusterAddress = argv[i];
                if (getToken(clusterAddress.toLowerCase()) != INVALID) {
                    printCommandError("noclusteraddress",  argv[i-1]);
                }
                break;
            case NUM_CLEAN_SESSIONS:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }

                String nCleanSessions = argv[i];
                if (getToken(nCleanSessions) != INVALID) {
                    printCommandError("nonumcleansessions",  argv[i-1]);
                }
                try {
                    numCleanSessions = Integer.parseInt(nCleanSessions);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i-1]);

                }
                break;
            case VERBOSE:
                verbose = true;
                break;
            case STATS_INTERVAL:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }

                String sInterval = argv[i];
                if (getToken(sInterval) != INVALID) {
                    printCommandError("nostatsinterval",  argv[i-1]);
                }
                try {
                    statsInterval = Long.parseLong(sInterval);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i-1]);

                }
                if (statsInterval <= 0) {
                    statsInterval = 60;
                }
		statsInterval = statsInterval * 1000; //converting to millisec
                statsEnabled = true;
                break;
            case HELP:
                System.err.println(bundle.getString("usage"));
                System.exit(0);
                break;
            case VERSION:
                System.out.println("\n" + bundle.getString("version"));
                System.exit(0);
                break;
            case NODE_STATUS_UPDATE_INTERVAL:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                String interval = argv[i];
                if (getToken(interval) != INVALID) {
                    printCommandError("nonodestatusupdateinterval",  
                                      argv[i-1]);
                }
                try {
                    nodeUpdateInterval = Long.parseLong(interval);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i-1]);

                }
                if (nodeUpdateInterval <= 0) {
                    nodeUpdateInterval = 5000;
                }
                break;
            default:
                System.err.println(bundle.getString("usage"));
                System.err.println(bundle.getString("invalid-option") + argv[i]);
                System.exit(1);
            }

        }
    }
    
    
    /**
     * Return true if arguments are valid.
     * 
     * @param argv
     *            Array of arguments.
     * @param bundle
     *            Resource Bundle.
     * @return true if arguments are valid.
     */
    static boolean validateArguments(String[] argv, ResourceBundle bundle) {
	int len = argv.length;
	boolean hasClusterAddress = false;
	boolean retValue = true;
        boolean hasPassword = false;
        boolean hasPwdFile = false;
	
        if (len == 0) {
	    retValue = false;
	} else if (len == 1) {
	    String arg = argv[0].toLowerCase();

	    if (!(arg.equals("--help") || 
	            arg.equals("-h") ||
	            arg.equals("--version") ||
	            arg.equals("-n")) )  {
		System.err.println(bundle.getString("invalid-option") + arg);
		retValue = false;
	    }
        } else {
            for (int i = 0; (i < (len -1)); i++) {
    	        String arg = argv[i].toLowerCase();
    	    	if (arg.equals("--clusteraddress") || arg.equals("-a")) {
    		    hasClusterAddress = true;
    		}
                if(arg.equals("--password") || arg.equals("-w")) {
                    hasPassword = true;
                }
                if(arg.equals("--passwordfile") || arg.equals("-f")) {
                    hasPwdFile = true;
                }
    	    }
            
            if(hasPassword && hasPwdFile) {
                retValue = false;
            }
                        
            if(!hasClusterAddress) {
                retValue = false;
            }
	}

	return retValue;
    }
    
    
    int getToken(String arg) {
        try {
            return(((Integer)arguments.get(arg)).intValue());
        } catch(Exception e) {
            return 0;
        }
    }
    
   
      
    private void deleteDirectory(String fileName) {
        File dir = new File(fileName);
        
        if(dir.exists()) {
            File files[] = dir.listFiles();
            for (int i=0; i < files.length; i++) {
                files[i].delete();
            }
        } else {
            dir.mkdir();
        }
        
    }

    // Check if the local daemon process is the master (longest-lived) 
    // BDB node
    static private void determineMasterDBNode() {
        synchronized (serverStatusMap) {                        
            Set s = serverStatusMap.keySet();
            Iterator iter = s.iterator();
            
            boolean masterDB = true;
            while (iter.hasNext()) {
                String key = (String)iter.next();
                NodeInfo info = (NodeInfo)serverStatusMap.get(key);
                if (info.startTime < localStartTime) {
                    masterDB = false;
                    break;
                }
            }
            isMasterNode = masterDB;        
        }
    }    

   static void debugMessage(String msg) {
        if (debug) {
            System.out.println(msg);
        }
    }

    static void pendRunning() {
        // System.in std input stream already opened by default.
        // Wrap in a new reader stream to obtain 16 bit capability.
        InputStreamReader reader = new InputStreamReader (System.in);

        // Wrap the reader with a buffered reader.
        BufferedReader buf_in = new BufferedReader (reader);

        String str = "q";
        try {
            System.out.print("Please press the return key to continue.");
            // Read a whole line a time. Check the string for
            // the "quit" input to jump from the loop.
            do {
                // Read text from keyboard
                str = buf_in.readLine ();
                if (str == null) 
                    debugMessage("------------>str is null !");
                else
                    debugMessage("------------>str is not null !");

            } while (!str.equals("q"));
        } catch (Exception e) {
                debugMessage("Exception in pendRunning : "+e);
                e.printStackTrace();
        }
    }

    // NodeInfo data structure
    private class NodeInfo {
        long nodeID;
        long startTime;
        long lastUpdateTime;
    }

    // This NodeStatusSender thread keeps performing the following
    // actions (interval defined 5 secs):
    // (1) Send out the local nodeID/startTime to its peer BDB nodes.
    // (2) Remove the outdated NodeInfo entries from the 
    //     serverStatusMap map if the information is obsolete.
    // (3) Determine whether the local BDB node can become the master
    //     BDB node.

    class NodeStatusSender implements Runnable {

        NodeStatusSender() {       
        }
        
        public void run() {
            while (true) {
                
                try {
                    if (isServerUp) {
                        long nextRun = System.currentTimeMillis()
                                + nodeUpdateInterval;
                        BytesMessage msg = (BytesMessage) tSession
                                .createBytesMessage();
                        msg.writeLong(localNodeID);
                        msg.writeLong(localStartTime);
                        dbNodeStatusPub.publish(msg);

                        long sleeptime = nextRun - System.currentTimeMillis();
                        if (sleeptime > 0) {
                            Thread.sleep(sleeptime);
                        }
                        RemoveOutdatedNodeInfo();
                        determineMasterDBNode();
                    } else {
                        Thread.sleep(nodeUpdateInterval);
                    }
                } catch (Exception e) {
                    isServerUp = false;
                    System.err.println(bundle.getString("brokerdown"));
                    if (verbose) {
                        e.printStackTrace();
                    }
                } catch (Throwable t) {
                    if (verbose) {
                        t.printStackTrace();
                    }     
               }
            }
        }

        // Remove the outdated NodeInfo from the serverStatusMap map 
        // if the information is obsolete.
        void RemoveOutdatedNodeInfo() {            
            synchronized (serverStatusMap) {
                Set s = serverStatusMap.keySet();
                Iterator iter = s.iterator();
                while (iter.hasNext()) {
                    String key = (String)iter.next();
                    NodeInfo info = (NodeInfo)serverStatusMap.get(key);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > 
                        (info.lastUpdateTime +                         
                         nodeUpdateInterval + 
                         nodeUpdateGraceperiod)) {
                        iter.remove();
                    }
                }        
            }
        }
        
    }
    
    // This NodeStatusReceiver thread keeps waiting for the message
    // sent by its peer BDB nodes and update the local serverStatusMap
    // accordingly.
    class NodeStatusReceiver implements Runnable {
        
        NodeStatusReceiver() {            
        }
        
        public void run() {
            while (true) {
                try {
                    if (isServerUp) {
                        BytesMessage msg = (BytesMessage) dbNodeStatusSub
                                .receive();
                        long nodeID = msg.readLong();    
                        long startTime = msg.readLong();
                        if (nodeID == localNodeID) {
                            // ignore the message sent by the local server
                            continue;
                        }
                        NodeInfo info = new NodeInfo();
                        info.nodeID = nodeID;
                        info.startTime = startTime;
                        info.lastUpdateTime = System.currentTimeMillis();
                        synchronized (serverStatusMap) {
                            serverStatusMap.put(String.valueOf(nodeID), info);
                        }
                    } else {
                        Thread.sleep(nodeUpdateInterval);
                    }
                } catch (Exception e) {
                    isServerUp = false;
                    System.err.println(bundle.getString("brokerdown"));
                    if (verbose) {
                        e.printStackTrace();
                    }
                } catch (Throwable t) {
                    if (verbose) {
                        t.printStackTrace();
                    }     
               }
            }
        }
    }

}
