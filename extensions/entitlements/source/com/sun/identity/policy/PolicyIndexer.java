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
 * $Id: PolicyIndexer.java,v 1.2 2009-01-17 18:26:25 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPolicyIndexDataStore;
import com.sun.identity.entitlement.PolicyIndexDataStoreFactory;
import com.sun.identity.entitlement.util.ResourceNameIndexGenerator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dennis
 */
public class PolicyIndexer {
    private PolicyIndexer() {
    }
    
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
        
        for (String ruleName : ruleNames) {
            try {
                Rule rule = policy.getRule(ruleName);
                URL url = new URL(rule.getResourceName());
                hostIndexes.add(ResourceNameIndexGenerator.getHostIndex(url));
                pathIndexes.add(ResourceNameIndexGenerator.getPathIndex(url));
            } catch (MalformedURLException e) {
                throw new PolicyException(e.getMessage());
            }
        }
        
        datastore.add(policyName, hostIndexes, pathIndexes, serPolicy);
    }
    
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
    
    public static Set<Policy> search(
        PolicyManager pm, String hostIndex, String pathIndex) {
        Set<Policy> policies = new HashSet<Policy>();
        try {
            IPolicyIndexDataStore datastore = PolicyIndexDataStoreFactory.getInstance().getDataStore();
            Set<Object> results = datastore.search(hostIndex, pathIndex);
            if ((results != null) && !results.isEmpty()) {
                for (Object o : results) {
                    policies.add(SerializedPolicy.deserialize(pm, 
                        (SerializedPolicy)o));
                }
            }
        } catch (EntitlementException e) {
            PolicyManager.debug.error("PolicyIndexer.store", e);
        }
        return policies;
        
    }
}
