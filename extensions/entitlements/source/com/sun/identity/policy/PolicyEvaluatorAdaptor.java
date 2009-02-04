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
 * $Id: PolicyEvaluatorAdaptor.java,v 1.12 2009-02-04 07:41:20 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.DataStoreEntry;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPolicyEvaluator;
import com.sun.identity.entitlement.util.IndexCache;
import com.sun.identity.entitlement.util.ResourceComp;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
import com.sun.identity.policy.interfaces.ResourceName;
import com.sun.identity.sm.SMSThreadPool;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

public class PolicyEvaluatorAdaptor implements IPolicyEvaluator {
    private PolicyDecisionTask tasks;
    private int tasksCount;
    static final String LBL_HOST_IDX = "host";
    static final String LBL_PATH_IDX = "path";
    static final String LBL_PATH_PARENT_IDX = "pathparent";
    
    public PolicyEvaluatorAdaptor() {
        tasks = new PolicyDecisionTask();
    }
    
    private Set<Policy> search(Subject adminSubject, String resourceName)
        throws SSOException, PolicyException {
        PolicyManager pm = new PolicyManager(getSSOToken(adminSubject), "/");
        ResourceComp comp = ResourceNameSplitter.split(resourceName);
        return PolicyIndexer.search(pm, comp.getHostIndexes(),
            comp.getPathIndexes());
    }

    static Set<DataStoreEntry> recursiveSearch(
        SSOToken token,
        Map<String, Set<String>> misses
    ) throws SSOException, PolicyException {
        PolicyManager pm = new PolicyManager(token, "/");
        Set<String> hostIndexes = misses.get(LBL_HOST_IDX);
        Set<String> pathIndexes = misses.get(LBL_PATH_IDX);
        Set<String> pathParentIndexes = misses.get(LBL_PATH_PARENT_IDX);

        String pathParent = ((pathParentIndexes != null) &&
            !pathParentIndexes.isEmpty()) ? pathParentIndexes.iterator().next():
                null;

        return PolicyIndexer.search(pm, hostIndexes, pathIndexes, pathParent);
    }

    /**
     * Returns <code>true</code> if the subject is granted to an
     * entitlement.
     *
     * @param adminubject Subject for performing the evaluation.
     * @param subject Subject who is under evaluation.
     * @param serviceTypeName Application type.
     * @param entitlement Entitlement object which describes the resource name 
     *        and actions.
     * @param envParameters Map of environment parameters.
     * @return <code>true</code> if the subject is granted to an
     *         entitlement.
     * @throws EntitlementException if the result cannot be determined.
     */
    public boolean hasEntitlement(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        Entitlement entitlement,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        try {
            String resourceName = entitlement.getResourceName();
            Set<PolicyDecision> policyDecisions = getPolicyDecisions(
                adminSubject, subject, serviceTypeName, resourceName, 
                envParameters);
            if ((policyDecisions == null) || policyDecisions.isEmpty()) {
                return false;
            }
            
            ServiceType serviceType = 
                ServiceTypeManager.getServiceTypeManager().getServiceType(
                serviceTypeName);
            Map<String, Object> actionValues = entitlement.getActionValues();
            PolicyDecision pd = mergePolicyDecisions(
                policyDecisions, serviceType);
            return doesActionDecisionMatch(pd, actionValues);
        } catch (SSOException e) {
            throw new EntitlementException(e.getMessage(), -1);
        } catch (PolicyException e) {
            throw new EntitlementException(e.getMessage(), -1);
        }
    }
    
