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
 * $Id: ListenerRestTest.java,v 1.1 2009-09-14 23:02:42 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.identity.security.AdminTokenAction;
import com.sun.jersey.api.representation.Form;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * To Test Listener Test interface
 */
public class ListenerRestTest {
    private static final String REALM = "/";
    private static final String NOTIFICATION_URL =
        "http://www.listenerresttest.com/notification";
    private static String ENC_NOTIFICATION_URL = null;

    private static final String PRIVILEGE_NAME = "ListenerRestTestPrivilege";
    private static final SSOToken adminToken = (SSOToken)
        AccessController.doPrivileged(AdminTokenAction.getInstance());
    private Subject adminSubject = SubjectUtils.createSuperAdminSubject();
    private static final String RESOURCE_NAME = "http://www.listenerresttest.com";
    private AMIdentity user;
    private WebResource listenerClient;

    @BeforeClass
    public void setup() throws Exception {
        PrivilegeManager pm = PrivilegeManager.getInstance(REALM,
            adminSubject);
        Privilege privilege = Privilege.getNewInstance();
        privilege.setName(PRIVILEGE_NAME);

        Map<String, Boolean> actions = new HashMap<String, Boolean>();
        actions.put("GET", true);
        Entitlement entitlement = new Entitlement(RESOURCE_NAME + "/*",
            actions);
        privilege.setEntitlement(entitlement);
        EntitlementSubject sbj = new AuthenticatedESubject();
        privilege.setSubject(sbj);
        pm.addPrivilege(privilege);
        AMIdentityRepository amir = new AMIdentityRepository(
            adminToken, REALM);
        user = createUser(amir, "ListenerRestTestUser");

        listenerClient = Client.create().resource(
            SystemProperties.getServerInstanceName() +
            "/ws/1/entitlement/listener");

        ENC_NOTIFICATION_URL = ESAPI.encoder().encodeForURL(NOTIFICATION_URL);
    }

    @AfterClass
    public void cleanup() throws Exception {
        PrivilegeManager pm = PrivilegeManager.getInstance(REALM,
            adminSubject);
        pm.removePrivilege(PRIVILEGE_NAME);
        AMIdentityRepository amir = new AMIdentityRepository(
            adminToken, REALM);
        Set<AMIdentity> users = new HashSet<AMIdentity>();
        users.add(user);
        amir.deleteIdentities(users);
    }

    private AMIdentity createUser(AMIdentityRepository amir, String id)
        throws SSOException, IdRepoException {
        Map<String, Set<String>> attrValues =
            new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
        set.add(id);
        attrValues.put("givenname", set);
        attrValues.put("sn", set);
        attrValues.put("cn", set);
        attrValues.put("userpassword", set);
        return amir.createIdentity(IdType.USER, id, attrValues);
    }

    @Test
    public void test() throws Exception {
        Form form = new Form();
        form.add("resources", RESOURCE_NAME + "/*");
        String result = listenerClient.path(ENC_NOTIFICATION_URL).post(
            String.class, form);
        if (!result.equals("OK")) {
            throw new Exception("ListenerRESTTest.test failed to add");
        }

        Set<EntitlementListener> listeners =
            ListenerManager.getInstance().getListeners(adminSubject);

        if ((listeners == null) || listeners.isEmpty()) {
            throw new Exception("ListenerTestTest.test: no listeners");
        }

        try {
            Set<String> res = new HashSet<String>();
            res.add(RESOURCE_NAME + "/*");
            EntitlementListener listener = new EntitlementListener(
                new URL(NOTIFICATION_URL),
                ApplicationTypeManager.URL_APPLICATION_TYPE_NAME, res);

            boolean found = false;
            for (EntitlementListener l : listeners) {
                if (l.equals(listener)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new Exception("ListenerTestTest.test: listener not found");
            }
        } catch (MalformedURLException e) {
            //ignore
        }
    }

    @Test(dependsOnMethods = {"test"})
    public void testAddMoreResources() throws Exception {
        Form form = new Form();
        form.add("resources", RESOURCE_NAME + "/a/*");
        String result = listenerClient.path(ENC_NOTIFICATION_URL).post(
            String.class, form);
        if (!result.equals("OK")) {
            throw new Exception(
                "ListenerRESTTest.testAddMoreResources failed to add");
        }

        Set<EntitlementListener> listeners =
            ListenerManager.getInstance().getListeners(adminSubject);

        if ((listeners == null) || listeners.isEmpty()) {
            throw new Exception(
                "ListenerTestTest.testAddMoreResources: no listeners");
        }

        try {
            Set<String> res = new HashSet<String>();
            res.add(RESOURCE_NAME + "/*");
            res.add(RESOURCE_NAME + "/a/*");
            EntitlementListener listener = new EntitlementListener(
                new URL(NOTIFICATION_URL),
                ApplicationTypeManager.URL_APPLICATION_TYPE_NAME, res);

            boolean found = false;
            for (EntitlementListener l : listeners) {
                if (l.equals(listener)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new Exception(
                    "ListenerTestTest.testAddMoreResources: listener not found");
            }
        } catch (MalformedURLException e) {
            //ignore
        }
    }

    @Test(dependsOnMethods = {"testAddMoreResources"})
    public void testAddDifferentApp() throws Exception {
        Form form = new Form();
        form.add("application", "sunBank");
        String result = listenerClient.path(ENC_NOTIFICATION_URL).post(
            String.class, form);
        if (!result.equals("OK")) {
            throw new Exception(
                "ListenerRESTTest.testAddDifferentApp failed to add");
        }

        Set<EntitlementListener> listeners =
            ListenerManager.getInstance().getListeners(adminSubject);

        if ((listeners == null) || listeners.isEmpty()) {
            throw new Exception(
                "ListenerTestTest.testAddDifferentApp: no listeners");
        }
    }

    @Test(dependsOnMethods = {"testAddDifferentApp"})
    public void testGetListener() throws Exception {
        String result = listenerClient.path(ENC_NOTIFICATION_URL).get(
            String.class);

        try {
            EntitlementListener retrieved = new EntitlementListener(
                new JSONObject(result));

            Set<String> res = new HashSet<String>();
            res.add(RESOURCE_NAME + "/*");
            res.add(RESOURCE_NAME + "/a/*");
            EntitlementListener listener = new EntitlementListener(
                new URL(NOTIFICATION_URL),
                ApplicationTypeManager.URL_APPLICATION_TYPE_NAME, res);
            Map<String, Set<String>> mapAppToRes = listener.getMapAppToRes();
            mapAppToRes.put("sunBank", new HashSet());

            if (!listener.equals(retrieved)) {
                throw new Exception(
                    "ListenerTestTest.testGetListener: listener not found");
            }
        } catch (MalformedURLException e) {
            //ignore
        }
    }


    @Test(dependsOnMethods = {"testAddDifferentApp"})
    public void testRemove() throws Exception {
        String result = listenerClient.path(ENC_NOTIFICATION_URL).
            delete(String.class);
        if (!result.equals("OK")) {
            throw new Exception(
                "ListenerRESTTest.testRemove failed to remove");
        }

        Set<EntitlementListener> listeners =
            ListenerManager.getInstance().getListeners(adminSubject);

        if ((listeners != null) && !listeners.isEmpty()) {
            throw new Exception(
                "ListenerTestTest.testRemove: no listeners");
        }
    }
}
