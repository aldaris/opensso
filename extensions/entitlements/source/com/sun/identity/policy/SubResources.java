/*
 * 
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
 * $Id: SubResources.java,v 1.1 2009-01-29 02:04:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.DataStoreEntry;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.util.ResourceComp;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class SubResources {
    private Map<String, Policy> hostIndexToPolicy;
    private Map<String, Policy> pathIndexToPolicy;
    private Set<String> resources;
    
    public List<Entitlement> evaluate(
        SSOToken token,
        ServiceType serviceType,
        String resourceName,
        Set<String> actionNames,
        Map<String, Set<String>> envParameters,
        Set<DataStoreEntry> entries
    ) {
        createHostIndexMap(entries);
        createPathIndexMap(entries);
        createResourceSet(entries);
        
        for (String r : resources) {
        }
        return null;
    }
    
    private void createHostIndexMap(Set<DataStoreEntry> entries) {
        hostIndexToPolicy = new HashMap<String, Policy>();
        for (DataStoreEntry entry : entries) {
            Policy p = (Policy)entry.getPolicy();
            for (String s : entry.getHostIndexes()) {
                hostIndexToPolicy.put(s, p);
            }
        }
    }

    private void createPathIndexMap(Set<DataStoreEntry> entries) {
        pathIndexToPolicy = new HashMap<String, Policy>();
        for (DataStoreEntry entry : entries) {
            Policy p = (Policy)entry.getPolicy();
            for (String s : entry.getPathIndexes()) {
                pathIndexToPolicy.put(s, p);
            }
        }
    }
    
    private void createResourceSet(Set<DataStoreEntry> entries) {
        resources = new HashSet<String>();
        for (DataStoreEntry entry : entries) {
            Policy p = (Policy)entry.getPolicy();
            Set<String> ruleNames = p.getRuleNames();
            for (String ruleName : ruleNames) {
                try {
                    Rule rule = p.getRule(ruleName);
                    resources.add(rule.getResourceName());
                } catch (PolicyException e) {
                    // ignore
                }
            }
        }
    }
    
    Set<Policy> search(Set<String> hostIndexes, Set<String>pathIndexes) {
        Set<Policy> policies  = new HashSet<Policy>();
        for (String s : hostIndexes) {
            Policy p = hostIndexToPolicy.get(s);
            if (p != null) {
                policies.add(p);
            }
        }
        for (String s : pathIndexes) {
            Policy p = pathIndexToPolicy.get(s);
            if (p != null) {
                policies.add(p);
            }
        }
        return policies;
    }

    private class Evaluator implements Runnable {
        private SSOToken token;
        private ServiceType serviceType;
        private String resourceName;
        private Set<String> actionNames;
        private Map<String, Set<String>> envParameters;
        private SubResources parent;

        private Evaluator(
            SubResources parent,
            SSOToken token,
            ServiceType serviceType,
            String resourceName,
            Set<String> actionNames,
            Map<String, Set<String>> envParameters
        ) {
            this.parent = parent;
            this.token = token;
            this.serviceType = serviceType;
            this.resourceName = resourceName;
            this.actionNames = actionNames;
            this.envParameters = envParameters;
        }
        
        public void run() {
            PolicyDecision result = null;
            ResourceComp comp = ResourceNameSplitter.split(resourceName);
            Set<Policy> policies = parent.search(
                comp.getHostIndexes(), comp.getPathIndexes());
            
            if (!policies.isEmpty()) {
                Set<PolicyDecision> policyDecisions = 
                    new HashSet<PolicyDecision>();
                
                for (Policy p : policies) {
                    try {
                        PolicyDecision pd = p.getPolicyDecision(
                            token, serviceType.getName(), resourceName,
                            actionNames, envParameters);
                        if (pd != null) {
                            policyDecisions.add(pd);
                        }
                    } catch (SSOException e) {
                        // TOFIX
                    } catch (PolicyException e) {
                        // TOFIX
                    }
                }
                
                result = PolicyEvaluatorAdaptor.mergePolicyDecisions(
                    policyDecisions, serviceType);
                
            }
        }
    }
}
