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
 * $Id: SetupProduct.java,v 1.14 2008-05-16 22:21:33 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.setup;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.iplanet.sso.SSOToken;
import com.sun.identity.qatest.common.FederationManager;
import com.sun.identity.qatest.common.LDAPCommon;
import com.sun.identity.qatest.common.SMSCommon;
import com.sun.identity.qatest.common.SMSConstants;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.qatest.common.TestConstants;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import java.util.logging.Level;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * This class calls the TestCommon.configureProduct method to configure
 * the product. In case product is already configured, nothing happens.
 */
public class SetupProduct extends TestCommon {
    
    private String FILE_CLIENT_PROPERTIES = "AMConfig.properties";
    private Map properties = new HashMap();
    private SMSCommon smsc;
    private SSOToken admintoken;
    List list;
    /**
     * If the system is execution in single server mode, it makes a call to
     * configure the product. If system is execution in multi server mode
     * (samlv2 etc), no product configuration call is made.
     */
    public SetupProduct(String serverName1, String serverName2)
    throws Exception {
        super("SetupProduct");
        
        try {
            boolean bserver1;
            boolean bserver2;
            String namingProtocol = "";
            String namingHost = "";
            String namingPort = "";
            String namingURI = "";
            
            if ((serverName1.indexOf("SERVER_NAME1") == -1) &&
                    (serverName2.indexOf("SERVER_NAME2") == -1)) {
                log(Level.FINE, "SetupProduct", "Initiating setup for " +
                        serverName1);
                ResourceBundle cfg1 = ResourceBundle.
                        getBundle("Configurator-" + serverName1);
                String strURL = cfg1.
                        getString(TestConstants.KEY_ATT_NAMING_SVC);
                log(Level.FINE, "SetupProduct", "Server URL: " + strURL);
                Map map = getURLComponents(strURL);
                log(Level.FINE, "SetupProduct", "Server URL Components: " +
                        map);
                namingProtocol = (String)map.get("protocol");
                namingHost = (String)map.get("host");
                namingPort = (String)map.get("port");
                namingURI = (String)map.get("uri");
                bserver1 = configureProduct(getConfigurationMap("Configurator-"
                        + serverName1, namingProtocol, namingHost, namingPort, 
                        namingURI));
                list = new ArrayList();
                if (!bserver1) {
                    log(Level.FINE, "SetupProduct",
                            "Configuration failed for " + serverName1);
                    assert false;
                } else {
                    String strUMDatastore1 = cfg1.getString("umdatastore");
                    if (!(strUMDatastore1.equals("embedded"))) {
                        String adminUser = cfg1.getString(
                                TestConstants.KEY_ATT_AMADMIN_USER);
                        String adminPassword = cfg1.getString(
                                TestConstants.KEY_ATT_AMADMIN_PASSWORD);
                        String defDatastoreName1 = cfg1.getString(
                                TestConstants.KEY_ATT_CONFIG_DEFDATASTORENAME);
                        
                        String loginURL = namingProtocol + ":" + "//" + 
                                namingHost + ":" + namingPort + namingURI + 
                                "/UI/Login";
                        String logoutURL = namingProtocol + ":" + "//" + 
                                namingHost + ":" + namingPort + namingURI + 
                                "/UI/Logout";
                        String famadmURL = namingProtocol + ":" + "//" + 
                                namingHost + ":" + namingPort + namingURI;
                        List datastoreList = new ArrayList();
                        
                        ResourceBundle cfg1Data =
                                ResourceBundle.getBundle("configGlobalData");
                        int dCount = new Integer(cfg1Data.getString(
                                SMSConstants.SMS_DATASTORE_PARAMS_PREFIX +
                                "0." +
                                SMSConstants.SMS_DATASTORE_COUNT)).intValue();
                        WebClient webClient = null;
                        try {
                            FederationManager famadm =
                                    new FederationManager(famadmURL);
                            webClient = new WebClient();
                            consoleLogin(webClient, loginURL, adminUser,
                                    adminPassword);

                            //delete default datastore if createDatastore
                            //is successful and the default datastore exists
                            if (createDataStoreUsingfamadm(webClient,
                                    famadm, cfg1Data, 0, dCount)) {
                                datastoreList.add(defDatastoreName1);
                                HtmlPage pageDStore =
                                        famadm.listDatastores(webClient,
                                        realm);
                                if (FederationManager.
                                        getExitCode(pageDStore) != 0) {
                                    log(Level.SEVERE, "setup",
                                            "listDatastores famadm" +
                                            " command failed");
                                    assert false;
                                }
                                if (getHtmlPageStringIndex(
                                        pageDStore, defDatastoreName1) ==
                                        -1) {
                                    log(Level.FINEST, "setup", "Default" +
                                            " Datastore doesn't exist " +
                                            list);
                                } else {
                                    if (FederationManager.getExitCode(
                                            famadm.deleteDatastores(
                                            webClient, realm,
                                            datastoreList)) != 0) {
                                        log(Level.SEVERE, "setup",
                                                "deleteDatastores famadm" +
                                                " command failed");
                                        assert false;
                                    }
                                }
                            } else {
                                log(Level.SEVERE, "setup", "DataStore" +
                                    " configuration didn't succeed for " +
                                    namingHost);
                            }
                        } catch (Exception e) {
                            log(Level.SEVERE, "setup", e.getMessage());
                            e.printStackTrace();
                        } finally {
                            consoleLogout(webClient, logoutURL);
                        }
                    }
                }
                
                //Configure second server.
                log(Level.FINE, "SetupProduct", "Initiating setup for " +
                        serverName2);
                bserver2 = configureProduct(
                        getConfigurationMap("Configurator-" + serverName2));
                if (!bserver2) {
                    log(Level.FINE, "SetupProduct", "Configuration failed" +
                            " for " + serverName2);
                    assert false;
                } else {
                    ResourceBundle cfg2 =
                            ResourceBundle.getBundle("Configurator-" +
                            serverName2);
                    String strUMDatastore2 = cfg2.getString("umdatastore");
                    String defDataStoreName2 = cfg2.getString(TestConstants.
                            KEY_ATT_CONFIG_DEFDATASTORENAME);
                    log(Level.FINE, "SetupProduct", "UM Datastore for " +
                            serverName2 + " is " + strUMDatastore2);
                    if (!(strUMDatastore2.equals("embedded"))) {
                        admintoken = getToken(adminUser, adminPassword, basedn);
                        smsc = new SMSCommon(admintoken, "SMSGlobalConfig");
                        smsc.createDataStore(1, "configGlobalData");
                        Set datastoreSet2 = smsc.listDataStore(realm);
                        if (datastoreSet2.contains(defDataStoreName2)) {
                            smsc.deleteDataStore(realm, defDataStoreName2);
                        }
                        modifyPolicyService();
                    }
                }
                log(Level.FINE, "SetupProduct", "Check if multiprotocol " +
                        "flag is enabled or not ");
                if (cfg1.getString(TestConstants.KEY_ATT_MULTIPROTOCOL_ENABLED).
                        equalsIgnoreCase("true")) {
                    //configure multiple sp's
                    log(Level.FINE, "SetupProduct", "Initiating setup for " +
                            "multiple SP's");
                    String serverName3 = cfg1.getString(TestConstants.
                            KEY_ATT_IDFF_SP);
                    String serverName4 = cfg1.getString(TestConstants.
                            KEY_ATT_WSFED_SP);
                    
                    ResourceBundle cfg3 = ResourceBundle.
                            getBundle("Configurator-" + serverName3);
                    strURL = cfg3.
                            getString(TestConstants.KEY_ATT_NAMING_SVC);
                    log(Level.FINE, "SetupProduct", "Server 3 URL: " + strURL);
                    map = getURLComponents(strURL);
                    log(Level.FINE, "SetupProduct", "Server3 URL Components: " +
                            map);
                    namingProtocol = (String)map.get("protocol");
                    namingHost = (String)map.get("host");
                    namingPort = (String)map.get("port");
                    namingURI = (String)map.get("uri");
                    
                    boolean bserver3 = configureProduct(
                            getConfigurationMap("Configurator-" + serverName3,
                            namingProtocol, namingHost, namingPort, 
                            namingURI));
                    if (!bserver3) {
                        log(Level.FINE, "SetupProduct", "Configuration failed" +
                                " for " + serverName3);
                        assert false;
                    } else {
                        String strUMDatastore1 = cfg3.getString("umdatastore");
                        if (!(strUMDatastore1.equals("embedded"))) {
                            String adminUser = cfg3.getString(
                                    TestConstants.KEY_ATT_AMADMIN_USER);
                            String adminPassword = cfg3.getString(
                                    TestConstants.KEY_ATT_AMADMIN_PASSWORD);
                            String defDatastoreName3 = cfg3.getString(
                                    TestConstants.
                                    KEY_ATT_CONFIG_DEFDATASTORENAME);
                            
                            String loginURL = namingProtocol + ":" + "//" + 
                                    namingHost + ":" + namingPort + 
                                    namingURI + "/UI/Login";
                            String logoutURL = namingProtocol + ":" + "//" + 
                                    namingHost + ":" + namingPort + 
                                    namingURI + "/UI/Logout";
                            String famadmURL = namingProtocol + ":" + "//" + 
                                    namingHost + ":" + namingPort + 
                                    namingURI;
                            List datastoreList = new ArrayList();
                            ResourceBundle cfg1Data =
                                    ResourceBundle.getBundle(
                                    "configGlobalData");
                            int dCount = new Integer(cfg1Data.getString(
                                    SMSConstants.SMS_DATASTORE_PARAMS_PREFIX +
                                    "2." +
                                    SMSConstants.SMS_DATASTORE_COUNT)).
                                    intValue();
                            WebClient webClient = null;
                            try {
                                FederationManager famadm =
                                        new FederationManager(famadmURL);
                                webClient = new WebClient();
                                consoleLogin(webClient, loginURL, adminUser,
                                        adminPassword);
                                
                                //delete default datastore if createDatastore
                                //is successful and the default datastore exists
                                if (createDataStoreUsingfamadm(webClient,
                                        famadm, cfg1Data, 2, dCount)) { 
                                    datastoreList.add(defDatastoreName3);
                                    HtmlPage pageDStore =
                                            famadm.listDatastores(webClient,
                                            realm);
                                    if (FederationManager.
                                            getExitCode(pageDStore) != 0) {
                                        log(Level.SEVERE, "setup",
                                                "listDatastores famadm" +
                                                " command failed");
                                        assert false;
                                    }
                                    if (getHtmlPageStringIndex(
                                            pageDStore, defDatastoreName3) ==
                                            -1) {
                                        log(Level.FINEST, "setup", "Default" +
                                                " Datastore doesn't exist " +
                                                list);
                                    } else {
                                        if (FederationManager.getExitCode(
                                                famadm.deleteDatastores(
                                                webClient, realm,
                                                datastoreList)) != 0) {
                                            log(Level.SEVERE, "SetupProduct",
                                                    "deleteDatastores famadm" +
                                                    " command failed");
                                            assert false;
                                        }
                                    }
                                } else
                                    log(Level.SEVERE, "setup", "DataStore" +
                                        " configuration didn't succeed for " + 
                                        namingHost);
                            } catch (Exception e) {
                                log(Level.SEVERE, "setup", e.getMessage());
                                e.printStackTrace();
                            } finally {
                                consoleLogout(webClient, logoutURL);
                            }
                        }
                    }
                    
                    //Configure SP for WSFed
                    ResourceBundle cfg4 =
                            ResourceBundle.getBundle("Configurator-" +
                            serverName4);
                    strURL = cfg4.
                            getString(TestConstants.KEY_ATT_NAMING_SVC);
                    log(Level.FINE, "SetupProduct", "Server 4 URL: " + strURL);
                    map = getURLComponents(strURL);
                    log(Level.FINE, "SetupProduct", "Server 4 URL Components:" +
                            " " +  map);
                    namingProtocol = (String)map.get("protocol");
                    namingHost = (String)map.get("host");
                    namingPort = (String)map.get("port");
                    namingURI = (String)map.get("uri");
                    
                    ResourceBundle cfg1Data =
                            ResourceBundle.getBundle("configGlobalData");
                    boolean bserver4 = configureProduct(
                            getConfigurationMap("Configurator-" + serverName4,
                            namingProtocol, namingHost, namingPort,
                            namingURI));
                    if (!bserver4) {
                        log(Level.FINE, "SetupProduct", "Configuration failed" +
                                " for " + serverName4);
                        assert false;
                    } else {
                        String strUMDatastore1 = cfg4.getString("umdatastore");
                        if (!(strUMDatastore1.equals("embedded"))) {
                            String adminUser = cfg4.getString(
                                    TestConstants.KEY_ATT_AMADMIN_USER);
                            String adminPassword = cfg4.getString(
                                    TestConstants.KEY_ATT_AMADMIN_PASSWORD);
                            String defDatastoreName4 = cfg4.getString(
                                    TestConstants.
                                    KEY_ATT_CONFIG_DEFDATASTORENAME);
                            
                            String loginURL = namingProtocol + ":" + "//" + 
                                    namingHost + ":" + namingPort + 
                                    namingURI + "/UI/Login";
                            String logoutURL = namingProtocol + ":" + "//" + 
                                    namingHost + ":" + namingPort + 
                                    namingURI + "/UI/Logout";
                            String famadmURL = namingProtocol + ":" + "//" + 
                                    namingHost + ":" + namingPort + 
                                    namingURI;
                            List datastoreList = new ArrayList();
                            
                            int dCount = new Integer(cfg1Data.getString(
                                    SMSConstants.SMS_DATASTORE_PARAMS_PREFIX +
                                    "3." +
                                    SMSConstants.SMS_DATASTORE_COUNT)).
                                    intValue();
                            WebClient webClient = null;
                            try {
                                FederationManager famadm =
                                        new FederationManager(famadmURL);
                                webClient = new WebClient();
                                consoleLogin(webClient, loginURL, adminUser,
                                        adminPassword);

                                //delete default datastore if createDatastore
                                //is successful and the default datastore exists
                                if (createDataStoreUsingfamadm(webClient,
                                        famadm, cfg1Data, 3, dCount)) {
                                    datastoreList.add(defDatastoreName4);
                                    HtmlPage pageDStore =
                                            famadm.listDatastores(webClient,
                                            realm);
                                    if (FederationManager.
                                            getExitCode(pageDStore) != 0) {
                                        log(Level.SEVERE, "setup",
                                                "listDatastores famadm" +
                                                " command failed");
                                        assert false;
                                    }
                                    if (getHtmlPageStringIndex(
                                            pageDStore, defDatastoreName4) ==
                                            -1) {
                                        log(Level.FINEST, "setup", "Default" +
                                                " Datastore doesn't exist " +
                                                list);
                                    } else {
                                        if (FederationManager.getExitCode(
                                                famadm.deleteDatastores(
                                                webClient, realm,
                                                datastoreList)) != 0) {
                                            log(Level.SEVERE, "SetupProduct",
                                                    "deleteDatastores famadm" +
                                                    " command failed");
                                            assert false;
                                        }
                                    }
                                } else
                                    log(Level.SEVERE, "setup", "DataStore" +
                                        " configuration didn't succeed for " +
                                        namingHost);
                            } catch (Exception e) {
                                log(Level.SEVERE, "setup", e.getMessage());
                                e.printStackTrace();
                            } finally {
                                consoleLogout(webClient, logoutURL);
                            }
                        }
                    }
                }
            } else if ((serverName1.indexOf("SERVER_NAME1") == -1) &&
                    (serverName2.indexOf("SERVER_NAME2") != -1)) {
                ResourceBundle cfg1 = ResourceBundle.getBundle("Configurator-" +
                        serverName1);                
                String namingURL = cfg1.getString(KEY_ATT_NAMING_SVC);
                Map namingURLMap = getURLComponents(namingURL);
                namingProtocol = (String) namingURLMap.get("protocol");
                namingHost = (String) namingURLMap.get("host");
                namingPort = (String) namingURLMap.get("port");
                namingURI = (String) namingURLMap.get("uri");
                bserver1 = configureProduct(getConfigurationMap("Configurator-"
                        + serverName1, namingProtocol, namingHost, namingPort,
                        namingURI));
                if (!bserver1) {
                    log(Level.FINE, "SetupProduct", "Configuration failed for" +
                            " " + serverName1);
                    setSingleServerSetupFailedFlag();
                    assert false;
                } else {
                    String strUMDatastore1 = cfg1.getString("umdatastore");
                    String defDataStoreName1 = cfg1.getString(TestConstants.
                            KEY_ATT_CONFIG_DEFDATASTORENAME);
                    log(Level.FINE, "SetupProduct", "UM Datastore for " +
                            serverName1 + " is " + strUMDatastore1);
                    if (!(strUMDatastore1.equals("embedded"))) {
                        admintoken = getToken(adminUser, adminPassword, basedn);
                        smsc = new SMSCommon(admintoken, "SMSGlobalConfig");
                        smsc.createDataStore(1, "configGlobalData");
                        Set datastoreSetSingle = smsc.listDataStore(realm);
                        if (datastoreSetSingle.contains(defDataStoreName1)) {                        
                            smsc.deleteDataStore(realm, defDataStoreName1);
                        }
                        modifyPolicyService();
                    }
                }
            } else if ((serverName1.indexOf("SERVER_NAME1") != -1) &&
                    (serverName2.indexOf("SERVER_NAME2") == -1)) {
                log(Level.FINE, "SetupProduct", "Unsupported configuration." +
                        " Cannot have SERVER_NAME2 specified without" +
                        " SERVER_NAME1.");
                assert false;
            }
            if (distAuthEnabled) {
                String strServiceName = "iPlanetAMAuthService";
                admintoken = getToken(adminUser, adminPassword, basedn);               
                ServiceConfigManager scm = new ServiceConfigManager(admintoken, 
                        strServiceName, "1.0");
                log(Level.FINEST, "SetupProduct", "get ServiceConfig");
                ServiceConfig sc = scm.getOrganizationConfig(realm, null);
                Map scAttrMap = sc.getAttributes();
                log(Level.FINEST, "SetupProduct", "Map " +
                        "returned from Org config is: " + scAttrMap);
                Set oriAuthAttrValues = null;
                oriAuthAttrValues = (Set) scAttrMap.get
                        ("iplanet-am-auth-login-success-url");
                log(Level.FINEST, "SetupProduct", "Value of " +
                        "iplanet-am-auth-login-success-url: " + 
                        oriAuthAttrValues);
                Set newAuthValues = new HashSet();
                newAuthValues.add(namingProtocol + "://" + namingHost + ":" + 
                        namingPort + namingURI + "/console");
                Map newAuthValuesMap = new HashMap();
                newAuthValuesMap.put("iplanet-am-auth-login-success-url", 
                        newAuthValues);
                log(Level.FINEST, "AuthServiceOrgModificationTest", "Set " +
                        "iplanet-am-auth-login-success-url to " + 
                        newAuthValuesMap);
                sc.setAttributes(newAuthValuesMap);
            }                        
        } catch(Exception e) {
            log(Level.SEVERE, "setup", e.getMessage());
            e.printStackTrace();
        } finally {          
            destroyToken(admintoken);
        }
    }
    
