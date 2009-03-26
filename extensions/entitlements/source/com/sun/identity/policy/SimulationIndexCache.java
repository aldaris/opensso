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
 * $Id: SimulationIndexCache.java,v 1.5 2009-03-26 22:50:10 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.IIndexCache;
import com.sun.identity.entitlement.ResourceSearchIndexes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Caches the indexes.
 */
public class SimulationIndexCache implements IIndexCache {
    private Map<String, Set<Policy>> hostIndexCache;
    private Map<String, Set<Policy>> pathIndexCache;
    private Map<String, Set<Policy>> pathParentIndexCache;


    public SimulationIndexCache() {
        clearCaches();
    }

    /**
     * Caches policy.
     *
     * @param policy Policy associated with this index.
     */
    public void cachePolicy(Policy policy)
        throws PolicyException {
        Set<String> ruleNames = policy.getRuleNames();
/*
        for (String ruleName : ruleNames) {
            Rule rule = policy.getRule(ruleName);
            String resName = rule.getResourceName();
            ResourceIndex resIdx = ResourceNameIndexGenerator.getResourceIndex(
                resName);
            cacheHostIndex(resIdx.getHostIndex(), policy);
            cachePathIndex(resIdx.getPathIndex(), policy);
            for (String s : resIdx.getPathParentIndex()) {
                cachePathParentIndex(s, policy);
            }
        }*/
    }

    /**
     * Caches host index.
     *
     * @param idx host index
     * @param policy Policy associated with this index.
     */
    private void cacheHostIndex(String idx, Policy policy) {
        Set<Policy> set = hostIndexCache.get(idx);
        if (set == null) {
            set = new HashSet<Policy>();
            hostIndexCache.put(idx, set);
        }
        set.add(policy);
    }

    /**
     * Caches path index.
     *
     * @param idx path index
     * @param policy Policy associated with this index.
     */
    private void cachePathIndex(String idx, Policy policy) {
        Set<Policy> set = pathIndexCache.get(idx);
        if (set == null) {
            set = new HashSet<Policy>();
            pathIndexCache.put(idx, set);
        }
        set.add(policy);
    }

    /**
     * Caches path parent index.
     *
     * @param idx path parent  index
     * @param policy Policy associated with this index.
     */
    private void cachePathParentIndex(
        String idx,
        Policy policy
    ) {
        Set<Policy> set = pathParentIndexCache.get(idx);
        if (set == null) {
            set = new HashSet<Policy>();
            pathParentIndexCache.put(idx, set);
        }
        set.add(policy);
    }

    private synchronized void clearCaches() {
        hostIndexCache = new HashMap<String, Set<Policy>>();
        pathIndexCache = new HashMap<String, Set<Policy>>();
        pathParentIndexCache = new HashMap<String, Set<Policy>>();
    }

    public void getPolicies(
        ResourceSearchIndexes indexes,
        Set<Policy> hits,
        Map<String, Set<String>> misses
    ) {
        Set<Policy> cachedPoliciesForPath = new HashSet<Policy>();
        // not null for sub tree policy evaluation
        Set<String> parentPathIndexes = indexes.getParentPathIndexes();
        if ((parentPathIndexes != null) && !parentPathIndexes.isEmpty()) {
            for (String r : parentPathIndexes) {
                Set<Policy> cached = (Set<Policy>) pathIndexCache.get(r);
                if (cached == null) {
                    updateMisses(misses, LBL_PATH_PARENT_IDX, r);
                } else {
                    cachedPoliciesForPath.addAll(cached);
                }
            }
        } else {
            for (String r : indexes.getPathIndexes()) {
                Set<Policy> cached = (Set<Policy>) pathIndexCache.get(r);
                if (cached == null) {
                    updateMisses(misses, LBL_PATH_IDX, r);
                } else {
                    cachedPoliciesForPath.addAll(cached);
                }
            }
        }

        Set<Policy> cachedPoliciesForHost = new HashSet<Policy>();
        for (String r : indexes.getHostIndexes()) {
            Set<Policy> cached = (Set<Policy>) hostIndexCache.get(r);
            if (cached == null) {
                updateMisses(misses, LBL_HOST_IDX, r);
            } else {
                cachedPoliciesForHost.addAll(cached);
            }
        }

        cachedPoliciesForPath.retainAll(cachedPoliciesForHost);
        hits.addAll(cachedPoliciesForPath);
   }

    private void updateMisses(
        Map<String, Set<String>> misses,
        String idx,
        String res
    ) {
        if (misses != null) {
            Set m = misses.get(idx);
            if (m == null) {
                m = new HashSet<String>();
                misses.put(idx, m);
            }
            m.add(res);
        }
    }
}
