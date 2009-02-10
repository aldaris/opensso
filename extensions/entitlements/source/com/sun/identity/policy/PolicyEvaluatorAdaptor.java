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
 * $Id: PolicyEvaluatorAdaptor.java,v 1.16 2009-02-10 19:31:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPolicyEvaluator;
import com.sun.identity.entitlement.util.ResourceComp;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
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
    int tasksCount;
    static final String LBL_HOST_IDX = "host";
    static final String LBL_PATH_IDX = "path";
    static final String LBL_PATH_PARENT_IDX = "pathparent";
    
    public PolicyEvaluatorAdaptor() {
    }

    Set<Policy> recursiveSearch(
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
    
    private static Entitlement getEntitlement(
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
    
    private static PolicyDecision mergePolicyDecisions(
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
    ) throws SSOException, PolicyException, EntitlementException {
        Set<PolicyDecision> policyDecisions = new HashSet<PolicyDecision>();
        Set<PolicyDecisionTask.Task> results = performPolicyEvaluation(
            adminSubject, subject, serviceTypeName, resourceName, envParameters
            );
        for (PolicyDecisionTask.Task t : results) {
            policyDecisions.add(t.policyDecision);
        }
        return policyDecisions;
    }

    protected Set<PolicyDecisionTask.Task> performPolicyEvaluation(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws SSOException, PolicyException, EntitlementException {
        Set<PolicyDecisionTask.Task> results = new
            HashSet<PolicyDecisionTask.Task>();
        ServiceType serviceType =
            ServiceTypeManager.getServiceTypeManager().getServiceType(
            serviceTypeName);
        SSOToken token = getSSOToken(subject);

        ResourceComp comp = ResourceNameSplitter.split(resourceName);
        Set<Policy> hits = new HashSet<Policy>();
        Map<String, Set<String>> misses = lookupCache(comp, hits);

        MissedEvaluatorThread missedThread = null;
        EvaluatorThread hitThread = null;

        if (!misses.isEmpty()) {
            missedThread = new MissedEvaluatorThread(this,
                token, serviceType, resourceName, envParameters, hits);
            missedThread.setSearchParameter(getSSOToken(adminSubject),
                misses);
        }
        if (!hits.isEmpty()) {
            hitThread = new EvaluatorThread(
                this, hits, token, serviceType, resourceName, envParameters);
        }

        Exception exception = null;
        Set<PolicyDecisionTask.Task> hitResults = null;
        Set<PolicyDecisionTask.Task> missedResults = null;

        synchronized(this) {
            if (hitThread != null) {
                SMSThreadPool.scheduleTask(hitThread);
            } else {
                hitResults = Collections.EMPTY_SET;
            }
            if (missedThread != null) {
                SMSThreadPool.scheduleTask(missedThread);
            } else {
                missedResults = Collections.EMPTY_SET;
            }

            while (true) {
                if (hitThread != null) {
                    exception = hitThread.getException();
                    hitResults = hitThread.getPolicyDecisions();
                }
                if (missedThread != null) {
                    if (exception == null) {
                        exception = missedThread.getException();
                    }
                    missedResults = missedThread.getPolicyDecisions();
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

        results.addAll(hitResults);
        results.addAll(missedResults);
        return results;
    }
    
    private List<Entitlement> getSubTreeEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        try {
            Set<PolicyDecisionTask.Task> tasks = performSubTreeEvaluation(
                adminSubject, subject, serviceTypeName, resourceName,
                envParameters);
            ServiceType serviceType =
                ServiceTypeManager.getServiceTypeManager().getServiceType(
                serviceTypeName);
            Map<String, PolicyDecision> mergedDecisions = mergePolicyDecisions(
                serviceType, tasks);
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

    protected Set<PolicyDecisionTask.Task> performSubTreeEvaluation(
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
            Set<Policy> hits = new HashSet<Policy>();
            Map<String, Set<String>> misses = lookupCache(comp, hits);
            MissedSubResources missedThread = null;
            SubResources hitThread = null;

            if (!misses.isEmpty()) {
                missedThread = new MissedSubResources(this,
                    getSSOToken(subject), serviceType,resourceName,
                    actionNames, envParameters, hits);
                missedThread.setSearchParameter(getSSOToken(adminSubject),
                    misses);
            }

            if (!hits.isEmpty()) {
                hitThread = new SubResources(this,
                    getSSOToken(subject), serviceType, resourceName,
                    actionNames, envParameters, hits);
            }
            Exception exception = null;
            Set<PolicyDecisionTask.Task> hitResults = null;
            Set<PolicyDecisionTask.Task> missedResults = null;

            synchronized(this) {
                if (hitThread != null) {
                    SMSThreadPool.scheduleTask(hitThread);
                } else {
                    hitResults = Collections.EMPTY_SET;
                }
                if (missedThread != null) {
                    SMSThreadPool.scheduleTask(missedThread);
                } else {
                    missedResults = Collections.EMPTY_SET;
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

            Set<PolicyDecisionTask.Task> results = new
                HashSet<PolicyDecisionTask.Task>();
            results.addAll(hitResults);
            results.addAll(missedResults);
            return results;
        } catch (SSOException e) {
            throw new EntitlementException(e.getMessage(), -1);
        } catch (PolicyException e) {
            throw new EntitlementException(e.getMessage(), -1);
        }
    }

    private Map<String, PolicyDecision> mergePolicyDecisions(
        ServiceType serviceType,
        Set<PolicyDecisionTask.Task> tasks
    ) {
        Map<String, PolicyDecision> result = new
            HashMap<String, PolicyDecision>();
        for (PolicyDecisionTask.Task t : tasks) {
            String res = t.resource;
            PolicyDecision pd = result.get(res);
            if (pd == null) {
                result.put(res, t.policyDecision);
            } else {
                result.put(res, PolicyEvaluator.mergePolicyDecisions(
                    serviceType, pd, t.policyDecision));
            }
        }
        
        return result;
    }

    private Map<String, Set<String>> lookupCache(
        ResourceComp comp,
        Set<Policy> map
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
        Set<Policy> hits,
        Map<String, Set<String>> misses
    ) {
        IIndexCache cache = getIndexCache();

        for (String resIndex : lookupSet) {
            Set<Policy> setCached = null;

            if (index.equals(LBL_HOST_IDX)) {
                setCached = cache.getHostIndex(resIndex);
            } else if (index.equals(LBL_PATH_IDX)) {
                setCached = cache.getPathIndex(resIndex);
            } else {
                setCached = cache.getPathParentIndex(resIndex);
            }

            if (setCached != null) {
                hits.addAll(setCached);
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

    IIndexCache getIndexCache() {
        return IndexCache.getInstance();
    }
}
