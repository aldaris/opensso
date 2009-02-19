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
 * $Id: IndexCache.java,v 1.5 2009-02-19 07:26:13 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.am.util.Cache;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceListener;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock

;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache implements ServiceListener, IIndexCache {
    private static IndexCache instance = new IndexCache();
    private static int DEFAULT_CACHE_SIZE = 100000; //TOFIX

    private Cache hostIndexCache;
    private Cache pathIndexCache;
    private Cache pathParentIndexCache;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();
    
    static {
        instance.clearCaches();

        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        try {
            ServiceConfigManager mgr = new ServiceConfigManager(
                "PolicyIndex", adminToken);
            mgr.addListener(instance);
        } catch (SSOException e) {
            //TOFIX
        } catch (SMSException e) {
            //TOFIX
        }
    }

    private IndexCache() {
    }

    /**
     * Returns an instance.
     *
     * @return an instance.
     */
    public static IndexCache getInstance() {
        return instance;
    }

    /**
     * Returns policies associated with a host index.
     *
     * @param idx host index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getHostIndex(String idx) {
        rwlock.readLock().lock();
        try {
            return (Set<Policy>)hostIndexCache.get(idx);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    /**
     * Returns policies associated with a path index.
     *
     * @param idx path index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getPathIndex(String idx) {
        rwlock.readLock().lock();
        try {
            return (Set<Policy>)pathIndexCache.get(idx);
        } finally {
            rwlock.readLock().unlock();
        }
    }

    /**
     * Returns policies associated with a path parent index.
     *
     * @param idx path parent index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getPathParentIndex(String idx) {
        rwlock.readLock().lock();
        try {
            return (Set<Policy>) pathParentIndexCache.get(idx);
        } finally {
            rwlock.readLock().unlock();
        }
}

    /**
     * Caches indexes.
     *
     * @param hostIndexes Map of host index to set of policies.
     * @param pathIndexes Map of path index to set of policies.
     * @param pathParentIndexes Map of path parent index to set of policies.
     */
    public void cache(
        Map<String, Set<Policy>> hostIndexes,
        Map<String, Set<Policy>> pathIndexes,
        Map<String, Set<Policy>> pathParentIndexes) {
        rwlock.writeLock().lock();
        try {
            if ((hostIndexes != null) && !hostIndexes.isEmpty()) {
                for (String i : hostIndexes.keySet()) {
                    Set<Policy> policies = hostIndexes.get(i);
                    Set<Policy> set = (Set<Policy>) hostIndexCache.get(i);
                    if (set == null) {
                        set = new HashSet<Policy>();
                        hostIndexCache.put(i, set);
                    }
                    set.addAll(policies);
                }
            }
            if ((pathIndexes != null) && !pathIndexes.isEmpty()) {
                for (String i : pathIndexes.keySet()) {
                    Set<Policy> policies = pathIndexes.get(i);
                    Set<Policy> set = (Set<Policy>) pathIndexCache.get(i);
                    if (set == null) {
                        set = new HashSet<Policy>();
                        pathIndexCache.put(i, set);
                    }
                    set.addAll(policies);
                }
            }
            if ((pathParentIndexes != null) && !pathParentIndexes.isEmpty()) {
                for (String i : pathParentIndexes.keySet()) {
                    Set<Policy> policies = pathParentIndexes.get(i);
                    Set<Policy> set = (Set<Policy>) pathParentIndexCache.get(i);
                    if (set == null) {
                        set = new HashSet<Policy>();
                        pathParentIndexCache.put(i, set);
                    }
                    set.addAll(policies);
                }
            }
        } finally {
            rwlock.writeLock().unlock();
        }
    }


    private synchronized void clearCaches() {
        rwlock.writeLock().lock();
        try {
            hostIndexCache = new Cache(DEFAULT_CACHE_SIZE);
            pathIndexCache = new Cache(DEFAULT_CACHE_SIZE);
            pathParentIndexCache = new Cache(DEFAULT_CACHE_SIZE);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public void globalConfigChanged(
        String serviceName,
        String version,
        String groupName,
        String serviceComponent,
        int type
    ) {
        clearCaches();
    }

    public void schemaChanged(String serviceName, String version) {
        //no-op
    }

    public void organizationConfigChanged(
        String serviceName,
        String version,
        String orgName,
        String groupName,
        String serviceComponent,
        int type
    ) {
        //no-op
    }
}