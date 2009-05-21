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
 * $Id: PrivilegeManagerTest.java,v 1.22 2009-05-21 06:35:31 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.opensso.OpenSSOPrivilege;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.ServiceManager;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

/**
 *
 * @author dillidorai
 */
public class PrivilegeManagerTest {
    private static final String APPL_NAME = "PrivilegeManagerTestAppl";
    private static final String PRIVILEGE_NAME = "PrivilegeManagerTest";
    private static final String PRIVILEGE_NAME1 = "PrivilegeManagerTest1";
    private static final String PRIVILEGE_DESC = "Test Description";
    private Privilege privilege;
    

    @BeforeClass
    public void setup() throws SSOException, IdRepoException, EntitlementException {
        Application appl = new Application(APPL_NAME,
            ApplicationTypeManager.getAppplicationType(
            ApplicationTypeManager.URL_APPLICATION_TYPE_NAME));
        Set<String> appResources = new HashSet<String>();
        appResources.add("http://www.privilegemanagertest.*");
        appl.addResources(appResources);
        appl.setEntitlementCombiner(DenyOverride.class);
        ApplicationManager.saveApplication("/", appl);
    }

    @AfterClass
    public void cleanup() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance("/",
            SubjectUtils.createSubject(adminToken));
        prm.removePrivilege(PRIVILEGE_NAME);
        
