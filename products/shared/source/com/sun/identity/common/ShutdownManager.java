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
 * $Id: ShutdownManager.java,v 1.4 2008-02-29 18:30:46 ww203982 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * ShutdownManager is a static instance which is used to trigger all the
 * ShutdownListener to call shutdown function.
 */

public class ShutdownManager {
    
    protected static ShutdownManager instance;
    
    protected Set[] listeners;
    
    /**
     * Constructor of ShutdownManager.
     */
    
    protected ShutdownManager() {
        int size = ShutdownPriority.HIGHEST.getIntValue();
        listeners = new HashSet[size];
        for (int i = 0; i < size; i++) {
            listeners[i] = new HashSet();
        }
        // add the trigger for stand alone application to shutdown.
        Runtime.getRuntime().addShutdownHook(new Thread(
            new Runnable() {
                public void run() {
                    shutdown();
                }
            }, "ShutdownThread"));
    }
    
    /**
     * Returns the static instance of ShutdownManager in the VM.
     *
     * @return The static instance of ShutdownManager
     */
    
    public static synchronized ShutdownManager getInstance() {
        if (instance == null) {
            instance = new ShutdownManager();
        }
        return instance;
    }
    
    /**
     * Adds a ShutdownListener to this ShutdownManager.
     *
     * @param listener The listener to be added
     */
    
    public void addShutdownListener(ShutdownListener listener) {
        addShutdownListener(listener, ShutdownPriority.DEFAULT);
    }
    
    /**
     * Adds a ShutdownListener to this ShutdownManager with indicated level.
     *
     * @param listener The listener to be added
     * @param priority The priority to shutdown for the shutdown listener
     */
    
    public void addShutdownListener(ShutdownListener listener,
        ShutdownPriority priority) {
        synchronized (listeners) {
            removeShutdownListener(listener);
            listeners[priority.getIntValue() - 1].add(listener);
        }
    }
    
    /**
     * Removes a ShutdownListener from this ShutdownManager.
     *
     * @param listener The listener to be removed
     */
    
    public void removeShutdownListener(ShutdownListener listener) {
        synchronized (listeners) {
            List priorities = ShutdownPriority.getPriorities();
            for (Iterator i = priorities.iterator(); i.hasNext();) {
                int index = ((ShutdownPriority) i.next()).getIntValue();
                if (listeners[index - 1].remove(listener)) {
                    break;
                }
            }
        }
    }

    /**
     * Shuts down all the listeners in this ShutdownManager.
     */
    
    public void shutdown() {
        synchronized (listeners) {
            List priorities = ShutdownPriority.getPriorities();
            for (Iterator i = priorities.iterator(); i.hasNext();) {
                int index = ((ShutdownPriority) i.next()).getIntValue();
                for (Iterator j = listeners[index - 1].iterator();
                    j.hasNext();) {
                    ShutdownListener element = (ShutdownListener) j.next();
                    element.shutdown();
                    // remove the components which have been shutdown to avoid
                    // problem when the shutdown function is called twice.
                    j.remove();
                }
            }
        }
    }
    
}