    private boolean doesActionDecisionMatch(
        PolicyDecision pd, 
        Map<String, Object> actionValues
    ) {
        Map<String, ActionDecision> decisionsMap = pd.getActionDecisions();
        if (decisionsMap != null) {
            for (String actionName : actionValues.keySet()) {
                Object expected = actionValues.get(actionName);
                ActionDecision decision = decisionsMap.get(actionName);
                if (decision == null) {
                    return false;
                }
                Set values = decision.getValues();
                if ((values == null) || !values.equals(expected)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    
    private SSOToken getSSOToken(Subject subject)
        throws SSOException {
        Set<Principal> principals = subject.getPrincipals();
        if (!principals.isEmpty()) {
            try {
                Principal p = principals.iterator().next();
                String tokenId = p.getName();
                SSOTokenManager mgr = SSOTokenManager.getInstance();
                return mgr.createSSOToken(tokenId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * Returns list of entitlements granted to a subject.
     *
     * @param adminubject Subject for performing the evaluation.
     * @param subject Subject who is under evaluation.
     * @param serviceTypeName Application type.
     * @param entitlement Entitlement object which describes the resource name 
     *        and actions.
     * @param envParameters Map of environment parameters.
     * @param recursive <code>true</code> to perform evaluation on sub resources
     *        from the given resource name.
     * @return list of entitlements granted to a subject.
     * @throws EntitlementException if the result cannot be determined.
     */
    public List<Entitlement> getEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException {
        if (!recursive) {
            return getEntitlements(adminSubject, subject, serviceTypeName,
                resourceName, envParameters);
        } else {
            return getSubTreeEntitlements(adminSubject, subject, 
                serviceTypeName, resourceName, envParameters);
        }
    }
        
    /**
     * Returns list of entitlements granted to a subject.
     *
     * @param adminubject Subject for performing the evaluation.
     * @param subject Subject who is under evaluation.
     * @param serviceTypeName Application type.
     * @param entitlement Entitlement object which describes the resource name 
     *        and actions.
     * @param envParameters Map of environment parameters.
     * @return list of entitlements granted to a subject.
     * @throws EntitlementException if the result cannot be determined.
     */
    public List<Entitlement> getEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {  
        try {
            Set<PolicyDecision> policyDecisions = getPolicyDecisions(
                adminSubject, subject, serviceTypeName, resourceName, 
                envParameters);
            if ((policyDecisions == null) || policyDecisions.isEmpty()) {
                return Collections.EMPTY_LIST;
            }
            ServiceType serviceType =
                ServiceTypeManager.getServiceTypeManager().getServiceType(
                serviceTypeName);

            PolicyDecision pd = mergePolicyDecisions(policyDecisions, 
                serviceType);
            List<Entitlement> result = new ArrayList<Entitlement>();
            result.add(getEntitlement(serviceType, resourceName, pd));
            return result;
        } catch (SSOException e) {
            throw new EntitlementException(e.getMessage(), -1);
        } catch (PolicyException e) {
            throw new EntitlementException(e.getMessage(), -1);
        }            
    }
    
    static Entitlement getEntitlement(
        ServiceType serviceType,
        String resourceName, 
        PolicyDecision pd
    ) throws PolicyException {
        Set<String> actionNames = serviceType.getActionNames();
        String falseValue = null;
        for (String name : actionNames) {
            ActionSchema as = serviceType.getActionSchema(name);
            falseValue = as.getFalseValue();
        }
        
        Map actionValues = new HashMap();
        Map<String, String> advices = new HashMap<String, String>();
        
        Map<String, ActionDecision> actionDecisions = pd.getActionDecisions();
        for (String name : actionDecisions.keySet()) {
            ActionDecision ad = actionDecisions.get(name);
            String actionName = ad.getActionName();
            Set<String> values = ad.getValues();
            
            // false value takes precedence
            Set curValueSet = (Set)actionValues.get(actionName);
            if (curValueSet == null) {
                Set<String> copyOfValues = new HashSet<String>();
                copyOfValues.addAll(values);
                actionValues.put(actionName, copyOfValues);
            } else if (falseValue != null) {
                // this results in non deterministic result if
                // false value is null
                if (!curValueSet.contains(falseValue)) {
                    Set<String> copyOfValues = new HashSet<String>();
                    copyOfValues.addAll(values);
                    actionValues.put(actionName, copyOfValues);
                }
            }
            
            advices.putAll(ad.getAdvices());            
        }
        
        Entitlement entitlement = new Entitlement(serviceType.getName(), 
            resourceName, actionValues);
        entitlement.setAdvices(advices);
        entitlement.setAttributes(pd.getResponseAttributes());
        return entitlement;
    }
    
    static PolicyDecision mergePolicyDecisions(
        Set<PolicyDecision> policyDecisions,
        ServiceType serviceType
    ) {
        PolicyDecision result = null;
        for (PolicyDecision pd : policyDecisions) {
            if (result == null) {
                result = pd;
            } else {
                result = PolicyEvaluator.mergePolicyDecisions(
                    serviceType, pd, result);
            }
        }
        return result;
    }
    
    private Set<PolicyDecision> getPolicyDecisions(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws SSOException, PolicyException {
        Set<Policy> policies = search(adminSubject, resourceName);
        if ((policies == null) || policies.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        ServiceType serviceType =
            ServiceTypeManager.getServiceTypeManager().getServiceType(
            serviceTypeName);
        ResourceName resComparator = serviceType.getResourceNameComparator();
        Set<String> actionNames = serviceType.getActionNames();
        SSOToken token = getSSOToken(subject);
        
        Set<PolicyDecisionTask.Task> policyEvalTasks = 
            new HashSet<PolicyDecisionTask.Task>();
        
        for (Policy p : policies) {
            PolicyDecisionTask.Task task = tasks.addTask(
                resComparator, p, resourceName);
            policyEvalTasks.add(task);
        }

        synchronized (this) {
            for (PolicyDecisionTask.Task task : policyEvalTasks) {
                EvaluatorThread eval = new EvaluatorThread(
                    this, task, token, serviceType, actionNames, envParameters);
                SMSThreadPool.scheduleTask(eval);
            }

            while (tasksCount > 0) {
                try {
                    this.wait();
                } catch (InterruptedException ex) {
                    //TOFIX
                }
            }
        }
        
        Set<PolicyDecision> policyDecisions = new HashSet<PolicyDecision>();
        for (PolicyDecisionTask.Task t : policyEvalTasks) {
            PolicyDecision pd = t.policyDecision;
            if (pd != null) {
                policyDecisions.add(pd);
            }
        }

        return policyDecisions;
    }
    
     public List<Entitlement> getSubTreeEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {  
        try {
            ServiceType serviceType =
                ServiceTypeManager.getServiceTypeManager().getServiceType(
                serviceTypeName);
            Set<String> actionNames = serviceType.getActionNames();

            ResourceComp comp = ResourceNameSplitter.split(resourceName);
            Map<Policy, Map<String, Set<String>>> hits = new
                HashMap<Policy, Map<String, Set<String>>>();
            Map<String, Set<String>> misses = lookupCache(comp, hits);
            MissedSubResources missedThread = null;
            SubResources hitThread = null;

            if (!misses.isEmpty()) {
                missedThread = new MissedSubResources(this,
                    getSSOToken(subject), serviceType,resourceName,
                    actionNames, envParameters, hits.keySet());
                missedThread.setSearchParameter(getSSOToken(adminSubject),
                    misses);
            }

            if (!hits.isEmpty()) {
                hitThread = new SubResources(this,
                    getSSOToken(subject), serviceType, resourceName,
                    actionNames, envParameters, hits);
            }
            Exception exception = null;
            Map<String, PolicyDecision> hitResults = null;
            Map<String, PolicyDecision> missedResults = null;

            synchronized(this) {
                if (hitThread != null) {
                    SMSThreadPool.scheduleTask(hitThread);
                } else {
                    hitResults = Collections.EMPTY_MAP;
                }
                if (missedThread != null) {
                    SMSThreadPool.scheduleTask(missedThread);
                } else {
                    missedResults = Collections.EMPTY_MAP;
                }

                while (true) {
                    if (hitThread != null) {
                        exception = hitThread.getException();
                        hitResults = hitThread.getResults();
                    }
                    if (missedThread != null) {
                        if (exception == null) {
                            exception = missedThread.getException();
                        }
                        missedResults = missedThread.getResults();
                    }

                    if (exception != null) {
                        break;
                    }
                    if ((hitResults != null) && (missedResults != null)) {
                        break;
                    }

                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        //TOFIX
                    }
                }
            }

            if (exception != null) {
                throw new EntitlementException(exception.getMessage(), -1);
            }

            Map<String, PolicyDecision> mergedDecisions = mergePolicyDecisions(
                serviceType, hitResults, missedResults);
            List<Entitlement> results = new ArrayList<Entitlement>();
            for (String res : mergedDecisions.keySet()) {
                results.add(getEntitlement(serviceType, res,
                    mergedDecisions.get(res)));
            }
            return results;
        } catch (SSOException e) {
            throw new EntitlementException(e.getMessage(), -1);
        } catch (PolicyException e) {
            throw new EntitlementException(e.getMessage(), -1);
        }
    }

    private Map<String, PolicyDecision> mergePolicyDecisions(
        ServiceType serviceType,
        Map<String, PolicyDecision> m1,
        Map<String, PolicyDecision> m2
    ) {
        Map<String, PolicyDecision> result = new
            HashMap<String, PolicyDecision>();
        result.putAll(m1);

        for (String r : m2.keySet()) {
            PolicyDecision pd = result.get(r);
            if (pd == null) {
                result.put(r, m2.get(r));
            } else {
                result.put(r, PolicyEvaluator.mergePolicyDecisions(
                    serviceType, pd, m2.get(r)));
            }
        }
        
        return result;
    }

    private Map<String, Set<String>> lookupCache(
        ResourceComp comp,
        Map<Policy, Map<String, Set<String>>> map
    ) {
        Map<String, Set<String>> misses = new HashMap<String, Set<String>>();
        lookupCache(LBL_HOST_IDX, comp.getHostIndexes(), map, misses);
        lookupCache(LBL_PATH_IDX, comp.getPathIndexes(), map, misses);
        Set<String> set = new HashSet<String>();
        set.add(comp.getPath());
        lookupCache(LBL_PATH_PARENT_IDX, set, map, misses);
        return misses;
    }

    private void lookupCache(
        String index,
        Set<String> lookupSet,
        Map<Policy, Map<String, Set<String>>> hits,
        Map<String, Set<String>> misses
    ) {
        IndexCache cache = IndexCache.getInstance();

        for (String resIndex : lookupSet) {
            Set<Object> setCached = null;

            if (index.equals(LBL_HOST_IDX)) {
                setCached = cache.getHostIndex(resIndex);
            } else if (index.equals(LBL_PATH_IDX)) {
                setCached = cache.getPathIndex(resIndex);
            } else {
                setCached = cache.getPathParentIndex(resIndex);
            }

            if (setCached != null) {
                for (Object policy : setCached) {
                    Map<String, Set<String>> indexes = hits.get(policy);
                    if (indexes == null) {
                        indexes = new HashMap<String, Set<String>>();
                        hits.put((Policy)policy, indexes);
                        Set<String> set = set = new HashSet<String>();
                        indexes.put(index, set);
                        set.add(resIndex);
                    } else {
                        Set<String> set = indexes.get(index);
                        if (set == null) {
                            set = new HashSet<String>();
                            indexes.put(index, set);
                        }
                        set.add(resIndex);
                    }
                }
            } else {
                Set<String> set = misses.get(index);
                if (set == null) {
                    set = new HashSet<String>();
                    misses.put(index, set);
                }
                set.add(resIndex);
            }
        }
    }

    private class EvaluatorThread implements Runnable {

        private PolicyEvaluatorAdaptor parent;
        private PolicyDecisionTask.Task task;
        private SSOToken token;
        private ServiceType serviceType;
        private Set<String> actionNames;
        private Map<String, Set<String>> envParameters;

        private EvaluatorThread(
            PolicyEvaluatorAdaptor parent,
            PolicyDecisionTask.Task task,
            SSOToken token,
            ServiceType serviceType,
            Set<String> actionNames,
            Map<String, Set<String>> envParameters
        ) {
            this.parent = parent;
            this.token = token;
            this.task = task;
            this.serviceType = serviceType;
            this.actionNames = actionNames;
            this.envParameters = envParameters;
        }

        public void run() {
            try {
                task.policyDecision = task.policy.getPolicyDecision(
                    token, serviceType.getName(), task.resource,
                    actionNames, envParameters);
            } catch (SSOException e) {
                // TOFIX
            } catch (PolicyException e) {
                // TOFIX
            }
            parent.tasksCount--;

            synchronized (parent) {
                parent.notify();
            }
        }
    }
}
