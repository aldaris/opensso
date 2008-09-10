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
 * $Id: SessionUpgrade.java,v 1.9 2008-09-10 15:26:49 cmwesley Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.ChoiceCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class <code>SessionUpgrade</code>.
 * Performs Session Upgrade tests.Session Upgrade is Login to the
 * first module instance and use the token create AuthContext and
 * Login to the second module instance should give the AuthType 
 * secondmoduleinstance|firstmoduleinstance and AuthLevel should be equal to
 * the authentication level of the highest authentication level in the modules
 * used for authentication.
 */
public class SessionUpgrade extends TestCommon {
    
    private int upgradedAuthLevel;
    private int failedAuthLevel;
    private ResourceBundle testResources;
    private String testModules;
    private static String orgName;
    private String url;
    private boolean userExists;
    private String createUserProp;
    private String FirstModuleName;
    private String FirstModuleUserName;
    private String FirstModulePassword;
    private String SecondModuleName;
    private String SecondModuleUserName;
    private String SecondModulePassword;
    private String configrbName = "authenticationConfigData";
    private WebClient webClient;
    private List moduleConfigData;
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String userName;
    private String password;
    private String logoutURL;
    private String absoluteRealm;
    private FederationManager fm;
    private List<String> testUserList = new ArrayList<String>();
    private String upgradedAuthType;
    private String failedAuthType;
    private AuthTestConfigUtil moduleConfig;
    private boolean useMultipleModules;
    
    /**
     * Default Constructor
     */
    public SessionUpgrade() {
        super("SessionUpgrade");
        url = getLoginURL("/");
        logoutURL = protocol + ":" + "//" + host + ":" + port + uri 
                + "/UI/Logout";
        String ssoadmURL = protocol + ":" + "//" + host + ":" + port + uri;
        fm = new FederationManager(ssoadmURL);
        testResources = ResourceBundle.getBundle("authentication" +
                fileseparator + "SessionUpgrade");
        moduleConfig = new AuthTestConfigUtil(configrbName);
    }
    
