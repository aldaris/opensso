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
 * $Id: RestTest.java,v 1.7 2009-11-02 23:52:03 dillidorai Exp $
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
import com.sun.identity.unittest.UnittestLog;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
//import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.api.representation.Form;
import com.sun.identity.security.AdminTokenAction;
import com.sun.jersey.api.client.UniformInterfaceException;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * To Test REST interface
 * @author Ravi Hingarajiya <ravi.hingarajiya@sun.com>
 */
public class RestTest {
    private static final String REALM = "/";
    private static final String PRIVILEGE_NAME = "RestTestPrivilege";
    private static final SSOToken adminToken = (SSOToken)
        AccessController.doPrivileged(AdminTokenAction.getInstance());
    private Subject adminSubject = SubjectUtils.createSuperAdminSubject();
    private static final String RESOURCE_NAME = "http://www.resttest.com";
    private static final String ATTR_NAME = "bankAcc";
    private static final float ATTR_VAL = 1234f;
    private AMIdentity user;
    private WebResource entitlementClient;
    private WebResource entitlementsClient;
    private WebResource decisionClient;
    private WebResource decisionsClient;

    @BeforeClass
    public void setup() throws Exception {
        try {
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

            NumericAttributeCondition cond = new NumericAttributeCondition();
            cond.setAttributeName(ATTR_NAME);
            cond.setOperator(NumericAttributeCondition.Operator.EQUAL);
            cond.setValue(ATTR_VAL);
            privilege.setCondition(cond);
            pm.addPrivilege(privilege);
            AMIdentityRepository amir = new AMIdentityRepository(
                adminToken, REALM);
            user = createUser(amir, "RestTestUser");

            decisionClient = Client.create().resource(
                SystemProperties.getServerInstanceName() +
                "/ws/1/entitlement/decision");
            decisionsClient = Client.create().resource(
                SystemProperties.getServerInstanceName() +
                "/ws/1/entitlement/decisions");
            entitlementClient = Client.create().resource(
                SystemProperties.getServerInstanceName() +
                "/ws/1/entitlement/entitlement");
            entitlementsClient = Client.create().resource(
                SystemProperties.getServerInstanceName() +
                "/ws/1/entitlement/entitlements");
        } catch (Exception e) {
            UnittestLog.logError("RestTest.setup() failed:", e);
            throw e;
        }
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
    public void getDecisionTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String decision = decisionClient.queryParams(params).accept(
            "text/plain").get(String.class);
        if ((decision == null) || !decision.equals("allow")) {
            throw new Exception("RESTTest.getDecisionTest() failed");
        }
    }

