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
 * $Id: PolicyEvaluatorAdaptor.java,v 1.2 2009-01-22 23:59:35 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.IPolicyEvaluator;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

public class PolicyEvaluatorAdaptor implements IPolicyEvaluator {

    private Set<Policy> search(Subject adminSubject, String resourceName)
        throws SSOException, PolicyException {
        PolicyManager pm = new PolicyManager(getSSOToken(adminSubject), "/");

        try {
            URL url = new URL(resourceName);
            return PolicyIndexer.search(pm,
                ResourceNameSplitter.splitHost(url),
                ResourceNameSplitter.splitPath(url));
        } catch (MalformedURLException e) {
            Set<String> set = new HashSet<String>();
            set.add(resourceName);
            return PolicyIndexer.search(pm, set, Collections.EMPTY_SET);
        }
    }

    public boolean isAllowed(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        Entitlement entitlement
    ) throws EntitlementException {
        try {
            String resourceName = entitlement.getResourceName();
            Set<Policy> policies = search(adminSubject, resourceName);
            if ((policies == null) || policies.isEmpty()) {
                return false;
            }

            Map<String, Object> actionValues = entitlement.getActionValues();
            SSOToken ssoToken = getSSOToken(subject);
            Set<PolicyDecision> policyDecisions = new HashSet<PolicyDecision>();
            for (Policy p : policies) {
                PolicyDecision pd =  p.getPolicyDecision(
                    ssoToken, serviceTypeName, resourceName, 
                    actionValues.keySet(), Collections.EMPTY_MAP);
                if (pd != null) {
                    policyDecisions.add(pd);
                }
            }
            
            if ((policyDecisions == null) || policyDecisions.isEmpty()) {
                return false;
            }
            
            PolicyDecision target = null;
            ServiceTypeManager stm = ServiceTypeManager.getServiceTypeManager();
            ServiceType serviceType = stm.getServiceType(serviceTypeName);
            for (PolicyDecision pd : policyDecisions) {
                if (target == null) {
                    target = pd;
                } else {
                    target = PolicyEvaluator.mergePolicyDecisions(
                        serviceType, pd, target);
                }
            }
            
            return doesActionDecisionMatch(target, actionValues);
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
        Map decisionsMap = pd.getActionDecisions();
        if (decisionsMap != null) {
            for (String actionName : actionValues.keySet()) {
                Object expected = actionValues.get(actionName);
                ActionDecision decision = (ActionDecision)decisionsMap.get(
                    actionName);
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
        Set principals = subject.getPrincipals();
        if (!principals.isEmpty()) {
            try {
            String tokenId = (String)principals.iterator().next();
            SSOTokenManager mgr = SSOTokenManager.getInstance();
            return mgr.createSSOToken(tokenId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
