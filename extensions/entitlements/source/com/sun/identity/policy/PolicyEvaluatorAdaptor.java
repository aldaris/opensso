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
 * $Id: PolicyEvaluatorAdaptor.java,v 1.9 2009-01-29 02:04:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.DataStoreEntry;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPolicyEvaluator;
import com.sun.identity.entitlement.util.ResourceComp;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
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

    private Set<Policy> search(Subject adminSubject, String resourceName)
        throws SSOException, PolicyException {
        PolicyManager pm = new PolicyManager(getSSOToken(adminSubject), "/");
        ResourceComp comp = ResourceNameSplitter.split(resourceName);
        return PolicyIndexer.search(pm, comp.getHostIndexes(),
            comp.getPathIndexes());
    }

    private Set<DataStoreEntry> recursiveSearch(
        Subject adminSubject, 
        String resourceName
    )
        throws SSOException, PolicyException {
        PolicyManager pm = new PolicyManager(getSSOToken(adminSubject), "/");
        ResourceComp comp = ResourceNameSplitter.split(resourceName);
        return PolicyIndexer.search(pm, comp.getHostIndexes(),
            comp.getPathIndexes(), comp.getPath());
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
        Set<String> actionNames = serviceType.getActionNames();
        SSOToken ssoToken = getSSOToken(subject);
        Set<PolicyDecision> policyDecisions = new HashSet<PolicyDecision>();
        for (Policy p : policies) {
            PolicyDecision pd = p.getPolicyDecision(
                ssoToken, serviceTypeName, resourceName,
                actionNames, envParameters);
            if (pd != null) {
                policyDecisions.add(pd);
            }
        }

        return (policyDecisions != null) ? policyDecisions : 
            Collections.EMPTY_SET;
    }

     public List<Entitlement> getSubTreeEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {  
        try {
            Set<DataStoreEntry> entries = recursiveSearch(
                adminSubject, resourceName);
            ServiceType serviceType =
                ServiceTypeManager.getServiceTypeManager().getServiceType(
                serviceTypeName);
            Set<String> actionNames = serviceType.getActionNames();
            SubResources subResources = new SubResources();
            return subResources.evaluate(getSSOToken(subject), serviceType,
                resourceName, actionNames, envParameters, entries);
        } catch (SSOException e) {
            throw new EntitlementException(e.getMessage(), -1);
        } catch (PolicyException e) {
            throw new EntitlementException(e.getMessage(), -1);
        }            
     }
}
