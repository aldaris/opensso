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
 * $Id: AuthTest.java,v 1.1 2007-03-20 22:02:25 rmisra Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.AccessManager;
import com.sun.identity.qatest.common.AuthenticationCommon;
import com.sun.identity.qatest.common.TestCommon;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
public class AuthTest extends TestCommon {

    private AuthenticationCommon ac;
    private Map mapConfig;
    private Map mapExecute;
    private int initSetup;
    private String module_servicename;
    private String module_subconfigname;
    private String module_datafile;
    private String module_subconfigid;
    private String service_servicename;
    private String service_subconfigname;
    private String service_subconfigid;
    private String rolename;
    private String user;
    private String password;
    private String svcName;
    private List list;

/**
 * Constructor for the class.
 */
    public AuthTest(Map mC,Map mE, int j) {
        super("AuthTest");
        mapConfig = mC;
        mapExecute = mE;
        initSetup = j;
        ac = new AuthenticationCommon();
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
    @BeforeClass(groups={"client"})
    public void setup()
        throws Exception {
        entering("setup", null);
        try {
            if (initSetup == 0) {
                module_servicename = 
                        (String)mapConfig.get("module_servicename");
                module_subconfigname = 
                        (String)mapConfig.get("module_subconfigname");
                module_datafile = (String)mapConfig.get("module_datafile");
                module_subconfigid = 
                        (String)mapConfig.get("module_subconfigid");

                service_servicename = 
                        (String)mapConfig.get("service_servicename");
                service_subconfigname = 
                        (String)mapConfig.get("service_subconfigname");
                service_subconfigid = 
                        (String)mapConfig.get("service_subconfigid");

                rolename = (String)mapConfig.get("rolename");
                user = (String)mapExecute.get("user");
                password = (String)mapExecute.get("password");

                log(logLevel, "setup", "module_servicename:" +
                        module_servicename);
                log(logLevel, "setup", "module_subconfigname:" +
                        module_subconfigname);
                log(logLevel, "setup", "module_datafile:" + module_datafile);
                log(logLevel, "setup", "module_subconfigid:" +
                        module_subconfigid);

                log(logLevel, "setup", "service_servicename:" +
                        service_servicename);
                log(logLevel, "setup", "service_subconfigname:" +
                        service_subconfigname);
                log(logLevel, "setup", "service_subconfigid:" +
                        service_subconfigid);

                log(logLevel, "setup", "rolename:" + rolename);
                log(logLevel, "setup", "username:" + user);
                log(logLevel, "setup", "userpassword:" + password);

                Reporter.log("ModuleServiceName" + module_servicename);
                Reporter.log("ModuleSubConfigName" + module_subconfigname);
                Reporter.log("ModuleDataFile" + module_datafile);
                Reporter.log("ModuleSubConfigId" + module_subconfigid);

                Reporter.log("ServiceServiceName" + service_servicename);
                Reporter.log("ServiceSubConfigName" + service_subconfigname);
                Reporter.log("ServiceSubConfigId" + service_subconfigid);

                Reporter.log("RoleName" + rolename);
                Reporter.log("UserName" + user);
                Reporter.log("UserPassword" + password);
                
                String url = protocol + ":" + "//" + host + ":" + port + uri;
                log(logLevel, "setup", url);
                String absFileName = getBaseDir() + "/xml/authentication/" +
                        module_datafile;
                log(logLevel, "setup", absFileName);
                list = getListFromFile(absFileName);
                if (module_servicename.equals("iPlanetAMAuthAnonymousService"))
                    list.add("iplanet-am-auth-anonymous-users-list=" + user);
                AccessManager am = new AccessManager(url);
                WebClient webClient = new WebClient();
                consoleLogin(webClient, url, adminUser, adminPassword);
                am.createSubConfiguration(webClient, module_servicename,
                        module_subconfigname, list, realm, module_subconfigid);

                list.clear();
                String svcData = "iplanet-am-auth-configuration=" + 
                        "<AttributeValuePair><Value>" + module_subconfigname +
                        " REQUIRED</Value></AttributeValuePair>";
                log(logLevel, "setup", svcData);
                list.add(svcData);
                am.createSubConfiguration(webClient, service_servicename,
                        service_subconfigname, list, realm,
                        service_subconfigid);

                int iIdx = service_subconfigname.indexOf("/");
                svcName = service_subconfigname.substring(iIdx+1,
                        service_subconfigname.length());
                log(logLevel, "setup", "svcName:" + svcName);

                list.clear();
                list.add("sn=" + user);
                list.add("cn=" + user);
                list.add("userpassword=" + password);
                list.add("iplanet-am-user-auth-config=" + svcName);
                am.createIdentity(webClient, realm, user, "User", list);

                am.createIdentity(webClient, realm, rolename, "Role", null);
                am.addMember(webClient, realm, user, "User", rolename, "Role");

                am.addServiceIdentity(webClient, realm, rolename, "Role",
                        service_servicename, null);
                list.clear();
                list.add("iplanet-am-auth-configuration=" + svcName);
                am.setIdentityServiceAttributes(webClient, realm, rolename,
                        "Role", service_servicename, list);
            }
        } catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
        }
        exiting("setup");
    }

