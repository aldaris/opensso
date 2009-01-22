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
 * $Id: PolicyEvaluatorTest.java,v 1.1 2009-01-22 23:59:35 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOToken;
import com.sun.identity.policy.interfaces.Subject;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.Test;

/**
 *
 * @author dennis
 */
public class PolicyEvaluatorTest {
    private static String URL_RESOURCE = "http://www.*.com:8080/private";
    
    @Test
    public void test() throws Exception {
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

        PolicyEvaluator pe = new PolicyEvaluator("iPlanetAMWebAgentService");
        if (!pe.isAllowed(adminToken, "http://www.sun.com:8080/private", "GET")){
            throw new Exception(
                "http://www.sun.com:8080/private evaluation failed");
        }
        
        //negative test
        if (pe.isAllowed(adminToken, "http://www.sun.com:8080/public", "GET")){
            throw new Exception(
                "http://www.sun.com:8080/public evaluation failed");
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
        Subject subject = mgr.getSubject("AuthenticatedUsers");
        Set<String> set = new HashSet<String>();
        subject.setValues(set);
        return subject;
    }
}
