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
 * $Id: AMDirectoryWrapper.java,v 1.1 2005-11-01 00:29:00 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;

/**
 * This class servers as a interface wrapper to pick up right class based on
 * whether Caching Mode is enabled or disabled.
 * 
 * <p>
 * <code>AMDirectoryManager</code> is the base class which has methods to
 * perform all Directory related operations. The class <code>AMCacheManager
 * </code>
 * extends the base class and overrides the methods for which caching needs to
 * be done. This clean distinguistion makes the implementation clean as the
 * implementation in AMDirectoryManager does not need to perform any caching
 * related operations. Also, this implementation is very scalable as the methods
 * that need caching can always be overridden in future.
 * 
 * <p>
 * At server start up, based on whether Caching is enabled or disabled, this
 * class instantiates the right class. So invoking a <code>getInstance()</code>
 * method of this class returns a handle to <code>AMCacheManager<code> singleton
 * instance if caching is enabled or <code>AMDirectoryManager<code> singleton
 * if caching is disabled. Hence all the classes in this package should get the 
 * handle to the instance only using the <code>getInstance()</code> of this 
 * class. The getInstance() methods of each of these individual classes are 
 * synchronized to guarantee a singleton instance. 
 * 
 * <p>Singleton pattern design reference:
 * @link http://developer.java.sun.com/developer/technicalArticles/Programming/
 * singletons/ Singleton Technical Article
 *
 */
public class AMDirectoryWrapper {

    private static AMDirectoryManager instance = null;

    private static final String CACHE_ENABLED_DISABLED_KEY = 
        "com.iplanet.am.sdk.caching.enabled";

    private static Debug debug = AMCommonUtils.debug;

    private static boolean cachingEnabled;

    static {
        // Check if the caching property is set in System runtime.
        String cachingMode = System.getProperty(CACHE_ENABLED_DISABLED_KEY);
        if ((cachingMode == null) || (cachingMode.length() == 0)) {
            // Check if caching property is set in AMConfig
            cachingMode = SystemProperties.get(CACHE_ENABLED_DISABLED_KEY,
                    "true");
        }

        if (cachingMode.equalsIgnoreCase("true")) {
            cachingEnabled = true;
            instance = AMCacheManager.getInstance();
            // Also start the Event listener threads
            // Since the event manager instance is intantiated only when cache
            // mode is true. Event threads will start only if caching is enabled
            initializeEventManager();
        } else { // Caching mode disabled
            cachingEnabled = false;
            instance = AMDirectoryManager.getInstance();
        }
        if (debug.messageEnabled()) {
            debug.message("AMDirectoryWrapper.static{} - Caching " + "Mode: "
                    + cachingEnabled);
        }
    }

    private static void initializeEventManager() {
        try {
            AMEventManager eventManager = new AMEventManager(instance);
            eventManager.start();
        } catch (AMEventManagerException pe) {
            debug.error("AMDirectoryWrapper.initializeEventManager(): Unable "
                    + "to start event manager: ", pe);
        }
    }

    public static AMDirectoryManager getInstance() {
        return instance;
    }

    protected static boolean isCachingEnabled() {
        return cachingEnabled;
    }
}
