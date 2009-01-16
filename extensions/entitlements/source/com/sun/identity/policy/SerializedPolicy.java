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
 * $Id: SerializedPolicy.java,v 1.1 2009-01-16 21:02:20 veiming Exp $
 * 
 */

package com.sun.identity.policy;

import com.sun.identity.policy.interfaces.Condition;
import com.sun.identity.policy.interfaces.Referral;
import com.sun.identity.policy.interfaces.ResponseProvider;
import com.sun.identity.policy.interfaces.Subject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class serializes and deserializes 
 * <code>com.sun.identity.policy.Policy</code> object.
 */
public class SerializedPolicy implements Serializable {
    private String originalName;
    private String policyName;
    private String description;
    private boolean referralPolicy;
    private boolean active;
    private int priority;
    private String organizationName;
    
    private Map<String, SerializedReferral> referrals;
    private Map<String, SerializedRule> rules;
    private Map<String, SerializedUser> subjects;
    private Map<String, SerializedCondition> conditions;
    private Map<String, SerializedResponseProvider> responseProviders;
    
    private static final long serialVersionUID = -403250971215465050L;

    public static SerializedPolicy serialize(Policy policy) {
        SerializedPolicy serPolicy = new SerializedPolicy();
        serPolicy.originalName = policy.getOriginalName();
        serPolicy.policyName = policy.getName();
        serPolicy.description = policy.getDescription();
        serPolicy.referralPolicy = policy.isReferralPolicy();
        serPolicy.active = policy.isActive();
        serPolicy.priority = policy.getPriority();
        serPolicy.organizationName = policy.getOrganizationName();
        
        if (serPolicy.referralPolicy) {
            serPolicy.referrals = serializeReferrals(policy);
        } else {
            serPolicy.rules = serializeRules(policy);
            serPolicy.subjects = serializeSubjects(policy);
            serPolicy.conditions = serializeConditions(policy);
            serPolicy.responseProviders = serializeResponseProviders(policy);
        }
        return serPolicy;
    }
    
    public static Map<String, SerializedRule> serializeRules(Policy policy) {
        Map<String, SerializedRule> map = new HashMap<String, SerializedRule>();
        Set ruleNames = policy.getRuleNames();
        for (Iterator i = ruleNames.iterator(); i.hasNext(); ) {
            try {
                String name = (String) i.next();
                Rule rule = policy.getRule(name);
                map.put(name, SerializedRule.serialize(rule));
            } catch (NameNotFoundException ex) {
                //drop the rule.
            }
        }
        return map;
    }
    
    public static Map<String, SerializedUser> serializeSubjects(Policy policy){
        Map <String, SerializedUser> map =
            new HashMap<String, SerializedUser>();
        Set names = policy.getSubjectNames();
        for (Iterator i = names.iterator(); i.hasNext(); ) {
            try {
                String name = (String) i.next();
                Subject subject = policy.getSubject(name);
                boolean isExculsive = policy.isSubjectExclusive(name);
                boolean isRealmSubject = policy.isRealmSubject(name);
                
                map.put(name, SerializedUser.serialize(
                    isExculsive, isRealmSubject, subject));
            } catch (NameNotFoundException ex) {
                //drop the subject
            }
        }
        return map;
    }

    public static Map<String, SerializedCondition> serializeConditions(
        Policy policy
    ){
        Map <String, SerializedCondition> map =
            new HashMap<String, SerializedCondition>();
        Set names = policy.getConditionNames();
        for (Iterator i = names.iterator(); i.hasNext(); ) {
            try {
                String name = (String)i.next();
                Condition condition = policy.getCondition(name);
                map.put(name, SerializedCondition.serialize(condition));
            } catch (NameNotFoundException ex) {
                //drop the condition
            }
        }
        return map;
    }

    public static Map<String, SerializedResponseProvider> 
        serializeResponseProviders(Policy policy
    ){
        Map <String, SerializedResponseProvider> map =
            new HashMap<String, SerializedResponseProvider>();
        Set names = policy.getResponseProviderNames();
        for (Iterator i = names.iterator(); i.hasNext(); ) {
            try {
                String name = (String)i.next();
                ResponseProvider rp = policy.getResponseProvider(name);
                map.put(name, SerializedResponseProvider.serialize(rp));
            } catch (NameNotFoundException ex) {
                //drop the response provider
            }
        }
        return map;
    }
    
    public static Map<String, SerializedReferral> serializeReferrals(
        Policy policy
    ){
        Map <String, SerializedReferral> map =
            new HashMap<String, SerializedReferral>();
        Set names = policy.getReferralNames();
        for (Iterator i = names.iterator(); i.hasNext(); ) {
            try {
                String name = (String)i.next();
                Referral referral = policy.getReferral(name);
                map.put(name, SerializedReferral.serialize(referral));
            } catch (NameNotFoundException ex) {
                //drop the response provider
            }
        }
        return map;
    }
}
