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
* $Id: PersistSession.java,v 1.2 2007-11-14 00:18:54 manish_rustagi Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.jmqdb;

import java.util.Hashtable;
import java.util.Random;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

 
/**
 * This class <code>PersistSession</code> implements 
 * </code> MessageListener</code> which is to receive 
 * asynchronously delivered messages  
 */
public class PersistSession implements MessageListener {
    
    /** Represents JMS message read Operation */
    static public final String READ = "READ";

    /** Represents JMS write Operation */
    static public final String WRITE = "WRITE";

    /** Represents delete Operation */
    static public final String DELETE = "DELETE";

    /** Represents delete Date Operation */
    static public final String DELETEBYDATE = "DELETEBYDATE";

    /** Represents shut down Operation */
    static public final String SHUTDOWN = "SHUTDOWN";

    /** Represents not found */
    static public final String NOT_FOUND = "notfound";

    /** Represents Operation status */
    static public final String OP_STATUS = "opstatus";

    /** Represents the <code>Session</code> count*/
    static public final String GET_SESSION_COUNT = "GET_SESSION_COUNT";

    /* JMQ Queue/Topic names */
    static public final String DBREQUEST = "AM_DBREQUEST";

    static public final String DBRESPONSE = "AM_DRESPONSE";

    /* JMQ Properties */
    static public final String ID = "ID";

    static public int TIMEOUT = 1000;

    /* JMQ Map keys */
    static public final String SESSIONID = "SessionId";

    static public final String EXPIRYDATE = "ExpiryDate";

    static public final String DATA = "Data";

    // Private data members
    private String _id;

    TopicConnectionFactory tFactory = null;

    TopicConnection tConn = null;

    TopicSession tSession = null;

    Topic reqTopic = null;

    Topic resTopic = null;

    TopicPublisher reqPub = null;

    TopicSubscriber resSub = null;

    Hashtable processedMsgs = new Hashtable();

    Random rdmGen = new Random();

    /* Config data */
    int msgcount = 0;

   /**
    *
    * Constructs new PersistSession
    * @param id SessionId
    *
    */
   public PersistSession(String id) throws Exception {
        _id = id;
        // Initialize all message queues/topics
        ConnectionFactoryProvider provider = ConnectionFactoryProviderFactory
                .getProvider();
        tFactory = provider.newTopicConnectionFactory();
        tConn = tFactory.createTopicConnection();
        int flag = Session.DUPS_OK_ACKNOWLEDGE;
        tSession = tConn.createTopicSession(false, flag);
        reqTopic = tSession.createTopic(DBREQUEST);
        resTopic = tSession.createTopic(DBRESPONSE);

        reqPub = tSession.createPublisher(reqTopic);
        String selector = "ID = '" + _id + "'";
        resSub = tSession.createSubscriber(resTopic, selector, true);
        resSub.setMessageListener(this);
        tConn.start();
    }

    private String serverList = null;

    private String userName = null;

    private String password = null;

    // The read timout for retrieving the session (in SFO) needs
    // to be as small as possible since in the case where there
    // is an existing session cookie in client's browser and
    // there is no corresponding session entry in the repository
    // (e.g. timeout), client is forced to wait until this timeout
    // to be able to be redirected back to the login page.
    private int readTimeOut = 5 * 1000; /* 5 sec in millisec */

    // The read timout for getting the session count (for session
    // constraint) is different from the SFO case because the
    // master BDB node will send the response message to the
    // client even though the session count is 0.
    private int readTimeOutForConstraint = 6 * 1000;

   /**
    *
    * Constructs new PersistSession
    * @param id SessionId
    * @param sList Server list
    * @param uName user name
    * @param pwd password
    * @param conTimeOut Connection Timeout
    * @param maxWaitTimeForConstraint Maximum Wait Time
    */
   public PersistSession(String id, String sList, String uName, String pwd,
            int conTimeOut, int maxWaitTimeForConstraint) throws Exception {
        _id = id;
        // Initialize all message queues/topics
        serverList = sList;
        userName = uName;
        password = pwd;
        readTimeOut = conTimeOut;
        readTimeOutForConstraint = maxWaitTimeForConstraint;
        ConnectionFactoryProvider provider = ConnectionFactoryProviderFactory
                .getProvider();
        tFactory = provider
        .newTopicConnectionFactory(serverList,true, true, userName, password);
        tConn = tFactory.createTopicConnection();
        int flag = Session.DUPS_OK_ACKNOWLEDGE;
        tSession = tConn.createTopicSession(false, flag);
        reqTopic = tSession.createTopic(DBREQUEST);
        resTopic = tSession.createTopic(DBRESPONSE);

        reqPub = tSession.createPublisher(reqTopic);
        reqPub.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        String selector = "ID = '" + _id + "'";
        resSub = tSession.createSubscriber(resTopic, selector, true);
        resSub.setMessageListener(this);
        tConn.start();
    }

