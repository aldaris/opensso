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
 * $Id: ApplicationAuthTest.java,v 1.7 2009-02-03 19:54:20 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.iplanet.sso.SSOToken;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.SMSCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class <code>ApplicationAuthTest</code>.
 * Performs Application Authentication tests.Login module as application
 * with the created agent profile. The token when logged in as application
 * should be non-expiring.
 * 
 */

public class ApplicationAuthTest extends TestCommon {
    
    private SSOToken adminToken;
    private SSOToken ssoToken;
    private ResourceBundle rbg;
    private IDMCommon idmc;
    private String agentId;
    private String agentPassword;
    private AMIdentity amid;
    private AMIdentityRepository idrepo;
    private String strGblRB = "ApplicationAuthTest";
    private AuthTestConfigUtil moduleConfig;
    private String configrbName = "authenticationConfigData";
    private String absoluteRealm;

    /**
     * Default Constructor
     */
    public ApplicationAuthTest() {
        super("ApplicationAuthTest");
        rbg = ResourceBundle.getBundle("authentication" + fileseparator +
                strGblRB);
        agentId = rbg.getString("am-auth-applicationauth-test-agentId");
        agentPassword = rbg.getString("am-auth-applicationauth-test-" +
                "agentPassword");
        idmc = new IDMCommon();
        moduleConfig = new AuthTestConfigUtil(configrbName);
    }
    
    /**
     * Create Agent Profile to perform the application/agent authentiation tests
     * @param testRealm - the realm in which the authentication should be
     * performed.
     */
    @Parameters({"testRealm"})
    @BeforeClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad",
        "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void createAgentProfile(String testRealm)
    throws Exception {
        entering("createAgentProfile", null);
        try {
            log(Level.FINEST, "createAgentProfile", "testRealm: " + testRealm);
            log(Level.FINEST, "createAgentProfile", "agentId: " + agentId);
            log(Level.FINEST, "createAgentProfile", "agentPassword: " 
                    + agentPassword);
            Reporter.log("TestRealm: " + testRealm);
            Reporter.log("AgentID: " + agentId);
            Reporter.log("AgentPassword: " + agentPassword);

            absoluteRealm = testRealm;
            if (!testRealm.equals("/")) {
                if (testRealm.indexOf("/") != 0) {
                    absoluteRealm = "/" + testRealm;
                }
                log(Level.FINE, "setup", "Creating the sub-realm " + testRealm);
                moduleConfig.createRealms(absoluteRealm);
            }
            
            adminToken = getToken(adminUser, adminPassword, realm);
            idrepo = new AMIdentityRepository(adminToken, realm);            
            Map map = new HashMap();
            Set set = new HashSet();

            set.add(agentPassword);
            map.put("userpassword", set);
            set = new HashSet();
            set.add("Active");
            map.put("sunIdentityServerDeviceStatus", set);
            set = new HashSet();
            SMSCommon smsC = new SMSCommon(adminToken);
            if ((smsC.isAMDIT())) {
                 idmc.createIdentity(adminToken, testRealm, IdType.AGENT,
                         agentId, map);
            } else {
                log(Level.FINE, "createAgentProfile", "This is FAM " +
                        "DIT, Agents are part of SM node");
                set.add("webagent");
                map.put("AgentType", set);
                idmc.createIdentity(adminToken, testRealm, IdType.AGENTONLY,
                        agentId, map);
            }
            amid = idmc.getFirstAMIdentity(adminToken, agentId, 
                    IdType.AGENTONLY, testRealm);
        } catch (Exception e) {
            log(Level.SEVERE, "createAgentProfile", e.getMessage());
            deleteAgentProfile(testRealm);
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(adminToken);
        }
        exiting("createAgentProfile");
    }
    
    /**
     * Authenticate using appliation authentication.
     * @param testRealm - the realm in which the authentication should be
     * performed.
     * @param negativeTest - a String indicating whether a negative test
     * should be performed.  negativeTest should be set to "true" if a
     * failed authentication should be attempted.  negativeTest should be to
     * "false" if a successful authentication should be done.
     */
    @Parameters({"testRealm", "negativeTest"})
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec",
        "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void testApplicationAuthPositive(String testRealm,
            String negativeTest)
    throws Exception {
        String authPassword = agentPassword;
        boolean testFailedAuth = Boolean.parseBoolean(negativeTest);
        if (testFailedAuth) {
            authPassword = "not" + agentPassword;
        }
        try {
            AuthContext authContext = new AuthContext(testRealm);
            authContext.login(AuthContext.IndexType.MODULE_INSTANCE, 
                    "Application");
            if (authContext.hasMoreRequirements()) {
                Callback[] callbacks = authContext.getRequirements();
                if (callbacks != null) {
                    addLoginCallbackMessage(callbacks, agentId, authPassword);
                    authContext.submitRequirements(callbacks);
                }
            }
            AuthContext.Status authStatus = authContext.getStatus();
            if (!testFailedAuth) {
                if (authStatus == AuthContext.Status.SUCCESS) {
                    ssoToken = authContext.getSSOToken();
                }
                if (ssoToken != null) {
                    assert (ssoToken.getTimeLeft() > Long.MAX_VALUE/100);
                } else {
                    log(Level.SEVERE, "testApplicationAuthPositive",
                            "SSOToken is null!");
                    assert false;
                }
            } else {
                assert (authStatus == AuthContext.Status.FAILED);
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
     * Clean up the system to its original state
     * @param testRealm - the realm in which the authentication should be
     * performed
     */
    @Parameters({"testRealm"})
    @AfterClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad",
        "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void deleteAgentProfile(String testRealm)
    throws Exception {
        entering("deleteAgentProfile", null);
        try {
            log(Level.FINE, "deleteAgentProfile", 
                    "Deleting the agent identity " + agentId + " ...");  
            adminToken = getToken(adminUser, adminPassword, realm);
            if (amid == null) {
                amid = idmc.getFirstAMIdentity(adminToken, agentId, 
                        IdType.AGENTONLY, testRealm);
            }            
            idrepo = new AMIdentityRepository(adminToken, testRealm);
            Set idDelete = new HashSet();
            idDelete.add(amid);
            idrepo.deleteIdentities(idDelete);
            if (!absoluteRealm.equals("/")) {
                log(Level.FINE, "cleanup", "Deleting the sub-realm " +
                        absoluteRealm);
                idmc.deleteRealm(adminToken, absoluteRealm);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "deleteAgentProfile", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(adminToken);
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
