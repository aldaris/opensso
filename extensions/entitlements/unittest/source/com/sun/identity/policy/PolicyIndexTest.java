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
 * $Id: PolicyIndexTest.java,v 1.4 2009-01-22 07:54:46 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.policy.interfaces.Subject;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

/**
 * Test the policy indexing. store and retrieving of policy object.
 * @author dennis
 */
public class PolicyIndexTest {
    private static String URL_RESOURCE = "http://www.sun.com:8080/private";
        
    @Test
    public void storeAndRetrieve() 
        throws SSOException, PolicyException, EntitlementException, Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        Policy policy = new Policy("policyTest1", "test - discard",
            false, true);
        policy.addRule(createRule());
        policy.addSubject("group", createSubject(pm));
        pm.addPolicy(policy);
        
        policy = pm.getPolicy("policyTest1");
        PolicyIndexer.store(policy);
        
        Set<String> hostIndexes = new HashSet<String>();
        Set<String> pathIndexes = new HashSet<String>();
        hostIndexes.add("http://www.sun.com");
        pathIndexes.add("/private");
        Set<Policy> result = PolicyIndexer.search(pm, hostIndexes, pathIndexes);
        Policy resultPolicy = (Policy)result.iterator().next();
        Rule rule = resultPolicy.getRule("rule1");
        if (!rule.getResourceName().equals(URL_RESOURCE)) {
            throw new Exception("incorrect deserialized policy");
        }
        PolicyIndexer.delete(policy);
        pm.removePolicy("policyTest1");
    }
    
    private Rule createRule() throws PolicyException {
        Map<String, Set<String>> actionValues = 
            new HashMap<String, Set<String>>();
        {
            Set<String> set = new HashSet<String>();
            set.add("allow");
            actionValues.put("GET", set);
        }
        {
            Set<String> set = new HashSet<String>();
            set.add("allow");
            actionValues.put("POST", set);
        }
        
        return new Rule("rule1", "iPlanetAMWebAgentService",
            URL_RESOURCE, actionValues);
    }
    
    private Subject createSubject(PolicyManager pm) throws PolicyException {
        SubjectTypeManager mgr = pm.getSubjectTypeManager();
        Subject subject = mgr.getSubject("LDAPGroups");
        Set<String> set = new HashSet<String>();
        set.add("testgroup");
        subject.setValues(set);
        return subject;
    }
}
