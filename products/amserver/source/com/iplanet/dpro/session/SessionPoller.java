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
 * $Id: SessionPoller.java,v 1.1 2005-11-01 00:29:48 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.am.util.ThreadPool;
import com.iplanet.am.util.ThreadPoolException;
import com.iplanet.dpro.session.service.SessionService;
import com.iplanet.dpro.session.share.SessionInfo;
import com.iplanet.dpro.session.share.SessionRequest;
import com.iplanet.dpro.session.share.SessionResponse;
import com.sun.identity.common.Constants;
import com.sun.identity.session.util.RestrictedTokenAction;
import com.sun.identity.session.util.RestrictedTokenContext;

/**
 * <code>SessionPoller</code> monitors the 
 * Session table for any session needs to
 * removed or destroyed
 */
public class SessionPoller extends Thread {

    Hashtable sessionTable;

    Hashtable destroyedTable;

    private SessionService sessionService = null;

    public static Debug debug = null;

    static long period = 180000; // in milliseconds

    static private ThreadPool threadPool = null;

    private static final int DEFAULT_POOL_SIZE = 10;

    private static final int DEFAULT_THRESHOLD = DEFAULT_POOL_SIZE * 1000;

    static {
        debug = Debug.getInstance("amSession");
        String pollTime = SystemProperties
                .get("com.iplanet.am.session.client.polling.period");
        try {
            period = Long.parseLong(pollTime);
            period = period * 1000;
        } catch (Exception pe) {
            if (debug.messageEnabled())
                debug.message("Can not get the polling time");
        }

        int poolSize;
        int threshold;

        try {
            poolSize = Integer.parseInt(SystemProperties
                    .get(Constants.POLLING_THREADPOOL_SIZE));
        } catch (Exception e) {
            poolSize = DEFAULT_POOL_SIZE;
        }

        try {
            threshold = Integer.parseInt(SystemProperties
                    .get(Constants.POLLING_THREADPOOL_THRESHOLD));
        } catch (Exception e) {
            threshold = DEFAULT_THRESHOLD;
        }

        threadPool = new ThreadPool("amSessionPoller", poolSize, threshold,
                true, debug);
    }

    /**
     * Constructs <code>SessionPoller</code>which associates the Session Table 
     * to be monitored by this class
     * 
     * @param table
     *            Session Table which is monitored by this class
     */
    public SessionPoller(Hashtable table) {

        sessionTable = table;
    }

    /**
     * This thread keep monitoring the state of the Session. If the session need
     * to be destroyed then this thread remove the session from the session
     * table.
     */
    public void run() {
        while (true) {
            Enumeration e = sessionTable.elements();
            long nextRun = System.currentTimeMillis() + period;

            try {
                while (e.hasMoreElements()) {
                    final Session sess = (Session) e.nextElement();
                    SessionID sid = sess.getID();
                    try {
                        if (sess.maxCachingTimeReached()
                                && !sess.getIsPolling()) {
                            RestrictedTokenContext.doUsing(sess.getContext(),
                                    new RestrictedTokenAction() {
                                        public Object run() throws Exception {
                                            return doPoll(sess);
                                        }
                                    });
                        }
                    } catch (SessionException se) {
                        Session.removeSID(sid);
                        debug.message(
                                "session is not in timeout state so clean it",
                                se);
                    }

                }
            } catch (Exception ex) {
                debug.error("Exception encountered while polling", ex);
            }

            try {
                long sleeptime = nextRun - System.currentTimeMillis();
                if (sleeptime > 0) {
                    sleep(sleeptime);
                }
            } catch (Exception ex) {
            }
        }
    }

   /**
    * Do Session Polling
    * @param Session
    * @return SessionInfo
    */
   private SessionInfo doPoll(Session session) {
        SessionInfo info = null;
        SessionID sid = session.getID();

        if (session.isLocal()) {
            try {
                sessionService = SessionService.getSessionService();
                info = sessionService.getSessionInfo(sid, false);
            } catch (SessionException se) {
                Session.removeSID(sid);
                if (debug.messageEnabled())
                    debug.message("Removed SID:" + sid);
                return null;
            }
        } else {
            try {
                session.setIsPolling(true);
                threadPool.run(new SessionPollerSender(session));
            } catch (ThreadPoolException e) {
                session.setIsPolling(false);
                debug.error("Send Polling Error: ", e);
            }
        }

        return info;
    }

    class SessionPollerSender implements Runnable {
        SessionInfo info = null;

        Session session = null;

        SessionID sid = null;


       /**
        * Constructs <code>SessionPollerSender</code>.
        * @param sess Session.
        */
        public SessionPollerSender(Session sess) {
            session = sess;
            sid = session.getID();
        }

        public void run() {
            try {
                SessionRequest sreq = new SessionRequest(
                        SessionRequest.GetSession, sid.toString(), false);
                SessionResponse sres = Session.sendPLLRequest(session
                        .getSessionServiceURL(), sreq);

                if (sres.getException() != null) {
                    Session.removeSID(sid);
                    return;
                }

                Vector infos = sres.getSessionInfoVector();
                info = (SessionInfo) infos.elementAt(0);
            } catch (Exception ex) {
                Session.removeSID(sid);
                if (debug.messageEnabled())
                    debug.message("Could not connect to the session server"
                            + ex.getMessage());
            }

            if (info != null) {
                if (debug.messageEnabled()) {
                    debug.message("Updating" + info.toXMLString());
                }
                try {
                    if (info.state.equals("invalid")
                            || info.state.equals("destroyed")) {
                        Session.removeSID(sid);
                    } else {
                        session.update(info);
                    }
                } catch (SessionException se) {
                    Session.removeSID(sid);
                    debug.error("Exception encountered while update in polling",
                                    se);

                }

            } else {
                Session.removeSID(sid);
            }
            session.setIsPolling(false);
        }
    }

}