    @Test
    public void postDecisionTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String decision = decisionClient.accept(
            "text/plain").post(String.class, params);
        if ((decision == null) || !decision.equals("allow")) {
            throw new Exception("RESTTest.postDecisionTest() failed");
        }
    }

    @Test
    public void getDecisionsTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resources", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = decisionsClient.queryParams(params).accept(
            "application/json").get(String.class);
        UnittestLog.logMessage("RestTest.getDecisionsTest():json string=" + json);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.getDecisionsTest() failed, status code not 200");
        }
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.getDecisionsTest() failed, body element is null");
        }
        JSONArray results = jbody.optJSONArray("results");
        if (results == null) {
            throw new Exception("RESTTest.getDecisionsTest() failed, results array is null");
        }
        if (results.length() < 1) {
            throw new Exception("RESTTest.getDecisionsTest() failed, results array is empty");
        }
        JSONEntitlement ent = new JSONEntitlement(results.getJSONObject(0));
        boolean result = ent.getActionValue("GET");
        if (!result) {
            throw new Exception("RESTTest.getDecisionsTest() failed");
        }
    }

    @Test
    public void postDecisionsTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resources", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = decisionsClient.queryParams(params).accept(
            "application/json").post(String.class, params);
        UnittestLog.logMessage("RestTest.postDecisionsTest():json string=" + json);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.postDecisionsTest() failed, status code not 200");
        }
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.postDecisionsTest() failed, body element is null");
        }
        JSONArray results = jbody.optJSONArray("results");
        if (results == null) {
            throw new Exception("RESTTest.postDecisionsTest() failed, results array is null");
        }
        if (results.length() < 1) {
            throw new Exception("RESTTest.postDecisionsTest() failed, results array is empty");
        }
        JSONEntitlement ent = new JSONEntitlement(results.getJSONObject(0));
        boolean result = ent.getActionValue("GET");
        if (!result) {
            throw new Exception("RESTTest.postDecisionsTest() failed");
        }
    }

    @Test
    public void getEntitlementTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = entitlementClient.queryParams(params).accept(
            "application/json").get(String.class);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.getEntitlementTest() failed, status code not 200");
        }
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.getEntitlementTest() failed, body element is null");
        }
        JSONEntitlement ent = new JSONEntitlement(jbody);
        boolean result = ent.getActionValue("GET");
        if (!result) {
            throw new Exception("RESTTest.getEntitlementTest() failed");
        }
    }

    @Test
    public void postEntitlementTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = entitlementClient.queryParams(params).accept(
            "application/json").post(String.class, params);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.postEntitlementTest() failed, status code not 200");
        }
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.postEntitlementTest() failed, body element is null");
        }
        JSONEntitlement ent = new JSONEntitlement(jbody);
        boolean result = ent.getActionValue("GET");
        if (!result) {
            throw new Exception("RESTTest.postEntitlementTest() failed");
        }
    }

    @Test
    public void getEntitlementsTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = entitlementsClient.queryParams(params).accept(
            "application/json").get(String.class);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.getEntitlementsTest() failed, status code not 200");
        }
        UnittestLog.logMessage("RestTest.getEntitlementsTest():json string=" + json);
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.getEntitlementsTest() failed, body element is null");
        }
        JSONArray results = jbody.optJSONArray("results");
        if (results == null) {
            throw new Exception("RESTTest.getEntitlementsTest() failed, results element is null");
        }
        if (results.length() < 1) {
            throw new Exception("RESTTest.getEntitlementsTest() failed, results array is empty");
        }
        JSONEntitlement ent = new JSONEntitlement(results.getJSONObject(0));
        boolean result = false;
        Object resultObj = ent.getActionValue("GET");
        if (resultObj != null) {
            result = ent.getActionValue("GET");
        } else {
            throw new Exception("RESTTest.getEntitlementsTest() failed: action value is null");
        }
        if (!result) {
            throw new Exception("RESTTest.getEntitlementsTest() failed: action value is false");
        }
    }

    @Test
    public void postEntitlementsTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String json = entitlementsClient.queryParams(params).accept(
            "application/json").post(String.class, params);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.postEntitlementsTest() failed, status code not 200");
        }
        UnittestLog.logMessage("RestTest.postEntitlementsTest():json string=" + json);
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.postEntitlementsTest() failed, body element is null");
        }
        JSONArray results = jbody.optJSONArray("results");
        if (results == null) {
            throw new Exception("RESTTest.postEntitlementsTest() failed, results element is null");
        }
        if (results.length() < 1) {
            throw new Exception("RESTTest.postEntitlementsTest() failed, results array is empty");
        }
        JSONEntitlement ent = new JSONEntitlement(results.getJSONObject(0));
        boolean result = false;
        Object resultObj = ent.getActionValue("GET");
        if (resultObj != null) {
            result = ent.getActionValue("GET");
        } else {
            throw new Exception("RESTTest.postEntitlementsTest() failed: action value is null");
        }
        if (!result) {
            throw new Exception("RESTTest.postEntitlementsTest() failed: action value is false");
        }
    }

    @Test
    public void negativeTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        //params.add("env", ATTR_NAME + "=" + ATTR_VAL); //to not get allow
        params.add("realm", REALM);
        params.add("admin", adminToken.getTokenID().toString());

        String decision = decisionClient.queryParams(params).accept(
            "text/plain").get(String.class);
        if ((decision != null) && decision.equals("allow")) {
            throw new Exception("RESTTest.negativeTest (/decision) failed");
        }

        String json = entitlementClient.queryParams(params).accept(
            "application/json").get(String.class);
        UnittestLog.logMessage("RestTest.negativeTest():json string=" + json);
        JSONObject jo = new JSONObject(json);
        if (jo.optInt("statusCode") != 200) {
            throw new Exception("RESTTest.negativeTest() failed, status code not 200");
        }
        JSONObject jbody = jo.optJSONObject("body");
        if (jbody == null) {
            throw new Exception("RESTTest.negativeTest() failed, body element is null");
        }
        JSONEntitlement ent = new JSONEntitlement(jbody);
        UnittestLog.logMessage("RestTest.negativeTest():jsonEnt=" + ent);
        boolean result = false;
        Object resultObj = ent.getActionValue("GET");
        if (resultObj != null) {
            result = ent.getActionValue("GET");
        }
        if (result) {
            throw new Exception("RESTTest.getnegativeTest() failed");
        }
        Map<String, Set<String>> advices = ent.getAdvices();
        Set<String> setNumericCondAdvice = advices.get(
            NumericAttributeCondition.class.getName());
        if ((setNumericCondAdvice == null) || setNumericCondAdvice.isEmpty()) {
            throw new Exception("RESTTest.negativeTest: no advice");
        }
        String advice = setNumericCondAdvice.iterator().next();
        if (!advice.equals(ATTR_NAME + "=" + ATTR_VAL)) {
            throw new Exception("RESTTest.negativeTest: incorrect advice");
        }
    }

    @Test
    public void missingResourceTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        // params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("admin", adminToken.getTokenID().toString());
        params.add("realm", REALM);

        try {
            entitlementClient.queryParams(params).accept(
                "application/json").get(String.class);
            throw new Exception(
                "RESTTest.missingResourceTest: no exception thrown.");
        } catch (UniformInterfaceException e) {
            int errorCode = e.getResponse().getStatus();
            if (errorCode != 420) {
                throw new Exception(
                    "RESTTest.missingResourceTest: incorrect error code");
            }
            String json = e.getResponse().getEntity(String.class);
            JSONObject jo = new JSONObject(json);
            if (jo.optInt("statusCode") != 420) {
                throw new Exception("RESTTest.missingResourceTest() failed, status code not 420");
            }
            if (jo.optJSONObject("body") != null) {
                throw new Exception("RESTTest.missingResourceTest() failed, body not empty");
            }
        }
    }

    @Test
    public void missingResourcesTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        //params.add("resources", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("admin", adminToken.getTokenID().toString());
        params.add("realm", REALM);

        try {
            decisionsClient.queryParams(params).accept(
                "application/json").get(String.class);
            throw new Exception(
                "RESTTest.missingResourceTest: no exception thrown.");
        } catch (UniformInterfaceException e) {
            int errorCode = e.getResponse().getStatus();
            if (errorCode != 424) { 
                throw new Exception(
                    "RESTTest.missingResourceTest: incorrect error code");
            }
            String json = e.getResponse().getEntity(String.class);
            JSONObject jo = new JSONObject(json);
            if (jo.optInt("statusCode") != 424) { 
                throw new Exception("RESTTest.missingResourcesTest() failed, status code not 420");
            }
            if (jo.optJSONObject("body") != null) {
                throw new Exception("RESTTest.missingResourcesTest() failed, body not empty");
            }
        }
    }

    @Test
    public void missingSubjectTest() throws Exception {
        Form params = new Form();
        // params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("admin", adminToken.getTokenID().toString());
        params.add("realm", REALM);

        try {
            entitlementClient.queryParams(params).accept(
                "application/json").get(String.class);
            throw new Exception(
                "RESTTest.missingSubjectTest: no exception thrown.");
        } catch (UniformInterfaceException e) {
            int errorCode = e.getResponse().getStatus();
            if (errorCode != 421) {
                throw new Exception(
                    "RESTTest.missingSubjectTest: incorrect error code");
            }
            String json = e.getResponse().getEntity(String.class);
            JSONObject jo = new JSONObject(json);
            if (jo.optInt("statusCode") != 421) {
                throw new Exception("RESTTest.missingSubjectTest() failed, status code not 420");
            }
            if (jo.optJSONObject("body") != null) {
                throw new Exception("RESTTest.missingSubjectTest() failed, body not empty");
            }
        }
    }

    @Test
    public void missingActionTest() throws Exception {
        Form params = new Form();
        params.add("subject", user.getUniversalId());
        params.add("resource", RESOURCE_NAME + "/index.html");
        // params.add("action", "GET");
        params.add("env", ATTR_NAME + "=" + ATTR_VAL);
        params.add("admin", adminToken.getTokenID().toString());
        params.add("realm", REALM);

        try {
            decisionClient.queryParams(params).accept(
                "text/plain").get(String.class);
            throw new Exception(
                "RESTTest.missingActionTest: no exception thrown.");
        } catch (UniformInterfaceException e) {
            int errorCode = e.getResponse().getStatus();
            if (errorCode != 422) {
                throw new Exception(
                    "RESTTest.missingActionTest: incorrect error code");
            }
        }
    }

}
