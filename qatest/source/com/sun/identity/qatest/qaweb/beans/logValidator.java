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
 * This class acts as a validator for log related properties
 */
public class logValidator implements Validator {

    /**
     * This method validates if the database is accessible for logging
     */
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String componentId = component.getId();
        PanelTabSet moduleTab = (PanelTabSet) context.getViewRoot().
                findComponent("ModuleForm:modulePaneltabset");
        int selectedTab = moduleTab.getSelectedIndex();
        HtmlInputText databaseLocation = (HtmlInputText) moduleTab.
                findComponent("logTab").
                findComponent("logGrid").
                findComponent("iplanet-am-logging-location");
        HtmlInputText dbUserName = (HtmlInputText) moduleTab.
                findComponent("logTab").
                findComponent("logGrid").
                findComponent("iplanet-am-logging-db-user");
        HtmlInputText dbPwd = (HtmlInputText) moduleTab.
                findComponent("logTab").
                findComponent("logGrid").
                findComponent("iplanet-am-logging-db-password");
        HtmlInputText dbDriver = (HtmlInputText) moduleTab.
                findComponent("logTab").
                findComponent("logGrid").
                findComponent("iplanet-am-logging-db-driver");
        String dbError = QawebCommon.getConnection(dbUserName.getValue().
                toString(), dbPwd.getValue().toString(), dbDriver.getValue().
                toString(), databaseLocation.getValue().
                toString());
        if (dbError != null) {
            FacesMessage msg = new FacesMessage(dbError);
            FacesContext.getCurrentInstance().addMessage(component.
                    getClientId(FacesContext.getCurrentInstance()),
                    msg);
        }

    }
}