    /**
     * In case product configuration fails, this method rewrites the client
     * side AMConfig.properties file and sets product setup flag to false.
     */
    public void setSingleServerSetupFailedFlag() {
        entering("setSingleServerSetupFailedFlag", null);
        try {
            log(Level.FINE, "setSingleServerSetupFailedFlag", "Single server" +
                    " product configuration failed.");
            Set set = new HashSet();
            set.add((String)TestConstants.KEY_ATT_PRODUCT_SETUP_RESULT);
            properties = getMapFromResourceBundle("AMConfig", set);
            properties.put(TestConstants.KEY_ATT_PRODUCT_SETUP_RESULT, "fail");
            createFileFromMap(properties, "resources" + fileseparator +
                    FILE_CLIENT_PROPERTIES);
        } catch(Exception e) {
            log(Level.SEVERE, "setSingleServerSetupFailedFlag", e.getMessage());
            e.printStackTrace();
        }
        exiting("setSingleServerSetupFailedFlag");
    }
    
    /**
     * This method creates the datastore using famadm.jsp
     */
    private boolean createDataStoreUsingfamadm(WebClient webClient,
            FederationManager famadm, ResourceBundle cfgData, int
            index, int dCount)
    throws Exception {
        String dsRealm = null;
        String dsType;
        String dsName;
        String adminId;
        String ldapServer;
        String ldapPort;
        String dsAdminPassword;
        String orgName;
        String sslEnabled;
        String authId;
        String psearchBase;
        String groupAtt;
        String roleAtt;
        String userObjClass;
        String userAtt;
        boolean dsCreated = false;
        
        for (int i = 0; i < dCount; i++) {
            dsCreated = false;
            dsType = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_DATASTORE_TYPE + "." +
                    i);
            dsName = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_DATASTORE_NAME + "." +
                    i);
            adminId = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_DATASTORE_ADMINID +
                    "." + i);
            dsRealm = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_DATASTORE_REALM +
                    "." + i);
            ldapServer = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_LDAP_SERVER +
                    "." + i);
            ldapPort = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_LDAP_PORT +
                    "." + i);
            dsAdminPassword = cfgData.getString(
                    SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_DATASTORE_ADMINPW +
                    "." + i);
            orgName = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.
                    SMS_LDAPv3_ORGANIZATION_NAME + "." + i);
            sslEnabled = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_LDAP_SSL_ENABLED
                    + "." + i);
            authId = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_AUTHID
                    + "." + i);
            psearchBase = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_LDAP_PSEARCHBASE
                    + "." + i);
            groupAtt = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_GROUP_ATTR
                    + "." + i);
            roleAtt = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_ROLE_ATTR
                    + "." + i);
            userObjClass = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_USER_OBJECT_CLASS
                    + "." + i);
            userAtt = cfgData.getString(SMSConstants.
                    SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                    SMSConstants.SMS_LDAPv3_USER_ATTR
                    + "." + i);
            list.add(SMSConstants.
                    SMS_LDAPv3_LDAP_SERVER + "=" +
                    ldapServer + ":" + ldapPort);
            list.add(SMSConstants.
                    SMS_LDAPv3_AUTHPW + "=" +
                    dsAdminPassword);
            list.add(SMSConstants.
                    SMS_LDAPv3_ORGANIZATION_NAME + "=" +
                    orgName);
            list.add(SMSConstants.
                    SMS_LDAPv3_LDAP_SSL_ENABLED + "=" +
                    sslEnabled);
            list.add(SMSConstants.
                    SMS_LDAPv3_AUTHID + "=" +
                    authId);
            list.add(SMSConstants.
                    SMS_LDAPv3_LDAP_PSEARCHBASE + "=" +
                    psearchBase);
            if (groupAtt.indexOf("|") != 0) {
                List locList = getAttributeList(groupAtt, "|");
                for (int j = 0; j < locList.size(); j++) {
                    list.add(SMSConstants.
                            SMS_LDAPv3_GROUP_ATTR + "=" +
                            (String)locList.get(j));
                }
            } else {
                list.add(SMSConstants.
                        SMS_LDAPv3_GROUP_ATTR + "=" +
                        groupAtt);
            }
            if (roleAtt.indexOf("|") != 0) {
                List locList = getAttributeList(roleAtt, "|");
                for (int j = 0; j < locList.size(); j++) {
                    list.add(SMSConstants.
                            SMS_LDAPv3_ROLE_ATTR + "=" +
                            (String)locList.get(j));
                }
            } else {
                list.add(SMSConstants.
                        SMS_LDAPv3_ROLE_ATTR + "=" +
                        roleAtt);
            }
            if (userObjClass.indexOf("|") != 0) {
                List locList = getAttributeList(userObjClass, "|");
                for (int j = 0; j < locList.size(); j++) {
                    list.add(SMSConstants.
                            SMS_LDAPv3_USER_OBJECT_CLASS + "=" +
                            (String)locList.get(j));
                }
            } else {
                list.add(SMSConstants.
                        SMS_LDAPv3_USER_OBJECT_CLASS + "=" +
                        userObjClass);
            }
            if (userAtt.indexOf("|") != 0) {
                List locList = getAttributeList(userAtt, "|");
                for (int j = 0; j < locList.size(); j++) {
                    list.add(SMSConstants.
                            SMS_LDAPv3_USER_ATTR + "=" +
                            (String)locList.get(j));
                }
            } else {
                list.add(SMSConstants.
                        SMS_LDAPv3_USER_ATTR + "=" +
                        userAtt);
            }
            log(Level.FINEST, "setup", "Datastore" +
                    " attributes list:" + list);
            HtmlPage page = famadm.listDatastores(webClient,
                    dsRealm);
            if (FederationManager.getExitCode(page) != 0) {
                log(Level.SEVERE, "setup", "listDatastores famadm command" +
                        " failed");
                assert false;
            }
            if (getHtmlPageStringIndex(
                    page, dsName) == -1)
                if (FederationManager.getExitCode(famadm.createDatastore(
                webClient, dsRealm, dsName, dsType, list)) != 0) {
                    log(Level.SEVERE, "setup", "createDatastore famadm" +
                            " command failed");
                    assert false;
                }
            page = famadm.listDatastores(webClient,
                    dsRealm);
            if (FederationManager.getExitCode(page) != 0) {
                log(Level.SEVERE, "setup", "listDatastores famadm command" +
                        " failed");
                assert false;
            }
            if (getHtmlPageStringIndex(
                    page, dsName) == -1) {
                log(Level.SEVERE, "setup", "Datastore" +
                    " creation failed: " + list);
                 assert false;
            }
            dsCreated = true;
            list.clear();
            LDAPCommon ldc = new LDAPCommon(ldapServer,
                    ldapPort, adminId, dsAdminPassword,
                    orgName);
            ResourceBundle smsGblCfg = ResourceBundle.
                    getBundle("SMSGlobalConfig");
            String schemaString = (String)smsGblCfg.
                    getString(SMSConstants.SMS_SCHEMNA_LIST
                    + "." +
                    SMSConstants.SMS_DATASTORE_TYPE_AMDS);
            String schemaAttributes = (String)smsGblCfg.
                    getString(SMSConstants.SMS_SCHEMNA_ATTR
                    + "." +
                    SMSConstants.SMS_DATASTORE_TYPE_AMDS);
            ldc.loadAMUserSchema(schemaString,
                    schemaAttributes);
            ldc.disconnectDServer();
        }
        return (dsCreated);
    }

    /**
     * This method modifies Policy Service using SMS API. LDAP server name,
     * port, bind bn, bind password, users base dn, roles base dn, ssl enabled
     * will be modified based on the configGlobalData single server properties.
     */
    private void modifyPolicyService()
    throws Exception {
        entering("modifyPolicyService", null);
        String dsRealm = null;
        String adminId;
        String ldapServer;
        String ldapPort;
        String dsAdminPassword;
        String orgName;
        String sslEnabled;
        String authId;
        String umDSType;
        int index = 1;
        int i = 0;
        List svcAttrList = new ArrayList();
        List removeSvcAttrList = new ArrayList();

        ResourceBundle amCfgData = ResourceBundle.getBundle("AMConfig");
        ResourceBundle cfgData = ResourceBundle.getBundle("configGlobalData");

        umDSType = amCfgData.getString("umdatastore");

        try {
            if ((umDSType).equals("dirServer")) {
                adminId = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_DATASTORE_ADMINID + "." + i);
                dsRealm = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_DATASTORE_REALM + "." + i);
                ldapServer = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_LDAPv3_LDAP_SERVER +
                        "." + i);
                ldapPort = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_LDAPv3_LDAP_PORT +
                        "." + i);
                dsAdminPassword = cfgData.getString(
                        SMSConstants.SMS_DATASTORE_PARAMS_PREFIX +
                        index + "." + SMSConstants.SMS_DATASTORE_ADMINPW +
                        "." + i);
                orgName = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_LDAPv3_ORGANIZATION_NAME + "." + i);
                sslEnabled = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_LDAPv3_LDAP_SSL_ENABLED + "." + i);
                authId = cfgData.getString(SMSConstants.
                        SMS_DATASTORE_PARAMS_PREFIX + index + "." +
                        SMSConstants.SMS_LDAPv3_AUTHID + "." + i);

                Map scAttrMap =
                        smsc.getAttributes("iPlanetAMPolicyConfigService",
                        dsRealm, "Organization");
                log(Level.FINEST, "modifyPolicyService",
                        "Policy service attributes before modification: " +
                        scAttrMap);

                Map newPolicyAttrValuesMap = new HashMap();
                Set newPolicyAttrValues = new HashSet();
                newPolicyAttrValues.add(ldapServer + ":" + ldapPort);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-ldap-server",
                        newPolicyAttrValues);
                newPolicyAttrValues = new HashSet();
                newPolicyAttrValues.add(dsAdminPassword);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-ldap-bind-password",
                        newPolicyAttrValues);
                newPolicyAttrValues = new HashSet();
                newPolicyAttrValues.add(authId);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-ldap-bind-dn",
                        newPolicyAttrValues);
                newPolicyAttrValues = new HashSet();
                newPolicyAttrValues.add(sslEnabled);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-ldap-ssl-enabled",
                        newPolicyAttrValues);
                newPolicyAttrValues = new HashSet();
                newPolicyAttrValues.add(orgName);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-ldap-base-dn",
                        newPolicyAttrValues);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-is-roles-base-dn",
                        newPolicyAttrValues);
                newPolicyAttrValuesMap.
                        put("iplanet-am-policy-config-ldap-users-base-dn",
                        newPolicyAttrValues);

                log(Level.FINEST, "modifyPolicyService",
                        "Policy attributes being modified: " +
                        newPolicyAttrValuesMap);
                smsc.updateSvcAttribute(dsRealm,
                        "iPlanetAMPolicyConfigService", newPolicyAttrValuesMap,
                        "Organization");

                Map scAttrMapNew =
                        smsc.getAttributes("iPlanetAMPolicyConfigService",
                        dsRealm, "Organization");
                log(Level.FINEST, "modifyPolicyService",
                        "Policy service attributes after modification: "
                        + scAttrMapNew);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "modifyPolicyService", e.getMessage());
            e.printStackTrace();
            throw e;
        }
        exiting("modifyPolicyService");
    }

    public static void main(String args[]) {
        try {
            SetupProduct cp = new SetupProduct(args[0], args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