        ApplicationManager.deleteApplication("/", APPL_NAME);
    }

    @Test
    public void testResourceValidationPrivilege() throws Exception {
        Application appl = ApplicationManager.getApplication("/", APPL_NAME);

        ValidateResourceResult res =
            appl.validateResourceName("http://www.privilegemanagertest.com/hr");
        if (!res.isValid()) {
            throw new Exception(
                "PrivilegeManagerTest.testResourceValidationPrivilege" +
                " positive test failed");
        }

        res = appl.validateResourceName("http://www.test1.com:abc/hr");
        if (res.isValid()) {
            throw new Exception(
                "PrivilegeManagerTest.testResourceValidationPrivilege" +
                " negative test failed");
        }
    }

    @Test
    public void testNoSubjectInPrivilege() throws Exception {
        Map<String, Boolean> actionValues = new HashMap<String, Boolean>();
        actionValues.put("GET", Boolean.TRUE);
        actionValues.put("POST", Boolean.FALSE);
        String resourceName = "http://www.privilegemanagertest.com:80";
        Entitlement entitlement = new Entitlement(APPL_NAME,
                resourceName, actionValues);
        entitlement.setName("ent1");

        Set<EntitlementSubject> eSubjects = new HashSet<EntitlementSubject>();
        eSubjects.add(new AndSubject(null));
        OrSubject os = new OrSubject(eSubjects);

        try {
            new OpenSSOPrivilege(PRIVILEGE_NAME1, entitlement, os, null, null);
        } catch (EntitlementException e) {
            if (e.getErrorCode() != 310)  {
                throw e;
            } else {
                return;
            }
        }

        throw new Exception(
            "PrivilegeManagerTest.testNoSubjectInPrivilege failed");
    }

    @Test
    public void testAddPrivilege() throws Exception {
        Map<String, Boolean> actionValues = new HashMap<String, Boolean>();
        actionValues.put("GET", Boolean.TRUE);
        actionValues.put("POST", Boolean.FALSE);
        // The port is required for passing equals  test
        // opensso policy would add default port if port not specified
        String resourceName = "http://www.privilegemanagertest.com:80";
        Entitlement entitlement = new Entitlement(APPL_NAME,
                resourceName, actionValues);
        entitlement.setName("ent1");

        String user11 = "id=user11,ou=user," + ServiceManager.getBaseDN();
        String user12 = "id=user12,ou=user," + ServiceManager.getBaseDN();
        UserSubject ua1 = new IdRepoUserSubject();
        ua1.setID(user11);
        UserSubject ua2 = new IdRepoUserSubject();
        ua2.setID(user12);
        Set<EntitlementSubject> subjects = new HashSet<EntitlementSubject>();
        subjects.add(ua1);
        subjects.add(ua2);
        OrSubject os = new OrSubject(subjects);

        Set<String> excludedResourceNames = new HashSet<String>();
        entitlement.setExcludedResourceNames(excludedResourceNames);

        String startIp = "100.100.100.100";
        String endIp = "200.200.200.200";
        IPCondition ipc = new IPCondition(startIp, endIp);
        ipc.setPConditionName("ipc");
        DNSNameCondition dnsc = new DNSNameCondition("*.sun.com");
        dnsc.setPConditionName("dnsc");
        TimeCondition tc = new TimeCondition("08:00", "16:00", "mon", "fri");
        tc.setPConditionName("tc");
        Set<EntitlementCondition> conditions = new HashSet<EntitlementCondition>();
        conditions.add(dnsc);
        conditions.add(tc);

        StaticAttributes sa1 = new StaticAttributes();
        Set<String> aValues = new HashSet<String>();
        aValues.add("a10");
        aValues.add("a20");
        sa1.setPropertyName("a");
        sa1.setPropertyValues(aValues);
        sa1.setPResponseProviderName("sa");
        
        StaticAttributes sa2 = new StaticAttributes();
        Set<String> bValues = new HashSet<String>();
        bValues.add("b10");
        bValues.add("b20");
        sa2.setPropertyName("b");
        sa2.setPropertyValues(bValues);
        sa2.setPResponseProviderName("sa");

        UserAttributes uat1 = new UserAttributes();
        uat1.setPropertyName("email");
        uat1.setPResponseProviderName("ua");

        UserAttributes uat2 = new UserAttributes();
        uat2.setPropertyName("uid");
        uat2.setPResponseProviderName("ua");

        Set<ResourceAttribute> ra = new HashSet<ResourceAttribute>();
        ra.add(sa1);
        ra.add(sa2);
        ra.add(uat1);
        ra.add(uat2);

        privilege = new OpenSSOPrivilege(PRIVILEGE_NAME, entitlement, os,
            ipc, ra);
        privilege.setDescription(PRIVILEGE_DESC);
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance("/",
            SubjectUtils.createSubject(adminToken));
        prm.addPrivilege(privilege);

        Privilege p = prm.getPrivilege(PRIVILEGE_NAME);

        IPCondition ipc1 = (IPCondition) p.getCondition();
        if (!ipc1.getStartIp().equals(startIp)) {
            throw new Exception(
                "PrivilegeManagerTest.testAddPrivlege():" + "READ startIp "
                + " does not equal set startIp");
        }
        if (!ipc1.getEndIp().equals(endIp)) {
            throw new Exception(
                "PrivilegeManagerTest.testAddPrivlege():" + "READ endIp "
                + " does not equal set endIp");
        }
        if (!privilege.equals(p)) {
            throw new Exception("PrivilegeManagerTest.testAddPrivlege():"
                + "read privilege not"
                + "equal to saved privilege");
        }

        {
            EntitlementSubject subjectCollections = privilege.getSubject();
            if (subjectCollections instanceof OrSubject) {
                OrSubject orSbj = (OrSubject)subjectCollections;
                Set<EntitlementSubject> subjs = orSbj.getESubjects();
                for (EntitlementSubject sbj : subjs) {
                    if (!sbj.equals(ua1) && !sbj.equals(ua2)) {
                        throw new Exception(
            "PrivilegeManagerTest.testAddPrivilege: Subject does not matched.");
                    }
                }
            }
        }
    }

    @Test(dependsOnMethods = {"testAddPrivilege"})
    public void testSerializePrivilege() throws Exception {
        String serialized = privilege.toJSONObject().toString();
        Privilege p = Privilege.getInstance(new JSONObject(serialized));
        if (!p.equals(privilege)) {
            throw new Exception(
                    "PrivilegeManagerTest.testSerializePrivilege: failed");
        }
    }

    @Test(dependsOnMethods = {"testAddPrivilege"})
    public void testListPrivilegeNames() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance("/",
            SubjectUtils.createSubject(adminToken));

        Set<PrivilegeSearchFilter> psf = new HashSet<PrivilegeSearchFilter>();
        psf.add(new PrivilegeSearchFilter(Privilege.NAME_ATTRIBUTE, "*"));
        Set privilegeNames = prm.searchPrivilegeNames(psf);
        if (!privilegeNames.contains(PRIVILEGE_NAME)) {
              throw new Exception(
                "PrivilegeManagerTest.testListPrivilegeNames():"
                + "got privilege names does not contain saved privilege");
        }

        psf = new HashSet<PrivilegeSearchFilter>();
        psf.add(new PrivilegeSearchFilter(Privilege.DESCRIPTION_ATTRIBUTE,
            PRIVILEGE_DESC));
        privilegeNames = prm.searchPrivilegeNames(psf);
        if (!privilegeNames.contains(PRIVILEGE_NAME)) {
              throw new Exception(
                "PrivilegeManagerTest.testListPrivilegeNames():"
                + "got privilege names does not contain saved privilege");
        }
    }

    @Test(dependsOnMethods = {"testAddPrivilege"})
    public void testGetPrivilege() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance("/",
            SubjectUtils.createSubject(adminToken));
        Privilege p = prm.getPrivilege(PRIVILEGE_NAME);

        if (p == null) {
            throw new Exception("PrivilegeManagerTest.testGetPrivilege: " +
                "failed to get privilege.");
        }

        if (!p.getDescription().equals(PRIVILEGE_DESC)) {
            throw new Exception("PrivilegeManagerTest.testGetPrivilege: " +
                "failed to get privilege description.");
        }
        
        String xml = prm.getPrivilegeXML(PRIVILEGE_NAME);
        if ((xml == null) || (xml.trim().length() == 0)) {
            throw new Exception("PrivilegeManagerTest.testGetPrivilege: " +
                "failed to get privilege XML.");
        }
    }

    @Test(dependsOnMethods = {"testAddPrivilege"})
    public void testLastModifiedDate() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance("/", 
            SubjectUtils.createSubject(adminToken));
        prm.modifyPrivilege(privilege);
        Long cdate = privilege.getCreationDate();
        Long mdate = privilege.getLastModifiedDate();
        if (cdate == mdate) {
            throw new Exception("PrivilegeManagerTest.testLastModifiedDate: " +
                "creation and last modified date are the same.");
        }
    }
}
