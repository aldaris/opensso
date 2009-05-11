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
 * $Id: PrivilegeUtils.java,v 1.3 2009-05-11 18:30:00 dillidorai Exp $
 */
package com.sun.identity.entitlement.xacml3;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceAttributes;

import com.sun.identity.entitlement.UserSubject;
import com.sun.identity.entitlement.xacml3.core.AllOf;
import com.sun.identity.entitlement.xacml3.core.AnyOf;
import com.sun.identity.entitlement.xacml3.core.AttributeValue;
import com.sun.identity.entitlement.xacml3.core.AttributeDesignator;
import com.sun.identity.entitlement.xacml3.core.EffectType;
import com.sun.identity.entitlement.xacml3.core.Match;
import com.sun.identity.entitlement.xacml3.core.ObjectFactory;
import com.sun.identity.entitlement.xacml3.core.Policy;
import com.sun.identity.entitlement.xacml3.core.Rule;
import com.sun.identity.entitlement.xacml3.core.Target;

import com.sun.identity.entitlement.util.DebugFactory;

import com.sun.identity.shared.debug.IDebug;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Class with utility methods to map from
 * <code>com.sun.identity.entity.Privilege</code>
 * to
 * </code>com.sun.identity.xacml3.core.Policy</code>
 */
public class PrivilegeUtils {
    
    static IDebug debug = DebugFactory.getDebug("Entitlement");

    /**
     * Constructs PrivilegeUtils
     */
    private PrivilegeUtils() {
    }

