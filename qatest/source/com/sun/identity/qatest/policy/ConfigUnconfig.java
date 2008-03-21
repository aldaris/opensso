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
 * $Id: ConfigUnconfig.java,v 1.2 2008-03-21 02:34:47 arunav Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.policy;

import com.iplanet.sso.SSOToken;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceListener;
import com.sun.identity.qatest.common.SMSConstants;
import com.sun.identity.qatest.common.TestCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.logging.Level;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This class configures the Policy service if the UMdatastore is dirServer.
 * Policy service is configured based on the UMDatastore properties.
 * This class also starts and stops the notification server.
 */
public class ConfigUnconfig extends TestCommon {
    
    private ResourceBundle amCfgData;
    private ResourceBundle cfgData;
    SSOToken token;
    ServiceConfigManager scm;
    ServiceConfig sc;
    String strServiceName;
    
    /**
     * Creates a new instance of ConfigUnconfig
     */
    public ConfigUnconfig() {
        super("ConfigUnconfig");
        
        amCfgData = ResourceBundle.getBundle("AMConfig");
        cfgData = ResourceBundle.getBundle("configGlobalData");
        try {
            token = getToken(adminUser, adminPassword, realm);
            log(Level.FINEST, "ConfigUnconfig", "Getting token");
        } catch (Exception e) {
            log(Level.SEVERE, "ConfigUnconfig", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * This method modifies Policy Service using SMS API. LDAP server name,
     * port, bind bn, bind password, users base dn, roles base dn, ssl enabled
     * will be modified based on the configGlobalData single server properties.
     *
     */
    @BeforeSuite(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void modifyPolicyService()
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
                
                strServiceName = "iPlanetAMPolicyConfigService";
                scm = new ServiceConfigManager(token, strServiceName, "1.0");
                sc = scm.getOrganizationConfig(dsRealm, null);
                Map scAttrMap = sc.getAttributes();
                log(Level.FINEST, "modifyPolicyService", "Map " +
                        "returned from config is: " + scAttrMap);
                
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
                
                sc.setAttributes(newPolicyAttrValuesMap);
                
                Map scAttrMapNew = sc.getAttributes();
                log(Level.FINEST, "modifyPolicyService", "Configuration" +
                        " attributes after policy service modification" + 
                        scAttrMapNew);
            }
        } catch (Exception e) {
            log(Level.SEVERE, "modifyPolicyService", e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            destroyToken(token);
        }
        exiting("modifyPolicyService");
    }
    
    /**
     * Start the notification (jetty) server for getting notifications from the
     * server.
     */
    @BeforeSuite(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void startServer()
    throws Exception {
        entering("startServer", null);
        startNotificationServer();
        exiting("startServer");
    }
    
    /**
     * Stop the notification (jetty) server for getting notifications from the
     * server.
     */
    @AfterSuite(groups = {"ds_ds", "ds_ds_sec", "ff_ds", "ff_ds_sec"})
    public void stopServer()
    throws Exception {
        entering("stopServer", null);
        stopNotificationServer();
        exiting("stopServer");
    }
}