    /**
     * Set up the system for testing.Create authentication instances
     * create users before testing the session Upgrade feature
     */
    @Parameters({"forceClientAuth", "testRealm", "useDifferentModules"}) 
    @BeforeClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void setup(String forceClientAuth, String testRealm, 
            String useDifferentModules) 
    throws Exception {
        Object[] params = {forceClientAuth, testRealm, useDifferentModules};
        entering("setup", params);
        SSOToken idToken = getToken(adminUser, adminPassword, realm);
        webClient = new WebClient();
        try {
            testModules = testResources.getString("am-auth-test-modules");
            orgName = testResources.getString("am-auth-test-realm");
            FirstModuleName = testResources.getString("am-auth-session" +
                    "-test-firstmodule");
            FirstModuleUserName = testResources.getString("am-auth-session" +
                    "-test-firstuser");
            FirstModulePassword = testResources.getString("am-auth-session-" +
                    "test-firstpasswd");
            SecondModuleName = testResources.getString("am-auth-session-" +
                    "test-secondmodule");
            SecondModuleUserName = testResources.getString("am-auth-session-" +
                    "test-seconduser");
            SecondModulePassword = testResources.getString("am-auth-session-" +
                    "test-secondpasswd");
            useMultipleModules = Boolean.parseBoolean(useDifferentModules);

            failedAuthLevel = new Integer(testResources.getString(
                    "am-auth-session-failed-auth-level")).intValue();            
            
            absoluteRealm = testRealm;
            if (!testRealm.equals("/")) {
                if (testRealm.indexOf("/") != 0) {
                    absoluteRealm = "/" + testRealm;
                }
                log(Level.FINE, "setup", "Creating the sub-realm " + testRealm);
                moduleConfig.createRealms(absoluteRealm);
            }
            
            log(Level.FINEST, "setup", "OrgName " + testRealm);
            log(Level.FINEST, "setup", "FirstModuleName: " + FirstModuleName);
            log(Level.FINEST, "setup", "FirstModuleUserName: " + 
                    FirstModuleUserName);
            log(Level.FINEST, "setup", "FirstModuleUserPass: " + 
                    FirstModulePassword);
            log(Level.FINEST, "setup", "forceClientAuthEnabled: " +
                    forceClientAuth);
            
            Reporter.log("OrgName: " + testRealm);
            Reporter.log("FirstModuleName: " + FirstModuleName);
            Reporter.log("FirstModuleUserName: " + FirstModuleUserName);
            Reporter.log("FirstModuleUserPass: " + FirstModulePassword);
            
            String[] moduleTokens = testModules.split(",");
            if (useMultipleModules) {
                log(Level.FINEST, "setup", "SecondModuleName: " + 
                        SecondModuleName);
                log(Level.FINEST, "setup", "SecondModuleUserName: " + 
                        SecondModuleUserName);
                log(Level.FINEST, "setup", "SecondModuleUserPass: " + 
                        SecondModulePassword);                
                Reporter.log("SecondModuleName: " + SecondModuleName);
                Reporter.log("SecondModuleUserName: " + SecondModuleUserName);
                Reporter.log("SecondModuleUserPass: " + SecondModulePassword);
                upgradedAuthLevel = Integer.parseInt(testResources.getString(
                        "am-auth-session-upgraded-auth-level"));                
                upgradedAuthType = SecondModuleName + "|" + FirstModuleName;                
            } else {
                String [] tmpArray = new String[1];
                tmpArray[0] = moduleTokens[0];
                moduleTokens = tmpArray;
                upgradedAuthLevel = 
                        Integer.parseInt(testResources.getString(
                        "am-auth-session-test-firstlevel"));
                upgradedAuthType = FirstModuleName;
            }
            log(Level.FINEST, "setup", "testModules " + testModules); 
            log(Level.FINEST, "setup", "upgradedAuthLevel: " + 
                    upgradedAuthLevel);            
            Reporter.log("testModuleNames: " + testModules);            
            Reporter.log("ForceClientAuthentication: " + forceClientAuth);
            Reporter.log("UpgradedAuthLevel: " + upgradedAuthLevel);
            
            failedAuthType = FirstModuleName;
            for (String modName: moduleTokens) {
                log(Level.FINE, "setup", "Creating " + modName + 
                        " module in realm " + testRealm + " ...");
                createModule(testRealm, modName);

                createUserProp = testResources.getString("am-auth-test-" +
                        modName + "-user-exists");
                userName = testResources.getString("am-auth-test-" +
                        modName + "-user");
                userName.trim();
                password = testResources.getString("am-auth-test-" +
                        modName + "-password");
                password.trim();
                userExists = new Boolean(createUserProp).booleanValue();
                log(Level.FINEST, "setup", "createUserProp = " + 
                        createUserProp);
                log(Level.FINEST, "setup", "userName = " + userName);
                log(Level.FINEST, "setup", "password = " + password);
                
                if (!userExists) {
                    log(Level.FINE, "setup", "Creating the user " + userName);
                    testUserList.add(userName);
                    createUser(testRealm, userName, password);
                }
            }  
        } catch(Exception e) {
            cleanup(testRealm);
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (idToken != null) {
                destroyToken(idToken);
            }
            consoleLogout(webClient, logoutURL);
        }
        exiting("setup");
    }
    
