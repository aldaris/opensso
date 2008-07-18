/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: SMSThreadPool.java,v 1.1 2008-07-18 00:40:22 kenwho Exp $
 *
 */

package com.sun.identity.sm;

import com.sun.identity.common.Constants;
import com.sun.identity.common.ShutdownListener;
import com.sun.identity.common.ShutdownManager;
import com.sun.identity.shared.debug.Debug;
import java.util.Iterator;
import java.util.Set;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.am.util.ThreadPool;
import com.iplanet.am.util.ThreadPoolException;

/**
 * The class <code>SMSThreadPool</code> provides interfaces to manage
 * notfication thread pools shared by idm and sm. 
 *
 * @supported.api
 */
public class SMSThreadPool {
    
    private static ThreadPool thrdPool;
    
    private static Debug debug = Debug.getInstance("amSMS");

    private static final int DEFAULT_POOL_SIZE = 10;

    private static final int DEFAULT_TRESHOLD= 0;

    private static boolean initialized;

    static void initialize() {
        int poolSize;
        // Check if already initialized
        if (initialized)
            return;
        try {
            poolSize = Integer.parseInt(SystemProperties.get(
                Constants.SM_THREADPOOL_SIZE));
        } catch (Exception e) {
            poolSize = DEFAULT_POOL_SIZE;
        }
        if (debug.messageEnabled()) {
            debug.message("SMSThreadPool: poolSize=" + poolSize);
        }
        thrdPool = new ThreadPool("smIdmThreadPool", poolSize,
               DEFAULT_TRESHOLD, false, debug);
        ShutdownManager.getInstance().addShutdownListener(
               new ShutdownListener() {
                   public void shutdown() {
                       thrdPool.shutdown();
                   }
               }
           );
        initialized = true;

    }

    
    /**
     * Schdule a task for 
     * <code>SMSThreadPool</code> to run.
     *
     * @param task 
     *            task to be scheduled.
     *
     * @supported.api
     */
    public static boolean scheduleTask(Runnable task) {
        boolean success = true;
        if (!initialized) {
            initialize(); 
        }
        try {
            thrdPool.run(task);
        } catch (ThreadPoolException e) {
            debug.error("SMSThreadPool: unable to schedule task" + e);
            success = false;
        }
        return success;
    }
    
}
