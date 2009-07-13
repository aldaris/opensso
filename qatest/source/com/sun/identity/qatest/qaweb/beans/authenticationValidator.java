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
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.sun.identity.qatest.qaweb.common.QawebCommon;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class acts as a vlidator for authentication related properties
 */
public class authenticationValidator implements Validator {
    
    /**
     * This method validates LDAP details for auth module.
     */
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String componentId = component.getId();
        PanelTabSet moduleTab = (PanelTabSet) context.getViewRoot().
                findComponent("ModuleForm:modulePaneltabset");
        int selectedTab = moduleTab.getSelectedIndex();
        QawebCommon qawebCommon = new QawebCommon();
        if (componentId.equals("ldap_iplanet-am-auth-ldap-server")) {
            HtmlInputText baseDN = (HtmlInputText) moduleTab.findComponent
                    ("authenticationTab").
                    findComponent("authenticationGrid").
                    findComponent("ldap_iplanet-am-auth-ldap-base-dn");
            HtmlInputText bindDN = (HtmlInputText) moduleTab.findComponent
                    ("authenticationTab").
                    findComponent("authenticationGrid").
                    findComponent("ldap_iplanet-am-auth-ldap-bind-dn");
            HtmlInputText bindPwd = (HtmlInputText) moduleTab.findComponent
                    ("authenticationTab").
                    findComponent("authenticationGrid").
                    findComponent("ldap_iplanet-am-auth-ldap-bind-passwd");
            int colonIndex = value.toString().indexOf(":");
            String hostName = value.toString().substring(0, colonIndex);
            String port = value.toString().substring(colonIndex + 1);
            String error = null;
            int ldapPort = 0;
            try {
                ldapPort = Integer.parseInt(port);
            } catch (NumberFormatException e) {
                error = "Please enter an integer for port";
                FacesMessage msg = new FacesMessage(error);
                FacesContext.getCurrentInstance().addMessage(component.
                        getClientId(FacesContext.getCurrentInstance()),msg);
                e.printStackTrace();
            }
            error = qawebCommon.validateSMHost((String) hostName,
                    ldapPort, (String) bindDN.getValue(),
                    (String) bindPwd.getValue(),
                    (String) baseDN.getValue());
            if (error != null) {
                FacesMessage msg = new FacesMessage(error);
                FacesContext.getCurrentInstance().addMessage(component.
                        getClientId(FacesContext.getCurrentInstance()), msg);
            }
        }
    }
}
