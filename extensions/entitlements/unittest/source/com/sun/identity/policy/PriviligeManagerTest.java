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
 * $Id: PriviligeManagerTest.java,v 1.1 2009-03-10 17:16:51 dillidorai Exp $
 */
package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.ESubject;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.OrESubject;
import com.sun.identity.entitlement.Privilige;
import com.sun.identity.entitlement.PriviligeManager;
import com.sun.identity.entitlement.UserESubject;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.policy.interfaces.Subject;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.unittest.UnittestLog;
import java.lang.String;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author dillidorai
 */
public class PriviligeManagerTest {

    private static String SERVICE_NAME = "iPlanetAMWebAgentService";
    private static String POLICY_NAME = "PriviligeTestPolicy";
    private static String PRIVILIGE_NAME = "TestPrivilige";
    private static String BASE_DN = ServiceManager.getBaseDN();

    @BeforeClass
    public void setup() throws PolicyException, SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        PriviligeManager prm = PriviligeManager.getInstance(null);
        try {
            // remove the policy
            pm.removePolicy(POLICY_NAME);
            pm.removePolicy(POLICY_NAME + "-copy");
            prm.removePrivilige(PRIVILIGE_NAME);
            deleteUsers(adminToken, "user11", "user12", "user21", "user22");
        } catch (Exception e) {
            //policy may not exist
            //privilige may not exist
        }
        Policy policy = new Policy(POLICY_NAME, "test1 - discard",
                false, true);
        policy.addRule(createRule("welcome"));
        policy.addRule(createRule("banner"));
        createUsers(adminToken, "user11", "user12", "user21", "user22");
        policy.addSubject("Users1",
                createUsersSubject(pm, "user11", "user12"), true);
        policy.addSubject("Users2",
                createUsersSubject(pm, "user21", "user22"));
        pm.addPolicy(policy);
    }

    @AfterClass
    public void cleanup() throws PolicyException, SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
    // leave the objects for visual inspection
    //pm.removePolicy(POLICY_NAME1);
    //pm.removePolicy(POLICY_NAME + "-copy");
    //prm.removePrivilige(PRIVILIGE_NAME);
    //deleteUsers(adminToken, "user11", "user12", "user21", "user22");
    }

    @Test
    public void testAddNewPrivilige() throws Exception {
        Map<String, Object> actionValues = new HashMap<String, Object>();
        Set<String> getValues = new HashSet<String>();
        getValues.add("allow");
        actionValues.put("GET", getValues);
        Set<String> postValues = new HashSet<String>();
        postValues.add("allow");
        actionValues.put("POST", postValues);
        String resourceName = "http://www.sun.com";
        Entitlement entitlement = new Entitlement(SERVICE_NAME,
                resourceName, actionValues);
        Set<Entitlement> entitlements = new HashSet<Entitlement>();
        entitlements.add(entitlement);
        String user11 = "id=user11,ou=user," + ServiceManager.getBaseDN();
        String user12 = "id=user12,ou=user," + ServiceManager.getBaseDN();
        ESubject es1 = new UserESubject(user11);
        ESubject es2 = new UserESubject(user12);
        Set<ESubject> subjects = new HashSet<ESubject>();
        subjects.add(es1);
        subjects.add(es2);
        OrESubject os = new OrESubject(subjects);
        Privilige privilige = new Privilige(
                PRIVILIGE_NAME,
                entitlements,
                os,
                null,
                null);
        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testAddNewPrivlige():" + "saving privilige=" + privilige);
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PriviligeManager prm = PriviligeManager.getInstance(null);
        prm.addPrivilige(privilige);
        Privilige p = prm.getPrivilige(PRIVILIGE_NAME);
        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testAddNewPrivlige():" + "read back privilige=" + p);
    }

    @Test
    public void testPoicyToPrivilige() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        PriviligeManager prm = PriviligeManager.getInstance(null);
        Policy policy = pm.getPolicy(POLICY_NAME);
        UnittestLog.logMessage(
                "Created in memory Policy =" + policy.toXML());
        Privilige privilige = PriviligeUtils.policyToPrivilige(policy);
        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testPriviligeEqualsPolicy():" + "policy mapped to privilige=" + privilige);
        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testPriviligeEqualsPolicy():" + "saving privilige");
        prm.addPrivilige(privilige);
        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testPriviligeEqualsPolicy():" + "reading policy");
        Policy policy1 = PriviligeUtils.priviligeToPolicy(privilige);
        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testPriviligeEqualsPolicy():" + "read policy=" + policy1.toXML());

        UnittestLog.logMessage(
                "PriviligeUnitlsTest.testPriviligeEqualsPolicy():" + "policy1=" + policy1.toXML());
        assert (policy1.equals(policy));
    }

    private Rule createRule(String ruleName) throws PolicyException {
        Map<String, Set<String>> actionMap = new HashMap<String, Set<String>>();
        Set<String> getValues = new HashSet<String>();
        getValues.add("allow");
        actionMap.put("GET", getValues);
        Set<String> postValues = new HashSet<String>();
        postValues.add("deny");
        actionMap.put("POST", postValues);
        return new Rule(ruleName,
                "iPlanetAMWebAgentService",
                "http://sample.com/" + ruleName,
                actionMap);
    }

    private Subject createUsersSubject(
            PolicyManager pm,
            String... userNames) throws PolicyException {
        SubjectTypeManager mgr = pm.getSubjectTypeManager();
        Subject subject = mgr.getSubject("AMIdentitySubject");
        Set<String> values = new HashSet<String>();
        for (String value : userNames) {
            String uuid = "id=" + value + ",ou=user," + BASE_DN;
            values.add(uuid);
        }
        subject.setValues(values);
        return subject;
    }

    private void createUsers(
            SSOToken adminToken,
            String... names)
            throws IdRepoException, SSOException {
        AMIdentityRepository amir = new AMIdentityRepository(
                adminToken, "/");
        for (String name : names) {
            Map attrMap = new HashMap();

            Set cnVals = new HashSet();
            cnVals.add(name);
            attrMap.put("cn", cnVals);

            Set snVals = new HashSet();
            snVals.add(name);
            attrMap.put("sn", snVals);

            Set nameVals = new HashSet();
            nameVals.add(name);
            attrMap.put("givenname", nameVals);

            Set passworVals = new HashSet();
            passworVals.add(name);
            attrMap.put("userpassword", passworVals);

            amir.createIdentity(IdType.USER, name, attrMap);
        }
    }

    private void deleteUsers(SSOToken adminToken,
            String... names)
            throws IdRepoException, SSOException {
        AMIdentityRepository amir = new AMIdentityRepository(
                adminToken, "/");
        Set identities = new HashSet();
        for (String name : names) {
            String uuid = "id=" + name + ",ou=user," + ServiceManager.getBaseDN();
            identities.add(IdUtils.getIdentity(adminToken, uuid));
        }
        amir.deleteIdentities(identities);
    }

    private void createGroups(
            SSOToken adminToken,
            String... names)
            throws IdRepoException, SSOException {
        AMIdentityRepository amir = new AMIdentityRepository(
                adminToken, "/");
        for (String name : names) {
            Map attrMap = new HashMap();
            amir.createIdentity(IdType.GROUP, name, attrMap);
        }
    }

    private void deleteGroups(SSOToken adminToken,
            String... names)
            throws IdRepoException, SSOException {
        AMIdentityRepository amir = new AMIdentityRepository(
                adminToken, "/");
        Set identities = new HashSet();
        for (String name : names) {
            String uuid = "id=" + name + ",ou=group," + ServiceManager.getBaseDN();
            identities.add(IdUtils.getIdentity(adminToken, uuid));
        }
        amir.deleteIdentities(identities);
    }
}
