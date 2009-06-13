/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: EntitlementThreadPool.java,v 1.4 2009-06-13 00:32:08 arviranga Exp $
 *
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IThreadPool;

/**
 * Thread Pool
 */
public class EntitlementThreadPool implements IThreadPool {
    private static ThreadPool thrdPool;
    private static final int DEFAULT_POOL_SIZE = 10;


    static {
        // TODO thread pool size should be configurable
        thrdPool = new ThreadPool("entitlementThreadPool",
            DEFAULT_POOL_SIZE);
    }

    public void submit(Runnable task) {
        try {
            thrdPool.run(task);
        } catch (ThreadPoolException e) {
            PrivilegeManager.debug.error("EntitlementThreadPool.submit", e);
        }
    }
}
