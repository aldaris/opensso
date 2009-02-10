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
 * $Id: SimulationIndexCache.java,v 1.1 2009-02-10 19:31:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.util.ResourceIndex;
import com.sun.identity.entitlement.util.ResourceNameIndexGenerator;
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
        }
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
     * Returns policies associated with a host index.
     *
     * @param idx host index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getHostIndex(String idx) {
        return hostIndexCache.get(idx);
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
     * Returns policies associated with a path index.
     *
     * @param idx path index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getPathIndex(String idx) {
        return pathIndexCache.get(idx);
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

    /**
     * Returns policies associated with a path parent index.
     *
     * @param idx path parent index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getPathParentIndex(String idx) {
        return pathParentIndexCache.get(idx);
    }

    private synchronized void clearCaches() {
        hostIndexCache = new HashMap<String, Set<Policy>>();
        pathIndexCache = new HashMap<String, Set<Policy>>();
        pathParentIndexCache = new HashMap<String, Set<Policy>>();
    }
}