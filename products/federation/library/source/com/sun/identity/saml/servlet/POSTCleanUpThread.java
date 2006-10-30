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
 * $Id: POSTCleanUpThread.java,v 1.1 2006-10-30 23:15:51 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml.servlet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.identity.saml.common.SAMLConstants;
import com.sun.identity.saml.common.SAMLServiceManager;
import com.sun.identity.saml.common.SAMLUtils;

/**
 * This is a helper class used by SAMLPOSTProfileServlet to clean up expired
 * assertionIDs from the map.
 */
public class POSTCleanUpThread extends Thread {
    private Map idTimeMap = null;

    /**
     * Constructor.
     * @param map the <code>Map</code> to be cleaned up.
     */
    public POSTCleanUpThread(Map map) {
        idTimeMap = map;
    }

    /**
     * Worker thread.
     */
    public void run() {
        Set aIDStrings = null;
        String aIDString = null;
        Iterator keyIter = null;
        Integer interval;
        long period;
        long nextRun;
        long now;
        Long time = null;
        Set expiredSet = null;
        long sleeptime;

        while (true) {
            now = System.currentTimeMillis();
            synchronized (idTimeMap) {
                aIDStrings = idTimeMap.keySet();
                keyIter = aIDStrings.iterator();
                expiredSet = new HashSet();
                while (keyIter.hasNext()) {
                    aIDString = (String) keyIter.next();
                    time = (Long) idTimeMap.get(aIDString);
                    if (time != null) {
                        if (time.longValue() < now) {
                            expiredSet.add(aIDString);
                        }
                    }
                }
                keyIter = expiredSet.iterator();
                while (keyIter.hasNext()) {
                    aIDString = (String) keyIter.next();
                    if (SAMLUtils.debug.messageEnabled()) {
                        SAMLUtils.debug.message("POSTCleanUpThread: deleting "
                            + aIDString);
                    }
                    idTimeMap.remove(aIDString);
                }
            }

            // obtain the interval
            interval = (Integer) SAMLServiceManager.getAttribute(
                        SAMLConstants.CLEANUP_INTERVAL_NAME);
            period = (interval.intValue()) * 1000;
            now = System.currentTimeMillis();
            nextRun = now + period;
            try {
                sleeptime = nextRun - System.currentTimeMillis();
                if (sleeptime > 0) {
                    sleep(sleeptime);
                }
            } catch (Exception e) {
                if (SAMLUtils.debug.messageEnabled()) {
                    SAMLUtils.debug.message("POSTCleanUpThread::run", e);
                }
            }
        }
    }
}
