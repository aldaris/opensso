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
 * $Id: PrivilegeManagerTest.java,v 1.3 2009-03-30 18:59:00 dillidorai Exp $
 */
package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.IPCondition;
import com.sun.identity.entitlement.TimeCondition;
import com.sun.identity.entitlement.OrCondition;
import com.sun.identity.entitlement.AndCondition;
import com.sun.identity.entitlement.OrSubject;
import com.sun.identity.entitlement.NotSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.ResourceAttributes;
import com.sun.identity.entitlement.UserAttributes;
import com.sun.identity.entitlement.StaticAttributes;
import com.sun.identity.entitlement.UserSubject;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.policy.interfaces.Subject;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.unittest.UnittestLog;
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
    private static String PRIVILEGE_NAME = "TestPrivilege";
    private static String BASE_DN = ServiceManager.getBaseDN();

    @BeforeClass
    public void setup() throws PolicyException, SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        try {
            // remove the policy
            prm.removePrivilege(PRIVILEGE_NAME);
        } catch (Exception e) {
            // supress exception, privilege may not exist
            throw new PolicyException(e);
        }
    }

    @AfterClass
    public void cleanup() throws PolicyException, SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        // not cleaning up to allow inspection using console
        //prm.removePrivilege(PRIVILIGE_NAME);
    }

    @Test
    public void testAddPrivilege() throws Exception {
        Map<String, Boolean> actionValues = new HashMap<String, Boolean>();
        actionValues.put("POST", Boolean.TRUE);
        String resourceName = "http://www.sun.com";
        Entitlement entitlement = new Entitlement(SERVICE_NAME,
                resourceName, actionValues);
        Set<Entitlement> entitlements = new HashSet<Entitlement>();
        entitlements.add(entitlement);

        String user11 = "id=user11,ou=user," + ServiceManager.getBaseDN();
        String user12 = "id=user12,ou=user," + ServiceManager.getBaseDN();
        UserSubject ua1 = new UserSubject(user11);
        UserSubject ua2 = new UserSubject(user12);
        Set<EntitlementSubject> subjects = new HashSet<EntitlementSubject>();
        subjects.add(ua1);
        subjects.add(ua2);
        OrSubject os = new OrSubject(subjects);
        NotSubject ns = new NotSubject(os);

        IPCondition ic = new IPCondition("*.sun.com");
        ic.setPConditionName("ic");
        TimeCondition tc = new TimeCondition("08:00", "16:00", "mon", "fri");
        tc.setPConditionName("tc");
        Set<EntitlementCondition> conditions = new HashSet<EntitlementCondition>();
        conditions.add(ic);
        conditions.add(tc);
        OrCondition oc = new OrCondition(conditions);
        AndCondition ac = new AndCondition(conditions);

        StaticAttributes sa = new StaticAttributes();
        Map<String, Set<String>> attrValues = new HashMap<String, Set<String>>();
        Set<String> aValues = new HashSet<String>();
        aValues.add("a10");
        aValues.add("a20");
        Set<String> bValues = new HashSet<String>();
        bValues.add("b10");
        bValues.add("b20");
        attrValues.put("a", aValues);
        attrValues.put("b", bValues);
        sa.setProperties(attrValues);
        sa.setPResponseProviderName("sa");

        UserAttributes ua = new UserAttributes();
        attrValues = new HashMap<String, Set<String>>();
        Set<String> mailAliases = new HashSet<String>();
        mailAliases.add("email1");
        mailAliases.add("email2");
        attrValues.put("mail", mailAliases);
        attrValues.put("uid", null);
        ua.setProperties(attrValues);
        ua.setPResponseProviderName("ua");

        Set<ResourceAttributes> ra = new HashSet<ResourceAttributes>();
        ra.add(sa);
        ra.add(ua);

        Privilege privilege = new Privilege(
                PRIVILEGE_NAME,
                entitlements,
                os,
                oc,
                ra);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testAPrivlege():"
                + "saving privilege=" + privilege);
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        prm.addPrivilege(privilege);

    }

    @Test(dependsOnMethods={"testAddPrivilege"})
    public void testGetPrivilege() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        Privilege p = prm.getPrivilege(PRIVILEGE_NAME);
        UnittestLog.logMessage(
                "PrivilegeManagerTest.testGetPrivlege():"
                + "read back privilege=" + p);
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