    /**
     * Retrieves session state from the repository
     * 
     * @param msg
     *            JMS message for READ request
     * @return JMS message object for response
     * @throws Exception
     *             if anything goes wrong
     */
    public Object read(Object msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        message.setStringProperty(ID, _id);
        message.setStringProperty("op", READ);
        // Allocate a random string for onMessage to find us.
        Long random = new Long(rdmGen.nextLong());
        processedMsgs.put(random, random);
        message.writeLong(random.longValue());
        // onMessage thread will wake us up when data is ready
        synchronized (random) {
            reqPub.publish(message);
            random.wait(readTimeOut);
        }
        // TODO : process timeout
        Message message1 = (Message) processedMsgs.remove(random);
        String opStatus = getStringProperty(message1, OP_STATUS);
        if (opStatus != null && opStatus.equals(NOT_FOUND)) {
            throw new Exception("Session not found in repository");
        }
        return message1;
    }

    /**
     * Deletes session record from the repository
     * 
     * @param msg
     *            JMS message for DELETE request
     * @throws Exception
     *             if anything goes wrong
     */
    public void delete(Object msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        message.setStringProperty(ID, _id);
        message.setStringProperty("op", DELETE);
        reqPub.publish(message);
    }

    /**
     * Deletes expired session records. This method is used by the background
     * clean up thread.
     * 
     * @param msg
     *            JMS message for DELETEBYDATE request
     * @throws Exception
     *             if anything goes wrong
     */
    public void delete_bydate(Object msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        message.setStringProperty(ID, _id);
        message.setStringProperty("op", DELETEBYDATE);
        reqPub.publish(message);
    }

    /**
     * Shutdown the session repository. This method is temporarily not used.
     * 
     * @param msg
     *            JMS message for SHUTDOWN request.
     * @throws Exception
     *             if anything goes wrong
     */
    public void shutdown(Object msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        message.setStringProperty(ID, _id);
        message.setStringProperty("op", SHUTDOWN);
        reqPub.publish(message);
    }

    /**
     * Saves session state to the repository
     * 
     * @param msg
     *            JMS message for WRITE request
     * @throws Exception
     *             if anything goes wrong
     */
    public void write(Object msg) throws Exception {
        Message message = (Message) msg;
        message.setStringProperty(ID, _id);
        message.setStringProperty("op", WRITE);
        reqPub.publish(message);
    }

    /**
     * Returns the expiration information of all sessions belonging to a user.
     * Returns <code>null</code> if there is no response received from JMQ
     * broker after the designated timeout.
     * 
     * @param msg
     *            JMS message for GET_SESSION_COUNT request
     * @throws Exception
     *             if there is any problem with accessing the session
     *             repository.
     */
    public Object getSessionsByUUID(Object msg) throws Exception {
        BytesMessage message = (BytesMessage) msg;
        message.setStringProperty(ID, _id);
        message.setStringProperty("op", GET_SESSION_COUNT);
        // Allocate a random string for onMessage to find us.
        Long random = new Long(rdmGen.nextLong());
        processedMsgs.put(random, random);
        message.writeLong(random.longValue());
        // onMessage thread will wake us up when data is ready
        synchronized (random) {
            reqPub.publish(message);
            random.wait(readTimeOutForConstraint);
        }
        Object retMsg = processedMsgs.remove(random);
        if (retMsg instanceof Long) {
            // timeout
            return null;
        } else {
            Message message1 = (Message) retMsg;
            return message1;
        }
    }

    /**
     * Creates a  JMQ Message
     * @return Object JMS message 
     */
    public Object createMessage() throws Exception {
        return tSession.createBytesMessage();
    }

    /**
     * Gets the Property Key from value the JMS message
     * @return String key
     * @throws Exception when cannot get the key
     */
    public String getStringProperty(Object message, String key)
            throws Exception {
        BytesMessage msg = (BytesMessage) message;
        return msg.getStringProperty(key);
    }

