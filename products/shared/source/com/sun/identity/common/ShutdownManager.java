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
 * $Id: ShutdownManager.java,v 1.2 2008-01-16 20:17:42 ww203982 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * ShutdownManager is a static instance which is used to trigger all the
 * ShutdownListener to call shutdown function.
 */

public class ShutdownManager {
    
    protected static ShutdownManager instance;
    
    public static final int HIGHEST_LEVEL = 3;
    public static final int DEFAULT_LEVEL = 2;
    public static final int LOWEST_LEVEL = 1;
    
    protected Set[] listeners;
    
    /**
     * Constructor of ShutdownManager.
     */
    
    protected ShutdownManager() {
        listeners = new HashSet[HIGHEST_LEVEL];
        for (int i = 0; i < HIGHEST_LEVEL; i++) {
            listeners[i] = new HashSet();
        }
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
        try {
            addShutdownListener(listener, DEFAULT_LEVEL);
        } catch(IllegalArgumentException ex) {
        }
    }
    
    /**
     * Adds a ShutdownListener to this ShutdownManager with indicated level.
     *
     * @param listener The listener to be added
     * @param level The level of the shutdown listener
     */
    
    public void addShutdownListener(ShutdownListener listener, int level)
        throws IllegalArgumentException {
        if ((level > HIGHEST_LEVEL) || (level < LOWEST_LEVEL)) {
            throw new IllegalArgumentException("Level out of range!");
        } else {
            synchronized (listeners) {
                removeShutdownListener(listener);
                listeners[level - 1].add(listener);
            }
        }
    }
    
    /**
     * Removes a ShutdownListener from this ShutdownManager.
     *
     * @param listener The listener to be removed
     */
    
    public void removeShutdownListener(ShutdownListener listener) {
        synchronized (listeners) {
            for (int i = 0; i < HIGHEST_LEVEL; i++) {
                if (listeners[i].remove(listener)) {
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
            for (int i = HIGHEST_LEVEL; i >= LOWEST_LEVEL; i--) {
                for (Iterator iter = listeners[i - 1].iterator();
                    iter.hasNext();) {
                    ShutdownListener element = (ShutdownListener) iter.next();
                    element.shutdown();
                }
            }
        }
    }
    
}