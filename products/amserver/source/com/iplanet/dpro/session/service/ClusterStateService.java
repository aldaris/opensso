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
 * $Id: ClusterStateService.java,v 1.1 2005-11-01 00:29:54 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A <code>ClusterStateService </code> class implements monitoring the state of 
 * server instances that are participating in the cluster environment. It is 
 * used in making routing decisions in "internal request routing" mode
 * 
 */

public class ClusterStateService implements Runnable {

    private class ServerInfo implements Comparable {
        String id;

        InetSocketAddress address;

        boolean isUp;

        public int compareTo(Object o) {
            return id.compareTo(((ServerInfo) o).id);
        }
    }

    /** Servers in the cluster environment*/
    private Map servers = new HashMap();

    /** Server Information */
    private ServerInfo[] serverSelectionList = new ServerInfo[0];

    /** Last selected Server*/
    private int lastSelected = -1;

    /** individual server wait default time out 10 milliseconds */
    public static final int DEFAULT_TIMEOUT = 1000;

    private int timeout = DEFAULT_TIMEOUT; // in milliseconds

    /** default ServerInfo check time 10 milliseconds */
    public static final long DEFAULT_PERIOD = 1000;

    private long period = DEFAULT_PERIOD; // in milliseconds

    // server instance id 
    private String localServerId = null;

    private Thread checker;

    /**
     * Constructs an instance for the cluster service
     * @param localServerId id of the server instance in which this 
     * ClusterStateService instance is running
     * @param timeout
     *            timeout for waiting on an individual server (millisec)
     * @param period
     *            checking cycle period (millisecs)
     * @param members
     *            map if server id - > url for all cluster members
     * @throws Exception
     */
    public ClusterStateService(String localServerId, int timeout, long period,
            Map members) throws Exception {

        this.localServerId = localServerId;
        this.timeout = timeout;
        this.period = period;
        serverSelectionList = new ServerInfo[members.size()];

        for (Iterator m = members.entrySet().iterator(); m.hasNext();) {
            ServerInfo info = new ServerInfo();
            Map.Entry entry = (Map.Entry) m.next();
            info.id = (String) entry.getKey();
            URL url = new URL((String) entry.getValue());
            info.address = new InetSocketAddress(url.getHost(), url.getPort());
            info.isUp = checkServerUp(info);
            servers.put(info.id, info);
            serverSelectionList[getNextSelected()] = info;
        }

        // to ensure that ordering in different server instances is identical
        Arrays.sort(serverSelectionList);

        checker = new Thread(this);
        checker.setName("ClusterStateService");
        checker.setDaemon(true);
        checker.start();
    }

    /**
     * Implements "wrap-around" lastSelected index advancement
     * 
     * @return updated lastSelected index value
     */

    private int getNextSelected() {
        lastSelected = (lastSelected + 1) % serverSelectionList.length;
        return lastSelected;
    }

    /**
     * Returns currently known status of the server instance identified by
     * serverId
     * 
     * @param serverId
     *            server instance id
     * @return true if server is up, false otherwise
     */
    boolean isUp(String serverId) {
        return ((ServerInfo) servers.get(serverId)).isUp;
    }

    /**
     * Actively checks and updates the status of the server instance identified
     * by serverId
     * 
     * @param serverId
     *            server instance id
     * @return true if server is up, false otherwise
     */

    boolean checkServerUp(String serverId) {
        ServerInfo info = (ServerInfo) servers.get(serverId);
        info.isUp = checkServerUp(info);
        return info.isUp;
    }

    /**
     * Returns size of the server list
     * 
     * @return size of the server list
     */
    int getServerSelectionListSize() {
        return serverSelectionList.length;
    }

    /**
     * Returns server id for a given index inside the server list
     * 
     * @param index
     *            index in the server list
     * @return server id
     */
    String getServerSelection(int index) {
        return serverSelectionList[index].id;
    }

    /**
     * Monitoring logic used by background thread
     */
    public void run() {
        while (true) {

            try {

                long nextRun = System.currentTimeMillis() + period;

                Iterator i = servers.values().iterator();

                while (i.hasNext()) {
                    ServerInfo info = (ServerInfo) i.next();
                    info.isUp = checkServerUp(info);
                }
                long sleeptime = nextRun - System.currentTimeMillis();
                if (sleeptime > 0) {
                    Thread.sleep(sleeptime);
                }
            } catch (Exception e) {
            }

        }
    }

    /**
     * Internal method for checking health status using sock.connect()
     * 
     * @param info
     *            server info instance
     * @return true if server is up, false otherwise
     */
    private boolean checkServerUp(ServerInfo info) {
        if (localServerId.equals(info.id)) {
            return true;
        }

        boolean result = false;
        Socket sock = new Socket();
        try {
            sock.connect(info.address, timeout);
            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            try {
                sock.close();
            } catch (Exception e) {
            }
        }
        return result;
    }
}
