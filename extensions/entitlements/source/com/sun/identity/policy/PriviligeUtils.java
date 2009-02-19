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
 * at opensso/legal/CDDLv1.0.txt
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: PriviligeUtils.java,v 1.3 2009-02-19 02:09:14 dillidorai Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.ECondition;
import com.sun.identity.entitlement.EResourceAttributes;
import com.sun.identity.entitlement.ESubject;
import com.sun.identity.entitlement.Privilige;
import com.sun.identity.policy.interfaces.Condition;
import com.sun.identity.policy.interfaces.ResponseProvider;
import com.sun.identity.policy.interfaces.Subject;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

/**
 * Class with utility methods to map from
 * <code>com.sun.identity.entity.Privilige</code>
 * to
 * </code>com.sun.identity.policy.Policy</code>
 */
public class PriviligeUtils {

    private PriviligeUtils() {
    }

    static Privilige policyToPrivilige(Policy policy) 
            throws PolicyException {

        if (policy == null) {
            return null;
        }

        String name = policy.getName();

        Set ruleNames = policy.getRuleNames();
        Set<Entitlement> entitlements = new HashSet<Entitlement>();
        for (Object ruleNameObj : ruleNames) {
            String ruleName = (String)ruleNameObj;
            Rule rule = policy.getRule(ruleName);
            Entitlement entitlement = ruleToEntitlement(rule);
            entitlements.add(entitlement);
        }

        Set subjectNames = policy.getSubjectNames();
        for (Object subjectNameObj : subjectNames) {
            String subjectName = (String)subjectNameObj;
            Subject subject = policy.getSubject(subjectName);
            String subjectClass = subject.getClass().getName();
            Set subjectValues = subject.getValues();
            boolean exclusive = policy.isSubjectExclusive(subjectName);
        }

        Set conditionNames = policy.getConditionNames();
        for (Object conditionNameObj : conditionNames) {
            String conditionName = (String)conditionNameObj;
            Condition condition = policy.getCondition(conditionName);
            String conditionClass = condition.getClass().getName();
            Map conditionProperties = condition.getProperties();
        }


        Set responseProviderNames = policy.getResponseProviderNames();
        for (Object responseProviderNameObj : responseProviderNames) {
            String responseProviderName = (String)responseProviderNameObj;
            ResponseProvider responseProvider 
                    = policy.getResponseProvider(responseProviderName);
            String responseProviderClass = responseProvider.getClass().getName();
            Map responseProviderProperties = responseProvider.getProperties();
        }

        return null;
    }

    static Policy priviligeToPolicy(Privilige privilige) 
            throws PolicyException {
        Policy policy = null;
        policy = new Policy(privilige.getName());
        Set<Entitlement> entitlements = privilige.getEntitlements();
        for(Entitlement entitlement: entitlements) {
            Rule rule = entitlementToRule(entitlement);
            policy.addRule(rule);
        }
        return policy;
    }

    static Entitlement ruleToEntitlement(Rule rule)
            throws PolicyException {
        String serviceName = rule.getServiceTypeName();
        String resourceName = rule.getResourceName();
        Map<String, Object> actionMap 
                = new HashMap<String, Object>();
        Set actionNames = rule.getActionNames();
        for (Object actionNameObj : actionNames) {
            String actionName = (String)actionNameObj;
            Set actionValues = rule.getActionValues(actionName);
            actionMap.put(actionName, actionValues);
        }
        return new Entitlement(serviceName, resourceName, actionMap);
    }

    static Rule entitlementToRule(Entitlement entitlement)
            throws PolicyException {
        return null;
    }

    static ESubject pSubjectsToESubject() {
        return null;
    }

    static Set<Subject> eSubjectToPSubjects() {
        return null;
    }

    static ECondition pConditionsToECondition() {
     return null;
    }

    static Set<Condition> eConditionToPConditions() {
     return null;
    }

    static Set<EResourceAttributes> ResponseProvidersToEResourceAttributes() {
     return null;
    }

    static Set<ResponseProvider> EResourceAttributesToResponseProviders() {
     return null;
    }

 
    
}
