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
 * $Id: SessionMonitor.java,v 1.2 2006-08-25 21:19:42 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.service;

import com.sun.identity.shared.debug.Debug;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * <code>SessionMonitor</code> monitors a given session table , If a session 
 * need to be removed or destroyed internal monitoring thread
 *  
 */
public class SessionMonitor extends Thread {

    Hashtable sessionTable;

    static final long period = 30000; // in milliseconds

    static final long delay = 0;

    static Debug sessMonDebug = Debug.getInstance("amSessionMonitor");

    static SessionService sessionService = null;

    /**
     * Constructor which associates the Session Table to be monitored by this
     * class
     * @param parent session service
     * @param table Session Table which is monitored by this class
     */
    public SessionMonitor(SessionService parent, Hashtable table) {
        sessionTable = table;
        sessionService = parent;
    }

    /**
     * This thread keep monitoring the state of the Session. If the session need
     * to be destroyed then this thread remove the session from the session
     * table.
     * 
     * Thread will run as often as once a minute, or continually if it can't
     * sweep through the table during that time. Optimally, you can make these
     * settings tunable via the profile service.
     */
    public void run() {

        while (true) {
            Enumeration e = sessionTable.elements();
            long nextRun = System.currentTimeMillis() + period;
            try {
                while (e.hasMoreElements()) {
                    InternalSession s = (InternalSession) e.nextElement();
                    if (s != null) {
                        if (SessionService.getUseInternalRequestRouting()) {
                            // getCurrentHostServer automatically releases local
                            // session replica if it does not belong locally
                            String hostServer = sessionService
                                    .getCurrentHostServer(s.getID());
                            // if session does not belong locally skip it
                            if (!sessionService.isLocalServer(hostServer))
                                continue;
                        }

                        sessionService.checkIfShouldDestroy(s);
                    }
                }

                long sleeptime = nextRun - System.currentTimeMillis();
                if (sleeptime > 0) {
                    sleep(sleeptime);
                }
                sleep(delay);
            } catch (Exception ex) {
            }
        }
    }
}