    /**
     * Sets the a key , value for the  JMS message object
     * @param message JMS message object
     * @param key property
     * @param value property value 
     * @throws Exception when property cannot be set
     */
    public void setStringProperty(Object message, String key, String value)
            throws Exception {
        Message msg = (Message) message;
        msg.setStringProperty(key, value);
    }


    /**
     * Sets the a String JMS message object
     * @param message JMS message object
     * @param key property
     * @param value property value 
     * @throws Exception when property cannot be set
     */
    public void setString(Object message, String key, String value)
            throws Exception {
        BytesMessage msg = (BytesMessage) message;
        msg.writeLong(value.length());
        msg.writeBytes(value.getBytes());
    }

    /**
     * Write Bytes to the message
     * @param message JMS message object
     * @param key property
     * @param value
     * @throws Exception when cannot write bytes to the message
     */
    public void setBytes(Object message, String key, byte[] value)
            throws Exception {
        BytesMessage msg = (BytesMessage) message;
        msg.writeLong(value.length);
        msg.writeBytes(value);
    }

    /**
     * Sets to value to message
     * @param message JMS message object
     * @param key property
     * @param value long value
     * @throws Exception when cannot write bytes to the message
     */
    public void setLong(Object message, String key, long value)
            throws Exception {
        BytesMessage msg = (BytesMessage) message;
        msg.writeLong(value);
    }

    /**
     * Sets Integer value to message
     * @param message JMS message object
     * @param key
     * @param value
     * @throws Exception when cannot write bytes to the message
     */
    public void setInt(Object message, String key, int value) throws Exception {
        BytesMessage msg = (BytesMessage) message;
        msg.writeInt(value);
    }

    /**
     * Sets boolean value to message
     * @param message JMS message object
     * @param key
     * @param value
     * @throws Exception when cannot write bytes to the message
     */
    public void setBoolean(Object message, String key, boolean value)
            throws Exception {
        BytesMessage msg = (BytesMessage) message;
        msg.writeBoolean(value);
    }

   /**
    * Gets <code>String</code> value to message
    * @param message JMS message object
    * @param key
    * @return String value
    * @throws Exception when cannot get the string
    */
   public String getString(Object message, String key) throws Exception {
        BytesMessage msg = (BytesMessage) message;
        int len = msg.readInt();
        byte[] bytes = new byte[len];
        msg.readBytes(bytes);
        return new String(bytes);
    }

   /**
    * Gets <code>Bytes</code> from the message object
    * @param message JMS message object
    * @param key
    * @return byte array
    * @throws Exception when cannot get the bytes
    */
   public byte[] getBytes(Object message, String key) throws Exception {
        BytesMessage msg = (BytesMessage) message;
        long len = msg.readLong();
        byte[] bytes = new byte[(int) len];
        msg.readBytes(bytes);
        return bytes;
    }

   /**
    * Reads <code>Long</code> from the message object
    * @param message JMS message object
    * @param key
    * @return Long 
    * @throws Exception when an error occurs while reading
    */
   public long getLong(Object message, String key) throws Exception {
        BytesMessage msg = (BytesMessage) message;
        return msg.readLong();
    }

   /**
    * Reads <code>Int</code> from the message object
    * @param message JMS message object
    * @param key
    * @return Int 
    * @throws Exception when an error occurs while reading
    */
   public int getInt(Object message, String key) throws Exception {
        BytesMessage msg = (BytesMessage) message;
        return msg.readInt();
    }

   /**
    * Reads <code>boolean</code> from the message object
    * @param message JMS message object
    * @param key
    * @return boolean 
    * @throws Exception when an error occurs while reading
    */
   public boolean getBoolean(Object message, String key) throws Exception {
        BytesMessage msg = (BytesMessage) message;
        return msg.readBoolean();
   }

   /** 
    * Passes a message to the listener.
    *
    * @param message the message passed to the listener
    */
   public void onMessage(Message message) {
        try {
            BytesMessage msg = (BytesMessage) message;
            long rndnum = msg.readLong();
            Long random = new Long(rndnum);

            // Determine if we have a read thread waiting...
            Object rnd = processedMsgs.get(random);
            if (rnd != null) {
                processedMsgs.put(rnd, message);
                synchronized (rnd) {
                    rnd.notify();
                }
            }
        } catch (Exception ex) {
            // Since we dont know the thread, not much we can do here -
            // we will just let the thread timeout.
            // TODO Debug.error.
        }
    }
}
