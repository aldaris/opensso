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
 *$Id: AccountLockoutTest.java,v 1.8 2009-01-26 23:47:43 nithyas Exp $*
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.iplanet.sso.SSOToken;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.SMSCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import com.sun.identity.qatest.common.authentication.AuthTestsValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class is called by the <code>AccountLockoutTest</code>.
 * Performs the tests for User Account Lockout and warnings
 * for the number of set login failure attempts. Test cases covered
 * AccountLock_2, AccountLock_3, AccountLock_4, AccountLock_6, AccountLock_9,
 * AccountLock_10, AccountLock_11, AccountLock_13, AccountLock_14
 */
public class AccountLockoutTest extends TestCommon {

    private ResourceBundle testResources;
    private String testModule;
    private String cleanupFlag;
    private boolean debug;
    private String createUserProp;
    private boolean userExists;
    private String lockUser;
    private String lockUserpass;
    private String nslockUser;
    private String nslockUserpass;
    private String lockStatusUser;
    private String lockStatusUserpass;
    private String lockAttrUser;
    private String lockAttrUserpass;
    private String warnUser;
    private String warnUserpass;
    private String lockAttrName;
    private String lockAttrValue;
    private String lockStatusAttrName;
    private String lockStatusAttrValue;
    private String nslockAttrName;
    private String nslockAttrValue;
    private String lockoutAttempts;
    private String warningAttempts;
    private String testURL;
    private String serviceName = "iPlanetAMAuthService";
    private String lockoutPassmsg;
    private String warnPassmsg;
    private String failpage;
    private FederationManager fm;
    private WebClient webClient;
    private List moduleConfigData;
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String url;
    private String ssoadmURL;
    private String logoutURL;
    private String configrbName = "authenticationConfigData";
    private List<String> testUserList = new ArrayList<String>();
    private List<String> userAttrList;
    private Map authServiceAttrs;

    /**
     * Default Constructor
     **/
    public AccountLockoutTest() {
        super("AccountLockoutTest");
        url = getLoginURL("/");
        logoutURL = protocol + ":" + "//" + host + ":" + port + uri + 
                "/UI/Logout";
        ssoadmURL = protocol + ":" + "//" + host + ":" + port + uri ;
        fm = new FederationManager(ssoadmURL);
    }

