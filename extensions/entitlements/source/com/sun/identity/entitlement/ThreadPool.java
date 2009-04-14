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
 * $Id: ThreadPool.java,v 1.2 2009-04-14 00:24:18 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IThreadPool;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread Pool for multi thread various tasks in the privilege evaluation
 * process.
 */
public final class ThreadPool implements IThreadPool {
    private ExecutorService exeService;

    public ThreadPool() {
        exeService = Executors.newCachedThreadPool();
    }

    /**
     * Submits a task to the thread pool.
     *
     * @param r Runnable task.
     */
    public void submit(Runnable r) {
        exeService.submit(r);
    }
}
