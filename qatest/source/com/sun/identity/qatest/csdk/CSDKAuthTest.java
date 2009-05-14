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
 * $Id: CSDKAuthTest.java
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.csdk;

import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.CSDKCommon;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.SMSConstants;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
import com.sun.identity.qatest.common.authentication.AuthenticationCommon;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
 * This class does the following:
 * (1) Create a module instance with a given auth level
 * (2) Create an auth service using the auth module
 * (3) Create a new user and assign the auth service to the user
 * (4) Create a new role
 * (5) Assign the auth service and the user to that role
 * (6) Do a module, role, service, level and user based authentication
 * using htmlunit and zero page login with URL parameters. 
 * (7) Repeat the following scenarios for Active Directory, LDAP, 
 * Membership, Anonymous, NT and JDBC modules)
 */
public class CSDKAuthTest extends TestCommon {

    private AuthenticationCommon ac;
    private AuthTestConfigUtil moduleConfigData;
    private boolean isValidTest = true;
    private CSDKCommon cc;
    private List list;
    private ResourceBundle rb;
    private String moduleServiceName;
    private String moduleSubConfigName;
    private String moduleSubConfigId;
    private String serviceName;
    private String serviceSubConfigName;
    private String serviceSubConfigId;
    private String rolename;
    private String user;
    private String password;
    private String svcName;
    private String loginURL;
    private String logoutURL;
    private String amadmURL;
    private String libraryPath;
    private String directoryPath;
    private String bootstrapFile;
    private String configurationFile;
    private String configrbName = "authenticationConfigData";
    private WebClient webClient;

    /**
     * Constructor for the class.
     */
    public CSDKAuthTest() {
        super("CSDKAuthTest");
        ac = new AuthenticationCommon();
        cc = new CSDKCommon();
        moduleConfigData = new AuthTestConfigUtil(configrbName);
        logoutURL = protocol + ":" + "//" + host + ":" + port + uri +
                "/UI/Logout";
    }

