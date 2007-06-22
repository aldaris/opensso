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
 * $Id: AuthTestConfigUtil.java,v 1.4 2007-06-22 21:52:28 sridharev Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common.authentication;

import com.gargoylesoftware.htmlunit.WebClient;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.TestCommon;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final String moduleSubconfigid = "serverconfig";
    private String testbaseDir;
    private String url;
    private String logoutURL;
    private String configInfo;
    private String testConfigRealm = "/";
    
    /**
     * Default Contructor
     * @param configuration data file name
     */
    public AuthTestConfigUtil(String config){
        super("AuthTestConfigUtil");
        this.configInfo = config;
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
            if (key.startsWith(moduleName)) {
                String value  = configdata.getString(key);
                mapModData.put(key, value);
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
        return moduleSubconfigid;
    }
    
    /**
     * Sets test realm
     * @param realm name
     */
    public void setTestConfigRealm(String realmName){
        if (!(realmName.equals("/")))
            testConfigRealm = testConfigRealm + realmName;
    }
    
    /**
     * Creates the module instances
     * @param moduleServicename
     * @param moduleSubconfig
     * @param moduleConfigdata
     **@param moduleConfigId
     */
    public void createModuleInstances(String modServname, String modSubconf,
            List modConfdata, String modConfId) throws Exception {
        log(logLevel, "createModuleInstances", "moduleServicename:" +
                modServname);
        log(logLevel, "createModuleInstances", "moduleServicename:" +
                modSubconf);
        log(logLevel, "createModuleInstances", "moduleServicename:" +
                modConfId);
        FederationManager am = new FederationManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createSubConfiguration(webClient, modServname,
                modSubconf, modConfdata, realm, modConfId);
        consoleLogout(webClient, logoutURL);
    }
    
    /**
     * Create Services
     * @param chainname
     * @param List service_data
     */
    public void createServices(String chainname, List servData)
    throws Exception {
        String servicename = "iPlanetAMAuthConfiguration";
        String subconfigid = "NamedConfiguration";
        String subconfigname = "Configurations/" + chainname;
        log(logLevel, "createServices", "servicename:" + servicename);
        log(logLevel, "createServices", "subconfig " + subconfigid);
        log(logLevel, "createServices", "subconfigname:" + subconfigname);
        FederationManager am = new FederationManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createSubConfiguration(webClient, servicename, subconfigname, 
                servData, realm, subconfigid);
        consoleLogout(webClient, logoutURL);
    }
    
    /**
     * Create Users
     * @param List user list
     * @param username
     */
    public void createUser(List userList, String uname)
    throws Exception{
        FederationManager am = new FederationManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createIdentity(webClient, realm, uname, "User", userList);
        log(logLevel, "createUser", "User:" + userList);
        consoleLogout(webClient, logoutURL);
    }
    
    /**
     * Creates the realm
     * @param realmname
     */
    public void createRealms(String realmName)
    throws Exception{
        FederationManager am = new FederationManager(url);
        WebClient webClient = new WebClient();
        consoleLogin(webClient, url, adminUser, adminPassword);
        am.createRealm(webClient, realmName);
        log(logLevel, "createRealms", "Realm:" + realmName);
        consoleLogout(webClient, logoutURL);
    }
    
    /**
     * Get the list of users from Map, to create the
     * users.This is need for the <code>FederationManager</code> to
     * create users on the System
     * @param Map of users to be creared
     * @param moduleName
     */
    public List getListFromMap(Map lMap, String moduleName){
        Object escapeModServiceName = moduleName + ".module-service-name";
        Object escapeModSubConfigName = moduleName + ".module-subconfig-name";
        lMap.remove(escapeModServiceName);
        lMap.remove(escapeModSubConfigName);
        List<String> list = new ArrayList<String>();
        for (Iterator iter = lMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = ( Map.Entry)iter.next();
            String userkey = (String)entry.getKey();
            int sindex = userkey.indexOf(".");
            CharSequence cseq = userkey.subSequence(0, sindex+1);
            userkey = userkey.replace(cseq , "");
            userkey.trim();
            String removeModname = moduleName + ".";
            String userval = (String)entry.getValue();
            String uadd = userkey + "=" + userval;
            uadd.trim();
            list.add(uadd);
            log(logLevel, "getListFromMap", "UserList" + list);
        }
        return list;
    }
}
