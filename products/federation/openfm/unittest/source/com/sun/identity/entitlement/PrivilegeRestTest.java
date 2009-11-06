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
 * $Id: PrivilegeRestTest.java,v 1.1 2009-11-06 21:56:52 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.security.AdminTokenAction;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author dennis
 */
public class PrivilegeRestTest {
    private static final String PRIVILEGE_NAME = "PrivilegeRestTestPrivilege";
    private Subject adminSubject = SubjectUtils.createSuperAdminSubject();
    private static final SSOToken adminToken = (SSOToken)
        AccessController.doPrivileged(AdminTokenAction.getInstance());

    private static final String RESOURCE_NAME =
        "http://www.PrivilegeRestTest.com";
    private WebResource webClient;

    @BeforeClass
    public void setup() throws Exception {
        PrivilegeManager pm = PrivilegeManager.getInstance("/",
            adminSubject);
        Privilege privilege = Privilege.getNewInstance();
        privilege.setName(PRIVILEGE_NAME);
        privilege.setDescription("desciption");
        Map<String, Boolean> actions = new HashMap<String, Boolean>();
        actions.put("GET", true);
        Entitlement entitlement = new Entitlement(RESOURCE_NAME + "/*",
            actions);
        privilege.setEntitlement(entitlement);
        EntitlementSubject sbj = new AuthenticatedESubject();
        privilege.setSubject(sbj);
        pm.addPrivilege(privilege);
        webClient = Client.create().resource(
            SystemProperties.getServerInstanceName() +
            "/ws/1/entitlement/privilege");

    }

    @AfterClass
    public void cleanup() throws Exception {
        PrivilegeManager pm = PrivilegeManager.getInstance("/",
            adminSubject);
        pm.removePrivilege(PRIVILEGE_NAME);
    }

    @Test
    public void search() throws Exception {
        String result = webClient.path("privileges")
            .queryParam("filter",
                Privilege.NAME_ATTRIBUTE + "=" + PRIVILEGE_NAME)
            .queryParam("admin", adminToken.getTokenID().toString())
            .get(String.class);
        
        JSONObject jbody = parseResult(result);
        JSONArray array = jbody.getJSONArray(PrivilegeResource.RESULT);
        if ((array == null) || (array.length() == 0)) {
            throw new Exception(
                "PrivilegeRestTest.search failed: cannot get privilege name");
        }

        String privilegeName = (String)array.get(0);
        if (!privilegeName.equals(PRIVILEGE_NAME)) {
            throw new Exception(
                "PrivilegeRestTest.search failed: incorrect privilege name");
        }
    }

    @Test (dependsOnMethods="search")
    public void getAndPut() throws Exception {
        String result = webClient.path(PRIVILEGE_NAME)
            .queryParam("admin", adminToken.getTokenID().toString())
            .get(String.class);
        JSONObject jbody = parseResult(result);
        String jsonStr = jbody.getString(PrivilegeResource.RESULT);

        Privilege privilege = Privilege.getNewInstance(PRIVILEGE_NAME,
            new JSONObject(jsonStr));
        privilege.setDescription("desciption1");

        Form form = new Form();
        form.add("privilege.json", privilege.toMinimalJSONObject());
        result = webClient.path(PRIVILEGE_NAME)
            .queryParam("admin", adminToken.getTokenID().toString())
            .put(String.class, form);
        parseResult(result); //OK
    }

    @Test (dependsOnMethods="getAndPut")
    public void getAndDeleteAndAdd()
        throws Exception {
        String result = webClient.path(PRIVILEGE_NAME)
            .queryParam("admin", adminToken.getTokenID().toString())
            .get(String.class);
        JSONObject jbody = parseResult(result);
        String jsonStr = jbody.getString(PrivilegeResource.RESULT);

        result = webClient.path(PRIVILEGE_NAME)
            .queryParam("admin", adminToken.getTokenID().toString())
            .delete(String.class);
        jbody = parseResult(result); //OK

        Form form = new Form();
        form.add("privilege.json", jsonStr);
        form.add("admin", adminToken.getTokenID().toString());
        result = webClient.path(PRIVILEGE_NAME).post(String.class, form);
        parseResult(result); //OK
    }
    
    private JSONObject parseResult(String result) throws Exception {
        JSONObject jo = new JSONObject(result);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("PrivilegeRestTest.search failed.");
        }
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception(
                "PrivilegeRestTest.search failed: body element is null");
        }
        return jbody;
    }
}
