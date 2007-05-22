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
 * $Id: ChainTest.java,v 1.2 2007-05-22 23:54:21 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.authentication.AuthTestConfigUtil;
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
 * This class drives the chaining tests, Each Chain can have the number of
 * module instances.
 * Creates the chain test setup based on the test chain
 * mentioned in the test resource bundle, The following are performed in order
 * to test chaining.
 * - Create the module instances by the given module instance names.
 * - Create services (Chain) for the number of modules involved in the chain.
 * - Validates each chain
 */
public class ChainTest extends TestCommon{
    
    private ResourceBundle testResources;
    private String chainModules;
    private String chainService;
    private String chainUserNames;
    private String chainModInstances;
    private String chainSuccessURL;
    private String chainFailureURL;
    private List moduleConfigData;
    private String moduleServiceName;
    private String moduleSubConfig;
    private String moduleSubConfigId;
    private String testChainName;
    private List chainInstances;
    private String configrbName = "authenticationConfigData";
    private List<String> testUserList = new ArrayList<String>();
    
    /**
     * Default Constructor
     **/
    public ChainTest() {
       super("ChainTest");
    }
    
    /**
     * <code>AuthChainTest</code> class has the implementation of the
     * Chaining/Service based authentication, This tests reads, configures,
     * the system based on the configuration details provided for testing and
     * performs the tests, the results are logged. If configuration fails for
     * any service that drives this class the tests will not be run.
     * @param test resources bundle holding the test data
     * @param Chain/service Name
     */
    public ChainTest(ResourceBundle rbName,String chainName){
        super("ChainTest");
        testResources = rbName;
        testChainName = chainName;
    }
    /**
     * Creates the necessary configuration for the chain suchs as 
     * creating module instances and chain service by the given service name
     * before performing the tests.This method is called first when this
     * class is instantiated by the <code>Factory</code> implementation in
     * testNg framework
     */
    @BeforeClass(groups = {"client"})
    public void setup(){
        entering("setup", null);
        chainModules = testResources.getString("am-auth-test-" + 
                testChainName + "-modules");
        chainService = testResources.getString("am-auth-test-" +
                testChainName + "-servicename");
        chainUserNames = testResources.getString("am-auth-test-" +
                testChainName+ "-users");
        chainModInstances = testResources.getString("am-auth-test-" +
                testChainName+ "-instances");
        chainSuccessURL = testResources.getString("am-auth-test-" + 
                testChainName + "-successURL");
        chainFailureURL = testResources.getString("am-auth-test-" + 
                testChainName + "-failureURL");
        String aliases = testResources.getString("am-auth-test-" + 
                testChainName + "-" +"administrator-alias");
        log(logLevel, "setup", "testChainName " + testChainName);
        log(logLevel, "setup", "Modules for Chain : " + chainModules);
        log(logLevel, "setup", "Service Name for Chain : " + chainService);
        log(logLevel, "setup", "Users for the chain : " +
                chainUserNames);
        log(logLevel, "setup", "Module Intances for chain :" +
                chainModInstances);
        log(logLevel, "setup", "SuccessURL for the Chain :" 
                + chainSuccessURL);
        log(logLevel, "setup", "Failure URL for the chain : " 
                + chainFailureURL);
        Reporter.log("ChainName " + testChainName);
        Reporter.log("Modules for Chain : " + chainModules);
        Reporter.log("Service Name for Chain : " + chainService);
        Reporter.log("Users for the chain : " + chainUserNames);
        Reporter.log("Module Intances for chain :" + chainModInstances);
        Reporter.log("SuccessURL for the Chain :" + chainSuccessURL);
        Reporter.log("Failure URL for the chain : " + chainFailureURL);
        StringTokenizer modTokens = new StringTokenizer(chainModules,",");
        List<String> chainModules = new ArrayList<String>();
        while (modTokens.hasMoreTokens()) {
            chainModules.add(modTokens.nextToken());
        }  
        for (String modName: chainModules){
            createModule(modName);
        }
        log(logLevel, "setup", "Modules Created");
        createService(chainService,chainModInstances,chainSuccessURL,
                chainFailureURL);
        log(logLevel, "setup", "ChainName " + testChainName);
        Map users = createUserMap(chainUserNames);
        createUsers(users,testChainName);
        exiting("setup");
    }

