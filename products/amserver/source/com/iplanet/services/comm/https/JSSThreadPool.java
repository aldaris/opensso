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
 * $Id: JSSThreadPool.java,v 1.1 2007-12-14 21:33:37 beomsuk Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.iplanet.services.comm.https;

import com.sun.identity.shared.configuration.SystemPropertiesManager;
import com.sun.identity.shared.debug.Debug;

public class JSSThreadPool {
    private static int threadPoolSize;
    private static ThreadPool threadPool;
    private static final int DEFAULT_THREAD_POOL_SIZE = 20;
    private static Debug debug = Debug.getInstance("amJSS");

    static {
        try {
            threadPoolSize = Integer.parseInt(SystemPropertiesManager.get(
                    "com.iplanet.am.jssproxy.threadpoolSize"));
        }
        catch (Exception ex) {
            threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        }

        if (debug.messageEnabled()) {
            debug.message("JSSThreadPool size = " + threadPoolSize);
        }

        threadPool = new ThreadPool(threadPoolSize);
    }

    public static void
    run(
        Runnable task
    ) throws InterruptedException
    {
        threadPool.run(task);
    }
}

class Que implements java.io.Serializable {

    private java.util.List list = new java.util.ArrayList();
    
    synchronized public void put(Object obj) {
        list.add(obj);
        notify();
    }

    synchronized public Object get() {
        while (list.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
        return list.remove(0);
    }
}

class ThreadPool {
    
    private Que tasks = new Que();

    public ThreadPool(int threadCount) {
        for (int i=0; i<threadCount; i++) {
            ThreadPoolThread thread = new ThreadPoolThread(this);
            thread.start();
        }
    }

    public void run(Runnable task) {
        tasks.put(task);
    }
    
    public Runnable getNextRunnable() {
        return (Runnable)tasks.get();
    }
}

class ThreadPoolThread extends Thread {

    private ThreadPool pool;

    public ThreadPoolThread(ThreadPool pool) {
        this.pool = pool;
    }

    public void run() {
        while (true) {
            Runnable task = pool.getNextRunnable();
            try {
                task.run();
            } catch (Exception ex) {
            }
        }
    }
}
