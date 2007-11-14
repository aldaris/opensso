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
 * $Id: SystemTimerPool.java,v 1.1 2007-11-14 18:55:34 ww203982 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common;

/**
 * SystemTimerPool is a TimerPool which shared in the system.
 */

public class SystemTimerPool {
    
    protected static TimerPool instance;
    public static int DEFAULT_POOL_SIZE = 3;
    
    /**
     * Create and return the system timer pool.
     */
    
    public static synchronized TimerPool getTimerPool() {
        if (instance == null) {
            instance = new TimerPool("SystemTimerPool", DEFAULT_POOL_SIZE,
                false);
            ShutdownManager shutdownMan = ShutdownManager.getInstance();
            shutdownMan.addShutdownListener(new ShutdownListener() {
                public void shutdown() {
                    instance.shutdown();
                }
            });
        }
        return instance;
    }
    
}