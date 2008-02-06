/* The contents of this file are subject to the terms
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
 * $Id: ApplicationAuthTest.java,v 1.2 2008-02-06 18:50:21 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.TestCommon;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * This class <code>ApplicationAuthTest</code>.
 * Performs Application Authentication tests.Login module as application
 * with the created agent profile. The token when logged in as application
 * should be non-expiring.
 * 
 */

public class ApplicationAuthTest extends TestCommon {
    
    private SSOToken admintoken;
    private SSOToken ssoToken;
    private ResourceBundle rbg;
    private IDMCommon idmc;
    private String agentId;
    private String agentPassword;
    private String strGblRB = "ApplicationAuthTest";

    /**
     * Default Constructor
     */
    public ApplicationAuthTest() {
        super("ApplicationAuthTest");
        rbg = ResourceBundle.getBundle(strGblRB);
        agentId = rbg.getString("am-auth-applicationauth-test-agentId");
        agentPassword = rbg.getString("am-auth-applicationauth-test-" +
                "agentPassword");
        idmc = new IDMCommon();
    }
    
    /**
     * Create Agent Profile to perform the application/agent authentiation tests
     */
    @BeforeClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void createAgentProfile() 
    throws Exception {
        entering("createAgentProfile", null);
        try {
            log(Level.FINEST, "createAgentProfile", "agentId: " + agentId);
            log(Level.FINEST, "createAgentProfile", "agentPassword: " 
                    + agentPassword);
            Reporter.log("AgentID: " + agentId);
            Reporter.log("AgentPassword: " + agentPassword);
            Map map = new HashMap();
            Set set = new HashSet();
            set.add(agentPassword);
            map.put("userpassword", set);
            set = new HashSet();
            set.add("Active");
            map.put("sunIdentityServerDeviceStatus", set);
            admintoken = getToken(adminUser, adminPassword, basedn);
            log(Level.FINE, "createAgentProfile", 
                    "Creating the agent identity " + agentId + " ...");
            idmc.createIdentity(admintoken, realm, IdType.AGENT, agentId, map);
        } catch (Exception e) {
            log(Level.SEVERE, "createAgentProfile", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(admintoken);
        }
        exiting("createAgentProfile");
    }
    
    /**
     * Login as application auth and check if the token is non-expiring
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testApplicationAuthPositive()
    throws Exception {
        try {
            AuthContext authContext = new AuthContext(realm);
            authContext.login(AuthContext.IndexType.MODULE_INSTANCE, 
                    "Application");
            if (authContext.hasMoreRequirements()) {
                Callback[] callbacks = authContext.getRequirements();
                if (callbacks != null) {
                    addLoginCallbackMessage(callbacks, agentId, agentPassword);
                    authContext.submitRequirements(callbacks);
                }
            }
            if (authContext.getStatus() == AuthContext.Status.SUCCESS) {
                ssoToken = authContext.getSSOToken();
            }
            if (ssoToken != null) {
                if ((ssoToken.getTimeLeft() > Long.MAX_VALUE/100)){
                    assert true;
                } else {
                    assert false;
                }
            } else {
                log(Level.SEVERE, "testApplicationAuthPositive", 
                        "SSOToken is null!");
                assert false;
            }
        } catch (AuthLoginException ale) {
            log(Level.SEVERE, "testApplicationAuthPositive", ale.getMessage());
            ale.printStackTrace();
        } catch (Exception e) {
            log(Level.SEVERE, "testApplicationAuthPositive", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
           destroyToken(ssoToken);
        }
    }
    
    /**
     * Negative test Application Auth Login should fail with invalid credentials
     */
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testApplicationAuthNegative()
    throws Exception {
        try {
            AuthContext authContext = new AuthContext(realm);
            authContext.login(AuthContext.IndexType.MODULE_INSTANCE, 
                    "Application");
            if (authContext.hasMoreRequirements()) {
                Callback[] callbacks = authContext.getRequirements();
                if (callbacks != null) {
                    addLoginCallbackMessage(callbacks, agentId, agentPassword 
                            + "tofail");
                    authContext.submitRequirements(callbacks);
                }
            }
            if (authContext.getStatus() == AuthContext.Status.SUCCESS) {
                assert false;
            } else {
                assert true;
            }
        } catch (AuthLoginException ale) {
            log(Level.SEVERE, "testApplicationAuthNegative", ale.getMessage());
            ale.printStackTrace();
        } catch (Exception e) {
            log(Level.SEVERE, "testApplicationAuthNegative", e.getMessage());
            e.printStackTrace();
            throw e;
        } 
    }

    /**
     * Clean up the system to its original state
     */
    @AfterClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void deleteAgentProfile()
    throws Exception {
        entering("deleteAgentProfile", null);
        try {
            admintoken = getToken(adminUser, adminPassword, basedn);
            log(Level.FINE, "deleteAgentProfile", 
                    "Deleting the agent identity " + agentId + " ...");            
            idmc.deleteIdentity(admintoken, realm, IdType.AGENT, agentId);           
        } catch (Exception e) {
            log(Level.SEVERE, "deleteAgentProfile", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(admintoken);
        }
        exiting("deleteAgentProfile");
    }
    
    /**
     * @param callbacks  array of callbacks
     * @param appUserName  application user name
     * @param appPassword for application user
     */
   private void addLoginCallbackMessage(
           Callback[] callbacks,
           String appUserName,
           String appPassword)
   throws UnsupportedCallbackException {
       for (int i = 0; i < callbacks.length; i++) {
           if (callbacks[i] instanceof NameCallback) {
               NameCallback nameCallback = (NameCallback) callbacks[i];
               nameCallback.setName(appUserName);
           } else if (callbacks[i] instanceof PasswordCallback) {
               PasswordCallback pwdCallback = (PasswordCallback) callbacks[i];
               pwdCallback.setPassword(appPassword.toCharArray());
           }
       }
   }
}
