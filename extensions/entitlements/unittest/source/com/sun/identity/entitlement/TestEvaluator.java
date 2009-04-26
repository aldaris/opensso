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
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: TestEvaluator.java,v 1.4 2009-04-26 07:20:43 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.internal.server.AuthSPrincipal;
import com.sun.identity.entitlement.opensso.OpenSSOPrivilege;
import com.sun.identity.entitlement.opensso.PolicyPrivilegeManager;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestEvaluator {
    private static final String PRIVILEGE1_NAME = "entitlementPrivilege1";
    private static final String USER1_NAME = "privilegeEvalTestUser1";
    private static final String USER2_NAME = "privilegeEvalTestUser2";
    private static final String URL1 = "http://www.sun.com:80/private";

    
    private AMIdentity user1;
    private AMIdentity user2;

    @BeforeClass
    public void setup() throws Exception {
        PrivilegeManager pm = new PolicyPrivilegeManager();
        pm.initialize(null);
        Map<String, Boolean> actions = new HashMap<String, Boolean>();
        actions.put("GET", Boolean.TRUE);
        Entitlement ent = new Entitlement(
            ApplicationTypeManager.URL_APPLICATION_TYPE_NAME, URL1, actions);
        user1 = createUser(USER1_NAME);
        user2 = createUser(USER2_NAME);
        Set<EntitlementSubject> esSet = new HashSet<EntitlementSubject>();
        EntitlementSubject es1 = new UserSubject(user1.getUniversalId());
        EntitlementSubject es2 = new UserSubject(user2.getUniversalId());
        esSet.add(es1);
        esSet.add(es2);

        EntitlementSubject eSubject = new OrSubject(esSet);
        Privilege privilege = new OpenSSOPrivilege(
            PRIVILEGE1_NAME, ent, eSubject, null, Collections.EMPTY_SET);
        pm.addPrivilege(privilege);
    }

    @AfterClass
    public void cleanup() throws Exception {
        PrivilegeManager pm = new PolicyPrivilegeManager();
        pm.initialize(null);
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        pm.removePrivilege(PRIVILEGE1_NAME);
        //pm.removePrivilege(PRIVILEGE2_NAME);

        AMIdentityRepository amir = new AMIdentityRepository(
            adminToken, "/");
        Set<AMIdentity> identities = new HashSet<AMIdentity>();
        identities.add(user1);
        identities.add(user2);
        amir.deleteIdentities(identities);
    }

    @Test
    public void postiveTest()
        throws Exception {
        if (!evaluate(URL1)) {
            throw new Exception("TestEvaluator.postiveTest failed");
        }
    }

    private boolean evaluate(String res)
        throws EntitlementException {
        Subject subject = createSubject(user1.getUniversalId());
        Set actions = new HashSet();
        actions.add("GET");
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        Evaluator evaluator = new Evaluator(
            SubjectUtils.createSubject(adminToken),
            ApplicationTypeManager.URL_APPLICATION_TYPE_NAME);
        return evaluator.hasEntitlement(subject,
            new Entitlement(res, actions), Collections.EMPTY_MAP);
    }

    
    private AMIdentity createUser(String name)
        throws SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        AMIdentityRepository amir = new AMIdentityRepository(
            adminToken, "/");
        Map<String, Set<String>> attrValues =new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
        set.add(name);
        attrValues.put("givenname", set);
        attrValues.put("sn", set);
        attrValues.put("cn", set);
        attrValues.put("userpassword", set);
        return amir.createIdentity(IdType.USER, name, attrValues);
    }

    public static Subject createSubject(String uuid) {
        Set<Principal> userPrincipals = new HashSet<Principal>(2);
        userPrincipals.add(new AuthSPrincipal(uuid));
        return new Subject(false, userPrincipals, new HashSet(),
            new HashSet());
    }

}
