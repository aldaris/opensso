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
 * $Id: IndexCache.java,v 1.3 2009-03-30 13:00:11 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.am.util.Cache;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache {

    private static int DEFAULT_CACHE_SIZE = 100000; //TOFIX
    private Cache hostIndexCache;
    private Cache pathIndexCache;
    private Cache parentPathIndexCache;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();


    public IndexCache() {
    }

    /**
     * Caches indexes.
     */
    public void cache(ResourceSaveIndexes indexes, String dn) {
        cache(dn, indexes.getHostIndexes(), hostIndexCache);
        cache(dn, indexes.getPathIndexes(), pathIndexCache);
        cache(dn, indexes.getParentPath(), parentPathIndexCache);
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

    public void clear(ResourceSaveIndexes indexes, String dn) {
        clear(dn, indexes.getHostIndexes(), hostIndexCache);
        clear(dn, indexes.getPathIndexes(), pathIndexCache);
        clear(dn, indexes.getParentPath(), parentPathIndexCache);
    }

    private void clear(String dn, Set<String> indexes, Cache cache) {
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
            parentPathIndexCache = new Cache(DEFAULT_CACHE_SIZE);
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
            Set<String> hostIndexes = indexes.getHostIndexes();


            for (String i : hostIndexes) {
                Set<String> r = (Set<String>)hostIndexCache.get(i);
                if (r != null) {
                    results.addAll(r);
                }
            }

            if (bSubTree) {
                Set<String> parentPathIndexes = indexes.getParentPathIndexes();
                Set<String> tmp = new HashSet<String>();
                for (String i : parentPathIndexes) {
                    Set<String> r = (Set<String>) parentPathIndexCache.get(i);
                    if (r != null) {
                        tmp.addAll(r);
                    }
                }
                results.retainAll(tmp);
            } else {
                Set<String> pathIndexes = indexes.getPathIndexes();
                Set<String> tmp = new HashSet<String>();
                for (String i : pathIndexes) {
                    Set<String> r = (Set<String>)pathIndexCache.get(i);
                    if (r != null) {
                        tmp.addAll(r);
                    }
                }
                results.retainAll(tmp);
            }

            return results;
        } finally {
            rwlock.readLock().unlock();
        }
    }
}