    /**
     * Peform the test validation for this chain/service. This method
     * performs the positive test validation by calling
     * <code>ChainTestValidation</code> and its appropriate method.
     */
    @Test(groups = {"client"})
    public void validatePositiveTests() throws Exception {
        entering("validatePositiveTests", null);
        Map executeMap;
        executeMap = new HashMap();
        executeMap.put("Chainname",testChainName);
        executeMap.put("passmessage",testResources.getString("am-auth-test-" +
                testChainName + "-passmg"));
        executeMap.put("failmessage",testResources.getString("am-auth-test-" + 
                testChainName + "-failmsg"));
        executeMap.put("users",testResources.getString("am-auth-test-" +
                testChainName + "-users"));
        executeMap.put("servicename",testResources.getString("am-auth-test-" +
                testChainName + "-servicename"));
        log(logLevel, "validatePositiveTests", "ExecuteMap:" + executeMap);
        ChainTestValidation ct = new ChainTestValidation(executeMap);
        ct.testServicebasedPositive();
        exiting("validatePositiveTests");
    }
    
    /**
     * Peform the test validation for this chain/service. This method
     * performs the Negative test validation by calling
     * <code>ChainTestValidation</code> and its appropriate method.
     */
    @Test(groups = {"client"})
    public void validateNegativeTests() throws Exception {
        entering("validateNegativeTests", null);
        Map executeMap;
        executeMap = new HashMap();
        executeMap.put("Chainname",testChainName);
        executeMap.put("passmessage",testResources.getString("am-auth-test-" +
                testChainName + "-passmg"));
        executeMap.put("failmessage",testResources.getString("am-auth-test-" + 
                testChainName + "-failmsg"));
        executeMap.put("users",testResources.getString("am-auth-test-" +
                testChainName + "-users"));
        executeMap.put("servicename",testResources.getString("am-auth-test-" +
                testChainName + "-servicename"));
        log(logLevel, "validateNegativeTests", "ExecuteMap:" + executeMap);
        ChainTestValidation ct = new ChainTestValidation(executeMap);
        ct.testServicebasedNegative();
        exiting("validateNegativeTests");
    }

