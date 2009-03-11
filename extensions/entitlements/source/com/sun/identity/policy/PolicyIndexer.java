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
 * $Id: PolicyIndexer.java,v 1.11 2009-03-11 04:57:49 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.DataStoreEntry;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPolicyIndexDataStore;
import com.sun.identity.entitlement.PolicyIndexDataStoreFactory;
import com.sun.identity.entitlement.util.ResourceIndex;
import com.sun.identity.entitlement.util.ResourceNameIndexGenerator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class PolicyIndexer {
    private PolicyIndexer() {
    }
    
    /**
     * Stores the serializable policy in datastore its indexes.
     * 
     * @param policy Policy object.
     * @throws PolicyException if errors getting the rules.
     * @throws EntitlementException if indexes cannot be created.
     */
    public static void store(Policy policy) 
        throws PolicyException, EntitlementException
    {
        IPolicyIndexDataStore datastore = 
            PolicyIndexDataStoreFactory.getInstance().getDataStore();
        
        SerializedPolicy serPolicy = SerializedPolicy.serialize(policy);
        String policyName = policy.getName();
        Set<String> ruleNames = policy.getRuleNames();
        Set<String> hostIndexes = new HashSet<String>();
        Set<String> pathIndexes = new HashSet<String>();
        Set<String> pathParentIndexes = new HashSet<String>();
        
        for (String ruleName : ruleNames) {
            Rule rule = policy.getRule(ruleName);
            String resName = rule.getResourceName();
            ResourceIndex resIdx = ResourceNameIndexGenerator.getResourceIndex(
                resName);
            hostIndexes.add(resIdx.getHostIndex());
            pathIndexes.add(resIdx.getPathIndex());
            pathParentIndexes.addAll(resIdx.getPathParentIndex());
        }

        datastore.add(policyName, hostIndexes, pathIndexes,
            pathParentIndexes, serPolicy);
    }
    
    /**
     * Deletes the serializable policy and its indexes in datastore.
     * 
     * @param policy Policy object.
     */
    public static void delete(Policy policy) {
        IPolicyIndexDataStore datastore = 
            PolicyIndexDataStoreFactory.getInstance().getDataStore();

        String policyName = policy.getName();
        try {
            datastore.delete(policyName);
        } catch (EntitlementException e) {
            PolicyManager.debug.error("PolicyIndexer.store", e);
        }
    }
    
    /**
     * Searches for a policy objects with given indexes.
     * 
     * @param pm Policy Manager.
     * @param hostIndexes Set of Host Indexes.
     * @param pathIndexes Set of Path Indexes.
     * @param pathParentIndex Path ParentIndex
     * @return Set of policy objects.
     */
    public static Set<Policy> search(
        PolicyManager pm, 
        Set<String> hostIndexes, 
        Set<String> pathIndexes,
        String pathParentIndex
    ) {
        Set<Policy> policies = new HashSet<Policy>();
        try {
            IPolicyIndexDataStore datastore = 
                PolicyIndexDataStoreFactory.getInstance().getDataStore();
            Set<DataStoreEntry> results = datastore.search(hostIndexes,
                pathIndexes, pathParentIndex);
            Map<String, Set<Policy>> mapHostIndexes = createCacheMap(
                hostIndexes);
            Map<String, Set<Policy>> mapPathIndexes = createCacheMap(
                pathIndexes);

            Map<String, Set<Policy>> mapPathParentIndexes = null;
            if (pathParentIndex != null) {
                mapPathParentIndexes = new HashMap<String, Set<Policy>>();
                mapPathParentIndexes.put(pathParentIndex,
                    new HashSet<Policy>());
            }

            IndexCache cache = IndexCache.getInstance();

            if ((results != null) && !results.isEmpty()) {
                for (DataStoreEntry d : results) {
                    Policy policy = SerializedPolicy.deserialize(
                        pm, (SerializedPolicy)d.getPolicy());
                    if (policy.isActive()) {
                        for (String r : d.getHostIndexes()) {
                            addToCacheMap(mapHostIndexes, r, policy);
                        }
                        for (String r : d.getPathIndexes()) {
                            addToCacheMap(mapPathIndexes, r, policy);
                        }
                        if (mapPathParentIndexes != null) {
                            String pp = d.getPathParent();
                            if (pp != null) {
                                addToCacheMap(mapPathParentIndexes, pp, policy);
                            }
                        }
                    }
                }
            }

            cache.cache(mapHostIndexes, mapPathIndexes, mapPathParentIndexes);
            cache.getPolicies(hostIndexes, pathIndexes, pathParentIndex,
                policies, null);
        } catch (EntitlementException e) {
            PolicyManager.debug.error("PolicyIndexer.store", e);
        }
        return policies;
    }

    private static Map<String, Set<Policy>> createCacheMap(Set<String> set) {
        Map<String, Set<Policy>> map = new HashMap<String, Set<Policy>>();
        if (set != null) {
            for (String s : set) {
                map.put(s, new HashSet<Policy>());
            }
        }
        return map;
    }

    private static void addToCacheMap(
        Map<String, Set<Policy>> map,
        String idx,
        Policy policy
    ) {
        Set<Policy> set = map.get(idx);
        if (set != null) {
            set.add(policy);
        }
    }
}