    /**
     * Reads the necessary test configuration and prepares the system
     * for Account Lockout testing
     * - Create module instances
     * - Enables the Account Lockout
     * - Sets the lockout attributes
     * - Create Users , If needed
     */
    @BeforeClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void setup() 
    throws Exception {
        SSOToken serviceToken = null;  
        Map authAttrMap = new HashMap();
        entering("setup", null);
        webClient = new WebClient();
        try {
            testResources = ResourceBundle.getBundle("authentication" +
                    fileseparator + "AccountLockoutTest");
            testModule = testResources.getString("am-auth-lockout-test-module");
            createUserProp = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-createTestUser");
            userExists = new Boolean(createUserProp).booleanValue();
            createModule(testModule);
            lockUser = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockusername");
            lockUser.trim();
            lockUserpass = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockuserpassword");
            lockUserpass.trim();
            lockoutPassmsg = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lock-passmsg");
            warnUser = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-warnusername");
            warnUser.trim();
            warnUserpass = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-warnuserpassword");
            warnUserpass.trim();
            warningAttempts = testResources.getString(
                    "am-auth-lockout-test-warning-attempts");
            lockoutAttempts = testResources.getString(
                    "am-auth-lockout-test-lockout-attempts");
            warnPassmsg = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-warn-passmsg");
            failpage = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-fail-page");
            testURL = url + "?module=" + testModule;
 
            Set valSet = new HashSet();
            valSet.add("true");
            authAttrMap.put("iplanet-am-auth-login-failure-lockout-mode", 
                    valSet);
            valSet = new HashSet();
            valSet.add(warningAttempts);
            authAttrMap.put("iplanet-am-auth-lockout-warn-user", valSet);
            valSet = new HashSet();
            valSet.add(lockoutAttempts);
            authAttrMap.put("iplanet-am-auth-login-failure-count", valSet);            

            log(Level.FINE, "setup", "Retrieving attribute values in " + 
                    serviceName + " ...");
            serviceToken = getToken(adminUser, adminPassword, realm);
            SMSCommon smsc = new SMSCommon(serviceToken);
            authServiceAttrs = smsc.getAttributes(serviceName, realm, 
                    "Organization");
           
            consoleLogin(webClient, url, adminUser, adminPassword);
            log(Level.FINE, "setup", "Setting the " +
                    "iplanet-am-auth-login-failure-lockout-mode, " +
                    "iplanet-am-auth-lockout-warn-user, and " +
                    "iplanet-am-auth-login-failure-count attributes in the" +
                    serviceName + " service ...");
            smsc.updateServiceAttrsRealm(serviceName, realm, authAttrMap);

            if (!userExists) {
                userAttrList = new ArrayList<String>();
                createUser(lockUser, lockUserpass, userAttrList);
                userAttrList = new ArrayList<String>();
                createUser(warnUser, warnUserpass, userAttrList);
            }
            nslockUser = (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-nslockusername")).trim();
            nslockUserpass =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-nslockuserpassword")).trim();
            nslockAttrName =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-nslockoutattrname")).trim();
            nslockAttrValue =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-nslockoutattrvalue")).trim();
            userAttrList = new ArrayList<String>();
            userAttrList.add(nslockAttrName + "=" + nslockAttrValue);
            createUser(nslockUser, nslockUserpass, userAttrList);
            lockStatusUser = (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockstatususer")).trim();
            lockStatusUserpass =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockstatususerpass")).trim();
            lockStatusAttrName =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockstatusattrname")).trim();
            lockStatusAttrValue =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockstatusattrvalue")).trim();
            userAttrList = new ArrayList<String>();
            createUser(lockStatusUser, lockStatusUserpass, userAttrList);
            lockAttrUser = (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockAttrUser")).trim();
            lockAttrUserpass =
                    (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockAttrUserpass")).trim();
            lockAttrName = (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockoutattrname")).trim();
            lockAttrValue = (testResources.getString("am-auth-lockout-test-" +
                    testModule + "-lockoutattrvalue")).trim();
            userAttrList = new ArrayList<String>();
            createUser(lockAttrUser, lockAttrUserpass, userAttrList);
        } catch (AssertionError ae) {
            log(Level.SEVERE, "setup",
                    "Calling cleanup due to failed ssoadm exit code ...");
            cleanup();
            throw ae;
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
            if (serviceToken != null) {
                destroyToken(serviceToken);
            }
        }
        exiting("setup");
    }

    /**
     * Validate the Account Lockout tests
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void validateAccountLockTest() 
    throws Exception {
        entering("validateAccountLockTest", null);
        Map executeMap = new HashMap();
        try {
            executeMap.put("Loginuser", lockUser);
            executeMap.put("Loginpassword", lockUserpass);
            executeMap.put("Loginattempts", lockoutAttempts);
            executeMap.put("Passmsg", lockoutPassmsg);
            executeMap.put("loginurl", testURL);
            executeMap.put("failpage", failpage);
            AuthTestsValidator lockTestValidator =
                    new AuthTestsValidator(executeMap);
            lockTestValidator.testAccountLockout();
        } catch (Exception e) {
            log(Level.FINE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("Loginuser" + lockUser);
            Reporter.log("TestCaseName: validateAccountLockTest");
            Reporter.log("loginurl" + testURL);
            Reporter.log("TestCaseDescription: This test is to verify the " +
                    "basic account lockout functionality");
        }
        exiting("validateAccountLockTest");
    }

    /**
     * Validate the warning tests
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void validateWarningTest() 
    throws Exception {
        entering("validateWarningTest", null);
        Map executeMap = new HashMap();
        try {
            executeMap.put("Loginuser", warnUser);
            executeMap.put("Loginpassword", warnUserpass);
            executeMap.put("Loginattempts", warningAttempts);
            executeMap.put("Passmsg", warnPassmsg);
            executeMap.put("loginurl", testURL);
            executeMap.put("failpage", failpage);
            AuthTestsValidator warnTestValidator =
                    new AuthTestsValidator(executeMap);
            warnTestValidator.testAccountLockWarning();
        } catch (Exception e) {
            log(Level.FINE, "validateWaringTest", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("Loginuser" + lockUser);
            Reporter.log("TestCaseName:validateWarningTest");
            Reporter.log("loginurl" + testURL);
            Reporter.log("TestCaseDescription: This test is to verify the " +
                    "warnings after the authentication failure ");
        }
        exiting("validateWarningTest");
    }

    /**
     * Validate the inetuserstatus attribute value after the lockout
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void validateAccountLockUserStatusTest()
    throws Exception {
        entering("validateAccountLockUserStatusTest", null);
        Map executeMap = new HashMap();
        try {
            executeMap.put("Loginuser", lockStatusUser);
            executeMap.put("Loginpassword", lockStatusUserpass);
            executeMap.put("Loginattempts", lockoutAttempts);
            executeMap.put("Passmsg", lockoutPassmsg);
            executeMap.put("loginurl", testURL);
            executeMap.put("failpage", failpage);
            AuthTestsValidator physicalLockTestValidator =
                    new AuthTestsValidator(executeMap);
            physicalLockTestValidator.
                    testAccountLockoutUserStatus(lockStatusUser);
        } catch (Exception e) {
            log(Level.FINE, "validateAccountLockUserStatusTest",
                    e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("Loginuser" + lockUser);
            Reporter.log("TestCaseName:validateAccountLockUserStatusTest ");
            Reporter.log("loginurl" + testURL);
            Reporter.log("TestCaseDescription: This test is to verify the " +
                    "inetuserstatus attribute change after lockout ");
        }
        exiting("validateAccountLockUserStatusTest");
    }

    /**
     * Validate the Custom attribute value change after the lockout
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void validateAccountLockUserAttrTest()
    throws Exception {
        entering("validateAccountLockUserAttrTest", null);
        List modifyAttrs = new ArrayList();
        modifyAttrs.add("iplanet-am-auth-lockout-attribute-name=" +
                lockAttrName);
        modifyAttrs.add("iplanet-am-auth-lockout-attribute-value=" +
                lockAttrValue);
        consoleLogin(webClient, url, adminUser, adminPassword);
        if (FederationManager.getExitCode(fm.setSvcAttrs(webClient, realm,
                serviceName, modifyAttrs)) != 0) {
            log(Level.SEVERE, "cleanup", "fm.setSvcAttrs ssoadm command" +
                    "failed");
            assert false;
        }
        Map executeMap = new HashMap();
        try {
            executeMap.put("Loginuser", lockAttrUser);
            executeMap.put("Loginpassword", lockAttrUserpass);
            executeMap.put("Loginattempts", lockoutAttempts);
            executeMap.put("Passmsg", lockoutPassmsg);
            executeMap.put("loginurl", testURL);
            executeMap.put("failpage", failpage);
            AuthTestsValidator lockTestValidator =
                    new AuthTestsValidator(executeMap);
            lockTestValidator.testAccountLockoutUserAttr(lockAttrUser,
                    lockAttrName, lockAttrValue);
        } catch (Exception e) {
            log(Level.FINE, "validateAccountLockUserAttrTest", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("Loginuser" + lockUser);
            Reporter.log("TestCaseName:validateAccountLockUserAttrTest");
            Reporter.log("loginurl" + testURL);
            Reporter.log("TestCaseDescription: This test is to verify the " +
                    "custom attribute change after lockout ");
            consoleLogout(webClient, logoutURL);
        }
        exiting("validateAccountLockUserAttrTest");
    }

    /**
     * Validate the nsaccountlock attribute will not change the value after 
     * lock out
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void validateNsAccountLockTest()
    throws Exception {
        entering("validateNsAccountLockTest", null);
        Map executeMap = new HashMap();
        try {
            executeMap.put("Loginuser", nslockUser);
            executeMap.put("Loginpassword", nslockUserpass);
            executeMap.put("Loginattempts", lockoutAttempts);
            executeMap.put("Passmsg", lockoutPassmsg);
            executeMap.put("loginurl", testURL);
            executeMap.put("failpage", failpage);
            AuthTestsValidator lockTestValidator =
                    new AuthTestsValidator(executeMap);
            lockTestValidator.testAccountLockoutUserAttr(nslockUser,
                    nslockAttrName, nslockAttrValue);
        } catch (Exception e) {
            log(Level.FINE, "validateNsAccountLockTest", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("Loginuser" + lockUser);
            Reporter.log("TestCaseName:validateNsAccountLockTest");
            Reporter.log("loginurl" + testURL);
            Reporter.log("TestCaseDescription: This test is to verify the " +
                    "nsaccountlockout attribute change after lockout ");
        }
        exiting("validateNsAccountLockTest");
    }

    /**
     * performs cleanup after tests are done.
     */
    @AfterClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void cleanup() 
    throws Exception {
        SSOToken serviceToken = null;
        entering("cleanup", null);
        webClient = new WebClient();
        try {
            log(Level.FINEST, "cleanup", url);
            log(Level.FINE, "cleanup", "Users:" + testUserList);
            List<String> listModInstance = new ArrayList<String>();
            listModInstance.add(moduleSubConfig);
            log(Level.FINE, "cleanup", "Deleting auth instance(s)" +
                    listModInstance + "....");
            consoleLogin(webClient, url, adminUser, adminPassword);
            if (FederationManager.getExitCode(fm.deleteAuthInstances(webClient,
                    realm, listModInstance)) != 0) {
                log(Level.SEVERE, "cleanup",
                        "deleteAuthInstances ssoadm command failed");
            }
            if (!testUserList.isEmpty()) {
                log(Level.FINEST, "cleanup", "Deleting user(s) " + 
                        testUserList +  " ...");                       
                if (FederationManager.getExitCode(fm.deleteIdentities(webClient,
                        realm, testUserList, "User")) != 0) {
                    log(Level.SEVERE, "cleanup",
                            "deleteIdentities ssoadm command failed");
                }
            }
            
            serviceToken = getToken(adminUser, adminPassword, realm); 
            SMSCommon smsc = new SMSCommon(serviceToken);
            log(Level.FINE, "setup", "Restoring attribute values in " + 
                    serviceName + " to " + authServiceAttrs);
            smsc.updateServiceAttrsRealm(serviceName, realm, authServiceAttrs);                
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
            if (serviceToken != null) {
                destroyToken(serviceToken);
            }
        }
        exiting("cleanup");
    }

    /**
     * Call Authentication Utility class to create the module instances
     * for a given module instance name
     * @param moduleName
     */
    private void createModule(String mName) 
    throws Exception {
        try {
            AuthTestConfigUtil moduleConfig =
                    new AuthTestConfigUtil(configrbName);
            Map modMap = moduleConfig.getModuleData(mName);
            moduleServiceName = moduleConfig.getModuleServiceName();
            moduleSubConfig = moduleConfig.getModuleSubConfigName();
            moduleSubConfigId = moduleConfig.getModuleSubConfigId();
            moduleConfigData = moduleConfig.getListFromMap(modMap, mName);
            log(Level.FINE, "createModule", "ModuleServiceName :" +
                    moduleServiceName);
            log(Level.FINE, "createModule", "ModuleSubConfig :" +
                    moduleSubConfig);
            log(Level.FINE, "createModule", "ModuleSubConfigId :" +
                    moduleSubConfigId);
            moduleConfig.createModuleInstances(moduleServiceName,
                    moduleSubConfig, moduleConfigData, moduleSubConfigId);
        } catch (Exception e) {
            log(Level.SEVERE, "createModule", e.getMessage());
            e.printStackTrace();
        } finally {
            Reporter.log("createModule: ModuleServiceName :" +
                    moduleServiceName);
            Reporter.log("createModule:ModuleSubConfig:" + moduleSubConfig);
        }
    }

    /**
     * Creates the required test users on the system
     * @param username
     * @param password
     * @param attribute list
     **/
    private void createUser(String newUser, String userpassword,
            List attrList)
    throws Exception {
        attrList.add("sn=" + newUser);
        attrList.add("cn=" + newUser);
        attrList.add("userpassword=" + userpassword);
        attrList.add("inetuserstatus=Active");
        log(Level.FINE, "createUser", "UserattrList " + attrList);
        testUserList.add(newUser);
        try {
            log(Level.FINE, "createUser", "Creating user " + newUser + "...");
            if (FederationManager.getExitCode(fm.createIdentity(webClient,
                    realm, newUser, "User", attrList)) != 0) {
                log(Level.SEVERE, "createUser",
                        "createIdentity ssoadm command failed");
                assert false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "createUser", e.getMessage());
            e.printStackTrace();
        } 
    }
}
