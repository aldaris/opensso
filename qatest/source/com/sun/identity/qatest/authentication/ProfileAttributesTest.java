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
 * $Id: ProfileAttributesTest.java,v 1.11 2009-01-26 23:47:48 nithyas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import com.sun.identity.qatest.common.authentication.AuthTestsValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class is called by the <code>ProfileAttributesTest</code>.
 * Performs the tests for User profile attributes .Test cases
 * coveres are Global Test cases
 * AMSubRealmAuth,Core_5,Core_7
 * 
 */
public class ProfileAttributesTest extends TestCommon {
    
    private ResourceBundle testResources;
    private String testModule;
    private String locTestProfile;
    private String testAttribute;
    private boolean createTestUser;
    private String testUser;
    private String testUserpass;
    private String testPassmsg;
    private String testURL;
    private String servicename = "iPlanetAMAuthService";
    private List<String> attributevalues = new ArrayList<String>();
    private FederationManager fm;
    private WebClient webClient;
    private List moduleConfigData;
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String testModName;
    private String url;
    private String ssoadmURL;
    private String logoutURL;
    private String configrbName = "authenticationConfigData";
    private List<String> testUserList = new ArrayList<String>();
    
    /**
     * Default Constructor
     **/
    public ProfileAttributesTest() {
        super("ProfileAttributesTest");
        url = getLoginURL("/");
        logoutURL = protocol + ":" + "//" + host + ":" + port + 
                uri + "/UI/Logout";
        ssoadmURL  = protocol + ":" + "//" + host + ":" + port + uri ;
        fm = new FederationManager(ssoadmURL);
    }
    