    /**
     * This method is to configure the initial setup. It does the following:
     * (1) Create a module instance with a given auth level
     * (2) Create an auth service using the auth module
     * (3) Create a new user and assign the auth service to the user
     * (4) Create a new role
     * (5) Assign the auth service and the user to that role
     * This is called only once per auth module.
     */
    @Parameters({"testModule", "testMode"})
    @BeforeClass(groups = {"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad",
        "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void setup(String testModule, String testMode)
            throws Exception {
        Object[] params = {testModule, testMode};
        entering("setup", params);
        Map ldMap = cc.getLibraryPath();
        libraryPath = (String) ldMap.get("libraryPath");
        directoryPath = (String) ldMap.get("directoryPath");
        bootstrapFile = cc.getBootStrapFilePath();
        configurationFile = cc.getConfigurationFilePath();
        webClient = new WebClient();
        try {
            isValidTest = moduleConfigData.isValidModuleTest(testModule);
            if (isValidTest) {
                rb = ResourceBundle.getBundle("csdk" + fileseparator +
                        "CSDKAuthTest");
                list = moduleConfigData.getModuleDataAsList(testModule);
                moduleServiceName = (String) rb.getString("CSDKAuthTest" + "."
                        + testModule + ".module_servicename");
                moduleSubConfigName = (String) rb.getString("CSDKAuthTest" + 
                        "." + testModule + ".module_subconfigname");
                moduleSubConfigId = (String) rb.getString("CSDKAuthTest" + "." 
                        + testModule + ".module_subconfigid");
                serviceName = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".service_servicename");
                serviceSubConfigName = (String) rb.getString("CSDKAuthTest" +
                        "." + testModule + ".service_subconfigname");
                serviceSubConfigId = (String) rb.getString("CSDKAuthTest" + 
                        "." + testModule + ".service_subconfigid");
                rolename = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".rolename");
                user = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".user");
                password = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".password");

                log(Level.FINEST, "setup", "moduleServiceName: " +
                        moduleServiceName);
                log(Level.FINEST, "setup", "moduleSubConfigName: " +
                        moduleSubConfigName);
                log(Level.FINEST, "setup", "moduleSubConfigId: " +
                        moduleSubConfigId);

                log(Level.FINEST, "setup", "serviceName: " +
                        serviceName);
                log(Level.FINEST, "setup", "serviceSubConfigName: " +
                        serviceSubConfigName);
                log(Level.FINEST, "setup", "serviceSubConfigId: " +
                        serviceSubConfigId);
                log(Level.FINEST, "setup", "module_subconfig_list: " + list);

                log(Level.FINEST, "setup", "rolename: " + rolename);
                log(Level.FINEST, "setup", "username: " + user);
                log(Level.FINEST, "setup", "userpassword: " + password);

                Reporter.log("ModuleServiceName: " + moduleServiceName);
                Reporter.log("ModuleSubConfigName: " + moduleSubConfigName);
                Reporter.log("ModuleSubConfigId: " + moduleSubConfigId);

                Reporter.log("ServiceServiceName: " + serviceName);
                Reporter.log("ServiceSubConfigName: " + serviceSubConfigName);
                Reporter.log("ServiceSubConfigId: " + serviceSubConfigId);

                Reporter.log("ModuleSubConfigList: " + list);

                Reporter.log("RoleName: " + rolename);
                Reporter.log("UserName: " + user);
                Reporter.log("UserPassword: " + password);

                loginURL = getLoginURL("/");
                amadmURL = protocol + ":" + "//" + host + ":" + port +
                        uri;
                log(Level.FINEST, "setup", loginURL);
                log(Level.FINEST, "setup", logoutURL);
                log(Level.FINEST, "setup", amadmURL);
                if (moduleServiceName.equals("iPlanetAMAuthAnonymousService")) {
                    list.add("iplanet-am-auth-anonymous-users-list=" + user);
                }
                FederationManager am = new FederationManager(amadmURL);
                consoleLogin(webClient, loginURL, adminUser, adminPassword);
                log(Level.FINE, "setup", "Creating module sub-configuration " +
                        moduleSubConfigName + "...");
                if (FederationManager.getExitCode(am.createSubCfg(webClient,
                        moduleServiceName, moduleSubConfigName, list, realm,
                        moduleSubConfigId, "0")) != 0) {
                    log(Level.SEVERE, "setup",
                            "createSubCfg (module) ssoadm command failed");
                    assert false;
                }
                list.clear();
                String svcData = "iplanet-am-auth-configuration=" +
                        "<AttributeValuePair><Value>" + moduleSubConfigName +
                        " REQUIRED</Value></AttributeValuePair>";
                log(Level.FINEST, "setup", "ServiceData: " + svcData);
                list.add(svcData);
                log(Level.FINE, "setup", "Creating service sub-configuration " +
                        serviceSubConfigName + "...");
                if (FederationManager.getExitCode(am.createSubCfg(webClient,
                        serviceName, serviceSubConfigName, list, realm,
                        serviceSubConfigId, "0")) != 0) {
                    log(Level.SEVERE, "setup",
                            "createSubCfg (service) ssoadm command failed");
                    assert false;
                }

                int iIdx = serviceSubConfigName.indexOf("/");
                svcName = serviceSubConfigName.substring(iIdx + 1,
                        serviceSubConfigName.length());
                log(Level.FINEST, "setup", "svcName:" + svcName);

                list.clear();
                list.add("sn=" + user);
                list.add("cn=" + user);
                list.add("userpassword=" + password);
                list.add("inetuserstatus=Active");
                list.add("iplanet-am-user-auth-config=" + svcName);
                log(Level.FINE, "setup", "Creating user " + user + " ...");
                if (FederationManager.getExitCode(am.createIdentity(webClient,
                        realm, user, "User", list)) != 0) {
                    log(Level.SEVERE, "setup",
                            "createIdentity (User) ssoadm command failed");
                    assert false;
                }

                if (ac.getSMSCommon().isPluginConfigured(
                        SMSConstants.UM_DATASTORE_SCHEMA_TYPE_AMSDK, realm)) {
                    log(Level.FINE, "setup", "Creating role " + rolename +
                            " ...");
                    if (FederationManager.getExitCode(am.createIdentity(
                            webClient, realm, rolename, "Role", null)) != 0) {
                        log(Level.SEVERE, "setup",
                                "createIdentity (Role) ssoadm command failed");
                        assert false;
                    }
                    log(Level.FINE, "setup", "Assigning the user " + user +
                            " to role " + rolename + " ...");
                    if (FederationManager.getExitCode(am.addMember(webClient,
                            realm, user, "User", rolename, "Role")) != 0) {
                        log(Level.SEVERE, "setup",
                                "addMember ssoadm (User) call failed");
                        assert false;
                    }
                    list.clear();
                    list.add("iplanet-am-auth-configuration=" + svcName);
                    log(Level.FINE, "setup", "Assigning the service " +
                            serviceName + " to the role " +
                            rolename + "...");
                    if (FederationManager.getExitCode(am.addSvcIdentity(
                            webClient, realm, rolename, "Role",
                            serviceName, list)) != 0) {
                        log(Level.SEVERE, "setup",
                                "addSvcIdentity ssoadm command failed");
                        assert false;
                    }
                } else {
                    log(Level.FINEST, "setup",
                            "Creation of a role, assignment of user to role, " +
                            "and assignment of service to role skipped for " +
                            "non amsdk plugin ...");
                }
            } else {
                log(Level.FINEST, "setup", "Skipping setup of " + testModule +
                        " auth module test on a Windows based server");
            }
        } catch (AssertionError ae) {
            log(Level.SEVERE, "setup",
                    "Calling cleanup due to failed ssoadm exit code ...");
            cleanup(testModule);
            throw ae;
        } catch (Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (isValidTest) {
                consoleLogout(webClient, logoutURL);
                Thread.sleep(notificationSleepTime);
            }
        }
        exiting("setup");
    }

    /**
     * Tests for successful login into the system using correct
     * credentials
     */
    @Parameters({"testModule", "testMode", "testModeNo"})
    @Test(groups = {"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec",
        "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void testCSDKAuthPositive(String testModule, String testMode,
            String testModeNo)
            throws Exception {
        Object[] params = {testModule, testMode, testModeNo};
        entering("testCSDKAuthPositive", params);
        if (isValidTest) {
            try {
                String results;
                String error;
                String loginUser = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".user");
                String loginPassword = (String) rb.getString("CSDKAuthTest" +
                        "." + testModule +
                        ".password");
                String modevalue = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule +
                        ".modevalue." + testMode);

                Reporter.log("Test Description : " + (String) rb.getString
                        ("CSDKAuthTest" + "." + testModule + ".modevalue." +
                        testMode + ".description") + (String) rb.getString
                        ("CSDKAuthTest.positiveTestMessage"));
                Reporter.log("Execution command :" + " am_auth_test " +
                        " -u " + loginUser + " -p " +
                        loginPassword +
                        " -f " + "  bootstrapFile " + " -o " + realm +
                        " -t " + testModeNo + " -m " + modevalue);
                ProcessBuilder pb = new ProcessBuilder(directoryPath +
                        fileseparator + "am_auth_test", "-u", loginUser,
                        "-p", loginPassword, "-f", bootstrapFile, "-o", realm,
                        "-t", testModeNo,
                        "-m", modevalue);
                pb.environment().put("LD_LIBRARY_PATH", libraryPath);
                pb.directory(new File(directoryPath));
                Process p = pb.start();
                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(p.getInputStream()));
                StringBuffer sbResults = new StringBuffer();
                while ((results = stdInput.readLine()) != null) {
                    sbResults = sbResults.append(results);
                }
                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(p.getErrorStream()));
                while ((error = stdError.readLine()) != null) {
                    sbResults = sbResults.append(error);
                }
                if (sbResults.toString().contains("Succeeded!")) {
                    assert true;
                } else {
                    assert false;
                }
                log(Level.FINEST, "testCSDKAuthPositive", sbResults);
            } catch (Exception e) {
                log(Level.SEVERE, "testCSDKAuthPositive", e.getMessage());
                cleanup(testModule);
                throw e;
            }
        } else {
            log(Level.FINEST, "testCSDKAuthPositive", "Skipping " + testModule +
                    " auth module test on a Windows based server");
        }
        exiting("testCSDKAuthPositive");
    }

    /**
     * Tests for unsuccessful login into the system using incorrect
     * credentials
     */
    @Parameters({"testModule", "testMode", "testModeNo"})
    @Test(groups = {"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad", "ad_sec",
        "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void testCSDKAuthNegative(String testModule, String testMode,
            String testModeNo)
            throws Exception {
        Object[] params = {testModule, testMode, testModeNo};
        entering("testCSDKAuthNegative", params);
        if (isValidTest) {
            try {
                String error;
                String results;
                String loginUser = (String) rb.getString("CSDKAuthTest" + "."
                        + testModule + ".user");
                String loginPassword = (String) rb.getString("CSDKAuthTest" +
                        "." + testModule +
                        ".password");
                String modevalue = (String) rb.getString("CSDKAuthTest" + 
                        "." + testModule + ".modevalue." + testMode);
                Reporter.log("Test Description : " + (String) rb.getString
                        ("CSDKAuthTest" + "." + testModule + ".modevalue." +
                        testMode + ".description") + (String) rb.getString
                        ("CSDKAuthTest.negativeTestMessage"));
                Reporter.log("Execution command :" + " am_auth_test " +
                        " -u " + loginUser + " -p " +
                        loginPassword +
                        " -f " + "  bootstrapFile " + " -o " + realm +
                        " -t " + testModeNo + " -m " + modevalue);
                ProcessBuilder pb = new ProcessBuilder(directoryPath +
                        fileseparator + "am_auth_test", "-u", loginUser +
                        "negative", "-p", loginPassword, "-f",
                        ".." + fileseparator + "config" + fileseparator +
                        "OpenSSOAgentBootstrap.properties", "-o", realm,
                        "-t", testModeNo,
                        "-m", modevalue);
                pb.environment().put("LD_LIBRARY_PATH", libraryPath);
                pb.directory(new File(directoryPath));
                Process p1 = pb.start();
                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(p1.getInputStream()));
                StringBuffer sbResults = new StringBuffer();
                while ((results = stdInput.readLine()) != null) {
                    sbResults = sbResults.append(results);
                }
                BufferedReader stdError = new BufferedReader(new
                        InputStreamReader(p1.getErrorStream()));
                while ((error = stdError.readLine()) != null) {
                    sbResults = sbResults.append(error);
                }
                if (sbResults.toString().contains("Succeeded!")) {
                    assert false;
                } else {
                    assert true;
                }
                log(Level.FINEST, "testCSDKAuthNegative", error);
            } catch (Exception e) {
                log(Level.SEVERE, "testCSDKAuthNegative", e.getMessage());
                cleanup(testModule);
                throw e;
            }
        } else {
            log(Level.FINEST, "testCSDKAuthNegative", "Skipping " + testModule +
                    " auth module test on a Windows based server");
        }
        exiting("testCSDKAuthNegative");
    }

    /**
     * This method is to clear the initial setup. It does the following:
     * (1) Delete authentication service
     * (2) Delete authentication instance
     * (3) Delete all users and roles
     * This is called only once per auth module.
     */
    @Parameters({"testModule"})
    @AfterClass(groups = {"ldapv3", "ldapv3_sec", "s1ds", "s1ds_sec", "ad",
        "ad_sec", "amsdk", "amsdk_sec", "jdbc", "jdbc_sec"})
    public void cleanup(String testModule)
            throws Exception {
        Object[] params = {testModule};
        entering("cleanup", params);
        webClient = new WebClient();
        if (isValidTest) {
            try {
                user = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".user");
                rolename = (String) rb.getString("CSDKAuthTest" + "." +
                        testModule + ".rolename");

                log(Level.FINEST, "cleanup", "UserName:" + user);
                log(Level.FINEST, "cleanup", "RoleName:" + rolename);

                Reporter.log("UserName:" + user);
                Reporter.log("RoleName:" + rolename);

                FederationManager am = new FederationManager(amadmURL);
                consoleLogin(webClient, loginURL, adminUser, adminPassword);
                list = new ArrayList();
                list.add(user);
                log(Level.FINE, "cleanup", "Deleting user " + user + " ...");
                if (FederationManager.getExitCode(am.deleteIdentities(webClient,
                        realm, list, "User")) != 0) {
                    log(Level.SEVERE, "cleanup",
                            "deleteIdentities (User) ssoadm command failed");
                }
                list.clear();
                list.add(rolename);

                if (ac.getSMSCommon().isPluginConfigured(
                        SMSConstants.UM_DATASTORE_SCHEMA_TYPE_AMSDK, realm)) {
                    log(Level.FINE, "cleanup", "Deleting role " + rolename +
                            " ...");
                    if (FederationManager.getExitCode(
                            am.deleteIdentities(webClient, realm, list,
                            "Role")) != 0) {
                        log(Level.SEVERE, "cleanup",
                                "deleteIdentities(Role) ssoadm command failed");
                    }
                }

                log(Level.FINE, "cleanup",
                        "Deleting service sub-configuration " +
                        serviceSubConfigName + " ...");
                if (FederationManager.getExitCode(am.deleteSubCfg(webClient,
                        serviceName, serviceSubConfigName, realm)) != 0) {
                    log(Level.SEVERE, "cleanup",
                            "deleteSubCfg (Service) ssoadm command failed");
                }
                list.clear();
                list.add(moduleSubConfigName);

                log(Level.FINE, "cleanup", "Deleting module instance(s) " +
                        list + " ...");
                if (FederationManager.getExitCode(
                        am.deleteAuthInstances(webClient, realm, list)) != 0) {
                    log(Level.SEVERE, "cleanup",
                            "deleteAuthInstances ssoadm command failed");
                }
            } catch (Exception e) {
                log(Level.SEVERE, "cleanup", e.getMessage());
                e.printStackTrace();
                throw e;
            } finally {
                consoleLogout(webClient, logoutURL);
                Thread.sleep(5000);
            }
        } else {
            log(Level.FINEST, "setup", "Skipping cleanup for " + testModule +
                    " auth module test on a Windows based server");
        }
        exiting("cleanup");
    }
}
