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
 * $Id: AuthTestConfigUtil.java,v 1.1 2007-05-04 20:46:13 sridharev Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.AccessManager;
import com.sun.identity.qatest.common.TestCommon;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Authentication Test Utility class <code>AuthTestUtil</code>
 * extends <code>TestCommon</code> helper class contains utility
 * methods to create module instances,services,users,gets the default
 * module related configuration data
 */
public class AuthTestConfigUtil extends TestCommon {
    
    private String moduleName;
    private Map<String, String> mapModData;
    private String moduleService;
    private String moduleSubConfigName;
    private ResourceBundle configdata;
    private static final String module_subconfigid ="serverconfig";
    private String  testbaseDir;
    private String url;
    private String logoutURL;
    private String configInfo;
    
    /**
     * Default Contructor
     * @param configuration data file name
     */
    public AuthTestConfigUtil(String config_data){
        super("AuthTestConfigUtil");
        this.configInfo = config_data;
        this.configdata =  ResourceBundle.getBundle(configInfo);
        url = protocol + ":" + "//" + host + ":" + port + uri;
        logoutURL = url + "/UI/Logout";
    }
    
    /**
     *
     * Returns the module configuration data
     * @param moduleName
     * @return module configuration data map
     */
    public Map getModuleData(String modName){
        moduleName = modName;
        mapModData = new HashMap<String, String>();
        moduleService = configdata.getString(modName
                + ".module-service-name");
        moduleSubConfigName = configdata.getString(modName
                + ".module-subconfig-name");
        Enumeration bundleKeys = configdata.getKeys();
        while (bundleKeys.hasMoreElements()) {
            String key = (String)bundleKeys.nextElement();
            if(key.startsWith(moduleName)){
                String value  = configdata.getString(key);
                mapModData.put(key,value);
            }
        }
        log(logLevel, "getModuleData", "ModuleData:" + mapModData);
        return mapModData;
    }
    
    /**
     * Returns test URL for this test instance
     * @return url
     */
    public String getURL(){
        return url;
    }
    
    /**
     * Returns Module Service Name
     * @return moduleServiceName
     *
     */
    public String getModuleServiceName(){
        return  moduleService;
    }
    
    /**
     * Returns the module SubconfigName
     * @return moduleSubConfigName
     */
    public String getModuleSubConfigName(){
        return moduleSubConfigName;
    }
    
    /**
     * Returns module Sunconfig ID
     * @return module subconfig Id
     */
    public String getModuleSubConfigId(){
        return module_subconfigid;
    }
    
    /**
     * Creates the module instances
     * @param mod_servicename
     * @param mod_subconfig
     * @param mod_configdata
     **@param mod_configId
     */
    public void createModuleInstances(String mod_servicename,String mod_subconfig,
            List mod_configdata,String mod_configId) throws Exception {
        log(logLevel, "createModuleInstances", "mod_servicename:" +
                mod_servicename);
        log(logLevel, "createModuleInstances", "mod_servicename:" +
                mod_subconfig);
        log(logLevel, "createModuleInstances", "mod_servicename:" +
                mod_configId);
        AccessManager am = new AccessManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createSubConfiguration(webClient, mod_servicename,
                mod_subconfig, mod_configdata, realm, mod_configId);
        consoleLogout(webClient,logoutURL);
    }
    
    /**
     * Create Services
     * @param chainname
     * @param List service_data
     */
    public void createServices(String chainname,List service_data )
    throws Exception {
        String service_servicename = "iPlanetAMAuthConfiguration";
        String service_subconfigid="NamedConfiguration";
        String service_subconfigname ="Configurations/" + chainname;
        log(logLevel, "createServices", "service_servicename:" +
                service_servicename);
        log(logLevel, "createServices", "service_subconfigid:" +
                service_subconfigid);
        log(logLevel, "createServices", "service_subconfigname:" +
                service_subconfigname);
        AccessManager am = new AccessManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createSubConfiguration(webClient, service_servicename,
                service_subconfigname, service_data, realm,
                service_subconfigid);
        consoleLogout(webClient,logoutURL);
    }
    
    /**
     * Create Users
     * @param List user list
     * @param username
     */
    public void createUser(List user_list,String uname)
    throws Exception{
        AccessManager am = new AccessManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createIdentity(webClient, realm, uname, "User", user_list);
        log(logLevel, "createUser", "User:" + user_list);
        consoleLogout(webClient,logoutURL);
    }
    
}
