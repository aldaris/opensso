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
 * $Id: HeadTaskRunnable.java,v 1.1 2007-09-13 18:12:17 ww203982 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common;

import java.util.Date;
import java.util.Set;

/**
 * HeadTaskRunnable is designed to be the head of the linked-list when
 * TaskRunnable needs to be linked together. Whenever there is an insertion or
 * deletion, HeadTaskRunnable must be locked to guarantee correct
 * synchronization. Besides, when last element of the linked-list is removed
 * (setNext(null)), HeadTaskRunnable should use Triggerable to remove or destroy
 * the linked-list.
 */

public class HeadTaskRunnable implements TaskRunnable {
    
    protected Date time;
    protected volatile TaskRunnable nextTask;
    protected Triggerable parent;
    
    /**
     * Constructor of HeadTaskRunnable.
     *
     * @param parent The Triggerable interface to be run when the linked-list is
     *        empty
     * @param nextTask The TaskRunnable next to this TaskRunnable
     * @param time The time this TaskRunnable is scheduled
     */
    
    public HeadTaskRunnable(Triggerable parent, TaskRunnable nextTask,
        Date time) throws IllegalArgumentException {
        if ((time == null) || (nextTask == null)) {
            throw new IllegalArgumentException();
        }
        this.time = time;
        this.nextTask = nextTask;
        this.nextTask.setHeadTask(this);
        this.nextTask.setPrevious(this);
        this.parent = parent;
    }
    
    /**
     * Implement for TaskRunnable interface, no actual use for HeadTaskRunnable.
     */
    
    public void setHeadTask(TaskRunnable headTask) {
    }
    
    /**
     * Implement for TaskRunnable interface, no actual use for HeadTaskRunnable.
     */
    
    public TaskRunnable getHeadTask() {
        return null;
    }
    
    /**
     * Implements for TaskRunnable interface, always return false.
     *
     * @return false means nothing can be added to this TaskRunnable
     */
    
    public boolean addElement(Object key) {
        return false;
    }
    
    /**
     * Implements for TaskRunnable interface, always return false.
     *
     * @return false means nothing can be removed from this TaskRunnable
     */
    
    public boolean removeElement(Object key) {
        return false;
    }
    
    /**
     * Implements for TaskRunnable interface, always return false.
     *
     * @return true means this TaskRunnable is always empty
     */
    
    public boolean isEmpty() {
        return true;
    }
    
    /** 
     * Sets the TaskRunnable next to this TaskRunnable in the linked-list.
     *
     * @param task The next TaskRunnable
     */
    
    public void setNext(TaskRunnable task) {
        if (task == null) {
            synchronized (this) {
                if (parent != null) {
                    parent.trigger(time);
                }
            }
            nextTask = null;
        } else {
            nextTask = task;
        }
    }
    
    /**
     * Implements for TaskRunnable interface, There is no previous element for
     * HeadTaskRunnable.
     */
    
    public void setPrevious(TaskRunnable task) {
    }
    
    /**
     * Returns the TaskRunnable next to this TaskRunnable in the linked-list.
     *
     * @return next TaskRunnable object or null if it is not set
     */
    
    public TaskRunnable next() {
        return nextTask;
    }
    
    /**
     * Implements for TaskRunnable interface, there is no previous element for
     * HeadTaskRunnable.
     *
     * @return null means there is no previous element
     */
    
    public TaskRunnable previous() {
        return null;
    }
    
    /**
     * Implements for TaskRunnable interface, HeadTaskRunnable doesn't have a
     * run period.
     *
     * @return -1 means the task only will be run once
     */
    
    public long getRunPeriod() {
        return -1;
    }
    
    /**
     * Sets the Triggerable interface which will be run when the linked-list is
     * empty.
     *
     * @param parent The Triggerable interface to be run when the linked-list is
     *        empty
     */
    
    public void setTrigger(Triggerable parent) {
        synchronized (this) {
            if (parent != null){
                this.parent = parent;
            }
        }
    }
    
    /**
     * Returns the time which this HeadTaskRunnable is scheduled.
     *
     * @return The long value which represents the time this task is scheduled
     */
    
    public long scheduledExecutionTime() {
        return time.getTime();
    }
    
    /**
     * Implements for TaskRunnable interface, just run the next TaskRunnable.
     */
    
    public void run() {    
        TaskRunnable taskToRun = next();
        do {
            taskToRun.run();
        } while ((taskToRun = taskToRun.next()) != null);
    }
    
}