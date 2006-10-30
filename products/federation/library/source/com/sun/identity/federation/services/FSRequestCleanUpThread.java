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
 * $Id: FSRequestCleanUpThread.java,v 1.1 2006-10-30 23:14:23 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.federation.services;

import com.sun.identity.federation.common.FSUtils;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This is a helper class used by FSSessionManager to clean up expired
 * assertionIDs from the map.
 */
public class FSRequestCleanUpThread extends Thread {
    private Map idRequestTimeoutMap = null;
    private Map idRequestMap = null;
    private Map idDestMap = null;
    // thread cleanup interval
    private long interval;

    /**
     * Constructor.
     * @param providerId provider ID
     * @param idAuthnRequestTimeoutMap request ID (String) and timeout(Long) map
     * @param idAuthnRequestMap request ID (String) and FSAuthnRequest map
     * @param idDestnMap request ID (String) and FSProviderDescriptor map
     * @param threadCleanupInterval thread cleanup interval 
     */
    public FSRequestCleanUpThread(String providerId, 
        Map idAuthnRequestTimeoutMap, Map idAuthnRequestMap, 
        Map idDestnMap, long threadCleanupInterval) {
        super(providerId);
        idRequestTimeoutMap = idAuthnRequestTimeoutMap;
        idRequestMap = idAuthnRequestMap;
        idDestMap =  idDestnMap;
        interval = threadCleanupInterval;
    }

    /**
     * Runs the clean up thread.
     */
    public void run() {
        Set aIDStrings = null;
        String aIDString = null;
        Iterator keyIter = null;
        long nextRun;
        long now;
        Long time = null;
        long sleeptime;

        while (true) {
            FSUtils.debug.message("FSRequestCleanUpThread:run thread wakeup");
            now = System.currentTimeMillis();
            aIDStrings = idRequestTimeoutMap.keySet();
            keyIter = aIDStrings.iterator();
            while (keyIter.hasNext()) {
                aIDString = (String) keyIter.next();
                time = (Long) idRequestTimeoutMap.get(aIDString);
                if (time != null && time.longValue() < now) {
                    // remove from all three map
                    if (FSUtils.debug.messageEnabled()) {
                        FSUtils.debug.message("FSRequestCleanUpThread:run," +
                            " request expired " + aIDString);
                    }
                    keyIter.remove();
                    idRequestMap.remove(aIDString);
                    idDestMap.remove(aIDString);
                }
            }

            nextRun = now + interval;
            try {
                sleeptime = nextRun - System.currentTimeMillis();
                if (sleeptime > 0) {
                    sleep(sleeptime);
                }
            } catch (Exception e) {
                if (FSUtils.debug.messageEnabled()) {
                    FSUtils.debug.message("FSRequestCleanUpThread::run", e);
                }
            }
        }
    }
}
