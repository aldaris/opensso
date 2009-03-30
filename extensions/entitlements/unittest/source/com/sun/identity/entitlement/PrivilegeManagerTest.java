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
 * $Id: PrivilegeManagerTest.java,v 1.3 2009-03-30 18:58:59 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.encode.Base64;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.unittest.UnittestLog;
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

    private static String SERVICE_NAME = "iPlanetAMWebAgentService";
    private static String PRIVILEGE_NAME = "TestPrivilege";
    private static String BASE_DN = ServiceManager.getBaseDN();
    private Privilege privilege;

    @BeforeClass
    public void setup() throws SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        try {
            // remove the policy
            prm.removePrivilege(PRIVILEGE_NAME);
        } catch (Exception e) {
            // supress exception, privilege may not exist
            //throw new PolicyException(e);
        }
    }

    @AfterClass
    public void cleanup() throws SSOException, IdRepoException {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
        PrivilegeManager prm = PrivilegeManager.getInstance(null);
        // not cleaning up to allow inspection using console
        //prm.removePrivilege(PRIVILIGE_NAME);
    }

    @Test
    public void testAddPrivilege() throws Exception {
        Map<String, Boolean> actionValues = new HashMap<String, Boolean>();
        Set<String> getValues = new HashSet<String>();
        actionValues.put("GET", Boolean.TRUE);
        Set<String> postValues = new HashSet<String>();
        actionValues.put("POST", Boolean.FALSE);
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

        privilege = new Privilege(
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
    public void testSerializePrivilege() throws Exception {
        String serialized = serializeObject(privilege);
        Privilege p = (Privilege)deserializeObject(serialized);
        if (!p.equals(privilege)) {
            throw new Exception(
                "PrivilegeManagerTest.testSerializePrivilege: failed");
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
}
