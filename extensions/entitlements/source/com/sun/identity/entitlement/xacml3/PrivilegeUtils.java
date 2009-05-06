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
 * $Id: PrivilegeUtils.java,v 1.1 2009-05-06 22:35:23 dillidorai Exp $
 */
package com.sun.identity.entitlement.xacml3;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.sun.identity.entitlement.AndSubject;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.OrSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceAttributes;
import com.sun.identity.entitlement.StaticAttributes;
import com.sun.identity.entitlement.UserAttributes;
import com.sun.identity.entitlement.xacml3.core.AllOf;
import com.sun.identity.entitlement.xacml3.core.AnyOf;
import com.sun.identity.entitlement.xacml3.core.AttributeValue;
import com.sun.identity.entitlement.xacml3.core.AttributeDesignator;
import com.sun.identity.entitlement.xacml3.core.Match;
import com.sun.identity.entitlement.xacml3.core.Policy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Class with utility methods to map from
 * <code>com.sun.identity.entity.Privilege</code>
 * to
 * </code>com.sun.identity.xacml3.Policy</code>
 */
public class PrivilegeUtils {

    /**
     * Constructs PrivilegeUtils
     */
    private PrivilegeUtils() {
    }

    public static String toXACML(Privilege privilege) {
        return null;
    }

    public static Policy privilegeToPolicy(Privilege privilege)
            throws SSOException {

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

        String privilegeName = privilege.getName();
        String applicationName = null; //privilege.getApplicationName();
        String description = null; //privilege.getDescription();
        // PolicyIssuer policyIssuer = null;
        // Version version = null;
        // Defaults policyDefaults = null;
        // String ruleCombiningAlgId = "rca";
        // Target target = null;

        Entitlement entitlement = privilege.getEntitlement();

        Set<String> resources = entitlement.getResourceNames();
        Set<String> excludedResources = entitlement.getExcludedResourceNames();

        Map<String, Boolean> actionValues = entitlement.getActionValues();

        // XACML Target ontains a  list of AnyOf(s)
        // XACML AnyOf contains a list of AllOf(s)
        // XACML AllOf contains a list of Match(s)

        String policyId = privilegeNameToPolicyId(privilegeName,
                applicationName);

        AnyOf anyOfApplication = applicationNameToAnyOf(applicationName);
        if (anyOfApplication != null) {
        }

        List<AnyOf> anyOfResources = resourceNamesToAnyOf(resources);
        if (anyOfResources != null) {
        }

        List<AnyOf> anyOfActionValues = actionValuesToAnyOf(actionValues);
        if (anyOfResources != null) {
        }

        EntitlementSubject es = privilege.getSubject();
        List<AnyOf> anyOfSubjects = entitlementSubjectToAnyOf(es);
        if (anyOfSubjects != null) {
        }

        // PermitRule, DenyRule
        List permitActions = null; // effect: Permit
        List denyActions = null; // effect: Deny

        List<AnyOf> anyOfExcludedResources = excludedResourceNamesToAnyOf(
                excludedResources);
        if (anyOfResources != null) {
        }

        EntitlementCondition ec = privilege.getCondition();

        Set<ResourceAttributes> ra = privilege.getResourceAttributes();

        Policy policy = new Policy();

        return policy;
    }

    public static AnyOf applicationNameToAnyOf(String applicationName) {
        return null;
    }

    public static String privilegeNameToPolicyId(String privilegeName,
            String applicationName) {
        return null;
    }

    public static List<AnyOf> resourceNamesToAnyOf(Set<String> resourceNames) {
        if (resourceNames == null || resourceNames.isEmpty()) {
            return null;
        }
        List<AnyOf> anyOfResources = new ArrayList<AnyOf>();
        for (String resourceName : resourceNames) {
            List<AllOf> allOfResources = new ArrayList<AllOf>();
            AllOf allOfResource = new AllOf();
            allOfResource.getMatch().add(resourceNameToMatch(resourceName));
        }
        return null;
    }

    public static Match resourceNameToMatch(String resourceName) {
        if (resourceName == null) {
            return null;
        }

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

        Match match = new Match();
        String matchId = "matchId";
        match.setMatchId(matchId);
        match.setAttributeValue(attributeValue);
        match.setAttributeDesignator(attributeDesignator);

        return match;
    }

    public static List<AnyOf> excludedResourceNamesToAnyOf(
            Set<String> excludedResources) {
        return null;
    }

    public static List<AnyOf> actionValuesToAnyOf(
            Map<String, Boolean> actionValues) {
        return null;
    }

    public static List<AnyOf> entitlementSubjectToAnyOf(
            EntitlementSubject entitlementSubject) {
        return null;
    }
    
}
