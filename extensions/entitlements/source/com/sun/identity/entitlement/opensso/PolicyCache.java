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
 * $Id: PolicyCache.java,v 1.2 2009-05-07 23:00:25 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.am.util.Cache;
import com.sun.identity.entitlement.Privilege;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Policy Cache
 */
class PolicyCache {
    private int size;
    private Cache cache;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();

    PolicyCache(int size) {
        this.size = size;
        cache = new Cache(size);
    }

    /**
     * Caches a privilege.
     *
     * @param dn DN of the privilege object.
     * @param p Privilege.
     */
    public void cache(String dn, Privilege p) {
        rwlock.writeLock().lock();
        try {
            cache.put(dn, p);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public void cache(Map<String, Privilege> privileges, boolean force) {
        rwlock.writeLock().lock();
        try {
            for (String dn : privileges.keySet()) {
                if (force) {
                    cache.put(dn, privileges.get(dn));
                } else {
                    Privilege p = (Privilege)privileges.get(dn);
                    if (p == null) {
                        cache.put(dn, privileges.get(dn));
                    }
                }
            }
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public void decache(String dn) {
        rwlock.writeLock().lock();
        try {
            cache.remove(dn);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public Privilege getPolicy(String dn) {
        rwlock.readLock().lock();
        try {
            return (Privilege)cache.get(dn);
        } finally {
            rwlock.readLock().unlock();
        }
    }
}
