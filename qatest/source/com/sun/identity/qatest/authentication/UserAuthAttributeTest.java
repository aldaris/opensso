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
 * $Id: UserAuthAttributeTest.java,v 1.5 2009-01-26 23:47:50 nithyas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import com.sun.identity.qatest.common.authentication.AuthTestsValidator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class performs the tests for setting up different user authentication
 * attibutes and verifies the login status and also verifies that the attributes
 * are set in the user entry correctly. The login is set to the module based
 * login
 */
public class UserAuthAttributeTest extends TestCommon {

    private ResourceBundle testResources;
    private String testModule;
    private FederationManager fm;
    private WebClient webClient;
    private List moduleConfigData;
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String url;
    private String logoutURL;
    private String configrbName = "authenticationConfigData";
    private IDMCommon idmc;
    String testUserName;
    String testCaseName;
    String testCaseDesc;
    String testUserPass;
    String testUserStatus;
    String testAttrName;
    String testAttrValue;
    String testPassMsg;
    int noOfAttributes;
    Map userAttrMap;

    /**
     * Default Constructor
     */
    public UserAuthAttributeTest() {
        super("UserAuthAttributeTest");
        testResources = ResourceBundle.getBundle("authentication" +
                fileseparator + "UserAuthAttributeTest");
        idmc = new IDMCommon();
        url = getLoginURL("/");
        logoutURL = protocol + ":" + "//" + host + ":" + port + uri 
                + "/UI/Logout";
        String ssoadmURL  = protocol + ":" + "//" + host + ":" + port + uri ;
        fm = new FederationManager(ssoadmURL);
    }

    /**
     * Reads the necessary test configuration and prepares the system
     * for Authentication related properties for testing
     * - Create module instance and create users wutg required attributes
     */
    @Parameters({"testcaseName"})
    @BeforeClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void setup(String testcaseName) 
    throws Exception {
        Object[] params = {testcaseName};
        entering("setup", params);
        webClient = new WebClient();
        SSOToken admintoken = null;
        String expireDate = null;
        testCaseName = testcaseName;
        try {
            testUserName = testResources.getString("am-auth-" + testCaseName +
                    "-" + "username");
            testUserPass = testResources.getString("am-auth-" + testCaseName +
                    "-" + "userpassword");
            testPassMsg = testResources.getString("am-auth-" + testCaseName +
                    "-" + "passmessage");
            noOfAttributes = new Integer(testResources.getString("am-auth-" +
                    testCaseName + "-" + "noofattributes")).intValue();
            testCaseDesc = testResources.getString("am-auth-" + testCaseName +
                    "-" + "description");
            userAttrMap = new HashMap();
            Set valSet = new HashSet();
            valSet.add(testUserPass);
            userAttrMap.put("userpassword", valSet);
            for (int i = 0; i < noOfAttributes; i++) {
                testAttrName = (testResources.getString("am-auth-" +
                        testCaseName + "-" + "attr" + i + "-name")).trim();
                testAttrValue = (testResources.getString("am-auth-" +
                        testCaseName + "-" + "attr" + i + "-value")).trim();
                if (testAttrName.equals("iplanet-am-user-account-life")) {
                    if (testAttrValue.equals("positive")) {
                        Calendar rightNow = Calendar.getInstance();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar working = (Calendar) rightNow.clone();
                        working.add(Calendar.DAY_OF_YEAR, +2);
                        expireDate = (formatter.format(working.getTime()));
                    } else if (testAttrValue.equals("negative")) {
                        Calendar rightNow = Calendar.getInstance();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar working = (Calendar) rightNow.clone();
                        working.add(Calendar.DAY_OF_YEAR, - 2);
                        expireDate = (formatter.format(working.getTime()));
                    }
                    testAttrValue = expireDate;
                }
                valSet = new HashSet();
                valSet.add(testAttrValue);
                userAttrMap.put(testAttrName, valSet);
            }
            admintoken = getToken(adminUser, adminPassword, realm);
            testModule = testResources.
                    getString("am-auth-userauthattributetest-module");
            createModule(testModule);
            idmc.createIdentity(admintoken, realm, IdType.USER, testUserName,
                    userAttrMap);
            log(Level.FINE, "setup", "userName" + testUserName);
            log(Level.FINE, "setup", "testCaseName" + testCaseName);
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(admintoken);
            Reporter.log("TestUserName" + testUserName);
            Reporter.log("Testuserpassword" + testPassMsg);
            Reporter.log("TestCaseName:" + testCaseDesc);
        }
        exiting("setup");
    }

    /**
     * This test verifies the account login status for the users with 
     * different account management attributes.
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void runTest() 
    throws Exception {
        entering("runTest", null);
        try {
            Map executeMap = new HashMap();
            executeMap.put("userName", testUserName);
            executeMap.put("password", testUserPass);
            executeMap.put("modulePassMsg", testPassMsg);
            executeMap.put("modulesubConfig", moduleSubConfig);
            executeMap.put("uniqueIdentifier", testCaseName);
            AuthTestsValidator physicalLockTestValidator =
                    new AuthTestsValidator(executeMap);
            physicalLockTestValidator.testUserLoginAuthAttribute(userAttrMap);
            log(Level.FINE, "runTest", "testcaseName" + testUserName);
            log(Level.FINE, "runTest", "testuserpassword" + testPassMsg);
            log(Level.FINE, "runTest", "testcaseName" + testCaseName);
        } catch (Exception e) {
            log(Level.SEVERE, "runTest", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            Reporter.log("TestcaseName" + testUserName);
            Reporter.log("Testuserpassword" + testPassMsg);
            Reporter.log("TestCaseDescription:" + testCaseDesc);
        }
        exiting("runTest");
    }

    /**
     * performs cleanup after tests are done.
     */
    @AfterClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void cleanup() 
    throws Exception {
        entering("cleanup", null);
        webClient = new WebClient();
        SSOToken adminToken = null;
        try {
            log(Level.FINE, "cleanup", url);
            List<String> listModInstance = new ArrayList<String>();
            listModInstance.add(moduleSubConfig);
            log(Level.FINE, "cleanup", "listModInstance" + listModInstance);
            consoleLogin(webClient, url, adminUser, adminPassword);
            if (FederationManager.getExitCode(fm.deleteAuthInstances(webClient,
                    realm, listModInstance)) != 0) {
                log(Level.SEVERE, "cleanup",
                        "deleteAuthInstances ssoadm command failed");
            }
            adminToken = getToken(adminUser, adminPassword, realm);
            idmc.deleteIdentity(adminToken, realm, IdType.USER, testUserName);
            log(Level.FINE, "cleanup", "User:" + testUserName);
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
            destroyToken(adminToken);
            Reporter.log("TestcaseName" + testUserName);
            Reporter.log("Testuserpassword" + testPassMsg);            
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
        } 
    }
}