    /**
     * Tests for SessionUpgrade by login into the system using correct
     * credentials for two different modules
     * forceClientAuth - a <code>String</code> containing "true" or "false" 
     * indicating whether the same session will be used on the subsequent 
     * authentication.
     * testRealm - the realm in which the session upgrade will take place
     * testMode - the type of authentication (e.g. module-based or level-based) 
     * that will be used in the test case.
     * useDifferentModules - a String indicating whether two different auth
     * modules will be used.  If the String is "true" then two auth modules will
     * be used or if the String is "false" then a single auth module will be 
     * used.
     */
    @Parameters({"forceClientAuth", "testRealm", "testMode", 
            "useDifferentModules"}) 
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testSessionUpgrade(String forceClientAuth, String testRealm, 
            String testMode, String useDifferentModules)
    throws Exception {
        Object[] params = {forceClientAuth, testRealm, testMode, 
                useDifferentModules};
        entering("testSessionUpgrade", params);  
        log(Level.FINE, "testSessionUpgrade", 
                "Test Description: Testing session upgrade in the realm " + 
                testRealm + " with " + testMode + 
                "-based authentication, forceClientAuth " + forceClientAuth + 
                " and " + 
                (useMultipleModules ? "multiple modules" : "a single module"));
        Reporter.log("Test Description: Testing session upgrade in the realm " + 
                testRealm + " with " + testMode + 
                "-based authentication, forceClientAuth " + forceClientAuth + 
                " and " + 
                (useMultipleModules ? "multiple modules" : "a single module"));
        AuthContext lc = null;
        Callback[] callbacks = null;
        Callback[] acallback = null;
        SSOToken obtainedToken = null;
        SSOToken upgradedToken = null;
        AuthContext.IndexType indexType = null;
        String indexName1 = null;
        String indexName2 = null;
        
        if (testMode != null) {
            if (testMode.equals("module")) {
                indexType = AuthContext.IndexType.MODULE_INSTANCE;
                indexName1 = FirstModuleName;
                indexName2 = SecondModuleName;
            } else if (testMode.equals("level")) {
                indexType = AuthContext.IndexType.LEVEL;
                indexName1 = testResources.getString(
                        "am-auth-session-test-firstlevel");
                indexName2 = testResources.getString(
                        "am-auth-session-test-secondlevel");
            } else {
                log(Level.SEVERE, "testSessionUpgrade", 
                        "Unsupported testMode value from Test NG XML.  " +
                        "Expecting either \"module\" or \"level\".");
                assert false;
            }
        } else {
            log(Level.SEVERE, "testSessionUpgrade", 
                        "testMode parameter wa not set in Test NG XML.  " +
                        "Expecting either \"module\" or \"level\".");
            assert false;
        }
        
        if (!useMultipleModules) {
            indexName2 = indexName1;
        }
        
        try {
            lc = new AuthContext(testRealm);
            log(Level.FINE, "testSessionUpgrade", 
                    "Invoking AuthContext.login with indexName " + indexName1);
            lc.login(indexType, indexName1);
        } catch (AuthLoginException le) {
            log(Level.SEVERE, "testSessionUpgrade", le.getMessage());
            le.printStackTrace();
            return;
        }
        while (lc.hasMoreRequirements()) {
            callbacks = lc.getRequirements();
            if (callbacks != null) {
                try {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof NameCallback) {
                            NameCallback namecallback =
                                    (NameCallback)callbacks[i];
                            namecallback.setName(FirstModuleUserName);
                        }
                        if (callbacks[i] instanceof PasswordCallback) {
                            PasswordCallback passwordcallback =
                                    (PasswordCallback)callbacks[i];
                            passwordcallback.setPassword(
                                    FirstModulePassword.toCharArray());
                        }
                        if (callbacks[i] instanceof ChoiceCallback) {
                            ChoiceCallback choiceCallback = 
                                    (ChoiceCallback)callbacks[i];
                            String[] strChoices = choiceCallback.getChoices();
                            log(Level.FINEST, "testSessionUpgrade", 
                                    "Choice array = choice[0]=" + strChoices[0] +
                                    ", choice[1]=" + strChoices[1]);
                            int choiceIndex = -1;
                            for (int j=0; j < strChoices.length; j++) {
                                 if (strChoices[j].equals(FirstModuleName)) {
                                     choiceIndex = j;
                                     break;
                                 }
                            }
                            choiceCallback.setSelectedIndex(choiceIndex);
                        }
                    }
                    lc.submitRequirements(callbacks);
                } catch (Exception e) {
                    log(Level.SEVERE, "testSessionUpgrade", e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        }
        if (lc.getStatus() == AuthContext.Status.SUCCESS) {
            try {
                log(Level.FINEST, "testSessionUpgrade", "forceClientAuth = " +
                        forceClientAuth);
                Reporter.log("ForceClientAuth = " + forceClientAuth);
                boolean useForceClientAuth = 
                        Boolean.parseBoolean(forceClientAuth);
                obtainedToken = lc.getSSOToken();
                AuthContext newlc = new AuthContext(obtainedToken, 
                        useForceClientAuth);
                log(Level.FINE, "testSessionUpgrade", 
                    "Invoking AuthContext.login with indexName " + indexName2);                
                newlc.login(indexType, indexName2);
                while (newlc.hasMoreRequirements()) {
                    acallback = newlc.getRequirements();
                    if (callbacks != null) {
                        try {
                            for (int i = 0; i < acallback.length; i++){
                                if (acallback[i] instanceof NameCallback) {
                                    NameCallback namecallback =
                                            (NameCallback)acallback[i];
                                    namecallback.setName(SecondModuleUserName);
                                }
                                if (acallback[i] instanceof PasswordCallback) {
                                    PasswordCallback passwordcallback =
                                            (PasswordCallback)acallback[i];
                                    passwordcallback.setPassword(
                                            SecondModulePassword.toCharArray());
                                }
                            }
                            newlc.submitRequirements(acallback);
                        } catch (Exception e) {
                            log(Level.SEVERE, "testSessionUpgrade", 
                                    e.getMessage());
                            return;
                        }
                    }
                }
                if (newlc.getStatus() == AuthContext.Status.SUCCESS) {
                    upgradedToken = newlc.getSSOToken();
                    SSOTokenManager.getInstance().refreshSession(upgradedToken);
                    String tokenAuthType = 
                            upgradedToken.getProperty("AuthType");
                    int tokenAuthLevel = upgradedToken.getAuthLevel();
                    
                    if (tokenAuthType == null) {
                        log(Level.SEVERE, "testSessionUpgradeNegative", 
                                "Unable to obtain AuthType from inital " +
                                "SSOToken");
                        assert false;
                    }
  
                    log(Level.FINEST, "testSessionUpgrade", "AuthType in " + 
                            "upgraded token = " + tokenAuthType);
                    log(Level.FINEST, "testSessionUpgrade", "Expected " +
                            "AuthType in upgraded token = " + upgradedAuthType);                          
                    log(Level.FINEST, "testSessionUpgrade", 
                            "AuthLevel in upgraded token = " + 
                            upgradedToken.getAuthLevel());
                    log(Level.FINEST, "testSessionUpgrade", "Expected " + 
                            "AuthLevel in upgraded token = " + 
                            upgradedAuthLevel);
                    String obtainedSessionID = 
                            obtainedToken.getTokenID().toString();
                    String upgradedSessionID =
                            upgradedToken.getTokenID().toString();
                    log(Level.FINEST, "testSessionUpgrade", 
                            "Obtained token session ID = " + obtainedSessionID);
                    log(Level.FINEST, "testSessionUpgrade", 
                            "Upgraded token session ID = " + upgradedSessionID);
                    if (useMultipleModules) {
                        assert (upgradedToken.getProperty("AuthType").equals(
                                upgradedAuthType) && (tokenAuthLevel == 
                                upgradedAuthLevel) && (useForceClientAuth == 
                                (upgradedSessionID.equals(obtainedSessionID))));
                    } else {
                        assert (upgradedToken.getProperty("AuthType").equals(
                                upgradedAuthType) && (tokenAuthLevel == 
                                upgradedAuthLevel) &&  
                                (upgradedSessionID.equals(obtainedSessionID)));
                    }
                } else {
                    log(Level.SEVERE, "testSessionUpgrade", "The user " + 
                            SecondModuleUserName + 
                            " did not authenticate successfully.");
                    assert false;
                }   
            } catch (Exception ex) {
                log(Level.SEVERE, "testSessionUpgrade", ex.getMessage());
                ex.printStackTrace();
                throw ex;
            } finally {
                if (upgradedToken != null) {
                    destroyToken(upgradedToken);
                }
            } 
            exiting("testSessionUpgrade");
        } else {
            log(Level.SEVERE, "testSessionUpgrade", "The user " + 
                    FirstModuleUserName + 
                    " did not authenticate successfully.");
            assert false;
        }
    }
    
    /**
     * Tests that after a failed authentication with the second module that the
     * user's original session is maintained.
     * forceClientAuth - a <code>String</code> containing "true" or "false" 
     * indicating whether the same session will be used on the subsequent 
     * authentication.
     * testRealm - the realm in which the session upgrade will take place
     * testMode - the type of authentication (e.g. module-based or level-based) 
     * that will be used in the test case.
     */
    @Parameters({"forceClientAuth", "testRealm", "testMode", 
        "useDifferentModules"}) 
    @Test(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void testSessionUpgradeNegative(String forceClientAuth, 
            String testRealm, String testMode, String useDifferentModules)
    throws Exception {       
        Object[] params = {forceClientAuth, testRealm, testMode, 
                useDifferentModules};
        entering("testSessionUpgradeNegative", params);  
        log(Level.FINE, "testSessionUpgradeNegative", 
                "Test Description: Testing failed auth in the realm " + 
                testRealm + " with " + testMode + 
                "-based authentication, forceClientAuth " + forceClientAuth + 
                " and " + 
                (useMultipleModules ? "multiple modules" : "a single module"));
        Reporter.log("Test Description: Testing failed auth in the realm " + 
                testRealm + " with " + testMode + 
                "-based authentication, forceClientAuth " + forceClientAuth + 
                " and " + 
                (useMultipleModules ? "multiple modules" : "a single module"));
        AuthContext lc = null;
        Callback[] callbacks = null;
        Callback[] acallback = null;
        SSOToken obtainedToken = null;
        AuthContext.IndexType indexType = null;
        String indexName1 = null;
        String indexName2 = null;
        
        if (testMode != null) {
            if (testMode.equals("module")) {
                indexType = AuthContext.IndexType.MODULE_INSTANCE;
                indexName1 = FirstModuleName;
                indexName2 = SecondModuleName;
            } else if (testMode.equals("level")) {
                indexType = AuthContext.IndexType.LEVEL;
                indexName1 = testResources.getString(
                        "am-auth-session-test-firstlevel").trim();
                indexName2 = testResources.getString(
                        "am-auth-session-test-secondlevel").trim();
            } else {
                log(Level.SEVERE, "testSessionUpgradeNegative", 
                        "Unsupported testMode value from Test NG XML.  " +
                        "Expecting either \"module\" or \"level\".");
                assert false;
            }
        } else {
            log(Level.SEVERE, "testSessionUpgradeNegative", 
                        "testMode parameter wa not set in Test NG XML.  " +
                        "Expecting either \"module\" or \"level\".");
            assert false;
        }

        if (!useMultipleModules) {
            indexName2 = indexName1;
        }        
        
        try {
            lc = new AuthContext(testRealm);
            lc.login(indexType, indexName1);
        } catch (AuthLoginException le) {
            log(Level.SEVERE, "testSessionUpgradeNegative", le.getMessage());
            le.printStackTrace();
            return;
        }
        while (lc.hasMoreRequirements()) {
            callbacks = lc.getRequirements();
            if (callbacks != null) {
                try {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof NameCallback) {
                            NameCallback namecallback =
                                    (NameCallback)callbacks[i];
                            namecallback.setName(FirstModuleUserName);
                        }
                        if (callbacks[i] instanceof PasswordCallback) {
                            PasswordCallback passwordcallback =
                                    (PasswordCallback)callbacks[i];
                            passwordcallback.setPassword(
                                    FirstModulePassword.toCharArray());
                        }
                        if (callbacks[i] instanceof ChoiceCallback) {
                            ChoiceCallback choiceCallback = 
                                    (ChoiceCallback)callbacks[i];
                            String[] strChoices = choiceCallback.getChoices();
                            int choiceIndex = -1;
                            for (int j=0; j < strChoices.length; j++) {
                                log(Level.FINEST, "testSessionUpgrade", 
                                        "module " + j + " = " + strChoices[j]);
                                 if (strChoices[j].equals(FirstModuleName)) {
                                     choiceIndex = j;
                                     break;
                                 }
                            }
                            choiceCallback.setSelectedIndex(choiceIndex);
                        }                        
                    }
                    lc.submitRequirements(callbacks);
                } catch (Exception e) {
                    log(Level.SEVERE, "testSessionUpgradeNegative", 
                            e.getMessage());
                    e.printStackTrace();
                    return;
                }
            }
        }
        if (lc.getStatus() == AuthContext.Status.SUCCESS) {
            try {
                log(Level.FINEST, "testSessionUpgradeNegative", 
                        "forceClientAuth = " + forceClientAuth);
                Reporter.log("ForceClientAuth = " + forceClientAuth);
                String failedPassword = "wrong" + SecondModulePassword;
                boolean useForceClientAuth = 
                        Boolean.parseBoolean(forceClientAuth);
                obtainedToken = lc.getSSOToken();
                AuthContext newlc = new AuthContext(obtainedToken, 
                        useForceClientAuth);
                newlc.login(indexType, indexName2);
                while (newlc.hasMoreRequirements()) {
                    acallback = newlc.getRequirements();
                    if (callbacks != null) {
                        try {
                            for (int i = 0; i < acallback.length; i++){
                                if (acallback[i] instanceof NameCallback) {
                                    NameCallback namecallback =
                                            (NameCallback)acallback[i];
                                    namecallback.setName(SecondModuleUserName);
                                }
                                if (acallback[i] instanceof PasswordCallback) {
                                    PasswordCallback passwordcallback =
                                            (PasswordCallback)acallback[i];
                                    passwordcallback.setPassword(
                                            failedPassword.toCharArray());
                                }
                            }
                            newlc.submitRequirements(acallback);
                        } catch (Exception e) {
                            log(Level.SEVERE, "testSessionUpgradeNegative", 
                                    e.getMessage());
                            return;
                        }
                    }
                }
                
                AuthContext.Status newStatus = newlc.getStatus();
                SSOTokenManager.getInstance().refreshSession(obtainedToken);
                String tokenAuthType = 
                        obtainedToken.getProperty("AuthType");
                int tokenAuthLevel = obtainedToken.getAuthLevel();
                if (tokenAuthType == null) {
                    log(Level.SEVERE, "testSessionUpgradeNegative", 
                            "Unable to obtain AuthType from inital " +
                            "SSOToken");
                    assert false;
                }

                log(Level.FINEST, "testSessionUpgradeNegative", 
                        "The status of the second login was " + newStatus);
                log(Level.FINEST, "testSessionUpgradeNegative", 
                        "AuthType in original token = " + tokenAuthType);
                log(Level.FINEST, "testSessionUpgradeNegative", "Expected "
                        + "AuthType in original token = " + failedAuthType);                          
                log(Level.FINEST, "testSessionUpgradeNegative", 
                        "AuthLevel in original token = " + 
                        tokenAuthLevel);
                log(Level.FINEST, "testSessionUpgradeNegative", "Expected " 
                        + "AuthLevel in original token = " + 
                        failedAuthLevel); 
                
                if (newStatus == AuthContext.Status.FAILED) {
                    assert (tokenAuthType.equals(failedAuthType) && 
                            (tokenAuthLevel == failedAuthLevel));
                } else if (!useMultipleModules && 
                        newStatus == AuthContext.Status.SUCCESS) {
                   long obtainedMaxSessionTime = 
                           obtainedToken.getMaxSessionTime();
                   long obtainedMaxIdleTime = obtainedToken.getMaxIdleTime();
                   SSOToken newToken = newlc.getSSOToken();
                   SSOTokenManager.getInstance().refreshSession(newToken);
                   long newMaxSessionTime = newToken.getMaxSessionTime();
                   long newMaxIdleTime = newToken.getMaxIdleTime();
                   
                   log(Level.FINEST, "testSessionUpgradeNegative", 
                           "Max session time in original token = " + 
                           obtainedMaxSessionTime);
                   log(Level.FINEST, "testSessionUpgradeNegative",
                           "Max session time in new token = " +
                           newMaxSessionTime);
                   log(Level.FINEST, "testSessionUpgradeNegative", 
                           "Max idle time in original token = " + 
                           obtainedMaxIdleTime);
                   log(Level.FINEST, "testSessionUpgradeNegative",
                           "Max session time in new token = " + newMaxIdleTime);
                   assert((obtainedMaxSessionTime == newMaxSessionTime) && 
                           (obtainedMaxIdleTime == newMaxIdleTime) && 
                           tokenAuthType.equals(failedAuthType) && 
                           (tokenAuthLevel == failedAuthLevel));
                } else {
                    log(Level.SEVERE, "testSessionUpgradeNegative", 
                            "The status of the second login was " + newStatus +
                            " with forceClientAuth = " + forceClientAuth + ".");
                    assert false;
                }
            } catch (Exception ex) {
                log(Level.SEVERE, "testSessionUpgradeNegative", 
                        ex.getMessage());
                ex.printStackTrace();
                throw ex;
            } finally {
                if (obtainedToken != null) {
                    destroyToken(obtainedToken);
                }
            }
            exiting("testSessionUpgradeNegative");
        } else {
            log(Level.SEVERE, "testSessionUpgradeNegative", "The user " + 
                    FirstModuleUserName + 
                    " did not authenticate successfully with " + testMode + 
                    " = " + indexName1 + ".");
            assert false;
        }
    }    
    