  /**
   * Tests for successful login into the system using correct
   * credentials
   */
   @Test(groups = {"client"})
    public void testLoginPositive()
        throws Exception {
        entering("testLoginPositive", null);
        try {
            String user = (String)mapExecute.get("user");
            String password = (String)mapExecute.get("password");
            String mode = (String)mapExecute.get("mode");
            String modevalue = (String)mapExecute.get("modevalue");
            String msg = (String)mapExecute.get("passmsg");
            logTestngReport(mapExecute);
            WebClient wc = new WebClient();
            ac.testZeroPageLoginPositive(wc, user, password, mode, modevalue, msg);
        } catch (Exception e) {
            log(Level.SEVERE, "testLoginPositive", e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        }
        exiting("testLoginPositive");
    }

  /**
   * Tests for unsuccessful login into the system using incorrect
   * credentials
   */
    @Test(groups = {"client"})
    public void testLoginNegative()
        throws Exception {
        entering("testLoginNegative", null);
        try {
            String user = (String)mapExecute.get("user");
            String password = (String)mapExecute.get("password");
            String mode = (String)mapExecute.get("mode");
            String modevalue = (String)mapExecute.get("modevalue");
            String msg = (String)mapExecute.get("failmsg");
            logTestngReport(mapExecute);
            WebClient wc = new WebClient();
            ac.testZeroPageLoginNegative(wc, user, password, mode, modevalue, msg);
        } catch (Exception e) {
            log(Level.SEVERE, "testLoginNegative", e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        }
        exiting("testLoginNegative");
    }

   /**
    * This method is to clear the initial setup. It does the following:
    * (1) Delete authentication service
    * (2) Delete authentication instance
    * (3) Delete all users and roles
    * This is called only once per auth module.
    */
    @AfterClass(groups={"client"})
    public void cleanup()
        throws Exception {
        entering("cleanup", null);
        try {
            if (initSetup == 0) {
                String url = protocol + ":" + "//" + host + ":" + port + uri;
                log(logLevel, "cleanup", url);

                log(logLevel, "setup", "UserName:" + user);
                log(logLevel, "setup", "RoleName:" + rolename);
                log(logLevel, "setup", "service_servicename:" +
                        service_servicename);
                log(logLevel, "setup", "module_subconfigname:" +
                        module_subconfigname);
                log(logLevel, "setup", "svcName:" + svcName);

                Reporter.log("UserName:" + user);
                Reporter.log("RoleName:" + rolename);
                Reporter.log("service_servicename:" + service_servicename);
                Reporter.log("module_subconfigname:" + module_subconfigname);
                Reporter.log("service_subconfigname:" + service_subconfigname);

                AccessManager am = new AccessManager(url);
                WebClient webClient = new WebClient();
                consoleLogin(webClient, url, adminUser, adminPassword);
                list.clear();
                list.add(user);
                am.deleteIdentities(webClient, realm, list, "User");
                list.clear();
                list.add(rolename);
                am.deleteIdentities(webClient, realm, list, "Role");
                am.deleteSubConfiguration(webClient, service_servicename,
                        service_subconfigname, realm); 
                list.clear();
                list.add(module_subconfigname);
                am.deleteAuthInstances(webClient, realm, list); 
            }
        } catch (Exception e) {
            log(Level.SEVERE, "cleanup", e.getMessage(),
                    null);
            e.printStackTrace();
            throw e;
        }
        exiting("cleanup");
    }
}
