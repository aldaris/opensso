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
 * $Id: AMSessionDB.java,v 1.1 2005-11-01 00:29:53 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.jmqdb.client;

import java.io.PrintWriter;
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
import com.sleepycat.db.Db;
import com.sleepycat.db.DbEnv;
import com.sleepycat.db.DbSecondaryKeyCreate;
import com.sleepycat.db.DbTxn;
import com.sleepycat.db.Dbc;
import com.sleepycat.db.Dbt;

public class AMSessionDB implements DbSecondaryKeyCreate, Runnable {

    static AMSessionDB dbs;

    /* Operations */

    static public final String READ = "READ";

    static public final String WRITE = "WRITE";

    static public final String DELETE = "DELETE";

    static public final String DELETEBYDATE = "DELETEBYDATE";

    static public final String SHUTDOWN = "SHUTDOWN";

    static public final String GET_SESSION_COUNT = "GET_SESSION_COUNT";

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

    private static long nodeUpdateInterval = 5000; // 5 seconds in
                                                    // milli-seconds

    private static long nodeUpdateGraceperiod = 1000; // 1 second in
                                                        // milli-seconds

    DbEnv env = null;

    Db table = null;

    Db expdateindex = null;

    Db uidindex = null;

    /* Config data - TODO : move to properties/CLI options */

    private String databaseFileName = "amsessions.db";

    private int flags = 0; /* Db.DB_AUTO_COMMIT */

    private String userName = "guest";

    private String userPassword = "guest";

    private int cacheSize = 32; /* 32 mb has the best overall performance */

    private String clusterAddress = null;

    private String dbDirectory = "sessiondb";

    private int numCleanSessions = 1000;

    private boolean verbose = false;

    private long statsInterval = 60 * 1000; // 60 seconds

    private boolean statsEnabled = false;

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
        arguments.put("--nodestatusupdateinterval", new Integer(
                NODE_STATUS_UPDATE_INTERVAL));
        arguments.put("-p", new Integer(NODE_STATUS_UPDATE_INTERVAL));
        try {
            bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, Locale
                    .getDefault());
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

        // Init Env
        env = new DbEnv(0);
        env.setErrorStream(System.err);
        env.setErrorPrefix("SessionBDBEnv");
        // cachesize is in Megabytes
        env.setCacheSize(cacheSize * 1024 * 1024, 0);
        env.setFlags(/* Db.DB_AUTO_COMMIT | */Db.DB_TXN_WRITE_NOSYNC, true);
        env.open(dbDirectory, Db.DB_CREATE | Db.DB_INIT_LOCK | Db.DB_INIT_LOG
                | Db.DB_INIT_MPOOL | Db.DB_INIT_TXN, 0);

        // Initialize BDB
        table = new Db(env, 0);
        table.setErrorStream(System.err);
        table.setErrorPrefix("AMSessionDB");
        table.open(null, databaseFileName, "primary", Db.DB_BTREE,
                Db.DB_DIRTY_READ | Db.DB_CREATE, 0644);
        expdateindex = new Db(env, 0);
        expdateindex.setFlags(Db.DB_DUPSORT);
        expdateindex.setErrorStream(System.err);
        expdateindex.setErrorPrefix("SessionExpDateIdx");
        expdateindex.open(null, databaseFileName, "secondary", Db.DB_BTREE,
                Db.DB_CREATE | Db.DB_DIRTY_READ, 0644);
        table.associate(null, expdateindex, this, 0);