    /**
     * Performs cleanup after tests are done.
     * Deletes the authentication instances and users created
     * by this test scenario
     */
    @Parameters({"testRealm"})
    @AfterClass(groups={"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void cleanup(String testRealm)
    throws Exception {
        Object[] params = {testRealm};
        entering("cleanup", params);
        SSOToken idToken = getToken(adminUser, adminPassword, realm);

        if (webClient == null) {
            webClient = new WebClient();
        }
        try {
            log(Level.FINEST, "cleanup", url);
            List<String> listModInstance = new ArrayList<String>();
            listModInstance.add(FirstModuleName);
            if (useMultipleModules) {
                listModInstance.add(SecondModuleName);
            }
            consoleLogin(webClient, url, adminUser, adminPassword);

            log(Level.FINE, "cleanup", "Deleting auth instance(s) " + 
                    listModInstance + " ...");
            if (FederationManager.getExitCode(fm.deleteAuthInstances(
                    webClient, testRealm, listModInstance)) != 0) {
                log(Level.SEVERE, "cleanup", 
                        "deleteAuthInstances ssoadm command failed");
            }

            if ((testUserList != null) && !testUserList.isEmpty()) { 
                log(Level.FINE, "cleanup", "Deleting user(s) " + testUserList + 
                        "...");                                
                if (FederationManager.getExitCode(fm.deleteIdentities(webClient, 
                        testRealm, testUserList, "User")) != 0) {
                    log(Level.SEVERE, "cleanup", 
                            "deleteIdentities ssoadm command failed");
                }
            }
            absoluteRealm = testRealm;
            if (!absoluteRealm.equals("/")) {
                if (absoluteRealm.indexOf("/") != 0) {
                    absoluteRealm = "/" + testRealm;
                }             
                log(Level.FINE, "cleanup", "Deleting the sub-realm " + 
                        absoluteRealm);
                if (FederationManager.getExitCode(fm.deleteRealm(webClient, 
                        absoluteRealm, true)) != 0) {
                    log(Level.SEVERE, "cleanup", 
                            "deleteRealm ssoadm command failed");
                }
            }
        } catch(Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(idToken);
            consoleLogout(webClient, logoutURL);
        }
        exiting("cleanup");
    }
    
    /**
     * Creates the required test users on the system for each
     * Chain to be executed
     * @param user
     * @param password
     **/
    private void createUser(String testRealm, String newUser, 
            String userpassword) {
        List<String> userList = new ArrayList<String>();
        userList.add("sn=" + newUser);
        userList.add("cn=" + newUser);
        userList.add("userpassword=" + userpassword);
        userList.add("inetuserstatus=Active");
        log(Level.FINEST, "createUser", "userList " + userList);

        try {
            AuthTestConfigUtil userConfig =
                    new AuthTestConfigUtil(configrbName);
            userConfig.setTestConfigRealm(testRealm);
            userConfig.createUser(userList, newUser);
        } catch(Exception e) {
            log(Level.SEVERE, "createUsers", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Calls Authentication Utility class to create the module instances
     * for a given module instance name
     * @param moduleName
     */
    private void createModule(String testRealm, String mName) {
        try {
            Map modMap = moduleConfig.getModuleData(mName);
            moduleServiceName = moduleConfig.getModuleServiceName();
            moduleSubConfig = moduleConfig.getModuleSubConfigName();
            moduleSubConfigId = moduleConfig.getModuleSubConfigId();
            moduleConfigData = moduleConfig.getListFromMap(modMap, mName);
            log(Level.FINEST, "createModule", "ModuleServiceName :" +
                    moduleServiceName);
            log(Level.FINEST, "createModule", "ModuleSubConfig :" +
                    moduleSubConfig);
            log(Level.FINEST, "createModule", "ModuleSubConfigId :" +
                    moduleSubConfigId);
            moduleConfig.createModuleInstances(testRealm, moduleServiceName,
                    moduleSubConfig, moduleConfigData, moduleSubConfigId);
        } catch(Exception e) {
            log(Level.SEVERE, "createModule", e.getMessage());
            e.printStackTrace();
        }
    }
}
