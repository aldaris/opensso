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
 * $Id: MultipleResourceRestTest.java,v 1.3 2009-10-21 01:11:36 veiming Exp $
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
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.identity.security.AdminTokenAction;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * To Test REST interface
 */
public class MultipleResourceRestTest {
    private static final String REALM = "/";
    private static final String PRIVILEGE_NAME =
        "MultipleResourceRestTestPrivilege";
    private static final SSOToken adminToken = (SSOToken)
        AccessController.doPrivileged(AdminTokenAction.getInstance());
    private Subject adminSubject = SubjectUtils.createSuperAdminSubject();
    private static final String RESOURCE_NAME =
        "http://www.MultipleResourceRestTest.com";
    private AMIdentity user;
    private WebResource decisionsClient;
    private WebResource entitlementsClient;

    @BeforeClass
    public void setup() throws Exception {
        PrivilegeManager pm = PrivilegeManager.getInstance(REALM,
            adminSubject);

        {
            Privilege privilege = Privilege.getNewInstance();
            privilege.setName(PRIVILEGE_NAME + "1");
            Map<String, Boolean> actions = new HashMap<String, Boolean>();
            actions.put("GET", true);
            Entitlement entitlement = new Entitlement(RESOURCE_NAME + "/*",
                actions);
            privilege.setEntitlement(entitlement);
            EntitlementSubject sbj = new AuthenticatedESubject();
            privilege.setSubject(sbj);
            pm.addPrivilege(privilege);
        }
        {
            Privilege privilege = Privilege.getNewInstance();
            privilege.setName(PRIVILEGE_NAME  + "2");
            Map<String, Boolean> actions = new HashMap<String, Boolean>();
            actions.put("GET", false);
            Entitlement entitlement = new Entitlement(RESOURCE_NAME +
                "/index.html", actions);
            privilege.setEntitlement(entitlement);
            EntitlementSubject sbj = new AuthenticatedESubject();
            privilege.setSubject(sbj);
            pm.addPrivilege(privilege);
        }


        AMIdentityRepository amir = new AMIdentityRepository(
            adminToken, REALM);
        user = createUser(amir, "MultipleResourceRestTestUser");

        decisionsClient = Client.create().resource(
            SystemProperties.getServerInstanceName() +
            "/ws/1/entitlement/decisions");
        entitlementsClient = Client.create().resource(
            SystemProperties.getServerInstanceName() +
            "/ws/1/entitlement/entitlements");
    }

    @AfterClass
    public void cleanup() throws Exception {
        PrivilegeManager pm = PrivilegeManager.getInstance(REALM,
            adminSubject);
        pm.removePrivilege(PRIVILEGE_NAME  + "1");
        pm.removePrivilege(PRIVILEGE_NAME  + "2");
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
    public void testDecisions() throws Exception {
        MultivaluedMap params = new MultivaluedMapImpl();
        params.add("subject", user.getUniversalId());
        params.add("resources", RESOURCE_NAME + "/index.html");
        params.add("resources", RESOURCE_NAME + "/a");
        params.add("action", "GET");
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = decisionsClient.queryParams(params).accept(
            "application/json").get(String.class);
        List<JSONEntitlement> entitlements = JSONEntitlement.getEntitlements(
            new JSONObject(json));
        for (JSONEntitlement e : entitlements) {
            String res = e.getResourceName();
            Map<String, Boolean> actionValues = e.getActionValues();

            if (res.equals(RESOURCE_NAME + "/index.html")) {
                if (actionValues.get("GET")) {
                    throw new Exception(
                        "MultipleResourceRestTest.testDecisions: incorrect result for /index.html");
                }
            } else if (res.equals(RESOURCE_NAME + "/a")) {
                if (!actionValues.get("GET")) {
                    throw new Exception(
                        "MultipleResourceRestTest.testDecisions: incorrect result for /a");
                }
            }

        }
    }

    @Test
    public void testEntitlements() throws Exception {
        MultivaluedMap params = new MultivaluedMapImpl();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = entitlementsClient.queryParams(params).accept(
            "application/json").get(String.class);
        List<JSONEntitlement> entitlements = JSONEntitlement.getEntitlements(
            new JSONObject(json));
        for (JSONEntitlement e : entitlements) {
            String res = e.getResourceName();
            Map<String, Boolean> actionValues = e.getActionValues();

            if (res.equals(RESOURCE_NAME)) {
                if (!actionValues.isEmpty()) {
                    throw new Exception(
                        "MultipleResourceRestTest.testEntitlements: incorrect result for root");
                }
            } else if (res.equals(RESOURCE_NAME + ":80/index.html")) {
                if (actionValues.get("GET")) {
                    throw new Exception(
                        "MultipleResourceRestTest.testEntitlements: incorrect result for /index.html");
                }
            } else if (res.equals(RESOURCE_NAME + ":80/*")) {
                if (!actionValues.get("GET")) {
                    throw new Exception(
                        "MultipleResourceRestTest.testEntitlements: incorrect result for /*");
                }
            }
        }
    }
}