    /**
     * Reads the necessary test configuration and prepares the system
     * for user profile testing
     * - Create module instances
     * - Sets the profile attribute
     * - Create Users , If needed
     */
    @Parameters({"testProfile"})
    @BeforeClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void setup(String testProfile)
    throws Exception {
        Object[] params = {testProfile};
        entering("setup", params);
        webClient = new WebClient();
        try {
            locTestProfile = testProfile;
            testAttribute = "am-auth-" + locTestProfile ;
            testResources = ResourceBundle.getBundle("authentication" +
                    fileseparator + "ProfileAttributesTest");
            testModule = testResources.getString(testAttribute + 
                    "-test-module");
            String createUserProp = testResources.getString(testAttribute
                    + "-test-createUser");
            createTestUser = new Boolean(createUserProp).booleanValue();
            testUser = testResources.getString(testAttribute + 
                    "-test-username");
            testUser.trim();
            testUserpass = testResources.getString(testAttribute
                    + "-test-userpassword");
            testUserpass.trim();
            testPassmsg = testResources.getString(testAttribute + 
                    "-test-passmsg");
            
            log(Level.FINEST, "setup", "profileAttribute: " + locTestProfile);
            log(Level.FINEST, "setup", "testModule: " + testModule);
            log(Level.FINEST, "setup", "createTestUser: " + createTestUser);
            log(Level.FINEST, "setup", "testUser: " + testUser);
            log(Level.FINEST, "setup", "testUserPassword: " + testUserpass);
            log(Level.FINEST, "setup", "testPassmsg: " + testPassmsg);
            Reporter.log("Profile Creation Attribute: " + locTestProfile);
            Reporter.log("Auth Module: " + testModule);
            Reporter.log("Test Creates User: " + createTestUser);
            Reporter.log("User: " + testUser);
            Reporter.log("User Password: " + testUserpass);
            Reporter.log("Test Passed Msg: " + testPassmsg);
            
            createModule(testModule);
            testURL = url + "?module=" + moduleSubConfig;
            String attributeVal = getProfileAttribute(locTestProfile);
            attributevalues.add(attributeVal);
            consoleLogin(webClient, url, adminUser, adminPassword);
            log(Level.FINE, "setup", "Setting profile attribute to " + 
                    locTestProfile + " in the " + servicename + " service.");
            if (FederationManager.getExitCode(fm.setSvcAttrs(webClient, realm, 
                    servicename, attributevalues)) != 0) {
                log(Level.SEVERE, "setup", "setSvcAttrs ssoadm command failed");
                assert false;
            }
            
            testUserList.add(testUser);
            if (createTestUser) {
                createUser(testUser, testUserpass);
            }           
        } catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            cleanup();
            throw e;
        } finally {
            consoleLogout(webClient, logoutURL);
        }
        exiting("setup");
    }
    
    /*
     * Validate the profile tests
     */
    @Test(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec", 
      "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void testProfile()
    throws Exception {
        entering("testProfile", null);
        log(Level.FINEST, "testProfile", "Description: Test authentication " +
                "with iplanet-am-auth-dynamic-profile-creation set to " + 
                locTestProfile);
        Reporter.log("Description: Test authentication with " + 
                "iplanet-am-auth-dynamic-profile-creation set to " + 
                locTestProfile);
        Map executeMap = new HashMap();
        executeMap.put("Loginuser", testUser);
        executeMap.put("Loginpassword", testUserpass);
        executeMap.put("Passmsg", testPassmsg);
        executeMap.put("loginurl", testURL);
        executeMap.put("profileattr", locTestProfile);
        AuthTestsValidator profileTestValidator =
                new AuthTestsValidator(executeMap);
        profileTestValidator.testProfile();
        exiting("testProfile");
    }
    
    /**
     * performs cleanup after tests are done.
     */
    @AfterClass(groups={"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", 
      "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void cleanup()
    throws Exception {
        entering("cleanup", null);
        if (webClient == null) {
            webClient = new WebClient();
        }
        try {
            log(Level.FINEST, "cleanup", url);
            List<String> listModInstance = new ArrayList<String>();
            listModInstance.add(moduleSubConfig);
            consoleLogin(webClient, url, adminUser, adminPassword);
            String attributeVal = getProfileAttribute("required");
            attributevalues.clear();
            attributevalues.add(attributeVal);
            log(Level.FINE, "cleanup", "Resetting attribute value " + 
                    attributeVal + " in service " + servicename + ".");
            if (FederationManager.getExitCode(fm.setSvcAttrs(webClient, realm, 
                    servicename, attributevalues)) != 0) {
                log(Level.SEVERE, "setup", "setSvcAttrs ssoadm command failed");
            }
            
            log(Level.FINE, "cleanup", "Deleting authentication module(s) " +
                    listModInstance + "...");
            if (FederationManager.getExitCode(fm.deleteAuthInstances(webClient,
                    realm, listModInstance)) != 0) {
                log(Level.SEVERE, "cleanup", 
                        "deleteAuthInstances ssoadm command failed");
            }
            
            if (testUserList != null && !testUserList.isEmpty()) {
                log(Level.FINE, "cleanup", "Deleting user(s) " + testUserList + 
                        " ...");
                if (FederationManager.getExitCode(fm.deleteIdentities(webClient, 
                        realm, testUserList, "User")) != 0) {
                    log(Level.SEVERE, "cleanup", 
                            "deleteIdentities ssoadm command failed");
                }
            }
        } catch(Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
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
            moduleConfig.createModuleInstances(moduleServiceName,
                    moduleSubConfig, moduleConfigData, moduleSubConfigId);
        } catch(Exception e) {
            log(Level.SEVERE, "createModule", e.getMessage());
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
        log(Level.FINE, "createUser", "Creating user " + newUser + " ...");
        
        try {
            if (FederationManager.getExitCode(fm.createIdentity(webClient, 
                    realm, newUser, "User", userList)) != 0) {
                log(Level.SEVERE, "createUser", 
                        "createIdentity ssoadm command failed");
                assert false;
            }
        } catch(Exception e) {
            log(Level.SEVERE, "createUser", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the profile attribute based on the profile test performed
     * @param profile - the value which indicates how the profile creation 
     * should be set.
     * @return a String containing the profile creation attribute name/value 
     * pair to update in the authentication service.
     */
    private String getProfileAttribute(String profile){
        String profileAttribute = null;
        if (profile.equals("dynamic")) {
            profileAttribute = "iplanet-am-auth-dynamic-profile-creation=true";
        } else if(profile.equals("required")) {
            profileAttribute = "iplanet-am-auth-dynamic-profile-creation=false";
        } else {
            profileAttribute = 
                    "iplanet-am-auth-dynamic-profile-creation=ignore";
        }
        return profileAttribute;
    }
}
