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
 * $Id: IndexCache.java,v 1.1 2009-03-26 22:50:09 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.am.util.Cache;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceListener;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache implements ServiceListener {

    private static int DEFAULT_CACHE_SIZE = 100000; //TOFIX
    private Cache hostIndexCache;
    private Cache pathIndexCache;
    private Cache pathParentIndexCache;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();

    public IndexCache() {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        try {
            ServiceConfigManager mgr = new ServiceConfigManager(
                "PolicyIndex", adminToken);
            mgr.addListener(this);
        } catch (SSOException e) {
            //TOFIX
        } catch (SMSException e) {
            //TOFIX
        }
    }

    /**
     * Caches indexes.
     */
    public void cache(ResourceSaveIndexes indexes, String dn) {
        cache(dn, indexes.getHostIndexes(), hostIndexCache);
        cache(dn, indexes.getPathIndexes(), pathIndexCache);
        cache(dn, indexes.getParentPath(), pathParentIndexCache);
    }

    private void cache(String dn, Set<String> indexes, Cache cache) {
        rwlock.writeLock().lock();
        try {
            for (String s : indexes) {
                Set<String> setDNs = (Set<String>)cache.get(s);
                if (setDNs == null) {
                    setDNs = new HashSet<String>();
                    cache.put(s, setDNs);
                }
                setDNs.add(dn);
            }
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    public void delete(ResourceSaveIndexes indexes, String dn) {
        delete(dn, indexes.getHostIndexes(), hostIndexCache);
        delete(dn, indexes.getPathIndexes(), pathIndexCache);
        delete(dn, indexes.getParentPath(), pathParentIndexCache);
    }

    private void delete(String dn, Set<String> indexes, Cache cache) {
        rwlock.writeLock().lock();
        try {
            for (String s : indexes) {
                Set<String> setDNs = (Set<String>)cache.get(s);
                if (setDNs != null) {
                    setDNs.remove(dn);
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

    public Set<String> getMatchingEntries(
        ResourceSearchIndexes indexes,
        boolean bSubTree
    ) {
        rwlock.readLock().lock();
        try {
            Set<String> results = new HashSet<String>();

            if (bSubTree) {
                for (String i : indexes.getParentPathIndexes()) {
                    results.addAll((Set<String>)pathParentIndexCache.get(i));
                }
                Set<String> tmp = new HashSet<String>();
                for (String i : indexes.getPathIndexes()) {
                    tmp.addAll((Set<String>)pathIndexCache.get(i));
                }
                results.retainAll(tmp);
            } else {
                for (String i : indexes.getPathIndexes()) {
                    results.addAll((Set<String>)pathIndexCache.get(i));
                }
            }

            Set<String> tmp = new HashSet<String>();
            for (String i : indexes.getHostIndexes()) {
                tmp.addAll((Set<String>) hostIndexCache.get(i));
            }
            results.retainAll(tmp);
            return results;
        } finally {
            rwlock.readLock().unlock();
        }
    }

    public void globalConfigChanged(
        String serviceName,
        String version,
        String groupName,
        String serviceComponent,
        int type) {
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
        int type) {
        //no-op
    }
}