    /**
     * Clean up is called post execution of each chain before entering into
     * the next chain, This is done to maintain the system in a clean state
     * after executing each test scenario, in this case each chain/service
     * authentication is done.This processed in this method are:
     * 1. Delete the authentication service/Chain
     * 2. Delete the modules involved in that service/chain
     * 3. Delete the users involved/create for this chain.
     */
    @AfterClass(groups={"client"})
    public void cleanup()
        throws Exception {
        entering("cleanup", null);
        try {
            String url = protocol + ":" + "//" + host + ":" + port + uri;
            log(logLevel, "cleanup", url);
            log(logLevel, "cleanup", "chainName:" + testChainName);
            Reporter.log("chainName:" + testChainName);
            StringTokenizer moduleInstances = new StringTokenizer
                    (chainModInstances,",");
            List<String> chainList = new ArrayList<String>();
            chainList.add(chainService);
            log(logLevel, "cleanup", "ChainList: " + chainList);
            List<String> instanceNames = new ArrayList<String>();
            while (moduleInstances.hasMoreTokens()) {
                instanceNames.add(moduleInstances.nextToken());
            }
            FederationManager am = new FederationManager(url);
            WebClient webClient = new WebClient();
            consoleLogin(webClient, url, adminUser, adminPassword);
            HtmlPage page = (HtmlPage)am.deleteAuthConfigurations
                    (webClient,realm,chainList);
            log(logLevel, "cleanup", "Page:" + page.asXml());
            am.deleteAuthInstances(webClient,realm,instanceNames);
            log(logLevel, "cleanup", "Authentication Instances:" 
                    + instanceNames);
            am.deleteIdentities(webClient, realm, testUserList, "User");
            log(logLevel, "cleanup", "Users:" + testUserList);
            url = url + "/UI/Logout";
            consoleLogout(webClient, url);
        }catch(Exception e){
            log(Level.SEVERE, "cleanup", e.getMessage(), null);
            e.printStackTrace();
            throw e;
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
            log(logLevel,"createModule","ModuleServiceName :" + 
                    moduleServiceName);
            log(logLevel,"createModule","ModuleSubConfig :" + 
                    moduleSubConfig);
            log(logLevel,"createModule","ModuleSubConfigId :" + 
                    moduleSubConfigId);
            moduleConfigData = getListFromMap(modMap,mName);
            moduleConfig.createModuleInstances(moduleServiceName,
                    moduleSubConfig,moduleConfigData,moduleSubConfigId);
        }catch(Exception e){
            log(Level.SEVERE, "createModule", e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Creates authentication service for the given module instances
     * by instantiating the authentication utility class
     * @param name of service
     * @param module instances for this service
     * @param service successURL
     * @param service FailureURL
     **/
    private void createService(String mService,String mInstances,
            String mSuccess,String mFailure){
        try {
            String srv_attribute_name = "iplanet-am-auth-configuration=" +
                    "<AttributeValuePair>";
            String csrv_attribute = " REQUIRED</Value></AttributeValuePair>";
            List<String> chainInstances = new ArrayList<String>();
            StringTokenizer testChainInstances = 
                    new StringTokenizer(mInstances,",");
            String reqModules=""; 
            while(testChainInstances.hasMoreTokens()){
                reqModules = reqModules + "<Value>" + 
                        testChainInstances.nextToken() + 
                        " REQUIRED" + "</Value>";
            }
            String srvData = srv_attribute_name + reqModules + 
                    "</AttributeValuePair>";
            log(logLevel,"createService","ServiceData " + srvData);
            chainInstances.add(srvData);
            chainInstances.add("iplanet-am-auth-login-success-url=" + mSuccess);
            chainInstances.add("iplanet-am-auth-login-failure-url=" + mFailure);
            AuthTestConfigUtil serviceConfig = 
                    new AuthTestConfigUtil(configrbName);
            serviceConfig.createServices(mService,chainInstances);
        }catch(Exception e){
            log(Level.SEVERE, "createService", e.getMessage(), null);
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the required test users on the system for each
     * Chain to be executed
     * @param user map to be created
     * @param ChainName
     **/
    private void createUsers(Map testUsers,String testChain){
        List<String> userList = new ArrayList<String>();
        for (Iterator iter = testUsers.entrySet().iterator();iter.hasNext();)
        {
            Map.Entry entry = (Map.Entry)iter.next();
            String newUser = (String)entry.getKey();
            newUser.trim();
            testUserList.add(newUser);
            String userPassword = (String)entry.getValue();
            userPassword.trim();
            String aliasName = testResources.getString("am-auth-test-" +
                    testChain + "-" + newUser + "-alias");
            userList.add("sn=" + newUser);
            userList.add("cn=" + newUser);
            userList.add("userpassword=" + userPassword);
            userList.add("iplanet-am-user-alias-list=" + aliasName);
            userList.add("inetuserstatus=Active");
            log(logLevel,"createUsers","userList " + userList);
            try {
                AuthTestConfigUtil userConfig = 
                        new AuthTestConfigUtil(configrbName);
                userConfig.createUser(userList,newUser);
            }catch(Exception e){
                log(Level.SEVERE, "createUsers", e.getMessage(), null);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Creates users map
     * @param Row of users:password with a delimeter for each user involved
     */
    private Map createUserMap(String sUsers) throws MissingResourceException {
        Map UserMap = new HashMap();
        StringTokenizer userTokens = new StringTokenizer(sUsers,"|");
        List<String> chainUsers = new ArrayList<String>();
        while (userTokens.hasMoreTokens()) {
            chainUsers.add(userTokens.nextToken());
        }
        for (String uName: chainUsers){
            int uLength = uName.length();
            int uIndex = uName.indexOf(":");
            String userName = uName.substring(0,uIndex);
            String userPass = uName.substring(uIndex+1,uLength);
            UserMap.put(userName,userPass);
            log(logLevel,"createUserMap","User Map" + UserMap);
        }
        return UserMap;
    }
    
    /**
     * Get the list of users from Map, to create the
     * users.This is need for the <code>FederationManager</code> to
     * create users on the System
     * @param Map of users to be creared
     * @param moduleName
     */
    private List getListFromMap(Map lMap,String moduleName){
        Object escapeModServiceName = moduleName + ".module-service-name";
        Object escapeModSubConfigName = moduleName + ".module-subconfig-name";
        lMap.remove(escapeModServiceName);
        lMap.remove(escapeModSubConfigName);
        List<String> list = new ArrayList<String>();
        for(Iterator iter = lMap.entrySet().iterator();iter.hasNext();)
        {
            Map.Entry entry = ( Map.Entry)iter.next();
            String userkey = (String)entry.getKey();
            int sindex=userkey.indexOf(".");
            CharSequence cseq = userkey.subSequence(0,sindex+1);
            userkey=userkey.replace(cseq,"");
            userkey.trim();
            String removeModname = moduleName+".";
            String userval = (String)entry.getValue();
            String uadd = userkey + "=" + userval;
            uadd.trim();
            list.add(uadd);
            log(logLevel,"getListFromMap","UserList" + list);
        }
        return list;
    }
}
