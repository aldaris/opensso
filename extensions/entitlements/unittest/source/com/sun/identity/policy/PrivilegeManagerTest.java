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
 * $Id: PrivilegeManagerTest.java,v 1.1 2009-03-26 16:24:35 dillidorai Exp $
 */
package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.IPCondition;
import com.sun.identity.entitlement.OrSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.UserSubject;
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
public class PrivilegeManagerTest {

    private static String SERVICE_NAME = "iPlanetAMWebAgentService";
    private static String PRIVILIGE_NAME = "TestPrivilege";
    private static String POLICY_NAME = "TestPolicy";
    private static String BASE_DN = "dc=opensso,dc=java,dc=net"; //ServiceManager.getBaseDN();

    //@BeforeClass
    public void setup() throws PolicyException, SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        try {
            // remove the policy
            pm.removePolicy(POLICY_NAME);
            pm.removePolicy(POLICY_NAME + "-copy");
            prm.removePrivilege(PRIVILIGE_NAME);
        } catch (Exception e) {
            throw new PolicyException(e);
        }
       
        Policy policy = new Policy(POLICY_NAME, "test1 - discard",
                false, true);
        policy.addRule(createRule("welcome"));
        policy.addRule(createRule("banner"));
        policy.addSubject("Users1",
                createUsersSubject(pm, "user11", "user12"), true);
        policy.addSubject("Users2",
                createUsersSubject(pm, "user21", "user22"));
        pm.addPolicy(policy);
    }

    //@AfterClass
    public void cleanup() throws PolicyException, SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
    // leave the objects for visual inspection
    //pm.removePolicy(POLICY_NAME1);
    //pm.removePolicy(POLICY_NAME + "-copy");
    //prm.removePrivilege(PRIVILIGE_NAME);
    }

    @Test
    public void testAddNewPrivilege() throws Exception {
        try {
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
        EntitlementSubject es1 = new UserSubject(user11);
        EntitlementSubject es2 = new UserSubject(user12);
        Set<EntitlementSubject> subjects = new HashSet<EntitlementSubject>();
        subjects.add(es1);
        subjects.add(es2);
        OrSubject os = new OrSubject(subjects);
        EntitlementCondition ec = new IPCondition("*.sun.com");
        Privilege privilege = new Privilege(
                PRIVILIGE_NAME,
                entitlements,
                os, //orSubject
                ec, //entitlementCondition
                null);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testAddNewPrivlege():" + "saving privilege=" + privilege);
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        prm.addPrivilege(privilege);
        Privilege p = prm.getPrivilege(PRIVILIGE_NAME);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testAddNewPrivlege():" + "read back privilege=" + p);
        } catch (Exception e) {
             UnittestLog.logMessage(
                "PrivilegeManagerTest.testAddNewPrivlege(): caught exception"
                + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //@Test
    public void testPoicyToPrivilege() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PolicyManager pm = new PolicyManager(adminToken, "/");
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        Policy policy = pm.getPolicy(POLICY_NAME);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testPolicyPrivilege():"
                + "Created in memory Policy ="
                + policy.toXML());
        Privilege privilege = PrivilegeUtils.policyToPrivilege(policy);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testPolicyToPrivilege():" + "policy mapped to privilege=" + privilege);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testPolicyToPrivilege():" + "saving privilege");
        prm.addPrivilege(privilege);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testPolicyToPrivilege():" + "reading policy");
        Policy policy1 = PrivilegeUtils.privilegeToPolicy(privilege);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testPolicyToPrivilege():" + "read policy=" + policy1.toXML());

        UnittestLog.logMessage(
                "PrivilegeManagerTest.testPolicyToPrivilege():" + "policy1=" + policy1.toXML());
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