        uidindex = new Db(env, 0);
        uidindex.setFlags(Db.DB_DUPSORT);
        uidindex.setErrorStream(System.err);
        uidindex.setErrorPrefix("SessionUidIdx");
        uidindex.open(null, databaseFileName, "secondary2", Db.DB_BTREE,
                Db.DB_CREATE | Db.DB_DIRTY_READ, 0644);
        table.associate(null, uidindex, new UidIndex(), 0);

    }

    private void initJMQ() throws Exception {

        ConnectionFactoryProvider provider = ConnectionFactoryProviderFactory
                .getProvider();
        tFactory = provider.newTopicConnectionFactory(clusterAddress, "RANDOM",
                true, true, userName, userPassword);

        tConn = tFactory.createTopicConnection();
        tSession = tConn.createTopicSession(false, Session.DUPS_OK_ACKNOWLEDGE);
        reqTopic = tSession.createTopic(DBREQUEST);
        resTopic = tSession.createTopic(DBRESPONSE);

        reqSub = tSession.createSubscriber(reqTopic);
        resPub = tSession.createPublisher(resTopic);

        tNodeSubSession = tConn.createTopicSession(false,
                Session.DUPS_OK_ACKNOWLEDGE);
        tNodePubSession = tConn.createTopicSession(false,
                Session.DUPS_OK_ACKNOWLEDGE);

        dbNodeStatusSubTopic = tNodeSubSession.createTopic(DBNODESTATUS);
        dbNodeStatusSub = tNodeSubSession
                .createSubscriber(dbNodeStatusSubTopic);
        dbNodeStatusPubTopic = tNodePubSession.createTopic(DBNODESTATUS);
        dbNodeStatusPub = tNodePubSession.createPublisher(dbNodeStatusPubTopic);

        tConn.start();
        /*
         * reseting the StartTime for MasterDBNodeChecker in case of Broker
         * restart/connection failure
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
        if (verbose) {
            System.out.println(bundle.getString("waitfornodechecking"));
        }
        try {
            // Wait until the NodeInfo of all the peer BDB nodes to
            // be received by the local server.
            Thread.sleep(nodeUpdateInterval + nodeUpdateGraceperiod);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (verbose) {
                e.printStackTrace();
            }
        }
        determineMasterDBNode();
    }

    private void Shutdown() {
        try {
            expdateindex.close(0);
            uidindex.close(0);
            table.close(0);
            env.close(0);
        } catch (Exception e) {
            System.out.println("e.getMessage");
        }
    }

    public int process() throws Exception {
        BytesMessage message = (BytesMessage) reqSub.receive();

        String id = message.getStringProperty(ID);
        String op = message.getStringProperty("op");
        if (op.indexOf(READ) >= 0) {
            if (verbose) {
                System.out.println(bundle.getString("readmsgrecv"));
            }
            if (statsEnabled) {
                readCount++;
            }
            String sid = getLenString(message);
            long random = message.readLong();
            StringDbt key = new StringDbt(sid);
            Dbt data = new Dbt();

            int err = 0;
            try {
                err = table.get(null, key, data, 0);
                // TODO ERROR PROCESSING !!!!!!
                if ((err != 0 || data.getData() == null) && verbose) {
                    System.out.println("Table.get returned: " + err
                            + " datalen = " + data.getData() + " notfound="
                            + Db.DB_NOTFOUND + " keystuff" + Db.DB_KEYEMPTY);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("READ exc: " + ex);
                err = 1;
            }

            if (err == 0) {
                BytesMessage resmsg = tSession.createBytesMessage();
                resmsg.setStringProperty(ID, id);
                resmsg.writeLong(random);

                // Data fields:
                // - expdate => 8 bytes
                // - uuid length => 4 bytes
                // - uuid => variable length
                // - masterSID length => 4 bytes
                // - masterSID => variable length
                // - session state => 4 bytes
                // - session blob => variable length
                ByteBuffer buf = ByteBuffer.wrap(data.getData());
                int offset = 0;
                buf.getLong(); // skip the "expdate"
                offset += 8;
                int uuidLen = buf.getInt();
                offset += 4;
                byte[] uuid = new byte[uuidLen];
                buf.get(uuid, 0, uuidLen);
                offset += uuidLen;
                int masterSIDLen = buf.getInt();
                offset += 4;
                byte[] masterSID = new byte[masterSIDLen];
                buf.get(masterSID, 0, masterSIDLen);
                offset += masterSIDLen;
                buf.getInt();
                offset += 4;

                resmsg.writeLong(data.getSize() - offset);
                resmsg.writeBytes(data.getData(), offset,
                        (data.getSize() - offset));
                resPub.publish(resmsg);
            } else if (isMasterNode) {
                BytesMessage resmsg = tSession.createBytesMessage();
                resmsg.setStringProperty(ID, id);
                resmsg.setStringProperty(OP_STATUS, NOT_FOUND);
                resmsg.writeLong(random);
                resPub.publish(resmsg);
            }
        } else if (op.indexOf(WRITE) >= 0) {

            if (verbose) {
                System.out.println(bundle.getString("writemsgrecv"));
            }
            if (statsEnabled) {
                writeCount++;
            }
            String sid = getLenString(message);
            long expdate = message.readLong();
            byte[] uuid = getLenBytes(message);
            byte[] masterSID = getLenBytes(message);
            int state = message.readInt();
            byte[] stuff = getLenBytes(message);

            StringDbt key = new StringDbt(sid);
            // Data fields:
            // - expdate => 8 bytes
            // - uuid length => 4 bytes
            // - uuid => variable length
            // - masterSID length => 4 bytes
            // - masterSID => variable length
            // - session state => 4 bytes
            // - session blob => variable length
            int totalLen = 8 + 4 + uuid.length + 4 + masterSID.length + 4
                    + stuff.length;
            ByteBuffer buf = ByteBuffer.allocate(totalLen);
            buf.putLong(expdate).putInt(uuid.length).put(uuid).putInt(
                    masterSID.length).put(masterSID).putInt(state).put(stuff);

            Dbt data = new Dbt(buf.array());
            DbTxn txn = null; // env.txnBegin(null, 0);
            table.put(txn, key, data, flags);
            // txn.commit(0);

        } else if (op.indexOf(DELETEBYDATE) >= 0) {
            if (verbose) {
                System.out.println(bundle.getString("datemsgrecv"));
            }
            long expDate = message.readLong();
            deleteByDate(expDate, numCleanSessions);
        } else if (op.indexOf(DELETE) >= 0) {
            if (verbose) {
                System.out.println(bundle.getString("deletemsgrecv"));
            }
            if (statsEnabled) {
                deleteCount++;
            }
            String sid = getLenString(message);
            StringDbt key = new StringDbt(sid);
            table.delete(null, key, flags);
        } else if (op.indexOf(SHUTDOWN) >= 0) {
            Shutdown();
            return (1);
        } else if (op.indexOf(GET_SESSION_COUNT) >= 0) {

            if (verbose) {
                System.out.println(bundle.getString("getsessioncount"));
            }
            if (statsEnabled) {
                scReadCount++;
            }
            getSessionsByUUID(message, id);

        }
        return 0;
    }

    public void getSessionsByUUID(BytesMessage message, String id)
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

        try {
            Db idx = uidindex;
            DbTxn txn = null;
            Dbc iterator = null;
            try {
                txn = null; // env.txnBegin(null, 0);
                iterator = idx.cursor(txn, Db.DB_DIRTY_READ);
                StringDbt key = new StringDbt(uuid);
                Dbt data = new Dbt();

                // Get the 1st record
                int ret = iterator.get(key, data, Db.DB_DIRTY_READ | Db.DB_SET);
                while (ret == 0) {
                    // Data fields:
                    // - expdate => 8 bytes
                    // - uuid length => 4 bytes
                    // - uuid => variable length
                    // - masterSID length => 4 bytes
                    // - masterSID => variable length
                    // - session state => 4 bytes
                    // - session blob => variable length
                    ByteBuffer rec = ByteBuffer.wrap(data.getData());
                    long expdate = rec.getLong();
                    int uuidlen = rec.getInt();
                    byte[] uuidBytes = new byte[uuidlen];
                    rec.get(uuidBytes, 0, uuidlen);
                    int masterSIDLen = rec.getInt();
                    byte[] masterSID = new byte[masterSIDLen];
                    rec.get(masterSID, 0, masterSIDLen);
                    int state = rec.getInt();

                    // only the "valid" non-expired sessions with the
                    // right uuid will be counted
                    long currentTime = System.currentTimeMillis() / 1000;
                    if ((state == SESSION_VALID) && (currentTime < expdate)) {
                        nsessions++;
                        SessionExpTimeInfo info = new SessionExpTimeInfo();
                        info.masterSIDLen = masterSIDLen;
                        info.masterSID = masterSID;
                        info.expTime = expdate;
                        sessions.add(info);
                    }
                    // Get the next record with the same key (uuid)
                    ret = iterator.get(key, data, Db.DB_DIRTY_READ
                            | Db.DB_NEXT_DUP);
                }
            } catch (Exception ex1) {
                System.out.println(ex1.getMessage());
                if (verbose) {
                    ex1.printStackTrace();
                }
            }
            if (iterator != null)
                iterator.close();
            if (txn != null)
                txn.commit(0);
        } catch (Exception ex2) {
            if (verbose) {
                ex2.printStackTrace();
            }
        }
        // construct a response message which contains the
        // session count
        BytesMessage resmsg = tSession.createBytesMessage();
        resmsg.setStringProperty(ID, id);
        resmsg.writeLong(random);
        resmsg.writeInt(nsessions);
        for (int i = 0; i < sessions.size(); i++) {
            SessionExpTimeInfo info = (SessionExpTimeInfo) sessions.get(i);
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

    public void deleteByDate(long expTime, int cleanCount) {
        try {
            long startTime = System.currentTimeMillis();
            Db idx = expdateindex;
            DbTxn txn = null;
            Dbc iterator = null;
            try {
                txn = null; // env.txnBegin(null, 0);
                iterator = idx.cursor(txn, Db.DB_DIRTY_READ);
                Dbt key = new Dbt();
                Dbt data = new Dbt();
                int ntrans = 0;
                int expCount = 0;
                while (iterator.get(key, data, Db.DB_NEXT | 
                        Db.DB_DIRTY_READ) == 0) {
                    ntrans++;
                    long expdate = ByteBuffer.wrap(key.getData()).getLong();
                    if (expTime > expdate) {
                        int err = iterator.delete(0);
                        if (err != 0 && verbose) {
                            System.out.println("CLEANUP THREAD delete rec="
                                    + expdate + " err:" + err);
                        }
                        if (statsEnabled) {
                            expCount++;
                        }
                    }
                    if (ntrans == cleanCount) {
                        break;
                    }

                }
                if (statsEnabled) {
                    long elapseTime = System.currentTimeMillis() - startTime;
                    System.out.println(expCount
                            + bundle.getString("expsessionsclean") + " "
                            + elapseTime + " milliseconds");
                }

            } catch (Exception ex1) {
                System.out.println(ex1.getMessage());
                if (verbose) {
                    ex1.printStackTrace();
                }
            }
            if (iterator != null)
                iterator.close();
            if (txn != null)
                txn.commit(0);
        } catch (Exception ex2) {
            if (verbose) {
                ex2.printStackTrace();
            }
        }
    }

    /* Index related callback */
    public int secondaryKeyCreate(Db secondary, Dbt key, Dbt data, Dbt result) {
        result.setData(data.getData());
        result.setSize(8);
        return 0;
    }

    String getLenString(Message msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        long sidlen = message.readLong();
        byte[] sidbytes = new byte[(int) sidlen];
        message.readBytes(sidbytes);
        return (new String(sidbytes));
    }

    byte[] getLenBytes(Message msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        long sidlen = message.readLong();
        byte[] sidbytes = new byte[(int) sidlen];
        message.readBytes(sidbytes);
        return (sidbytes);
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        totalTrans = 0;
        while (true) {
            try {
                if (isServerUp) {
                    process();
                    totalTrans++;

                    if (statsEnabled
                            && ((System.currentTimeMillis() - startTime) 
                                    >= statsInterval)) {

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
                    long curTime = System.currentTimeMillis() / 1000;
                    int cleanCount = numCleanSessions * 5;
                    deleteByDate(curTime, cleanCount);
                    Thread.sleep(sleepTime);
                    System.out.println(bundle.getString("reconnecttobroker"));
                    initJMQ();
                    System.out
                            .println(bundle.getString("reconnectsuccessfull"));
                }
            } catch (Exception ex) {
                isServerUp = false;
                System.err.println(bundle.getString("brokerdown"));
                if (verbose) {
                    ex.printStackTrace();
                }
            } catch (Throwable t) {
                if (verbose) {
                    t.printStackTrace();
                }
            }
        }

    }

    static public void main(String args[]) {

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

    static/* inner class to generate Uid Index */
    class UidIndex implements DbSecondaryKeyCreate {
        public int secondaryKeyCreate(Db secondary, Dbt key, Dbt data,
                Dbt result) {
            ByteBuffer rec = ByteBuffer.wrap(data.getData());

            // offset 0 : expdate (long), skip it
            rec.getLong();
            // offset 8 : uuid length (int)
            int uuidlen = rec.getInt();
            // offset 12 : uuid starts here (bytes)
            result.setData(data.getData());
            result.setOffset(12);
            result.setSize(uuidlen);
            return 0;
        }
    }

    static/* inner */
    class StringDbt extends Dbt {
        StringDbt() {
            setFlags(Db.DB_DBT_MALLOC); // tell Db to allocate on retrieval
        }

        StringDbt(String value) {
            setString(value);
            setFlags(Db.DB_DBT_MALLOC); // tell Db to allocate on retrieval
        }

        void setString(String value) {
            byte[] data = value.getBytes();
            setData(data);
            setSize(data.length);
        }

        String getString() {
            return new String(getData(), getOffset(), getSize());
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
        statsWriter.println(bundle.getString("totalreq") + " " + totalTrans);
        statsWriter.println(bundle.getString("totalread") + " " + readCount);
        statsWriter.println(bundle.getString("totalwrite") + " " + writeCount);
        statsWriter
                .println(bundle.getString("totaldelete") + " " + deleteCount);
        statsWriter.println(bundle.getString("totalreadsessioncount") + " "
                + scReadCount);
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
                    printCommandError("nousername", argv[i - 1]);
                }
                break;
            case PASSWORD:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                userPassword = argv[i];
                if (getToken(userPassword.toLowerCase()) != INVALID) {
                    printCommandError("nopassword", argv[i - 1]);
                }
                break;
            case PASSWORD_FILE:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                String passwordfile = argv[i];
                if ((getToken(passwordfile.toLowerCase()) != INVALID)) {
                    printCommandError("nopasswordfile", argv[i - 1]);
                }
                String pwd = SFOCryptUtil.decrypt(SFOCryptUtil.DEFAULT_PBE_PWD,
                        AMSFOPassword.readEncPasswordFromFile(passwordfile));

                if (pwd == null) {
                    printCommandError("nopwdinfile", argv[i]);
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
                    printCommandError("nocachesize", argv[i - 1]);
                }
                try {
                    cacheSize = Integer.parseInt(cache);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i - 1]);

                }
                break;
            case DIRECTORY:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                dbDirectory = argv[i];
                if (getToken(dbDirectory.toLowerCase()) != INVALID) {
                    printCommandError("nodbdirectory", argv[i - 1]);
                }
                break;
            case CLUSTER_ADDRESS:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }
                clusterAddress = argv[i];
                if (getToken(clusterAddress.toLowerCase()) != INVALID) {
                    printCommandError("noclusteraddress", argv[i - 1]);
                }
                break;
            case NUM_CLEAN_SESSIONS:
                i++;
                if (i >= argv.length) {
                    printUsage();
                }

                String nCleanSessions = argv[i];
                if (getToken(nCleanSessions) != INVALID) {
                    printCommandError("nonumcleansessions", argv[i - 1]);
                }
                try {
                    numCleanSessions = Integer.parseInt(nCleanSessions);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i - 1]);

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
                    printCommandError("nostatsinterval", argv[i - 1]);
                }
                try {
                    statsInterval = Long.parseLong(sInterval);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i - 1]);

                }
                if (statsInterval <= 0) {
                    statsInterval = 60;
                }
                statsInterval = statsInterval * 1000; // converting to
                                                        // millisec
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
                            argv[i - 1]);
                }
                try {
                    nodeUpdateInterval = Long.parseLong(interval);
                } catch (NumberFormatException e) {
                    printCommandError("invalidvalue", argv[i - 1]);

                }
                if (nodeUpdateInterval <= 0) {
                    nodeUpdateInterval = 5000;
                }
                break;
            default:
                System.err.println(bundle.getString("usage"));
                System.err
                        .println(bundle.getString("invalid-option") + argv[i]);
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

            if (!(arg.equals("--help") || arg.equals("-h")
                    || arg.equals("--version") || arg.equals("-n"))) {
                System.err.println(bundle.getString("invalid-option") + arg);
                retValue = false;
            }
        } else {
            for (int i = 0; (i < (len - 1)); i++) {
                String arg = argv[i].toLowerCase();
                if (arg.equals("--clusteraddress") || arg.equals("-a")) {
                    hasClusterAddress = true;
                }
                if (arg.equals("--password") || arg.equals("-w")) {
                    hasPassword = true;
                }
                if (arg.equals("--passwordfile") || arg.equals("-f")) {
                    hasPwdFile = true;
                }
            }

            if (hasPassword && hasPwdFile) {
                retValue = false;
            }

            if (!hasClusterAddress) {
                retValue = false;
            }
        }

        return retValue;
    }

    int getToken(String arg) {
        try {
            return (((Integer) arguments.get(arg)).intValue());
        } catch (Exception e) {
            return 0;
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
                String key = (String) iter.next();
                NodeInfo info = (NodeInfo) serverStatusMap.get(key);
                if (info.startTime < localStartTime) {
                    masterDB = false;
                    break;
                }
            }
            isMasterNode = masterDB;
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
    // serverStatusMap map if the information is obsolete.
    // (3) Determine whether the local BDB node can become the master
    // BDB node.

    class NodeStatusSender implements Runnable {

        NodeStatusSender() {
        }

        public void run() {
            while (true) {

                try {
                    if (isServerUp) {
                        long nextRun = System.currentTimeMillis()
                                + nodeUpdateInterval;
                        BytesMessage msg = tSession.createBytesMessage();
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
                    String key = (String) iter.next();
                    NodeInfo info = (NodeInfo) serverStatusMap.get(key);
                    long currentTime = System.currentTimeMillis();
                    if (currentTime > (info.lastUpdateTime 
                            + nodeUpdateInterval + nodeUpdateGraceperiod)) {
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
