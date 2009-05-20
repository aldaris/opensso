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
 * $Id: IndexCache.java,v 1.5 2009-05-20 07:43:40 veiming Exp $
 */
package com.sun.identity.entitlement.opensso;

import com.iplanet.am.util.Cache;
import com.sun.identity.entitlement.ResourceSaveIndexes;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache {
    private int size = 1000000;
    private Cache subjectIndexCache;
    private Cache hostIndexCache;
    private Cache pathIndexCache;
    private Cache parentPathIndexCache;
    private ReadWriteLock rwlock = new ReentrantReadWriteLock();


    /**
     * Constructs
     *
     * @param size Size of cache.
     */
    public IndexCache(int size) {
        this.size = size;
        clearCaches();
    }

    /**
     * Caches indexes.
     *
     * @param indexes Resource cache indexes.
     * @param subjectIndexes Subject search indexes.
     * @param dn Distinguished name of the privilege.
     */
    public void cache(
        ResourceSaveIndexes indexes,
        Set<String> subjectIndexes,
        String dn) {
        cache(dn, subjectIndexes, subjectIndexCache);
        cache(dn, indexes.getHostIndexes(), hostIndexCache);
        cache(dn, indexes.getPathIndexes(), pathIndexCache);
        cache(dn, indexes.getParentPathIndexes(), parentPathIndexCache);
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

    /**
     * Clear index cache.
     *
     * @param indexes Resource cache indexes.
     * @param dn Distinguished name of the privilege.
     */
    public void clear(ResourceSaveIndexes indexes, String dn) {
        clear(dn, indexes.getHostIndexes(), hostIndexCache);
        clear(dn, indexes.getPathIndexes(), pathIndexCache);
        clear(dn, indexes.getParentPathIndexes(), parentPathIndexCache);
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
            subjectIndexCache = new Cache(size);
            hostIndexCache = new Cache(size);
            pathIndexCache = new Cache(size);
            parentPathIndexCache = new Cache(size);
        } finally {
            rwlock.writeLock().unlock();
        }
    }

    /**
     * Returns a set of DN that matches the resource and subject indexes.
     *
     * @param indexes Resource search indexes.
     * @param subjectIndexes Subject search indexes
     * @param bSubTree <code>true</code> for sub tree search mode.
     * @return A set of DN that matches the resource and subject indexes.
     */
    public Set<String> getMatchingEntries(
        ResourceSearchIndexes indexes,
        Set<String> subjectIndexes,
        boolean bSubTree
    ) {
        rwlock.readLock().lock();
        try {
            Set<String> results = new HashSet<String>();

            boolean hasSubjectIndexes = (subjectIndexes != null) &&
                !subjectIndexes.isEmpty();

            if (hasSubjectIndexes) {
                for (String i : subjectIndexes) {
                    Set<String> r = (Set<String>)subjectIndexCache.get(i);
                    if (r != null) {
                        results.addAll(r);
                    }
                }
                results.retainAll(getHostIndexes(indexes));
            } else {
                results.addAll(getHostIndexes(indexes));
            }

            if (bSubTree) {
                results.retainAll(getPathParentIndexes(indexes));
            } else {
                results.retainAll(getPathIndexes(indexes));
            }

            return results;
        } finally {
            rwlock.readLock().unlock();
        }
    }

    private Set<String> getPathParentIndexes(ResourceSearchIndexes indexes) {
        Set<String> parentPathIndexes = indexes.getParentPathIndexes();
        Set<String> results = new HashSet<String>();
        for (String i : parentPathIndexes) {
            Set<String> r = (Set<String>) parentPathIndexCache.get(i);
            if (r != null) {
                results.addAll(r);
            }
        }
        return results;
    }

    private Set<String> getPathIndexes(ResourceSearchIndexes indexes) {
        Set<String> pathIndexes = indexes.getPathIndexes();
        Set<String> results = new HashSet<String>();
        for (String i : pathIndexes) {
            Set<String> r = (Set<String>) pathIndexCache.get(i);
            if (r != null) {
                results.addAll(r);
            }
        }return results;
    }

    private Set<String> getHostIndexes(ResourceSearchIndexes indexes) {
        Set<String> results = new HashSet<String>();
        Set<String> hostIndexes = indexes.getHostIndexes();
        for (String i : hostIndexes) {
            Set<String> r = (Set<String>) hostIndexCache.get(i);
            if (r != null) {
                results.addAll(r);
            }
        }
        return results;
    }
}
