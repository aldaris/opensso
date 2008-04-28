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
 * $Id: PostAuthProcessingTest.java,v 1.1 2008-04-28 18:22:22 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.authentication;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.IdType;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import com.sun.identity.qatest.common.IDMCommon;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
public class PostAuthProcessingTest extends TestCommon {

    private AuthTestConfigUtil moduleConfig;
    private ResourceBundle testResources;
    private List moduleConfigData;
    private Set oriAuthAttrValues;
    private Set userProfileValSet;    
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String configrbName = "authenticationConfigData";
    private String strServiceName = "iPlanetAMAuthService";
    private IDMCommon idmc;
    private String testUserName;
    private String testUserPass;
    String testUserStatus;
    private int noOfAttributes;
    private Map userAttrMap;

    /**
     * Default Constructor
     */ 
    public PostAuthProcessingTest() {
        super("PostAuthProcessingTest");
        testResources = ResourceBundle.getBundle("PostAuthProcessingTest");
        idmc = new IDMCommon();
        moduleConfig = new AuthTestConfigUtil(configrbName);
        userProfileValSet = new HashSet();
    }

    /**
     * Reads the necessary test configuration and prepares the system
     * for Authentication related properties for testing
     * - Create module instance and create users wutg required attributes
     */
    @Parameters({"testModule", "testRealm"})
    @BeforeClass(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup(String testModule, String testRealm) 
    throws Exception {
        Object[] params = {testModule, testRealm};
        entering("setup", params);
        SSOToken realmToken = null;
        SSOToken idToken = null;
        SSOToken serviceToken = null;
        
        try {
            testUserName = testResources.getString("am-auth-postprocessing-" + 
                    "username");
            testUserPass = testResources.getString("am-auth-postprocessing-" +
                    "password");
            
            log(Level.FINEST, "setup", "testRealm: " + testRealm);
            log(Level.FINEST, "setup", "testModule: " + testModule);
            log(Level.FINEST, "setup", "userName: " + testUserName);
            log(Level.FINEST, "setup", "userPassword: " + testUserPass);

            Reporter.log("TestRealm: " + testRealm);
            Reporter.log("TestModule: " + testModule);
            Reporter.log("UserName: " + testUserName);
            Reporter.log("UserPassword: " + testUserPass);
            
            if (!testRealm.equals("/")) {
                if (testRealm.indexOf("/") != 0) {
                    testRealm = "/" + testRealm;
                }
                log(Level.FINE, "setup", "Creating the sub-realm " + testRealm);
                realmToken = getToken(adminUser, adminPassword, basedn);
                idmc.createSubRealm(realmToken, testRealm);
                if (!idmc.searchRealms(realmToken, testRealm.substring(1)).
                        contains(testRealm.substring(1))) {
                    log(Level.SEVERE, "setup", "Creation of sub-realm " + 
                            testRealm + " failed!");
                    assert false;
                }
            }
            
            noOfAttributes = new Integer(testResources.getString(
                    "am-auth-postprocessing-noofattributes")).intValue();
            userAttrMap = new HashMap();
            Set valSet = new HashSet();
            valSet.add(testUserPass);
            userAttrMap.put("userpassword", valSet);

            for (int i = 0; i < noOfAttributes; i++) {
                String testAttrName = (testResources.getString(
                        "am-auth-postprocessing-attr" + i + "-name")).trim();
                String testAttrValue = (testResources.getString(
                        "am-auth-postprocessing-attr" + i + "-value")).trim();
                valSet = new HashSet();
                valSet.add(testAttrValue);
                log(Level.FINE, "setup", "Setting user attribute " + 
                        testAttrName + " to \'" + testAttrValue + "\'.");
                userAttrMap.put(testAttrName, valSet);
                userProfileValSet.add(testAttrName + "|user." + testAttrName);
            }

            Map modMap = moduleConfig.getModuleData(testModule);
            moduleServiceName = moduleConfig.getModuleServiceName();
            moduleSubConfig = moduleConfig.getModuleSubConfigName();
            moduleSubConfigId = moduleConfig.getModuleSubConfigId();
            moduleConfigData = moduleConfig.getListFromMap(modMap, testModule);

            log(Level.FINEST, "setup", "ModuleServiceName: " +
                    moduleServiceName);
            log(Level.FINEST, "setup", "ModuleSubConfig: " +
                    moduleSubConfig);
            log(Level.FINEST, "setup", "ModuleSubConfigId: " +
                    moduleSubConfigId);
            log(Level.FINE, "setup", "Creating the authentication module " + 
                    moduleSubConfigId);
            moduleConfig.createModuleInstances(testRealm, moduleServiceName,
                    moduleSubConfig, moduleConfigData, moduleSubConfigId);
            
            log(Level.FINE, "setup", "Creating user " + testUserName + "...");
            idToken = getToken(adminUser, adminPassword, testRealm);
            idmc.createIdentity(idToken, testRealm, IdType.USER, 
                    testUserName, userAttrMap);
            Set idSearchResults = idmc.searchIdentities(idToken, testUserName,
                    IdType.USER, testRealm);
            if ((idSearchResults == null) || idSearchResults.isEmpty()) {
                log(Level.SEVERE, "setup", "Failed to create user identity " +
                        testUserName + "...");
                assert false;
            }
            
            serviceToken = getToken(adminUser, adminPassword, basedn);               
            ServiceConfigManager scm = new ServiceConfigManager(serviceToken, 
                    strServiceName, "1.0");
            log(Level.FINE, "setup", "Retrieve ServiceConfig");
            ServiceConfig sc = scm.getOrganizationConfig(testRealm, null);
            Map scAttrMap = sc.getAttributes();
            log(Level.FINEST, "setup", "Map returned from Org config is: " + 
                    scAttrMap);
            oriAuthAttrValues = (Set) scAttrMap.get
                    ("sunAMUserAttributesSessionMapping");
            log(Level.FINEST, "setup", "Original value of " +
                    "sunAMUserAttributesSessionMapping: " + 
                    oriAuthAttrValues);
            Map userProfileMappingAttrs = new HashMap();
            userProfileMappingAttrs.put("sunAMUserAttributesSessionMapping", 
                    userProfileValSet);
            log(Level.FINE, "setup", 
                    "Set sunAMUserAttributesSessionMapping to " + 
                    userProfileMappingAttrs);
            sc.setAttributes(userProfileMappingAttrs);           
        } catch (Exception e) {
            log(Level.SEVERE, "setup", "Exception message = " + e.getMessage());
            if (oriAuthAttrValues == null) {
                oriAuthAttrValues = new HashSet();
            }            
            cleanup(testModule, testRealm);
            throw e;
        } finally {
            if (realmToken != null) {
                destroyToken(realmToken);
            }
            if (idToken != null) {
                destroyToken(idToken);
            }
            if (serviceToken != null) {
                destroyToken(serviceToken);
            }
        }
        exiting("setup");
    }

    /**
     * This test verifies the account login status for the users with 
     * different account management attributes.
     */
    @Parameters({"testModule", "testRealm"})
    @Test(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testUserProfileMapping(String testModule, String testRealm) 
    throws Exception {
        boolean userPropsFound = true;
        SSOToken userToken  = null;
        String testCaseName = testResources.getString(
                "am-auth-postprocessing-test1-name");
        String testCaseDesc = testResources.getString(
                "am-auth-postprocessing-test1-description");
        log(Level.FINEST, "setup", "testCaseName: " + testCaseName);
        log(Level.FINEST, "setup", "testCaseDescription: " + testCaseDesc);
        Reporter.log("TestCaseName: " + testCaseName);
        Reporter.log("TestCaseDescription: " + testCaseDesc);
        
        Object[] params = {testRealm};
        entering("testUserProfileMapping", params);

        try {
            userToken = moduleConfig.moduleLogin(testRealm, moduleSubConfig,
                    testUserName, testUserPass);
            if (userToken != null) {
                for (Iterator i = userProfileValSet.iterator(); i.hasNext(); ) {
                    String mapping = (String) i.next();
                    log(Level.FINE, "testUserProfileMapping", 
                            "Obtained mapping " + mapping + 
                            " from user profile mapping set");
                    String[] mappingAttrs = mapping.split("\\|");
                    String profileAttrName = mappingAttrs[0];
                    String sessionAttrName = "am.protected." + mappingAttrs[1];
                    log(Level.FINE, "testUserProfileMapping", 
                            "Retrieving user profile attribute " + 
                            profileAttrName + " ...");
                    String expectedAttrValue = 
                            (String) ((Set)userAttrMap.get(profileAttrName)).
                            iterator().next();
                    log(Level.FINEST, "testUserProfileMapping", 
                            "User profile attribute " + profileAttrName + 
                            " = " + expectedAttrValue);
                    log(Level.FINE, "testUserProfileMapping",
                            "Retrieving session attribute " + sessionAttrName);
                    String retrievedValue =
                            userToken.getProperty(sessionAttrName);
                    log(Level.FINEST, "testUserProfileMapping",
                            "SSOToken attribute " + sessionAttrName + " = " +
                            retrievedValue);
                    if (retrievedValue.equals(expectedAttrValue)) {
                        log(Level.FINEST, "testUserProfileMapping",
                                "Session attribute " + sessionAttrName +
                                " has the value \'" + expectedAttrValue + "\'");
                    } else {
                        log(Level.SEVERE, "testUserProfileMapping",
                                "Session attribute " + sessionAttrName +
                                " had a value of \'" + retrievedValue +
                                "\' instead of expected value \'" +
                                expectedAttrValue + "\'.");
                        userPropsFound = false;
                    }
                }
            } else {
                log(Level.SEVERE, "testUserProfileMapping",
                        "SSOToken for user " + testUserName +
                        " was not retrieved!");
                userPropsFound = false;
            }
        } catch (Exception e) {
            log(Level.SEVERE, "loginModule", "Exception message: " +
                    e.getMessage());
            log(Level.SEVERE, "loginModule", testUserName +
                    " login resulted in an exception.");
            cleanup(testModule, testRealm);
            throw e;
        } finally {
            destroyToken(userToken);
            assert userPropsFound;
        }
        exiting("testUserProfileMapping");
    }
    
    /**
     * Attempt to modify the value of a protected user profile attribute in
     * the <code>SSOToken</code>.
     */
    @Parameters({"testModule", "testRealm"})
    @Test(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testSessionAttrModification(String testModule, String testRealm) 
    throws Exception {
        boolean propertyUnchanged = false;
        String sessionAttrName = null;
        String expectedAttrValue = null;
        SSOToken userToken = null;
        Object[] params = {testRealm};

        entering("testSessionAttrModification", params);
        String testCaseName = testResources.getString(
                "am-auth-postprocessing-test2-name");
        String testCaseDesc = testResources.getString(
                "am-auth-postprocessing-test2-description");
        log(Level.FINEST, "setup", "testCaseName: " + testCaseName);
        log(Level.FINEST, "setup", "testCaseDescription: " + testCaseDesc);
        Reporter.log("TestCaseName: " + testCaseName);
        Reporter.log("TestCaseDescription: " + testCaseDesc);

        try {
            userToken = moduleConfig.moduleLogin(testRealm, moduleSubConfig,
                    testUserName, testUserPass);
            if (userToken != null) {
                Iterator i = userProfileValSet.iterator();
                String mapping = (String) i.next();
                String[] mappingAttrs = mapping.split("\\|");
                String profileAttrName = mappingAttrs[0];
                sessionAttrName = "am.protected." + mappingAttrs[1];
                expectedAttrValue = (String) ((Set) userAttrMap.get(
                        profileAttrName)).iterator().next();
                log(Level.FINE, "testUserProfileMapping",
                        "Attempting to set session attribute " +
                        sessionAttrName);
                userToken.setProperty(sessionAttrName, "wrongvalue");
            } else {
                log(Level.SEVERE, "testSessionAttrModification",
                        "SSOToken for user " + testUserName +
                        " was not retrieved!");
            }
        } catch (SSOException ssoe) {
            log(Level.FINEST, "testSessionAttrModification", ssoe.getMessage());
            log(Level.FINE, "testSessionAttrModification",
                    "Verifying the value of the session attribute " +
                    sessionAttrName);
            String retrievedValue = userToken.getProperty(sessionAttrName);
            if (retrievedValue.equals(expectedAttrValue)) {
                log(Level.FINEST, "testSessionAttrModification",
                        "Session attribute " + sessionAttrName +
                        " has the value \'" + expectedAttrValue + "\'");
                propertyUnchanged = true;
            } else {
                log(Level.SEVERE, "testSessionAttrModification",
                        "Session attribute " + sessionAttrName +
                        " had a value of \'" + retrievedValue +
                        "\' instead of expected value \'" +
                        expectedAttrValue + "\'.");
            }
        } catch (Exception e) {
            log(Level.SEVERE, "loginModule", "Exception message: " +
                    e.getMessage());
            log(Level.SEVERE, "loginModule", testUserName +
                    " login resulted in an exception.");
            cleanup(testModule, testRealm);
            throw e;
        } finally {
            destroyToken(userToken);
            assert propertyUnchanged;
        }
    }
    
    /**
     * performs cleanup after tests are done.
     */
    @Parameters({"testModule", "testRealm"})
    @AfterClass(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void cleanup(String testModule, String testRealm) 
    throws Exception {
        Object[] params = {testModule, testRealm};
        entering("cleanup", params);
        SSOToken realmToken = null;
        SSOToken idToken = null;
        SSOToken serviceToken = null;
        
        try {
            log(Level.FINE, "cleanup", "Deleting module instance " + 
                    moduleSubConfig + "in realm " + testRealm + " ...");
            moduleConfig.deleteModuleInstances(testRealm, moduleServiceName,
                    moduleSubConfig);
            
            log(Level.FINE, "cleanup", "Deleting user " + testUserName + "...");
            idToken = getToken(adminUser, adminPassword, testRealm);
            idmc.deleteIdentity(idToken, testRealm, IdType.USER, testUserName);
            
            log(Level.FINE, "cleanup", "Restoring original value of " +
                    "sunAMUserAttributesSessionMapping");
            serviceToken = getToken(adminUser, adminPassword, basedn);               
            ServiceConfigManager scm = new ServiceConfigManager(serviceToken, 
                    strServiceName, "1.0");
            ServiceConfig sc = scm.getOrganizationConfig(testRealm, null);
            Map userProfileMappingAttrs = new HashMap();
            userProfileMappingAttrs.put("sunAMUserAttributesSessionMapping", 
                    oriAuthAttrValues);
            log(Level.FINE, "cleanup", 
                    "Set sunAMUserAttributesSessionMapping to " + 
                    oriAuthAttrValues);
            sc.setAttributes(userProfileMappingAttrs);    

            if (!testRealm.equals("/")) {
                log(Level.FINE, "cleanup", "Deleting the sub-realm " + 
                        testRealm);
                realmToken = getToken(adminUser, adminPassword, basedn);
                idmc.deleteRealm(realmToken, testRealm);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (idToken != null) {
                destroyToken(idToken);
            }
            if (serviceToken != null) {
                destroyToken(serviceToken);
            }
            if (realmToken != null) {
                destroyToken(realmToken);
            }
        }
        exiting("cleanup");
    }
}
