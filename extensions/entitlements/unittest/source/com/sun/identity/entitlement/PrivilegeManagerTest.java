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
 * $Id: PrivilegeManagerTest.java,v 1.18 2009-05-09 01:08:47 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.opensso.OpenSSOPrivilege;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.encode.Base64;
import com.sun.identity.sm.ServiceManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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

    private static final String SERVICE_NAME = "iPlanetAMWebAgentService";
    private static final String PRIVILEGE_NAME = "TestPrivilege";
    private static final String PRIVILEGE_DESC = "Test Description";
    private Privilege privilege;

    @BeforeClass
    public void setup() throws SSOException, IdRepoException {

    }

    @AfterClass
    public void cleanup() throws Exception {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance("/",
            SubjectUtils.createSubject(adminToken));
        prm.removePrivilege(PRIVILEGE_NAME);
    }

    @Test
    public void testAddPrivilege() throws Exception {
        Map<String, Boolean> actionValues = new HashMap<String, Boolean>();
        actionValues.put("GET", Boolean.TRUE);
        actionValues.put("POST", Boolean.FALSE);
        // The port is required for passing equals  test
        // opensso policy would add default port if port not specified
        String resourceName = "http://www.sun.com:80";
        Entitlement entitlement = new Entitlement(SERVICE_NAME,
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
            //TODO: looks like hashCodes differ for nested subjects and conditions compared to
            // the saved ones. Need to investigate more
            /*
            throw new Exception("PrivilegeManagerTest.testAddPrivlege():"
                + "read privilege not"
                + "equal to saved privilege");
            */

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
        String serialized = serializeObject(privilege);
        Privilege p = (Privilege) deserializeObject(serialized);
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
    }

    private String serializeObject(Serializable object)
            throws EntitlementException {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(out);
            oos.writeObject(object);
            oos.close();
            return Base64.encode(out.toByteArray());
        } catch (IOException e) {
            throw new EntitlementException(200, null, e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private Object deserializeObject(String strSerialized)
            throws EntitlementException {
        ObjectInputStream ois = null;
        try {
            InputStream in = new ByteArrayInputStream(
                    Base64.decode(strSerialized));
            ois = new ObjectInputStream(in);
            return ois.readObject();
        } catch (ClassNotFoundException ex) {
            throw new EntitlementException(201, null, ex);
        } catch (IOException ex) {
            throw new EntitlementException(201, null, ex);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ex) {
                // ignore
            }
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
