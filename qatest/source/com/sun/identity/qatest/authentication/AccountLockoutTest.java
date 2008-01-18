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
 * $Id: AccountLockoutTest.java,v 1.3 2008-01-18 00:42:50 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import com.sun.identity.qatest.common.authentication.AuthTestsValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * This class is called by the <code>AccountLockoutTest</code>.
 * Performs the tests for User Account Lockout and warnings
 * for the number of set login failure attempts, test cases covered
 * AccountLock_2,AccountLock_3,AccountLock_6,AccountLock_9,AccountLock_10
 * AccountLock_11,AccountLock_13,AccountLock_14
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
    private String warnUser;
    private String warnUserpass;
    private String lockoutattempts;
    private String warnattempts;
    private String testURL;
    private String servicename = "iPlanetAMAuthService";
    private List<String> attributevalues = new ArrayList<String>();
    private String lockoutPassmsg;
    private String warnPassmsg;
    private String failpage;
    private FederationManager fm;
    private WebClient webClient;
    private List moduleConfigData;
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String testModName;
    private String url;
    private String logoutURL;
    private String configrbName = "authenticationConfigData";
    private List<String> testUserList = new ArrayList<String>();
    
    /**
     * Default Constructor
     **/
    public AccountLockoutTest() {
        super("AccountLockoutTest");
        url = protocol + ":" + "//" + host + ":" + port + uri;
        logoutURL = url + "/UI/Logout";
        fm = new FederationManager(url);
    }
    
    /**
     * Reads the necessary test configuration and prepares the system
     * for Account Lockout testing
     * - Create module instances
     * - Enables the Account Lockout
     * - Sets the lockout attributes
     * - Create Users , If needed
     */
    @BeforeClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void setup()
    throws Exception {
        entering("setup", null);
        webClient = new WebClient();
        try {
            testResources = ResourceBundle.getBundle("AccountLockoutTest");
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
            lockoutattempts =
                    testResources.getString("am-auth-lockout-test-warning-attempts");
            warnattempts =
                    testResources.getString("am-auth-lockout-test-lockout-attempts");
            warnPassmsg = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-warn-passmsg");
            failpage = testResources.getString("am-auth-lockout-test-" +
                    testModule + "-fail-page");
            int ilockattempts = Integer.parseInt(lockoutattempts);
            int iwarnattempts = Integer.parseInt(warnattempts);
            testURL = url + "?module=" + testModule;
            attributevalues.add("iplanet-am-auth-login-failure-lockout-mode=true");
            attributevalues.add("iplanet-am-auth-lockout-warn-user="
                    + warnattempts);
            attributevalues.add("iplanet-am-auth-login-failure-count="
                    + lockoutattempts);
            consoleLogin(webClient, url, adminUser, adminPassword);
            fm.setSvcAttrs(webClient, realm, servicename, attributevalues);
            if (!userExists) {
                createUser(lockUser, lockUserpass);
                createUser(warnUser, warnUserpass);
            }
        } catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("setup");
    }
    
    /*
     * Validate the Account Lockout tests
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void validateAccountLockTest()
    throws Exception {
        entering("validateAccountLockTest", null);
        Map executeMap = new HashMap();
        executeMap.put("Loginuser", lockUser);
        executeMap.put("Loginpassword", lockUserpass);
        executeMap.put("Loginattempts", lockoutattempts);
        executeMap.put("Passmsg", lockoutPassmsg);
        executeMap.put("loginurl", testURL);
        executeMap.put("failpage",failpage);
        AuthTestsValidator lockTestValidator =
                new AuthTestsValidator(executeMap);
        lockTestValidator.testAccountLockout();
        exiting("validateAccountLockTest");
    }
    
    /**
     * Validate the warning tests
     */
    @Test(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void validateWarningTest()
    throws Exception {
        entering("validateWarningTest", null);
        Map executeMap = new HashMap();
        executeMap.put("Loginuser", warnUser);
        executeMap.put("Loginpassword", warnUserpass);
        executeMap.put("Loginattempts", warnattempts);
        executeMap.put("Passmsg", warnPassmsg);
        executeMap.put("loginurl", testURL);
        executeMap.put("failpage",failpage);
        AuthTestsValidator warnTestValidator =
                new AuthTestsValidator(executeMap);
        warnTestValidator.testAccountLockWarning();
        exiting("validateWarningTest");
    }
    
    /**
     * performs cleanup after tests are done.
     */
    @AfterClass(groups={"ds_ds","ds_ds_sec","ff_ds","ff_ds_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        WebClient webClient = new WebClient();
        try {
            log(logLevel, "cleanup", url);
            log(logLevel, "cleanup", "Users:" + testUserList);
            List<String> listModInstance = new ArrayList<String>();
            listModInstance.add(moduleSubConfig);
            log(logLevel, "cleanup", "listModInstance" + listModInstance);
            consoleLogin(webClient, url, adminUser, adminPassword);
            HtmlPage page = (HtmlPage)fm.deleteAuthInstances(webClient,
                    realm, listModInstance);
            log(logLevel, "cleanup", "Page" + page.asXml());
            fm.deleteIdentities(webClient, realm, testUserList, "User");
        } catch(Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("cleanup");
    }
    
    /**
     * Call Authentication Utility class to create the module instances
     * for a given module instance name
     * @param moduleName
     */
    private void createModule(String mName) {
        try {
            AuthTestConfigUtil moduleConfig =
                    new AuthTestConfigUtil(configrbName);
            Map modMap = moduleConfig.getModuleData(mName);
            moduleServiceName = moduleConfig.getModuleServiceName();
            moduleSubConfig = moduleConfig.getModuleSubConfigName();
            moduleSubConfigId = moduleConfig.getModuleSubConfigId();
            moduleConfigData = moduleConfig.getListFromMap(modMap, mName);
            log(logLevel, "createModule", "ModuleServiceName :" +
                    moduleServiceName);
            log(logLevel, "createModule", "ModuleSubConfig :" +
                    moduleSubConfig);
            log(logLevel, "createModule", "ModuleSubConfigId :" +
                    moduleSubConfigId);
            moduleConfig.createModuleInstances(moduleServiceName,
                    moduleSubConfig, moduleConfigData, moduleSubConfigId);
        } catch(Exception e) {
            log(Level.SEVERE, "createModule", e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the required test users on the system
     * @param username
     * @param password
     **/
    private void createUser(String newUser, String userpassword) {
        List<String> userList = new ArrayList<String>();
        userList.add("sn=" + newUser);
        userList.add("cn=" + newUser);
        userList.add("userpassword=" + userpassword);
        userList.add("inetuserstatus=Active");
        log(logLevel, "createUser", "userList " + userList);
        testUserList.add(newUser);
        try {
            fm.createIdentity(webClient, realm, newUser, "User", userList);
        } catch(Exception e) {
            log(Level.SEVERE, "createUser", e.getMessage(), null);
            e.printStackTrace();
        }
    }
}