    public static String toXACML(Privilege privilege) {
        StringWriter stringWriter = new StringWriter();
        Policy policy = privilegeToPolicy(privilege);
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            JAXBContext jaxbContext = JAXBContext.newInstance(
                    "com.sun.identity.entitlement.xacml3.core");
            JAXBElement<Policy> policyElement = objectFactory.createPolicy(policy);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.marshal(policyElement, stringWriter);
        } catch (JAXBException je) {
            //TOODO: handle, propogate exception
            debug.error("JAXBException while mapping privilege to policy:", je);
        }
        return stringWriter.toString();
    }

    public static Policy privilegeToPolicy(Privilege privilege)  {

        /*
         * See entitelement meeting minutes - 22apr09
         *
         * privilege name would map to policy id
         *
         * appliction name would map to application category attribute
         *
         * entitlement resource names would map to xacml policy target
         *
         * entitlement excluded resource names would map to xacml rule target
         *
         * simple one level entitlement subjects (without or, and etc) 
         * would map to policy target
         *
         * all entitlement subjects would also map to xacml rule condition
         *
         * entitlement conditions would map to xacml rule condition
         *
         * entitlement resource attributes would map to rule advice expression
         *
         * at present xacml obligation support is out of scope 
         */

        if (privilege == null) {
            return null;
        }

        Policy policy = new Policy();

        String privilegeName = privilege.getName();
        String applicationName = null; //privilege.getApplicationName();

        String policyId = privilegeNameToPolicyId(privilegeName,
                applicationName);
        policy.setPolicyId(policyId);

        String description = privilege.getDescription();
        policy.setDescription(description);

        // TODO
        //use VariableDefinition to set created-by, created-on
        // last-modified-by, last-modified-on


        // PolicyIssuer policyIssuer = null;

        // Version version = null;

        // Defaults policyDefaults = null;

        // String ruleCombiningAlgId = "rca";

        // XACML Target contains a  list of AnyOf(s)
        // XACML AnyOf contains a list of AllOf(s)
        // XACML AllOf contains a list of Match(s)

        Target target = new Target();
        policy.setTarget(target);

        List<AnyOf> targetAnyOfList = target.getAnyOf();

        EntitlementSubject es = privilege.getSubject();
        List<AnyOf> anyOfSubjectList = entitlementSubjectToAnyOfList(es);
        if (anyOfSubjectList != null) {
            targetAnyOfList.addAll(anyOfSubjectList);
        }

        Entitlement entitlement = privilege.getEntitlement();

        Set<String> resources = entitlement.getResourceNames();


        List<AnyOf> anyOfResourceList = resourceNamesToAnyOfList(resources);
        if (anyOfResourceList != null) {
            targetAnyOfList.addAll(anyOfResourceList);
        }

        AnyOf anyOfApplication = applicationNameToAnyOf(applicationName);
        if (anyOfApplication != null) {
            targetAnyOfList.add(anyOfApplication);
        }

        Map<String, Boolean> actionValues = entitlement.getActionValues();
        List<AnyOf> anyOfActionList 
                = actionNamesToAnyOfList(actionValues.keySet());
        if (anyOfActionList != null) {
            targetAnyOfList.addAll(anyOfActionList);
        }

        // PermitRule, DenyRule
        Set<String> permitActions = new HashSet<String>();
        Set<String> denyActions = new HashSet<String>();
        if (actionValues != null) {
            Set<String> actionNames = actionValues.keySet();
            for(String actionName : actionNames) {
                if (Boolean.TRUE.equals(actionValues.get(actionName))) {
                    permitActions.add(actionName);
                } else {
                    denyActions.add(actionName);
                }
            }
        }

        Set<String> excludedResources = entitlement.getExcludedResourceNames();
        List<AnyOf> anyOfExcludedResourceList = excludedResourceNamesToAnyOfList(
                excludedResources);

        EntitlementCondition ec = privilege.getCondition();

        Set<ResourceAttributes> ra = privilege.getResourceAttributes();

        if (!permitActions.isEmpty()) {
            Rule permitRule = new Rule();
            permitRule.setRuleId("permit-rule");
            permitRule.setDescription("permit-description");
            permitRule.setEffect(EffectType.PERMIT);
            Target permitTarget = new Target();
            permitRule.setTarget(permitTarget);
            List<AnyOf> permitTargetAnyOfList = permitTarget.getAnyOf();
            if (anyOfExcludedResourceList != null) {
                permitTargetAnyOfList.addAll(anyOfExcludedResourceList);
            }
            List<AnyOf> anyOfPermitActionList = actionNamesToAnyOfList(permitActions);
            if (anyOfPermitActionList != null) {
                permitTargetAnyOfList.addAll(anyOfPermitActionList);
            }
            List<Object> vrList 
                = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
            vrList.add(permitRule);
            
        }

        if (!denyActions.isEmpty()) {
            Rule denyRule = new Rule();
            denyRule.setRuleId("deny-rule");
            denyRule.setDescription("deny-description");
            denyRule.setEffect(EffectType.DENY);
            Target denyTarget = new Target();
            denyRule.setTarget(denyTarget);
            List<AnyOf> denyTargetAnyOfList = denyTarget.getAnyOf();
            if (anyOfExcludedResourceList != null) {
                denyTargetAnyOfList.addAll(anyOfExcludedResourceList);
            }
            List<AnyOf> anyOfDenyActionList = actionNamesToAnyOfList(denyActions);
            if (anyOfDenyActionList != null) {
                denyTargetAnyOfList.addAll(anyOfDenyActionList);
            }
            List<Object> vrList 
                = policy.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
            vrList.add(denyRule);
        }


        return policy;
    }

    public static String privilegeNameToPolicyId(String privilegeName,
            String applicationName) {
        return privilegeName;
    }

    public static List<AnyOf> entitlementSubjectToAnyOfList(
            EntitlementSubject es) {
        if (es == null) {
            return null;
        }
        List<AnyOf> anyOfList = new ArrayList<AnyOf>();
        AnyOf anyOf = new AnyOf();
        anyOfList.add(anyOf);
        List<AllOf> allOfList = anyOf.getAllOf();
        AllOf allOf = new AllOf();
        allOfList.add(allOf);
        List<Match> matchList = allOf.getMatch();
        if (es instanceof UserSubject) {
            UserSubject us = (UserSubject)es;
            String userId = us.getID();

            Match match = new Match();
            matchList.add(match);
            match.setMatchId("subject-match-id");

            AttributeValue attributeValue = new AttributeValue();
            String dataType = "datatype";
            attributeValue.setDataType(dataType);
            attributeValue.getContent().add(userId);

            AttributeDesignator attributeDesignator = new AttributeDesignator();
            String category = "category";
            attributeDesignator.setCategory(category);
            String attributeId = "attributeId";
            attributeDesignator.setAttributeId(attributeId);
            String dt = "dataType";
            attributeDesignator.setDataType(dt);
            String issuer = "issuer";
            attributeDesignator.setIssuer(issuer);
            boolean mustBePresent = true;
            attributeDesignator.setMustBePresent(mustBePresent);

            match.setAttributeValue(attributeValue);
            match.setAttributeDesignator(attributeDesignator);
        }
        return anyOfList;
    }


    public static List<AnyOf> resourceNamesToAnyOfList(Set<String> resourceNames) {
        if (resourceNames == null || resourceNames.isEmpty()) {
            return null;
        }
        List<AnyOf> anyOfList = new ArrayList<AnyOf>();
        AnyOf anyOf = new AnyOf();
        anyOfList.add(anyOf);
        List<AllOf> allOfList = anyOf.getAllOf();
        for (String resourceName : resourceNames) {
            AllOf allOf = new AllOf();
            List<Match> matchList = allOf.getMatch();
            matchList.add(resourceNameToMatch(resourceName));
            allOfList.add(allOf);
        }
        return anyOfList;
    }

    public static AnyOf applicationNameToAnyOf(String applicationName) {
        AnyOf anyOf = new AnyOf();
        return anyOf;
    }

    public static List<AnyOf> actionNamesToAnyOfList(
            Set<String> actionNames) {
        if (actionNames == null || actionNames.isEmpty()) {
            return null;
        }
        List<AnyOf> anyOfList = new ArrayList<AnyOf>();
        AnyOf anyOf = new AnyOf();
        anyOfList.add(anyOf);
        List<AllOf> allOfList = anyOf.getAllOf();
        for (String actionName : actionNames) {
            AllOf allOf = new AllOf();
            List<Match> matchList = allOf.getMatch();
            matchList.add(actionNameToMatch(actionName));
            allOfList.add(allOf);
        }
        return anyOfList;
    }

    public static List<AnyOf> excludedResourceNamesToAnyOfList(
            Set<String> excludedResources) {
        List<AnyOf> anyOfList = new ArrayList<AnyOf>();
        return anyOfList;
    }

    public static Match resourceNameToMatch(String resourceName) {
        if (resourceName == null | resourceName.length() == 0) {
            return null;
        }

        Match match = new Match();
        String matchId = "matchId";
        match.setMatchId(matchId);

        AttributeValue attributeValue = new AttributeValue();
        String dataType = "datatype";
        attributeValue.setDataType(dataType);
        attributeValue.getContent().add(resourceName);

        AttributeDesignator attributeDesignator = new AttributeDesignator();
        String category = "category";
        attributeDesignator.setCategory(category);
        String attributeId = "attributeId";
        attributeDesignator.setAttributeId(attributeId);
        String dt = "dataType";
        attributeDesignator.setDataType(dt);
        String issuer = "issuer";
        attributeDesignator.setIssuer(issuer);
        boolean mustBePresent = true;
        attributeDesignator.setMustBePresent(mustBePresent);

        match.setAttributeValue(attributeValue);
        match.setAttributeDesignator(attributeDesignator);

        return match;
    }

    public static Match actionNameToMatch(String actionName) {
        if (actionName == null | actionName.length() == 0) {
            return null;
        }

        Match match = new Match();
        String matchId = "matchId";
        match.setMatchId(matchId);

        AttributeValue attributeValue = new AttributeValue();
        String dataType = "datatype";
        attributeValue.setDataType(dataType);
        attributeValue.getContent().add(actionName);

        AttributeDesignator attributeDesignator = new AttributeDesignator();
        String category = "category";
        attributeDesignator.setCategory(category);
        String attributeId = "attributeId";
        attributeDesignator.setAttributeId(attributeId);
        String dt = "dataType";
        attributeDesignator.setDataType(dt);
        String issuer = "issuer";
        attributeDesignator.setIssuer(issuer);
        boolean mustBePresent = true;
        attributeDesignator.setMustBePresent(mustBePresent);

        match.setAttributeValue(attributeValue);
        match.setAttributeDesignator(attributeDesignator);

        return match;
    }

}
