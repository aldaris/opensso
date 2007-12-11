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
 * $Id: ThreadPool.java,v 1.4 2007-12-11 22:04:22 subashvarma Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.util;

import com.sun.identity.shared.debug.Debug;

/**
 * <p>
 * This thread pool maintains a number of threads that run the tasks from a task
 * queue one by one. The tasks are handled in asynchronous mode, which means it
 * will not block the main thread to proceed while the task is being processed
 * by the thread pool.
 * <p>
 * This thread pool has a fixed size of threads. It maintains all the tasks to
 * be executed in a task queue. Each thread then in turn gets a task from the
 * queue to execute. If the tasks in the task queue reaches a certain number(the
 * threshold value), it will log an error message and ignore the new incoming
 * tasks until the number of un-executed tasks is less than the threshold value.
 * This guarantees the thread pool will not use up the system resources under
 * heavy load.
 * @supported.all.api
 */
public class ThreadPool {

    // FIXME poolSize is not being enforced
    private int poolSize;

    private int threshold;

    private String poolName;

    private Debug debug;

    private java.util.ArrayList taskList = new java.util.ArrayList();

    /**
     * Constructs a thread pool with given parameters.
     * 
     * @param name
     *            name of the thread pool.
     * @param poolSize
     *            the thread pool size, indicates how many threads are created
     *            in the pool.
     * @param threshold
     *            the maximum size of the task queue in the thread pool.
     * @param daemon
     *            set the threads as daemon if true; otherwise if not.
     * @param debug
     *            Debug object to send debugging message to.
     */
    public ThreadPool(String name, int poolSize, int threshold, boolean daemon,
            Debug debug) {
        this.debug = debug;
        this.poolSize = poolSize;
        this.threshold = threshold;
        this.poolName = name;
        if (debug.messageEnabled()) {
            debug.message("Initiating login thread pool size = "
                    + this.poolSize + "\nThreshold = " + threshold);
        }
        for (int i = 0; i < poolSize; i++) {
            WorkerThread thread = new WorkerThread(name + "[" + i + "]");
            thread.setDaemon(daemon);
            thread.start();
        }
    }

    /**
     * Runs a user defined task.
     * 
     * @param task
     *            user defined task.
     * @throws ThreadPoolException
     */
    public final void run(Runnable task) throws ThreadPoolException {
        synchronized (taskList) {
            if (taskList.size() >= threshold) {
                throw new ThreadPoolException(poolName
                        + " thread pool's task queue is full.");
            } else {
                taskList.add(task);
                taskList.notify();
            }
        }
    }

    /**
     * Fetches a task from the task queue for a thread.
     */
    protected Runnable getTask() {
        synchronized (taskList) {
            while (taskList.isEmpty()) {
                try {
                    taskList.wait();
                } catch (InterruptedException e) {
                }
            }
            return (Runnable) taskList.remove(0);
        }
    }
    
    /*
     * Returns the size of the task list.
     */
    public int getCurrentSize() {
        return taskList.size();
    }

    // private thread class that fetches tasks from the task queue and
    // executes them.
    private class WorkerThread extends Thread {
        public WorkerThread(String name) {
            setName(name);
        }

        /**
         * Starts the thread pool.
         */
        public void run() {
            while (true) {
                Runnable task = getTask();
                try {
                    task.run();
                } catch (Exception e) {
                    debug.error("Running task " + task, e);
                }
            }
        }
    }
}
