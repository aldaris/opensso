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
 * $Id:
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.qaweb.beans;

import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.sun.identity.qatest.qaweb.common.QawebCommon;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class is validator. It validates the values of LDAP connection for
 * config store and Um store given by user in server.jsp.
 */
public class LDAPValidationActionListener implements Validator {

    String storedAmadminUserId = null;
    String storedAmadminPassword = null;
    String storedNamingUrl = null;

    /**
     * This method is called whenever server Form gets submitted
     * @param context Current FacesContext
     * @param component The UI COmponent which is submitted or has change in
     * value
     * @param value Value of the UI COmponent
     */
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String componentId = component.getId().toString();        
        String serverConfigError = null;
        String ldapError = null;
        int configstorePort = 0;
        int ldapPort = 0;
        PanelTabSet serverTab = (PanelTabSet) context.getViewRoot().
                findComponent("serverForm:serverPanelTabSet");
        int selectedTab = serverTab.getSelectedIndex();
        if (componentId.equals("directory_server" + selectedTab) ||
                componentId.equals("directory_port" + selectedTab) ||
                componentId.equals("config_root_suffix" + selectedTab) ||
                componentId.equals("ds_dirmgrdn" + selectedTab) ||
                componentId.equals("ds_dirmgrpasswd" + selectedTab)) {            
            HtmlInputText dirServer = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("configGrid" + selectedTab).
                    findComponent("directory_server" + selectedTab);
            HtmlInputText dirPort = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("configGrid" + selectedTab).
                    findComponent("directory_port" + selectedTab);
            HtmlInputText rootSuffix = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("configGrid" + selectedTab).
                    findComponent("config_root_suffix" + selectedTab);
            HtmlInputText drUserID = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("configGrid" + selectedTab).
                    findComponent("ds_dirmgrdn" + selectedTab);
            HtmlInputText drPwd = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("configGrid" + selectedTab).
                    findComponent("ds_dirmgrpasswd" + selectedTab);
            try {
                configstorePort = Integer.parseInt(dirPort.getValue().
                        toString());
            } catch (NumberFormatException e) {                
                serverConfigError = "Please enter an integer for port";
                FacesMessage msg = new FacesMessage(serverConfigError);
                FacesContext.getCurrentInstance().addMessage
                        (dirPort.getClientId(FacesContext.getCurrentInstance()),
                        msg);
                e.printStackTrace();
            }
            HtmlInputText namingUrl = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("serverGrid" + selectedTab).
                    findComponent("com_iplanet_am_naming_url" + selectedTab);
            HtmlInputText amadminUsername = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("serverGrid" + selectedTab).
                    findComponent("amadmin_username" + selectedTab);
            HtmlInputText amadmin_password = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("serverGrid" + selectedTab).
                    findComponent("amadmin_password" + selectedTab);
            HtmlSelectOneMenu datastoreType = (HtmlSelectOneMenu) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("serverGrid" + selectedTab).
                    findComponent("datastore" + selectedTab);
            QawebCommon qawebCommon = new QawebCommon();
            try {
                if (!amadminUsername.getValue().toString().
                        equals(storedAmadminUserId) ||
                        !amadmin_password.getValue().toString().
                        equals(storedAmadminPassword) ||
                        !namingUrl.getValue().toString().
                        equals(storedNamingUrl)) {
                    serverConfigError = qawebCommon.configureProduct(namingUrl.
                            getValue().toString(),
                            amadminUsername.getValue().toString(),
                            amadmin_password.getValue().toString());
                    storedAmadminUserId = amadminUsername.getValue().toString();
                    storedAmadminPassword = amadmin_password.getValue().
                            toString();
                    storedNamingUrl = namingUrl.getValue().toString();
                    if (serverConfigError != null) {
                        FacesMessage msg = new FacesMessage(serverConfigError +
                                " for Config Store.");
                        FacesContext.getCurrentInstance().addMessage
                                (amadmin_password.getClientId
                                (FacesContext.getCurrentInstance()), msg);
                    }
                }
            } catch (Exception e) {                
                e.printStackTrace();
            }
            if (serverConfigError == null && datastoreType.getValue().
                    equals("embedded")) {
                ldapError = qawebCommon.validateSMHost((String) dirServer.
                        getValue(),configstorePort, (String) drUserID.
                        getValue(),(String) drPwd.getValue(),
                        (String) rootSuffix.getValue());
            } else if (serverConfigError == null && datastoreType.getValue().
                    equals("dirServer")) {
                ldapError = qawebCommon.validateSMHost((String) dirServer.
                        getValue(),configstorePort, (String) drUserID.
                        getValue(),(String) drPwd.getValue(),
                        (String) rootSuffix.getValue());
            } else if (serverConfigError.equals("Product not configured")) {
                try {
                    Boolean canuseasPort = qawebCommon.canUseAsPort(
                            dirServer.getValue().toString(), configstorePort);
                    if (canuseasPort) {
                        serverConfigError = null;
                    } else {
                        serverConfigError = "Config Store port aready in use.";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }            
            if (ldapError != null) {
                FacesMessage msg = new FacesMessage(ldapError +
                        " for Config Store.");
                FacesContext.getCurrentInstance().addMessage(drPwd.
                        getClientId(FacesContext.getCurrentInstance()), msg);
            }
        } else if (componentId.equals("datastore-root-suffix" + selectedTab) ||
                componentId.equals("sun-idrepo-ldapv3-config-ldap-server" +
                selectedTab) ||
                componentId.equals("sun-idrepo-ldapv3-config-ldap-port" +
                selectedTab) ||
                componentId.equals("datastore-adminid" + selectedTab) ||
                componentId.equals("datastore-adminpw" + selectedTab) ||
                componentId.equals("sun-idrepo-ldapv3-config-authid" +
                selectedTab) ||
                componentId.equals("sun-idrepo-ldapv3-config-authpw" +
                selectedTab)) {           
            String ldapErrorforUm = null;
            HtmlInputText umServer = (HtmlInputText) serverTab.findComponent
                    ("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("sun-idrepo-ldapv3-config-ldap-server" +
                    selectedTab);
            HtmlInputText umPort = (HtmlInputText) serverTab.findComponent
                    ("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("sun-idrepo-ldapv3-config-ldap-port" +
                    selectedTab);
            HtmlInputText umRootSuffix = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("datastore-root-suffix" + selectedTab);
            HtmlInputText umAdminId = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("datastore-adminid" + selectedTab);
            HtmlInputText umAdminpw = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("datastore-adminpw" + selectedTab);
            HtmlInputText umAuthId = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("sun-idrepo-ldapv3-config-authid" +
                    selectedTab);
            HtmlInputText umAuthpw = (HtmlInputText) serverTab.
                    findComponent("server" + selectedTab).
                    findComponent("umGrid" + selectedTab).
                    findComponent("sun-idrepo-ldapv3-config-authpw" +
                    selectedTab);
            try {
                ldapPort = Integer.parseInt(umPort.getValue().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ldapErrorforUm = "Please enter an integer for port.";
            }
            QawebCommon datastoreQawebCommon = new QawebCommon();
            ldapErrorforUm = datastoreQawebCommon.validateSMHost
                    ((String) umServer.getValue(), ldapPort,
                    (String) umAdminId.getValue(),
                    (String) umAdminpw.getValue(),
                    (String) umRootSuffix.getValue());
            if (umAuthId.getValue().toString().equals
                    ("cn=dsameuser,ou=DSAME Users,dc=opensso,dc=java,dc=net")) {
                ldapErrorforUm = datastoreQawebCommon.validateSMHost
                        ((String) umServer.getValue(),
                        ldapPort, (String) umAuthId.getValue(),
                        (String) umAuthpw.getValue(),
                        (String) umRootSuffix.getValue());
            }
            if (ldapErrorforUm != null) {
                FacesMessage msg = new FacesMessage(ldapErrorforUm +
                        " for UmDataStore.");
                FacesContext.getCurrentInstance().
                        addMessage(umAdminId.getClientId
                        (FacesContext.getCurrentInstance()), msg);
            }
        } 
    }
}
