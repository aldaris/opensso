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
 * $Id: PolicyEvaluatorTest.java,v 1.10 2009-02-12 05:33:13 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.server.AuthSPrincipal;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.SimulatedResult;
import com.sun.identity.policy.interfaces.Subject;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author dennis
 */
public class PolicyEvaluatorTest {
    private static String POLICY_NAME1 = "PolicyEvaluatorTestP1";
    private static String POLICY_NAME2 = "PolicyEvaluatorTestP2";
    private static String POLICY_NAME3 = "PolicyEvaluatorTestP3";

    private static String URL_RESOURCE1 = "http://www.*.com:8080/private";
    private static String URL_RESOURCE2 = "http://www.sun.com:8080/private";
    private static String URL_RESOURCE3 = "http://www.ibm.com:8080/private";

    @BeforeClass
    public void setup() throws PolicyException, SSOException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        Policy policy = new Policy(POLICY_NAME1, "test1 - discard",
            false, true);
        policy.addRule(createRule1());
        policy.addSubject("group", createSubject(pm));
        pm.addPolicy(policy);

        policy = new Policy(POLICY_NAME2, "test2 - discard",
            false, true);
        policy.addRule(createRule2());
        policy.addSubject("group2", createSubject(pm));
        pm.addPolicy(policy);

