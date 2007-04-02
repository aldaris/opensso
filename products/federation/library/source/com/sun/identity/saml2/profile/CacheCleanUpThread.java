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
 * $Id: CacheCleanUpThread.java,v 1.3 2007-04-02 23:34:24 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.saml2.profile;

import java.util.Hashtable;
import java.util.Iterator;

import com.sun.identity.shared.configuration.SystemPropertiesManager;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Utils;

/**
 * This is a helper class used to clean up server caches.
 */
public class CacheCleanUpThread extends Thread {

    private int interval = SAML2Constants.CACHE_CLEANUP_INTERVAL_DEFAULT;

    /**
     *  Constructor.
     */
    public CacheCleanUpThread() {
        String intervalStr = SystemPropertiesManager.get(
            SAML2Constants.CACHE_CLEANUP_INTERVAL);
        
        try {
            if (intervalStr != null && intervalStr.length() != 0) {
                interval = Integer.parseInt(intervalStr);
                if (interval < 0) {
                    interval = 
                        SAML2Constants.CACHE_CLEANUP_INTERVAL_DEFAULT;
                }
            }
        } catch (NumberFormatException e) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("CacheCleanUpThread.constructor: "
                    + "invalid cleanup interval. Using default.");
            }
        }
    }

    /**
     * Starts the clean up thread.
     */
    public void run() {
        long nextRun;
        long now;
        long sleeptime;

        while (true) {
            now = System.currentTimeMillis();
            cleanup(SPCache.requestHash, now);
            cleanup(SPCache.responseHash, now);
            cleanup(SPCache.mniRequestHash, now);
            cleanup(SPCache.relayStateHash, now);
            cleanup(IDPCache.authnRequestCache, now);
            
            try {
                sleep(interval *1000);
            } catch (Exception e) {
                SAML2Utils.debug.message("CacheCleanUpThread.run", e);
            }
        }
    }

    private void cleanup(Hashtable hashtable, long now) {
        synchronized (hashtable) {
            Iterator iter = hashtable.keySet().iterator();
            long delay = interval * 1000;
            while (iter.hasNext()) {
                String key = (String) iter.next();
                long time = ((CacheObject) hashtable.get(key)).getTime();
                if ((time + delay) < now) {
                    iter.remove();
                }
            }
       }
   }
}