        policy = new Policy(POLICY_NAME3, "test3 - discard",
            false, true);
        policy.addRule(createRule3());
        policy.addSubject("group3", createSubject(pm));
        pm.addPolicy(policy);
    }
    
    @AfterClass
    public void cleanup() throws PolicyException, SSOException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        pm.removePolicy(POLICY_NAME1);
        pm.removePolicy(POLICY_NAME2);
        pm.removePolicy(POLICY_NAME3);
    }
    
    @Test
    public void testIsAllowed() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());

        PolicyEvaluator pe = new PolicyEvaluator("iPlanetAMWebAgentService");
        if (!pe.isAllowed(adminToken, "http://www.sun.com:8080/private", "GET")){
            throw new Exception("testIsAllowed" +
                "http://www.sun.com:8080/private evaluation failed");
        }
        
        //negative test
        if (pe.isAllowed(adminToken, "http://www.sun.com:8080/public", "GET")){
            throw new Exception("testIsAllowed" +
                "http://www.sun.com:8080/public evaluation failed");
        }
    }
    
    @Test
    public void testResourceSelf() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());

        PolicyEvaluator pe = new PolicyEvaluator("iPlanetAMWebAgentService");
        Set<ResourceResult> resResults = pe.getResourceResults(adminToken, 
            "http://www.sun.com:8080/private",
            ResourceResult.SELF_SCOPE, Collections.EMPTY_MAP);
        ResourceResult resResult = resResults.iterator().next();
        PolicyDecision pd = resResult.getPolicyDecision();
        Map<String, ActionDecision> decisions = pd.getActionDecisions();
        ActionDecision ad = decisions.get("GET");
        if (!ad.getValues().contains("allow")) {
            throw new Exception("testResourceSelf: " +
                "http://www.sun.com:8080/private evaluation failed");
        }
        ad = decisions.get("POST");
        if (!ad.getValues().contains("deny")) {
            throw new Exception("testResourceSelf: " +
                "http://www.sun.com:8080/private evaluation failed");
        }
    }
    
    @Test
    public void testResourceSubTree() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());

        PolicyEvaluator pe = new PolicyEvaluator("iPlanetAMWebAgentService");
        Set<ResourceResult> resResults = pe.getResourceResults(adminToken, 
            "http://www.sun.com:8080/private",
            ResourceResult.SUBTREE_SCOPE, Collections.EMPTY_MAP);
        if (resResults.size() != 2) {
            throw new Exception("testResourceSubTree: failed");
        }
    }

    private SimulationEvaluator createSimulator(
        javax.security.auth.Subject subject
    ) throws Exception {
        SimulationEvaluator eval = new SimulationEvaluator(
            subject, "iPlanetAMWebAgentService");
        eval.setResource("http://www.sun.com:8080/private");
        eval.setSubject(subject);
        Set<String> policyNames = new HashSet<String>();
        policyNames.add(POLICY_NAME1);
        policyNames.add(POLICY_NAME2);
        policyNames.add(POLICY_NAME3);
        eval.setPolicies(policyNames);
        return eval;
    }

    @Test
    public void testSimulationSelf() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        javax.security.auth.Subject subject = createSubject(adminToken);
        SimulationEvaluator eval = createSimulator(subject);
        eval.evaluate(false);
        List<SimulatedResult> details = eval.getSimulatedResults();
        for (SimulatedResult r : details) {
            String policyName = r.getPrivilegeName();
            if (policyName.equals(POLICY_NAME1)) {
                if (!r.isApplicable()) {
                    throw new Exception("testSimulationSelf: failed");
                }
            } else if (policyName.equals(POLICY_NAME2)) {
                if (!r.isApplicable()) {
                    throw new Exception("testSimulationSelf: failed");
                }
            } else if (policyName.equals(POLICY_NAME3)) {
                if (r.isApplicable()) {
                    throw new Exception("testSimulationSelf: failed");
                }
            }
        }
        List<Entitlement> results = eval.getEntitlements();
        for (Entitlement ent : results) {
            String res = ent.getResourceName();
            if (!res.equals("http://www.sun.com:8080/private")) {
                throw new Exception(
                    "testSimulationSelf: failed, resource name is incorrect.");
            }
            Map<String, Object> actionValues = ent.getActionValues();
            validateActionValues(actionValues, "GET", "allow");
            validateActionValues(actionValues, "POST", "deny");
        }
    }

    private void validateActionValues(
        Map<String, Object> actionValues,
        String key,
        String value
    ) throws Exception {
        Set setGet = (Set) actionValues.get(key);
        String strGet = (String) setGet.iterator().next();
        if (!strGet.equals(value)) {
            throw new Exception(
                "testSimulationSelf: failed, " + key + " result is incorrect.");
        }
    }
    
    @Test
    public void testSimulationSubTree() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        javax.security.auth.Subject subject = createSubject(adminToken);
        SimulationEvaluator eval = createSimulator(subject);
        eval.evaluate(true);
        List<SimulatedResult> details = eval.getSimulatedResults();
        for (SimulatedResult r : details) {
            System.out.println(r.getEntitlement().getResourceName());
        }
        List<Entitlement> results = eval.getEntitlements();
        for (Entitlement ent : results) {
            String res = ent.getResourceName();
            if (res.equals(URL_RESOURCE1)) {
                Map<String, Object> actionValues = ent.getActionValues();
                validateActionValues(actionValues, "GET", "allow");
                validateActionValues(actionValues, "POST", "deny");
            } else if (res.equals(URL_RESOURCE2)) {
                Map<String, Object> actionValues = ent.getActionValues();
                validateActionValues(actionValues, "GET", "allow");
            }
            System.out.println(res);
        }
    }

    private Rule createRule1() throws PolicyException {
        Map<String, Set<String>> actionValues = 
            new HashMap<String, Set<String>>();
        {
            Set<String> set = new HashSet<String>();
            set.add("allow");
            actionValues.put("GET", set);
        }
        {
            Set<String> set = new HashSet<String>();
            set.add("deny");
            actionValues.put("POST", set);
        }
        
        return new Rule("rule1", "iPlanetAMWebAgentService",
            URL_RESOURCE1, actionValues);
    }
    
    private Rule createRule2() throws PolicyException {
        Map<String, Set<String>> actionValues =
            new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
            set.add("allow");
        actionValues.put("POST", set);

        return new Rule("rule2", "iPlanetAMWebAgentService",
            URL_RESOURCE2, actionValues);
    }

    private Rule createRule3() throws PolicyException {
        Map<String, Set<String>> actionValues =
            new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
            set.add("allow");
        actionValues.put("POST", set);

        return new Rule("rule3", "iPlanetAMWebAgentService",
            URL_RESOURCE3, actionValues);
    }

    private Subject createSubject(PolicyManager pm) throws PolicyException {
        SubjectTypeManager mgr = pm.getSubjectTypeManager();
        Subject subject = mgr.getSubject("AuthenticatedUsers");
        Set<String> set = new HashSet<String>();
        subject.setValues(set);
        return subject;
    }


    private javax.security.auth.Subject createSubject(SSOToken token) {
        Principal userP = new AuthSPrincipal(token.getTokenID().toString());
        Set userPrincipals = new HashSet(2);
        userPrincipals.add(userP);
        return new javax.security.auth.Subject(true, userPrincipals,
            Collections.EMPTY_SET, Collections.EMPTY_SET);
    }

}
