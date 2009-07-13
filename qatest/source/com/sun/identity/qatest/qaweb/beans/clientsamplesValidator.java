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

import com.icesoft.faces.component.ext.HtmlSelectOneMenu;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.sun.identity.qatest.qaweb.common.QawebCommon;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * This class acts as a validator for clientsamples related properties
 */
public class clientsamplesValidator implements Validator {

    /**
     * This method validates war file existence if internal else access the url
     * if external
     */
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        String componentId = component.getId();        
        PanelTabSet moduleTab = (PanelTabSet) context.getViewRoot().
                findComponent("ModuleForm:modulePaneltabset");
        int selectedTab = moduleTab.getSelectedIndex();
        QawebCommon qawebCommon = new QawebCommon();
        if (componentId.equals("war_file")) {
            HtmlSelectOneMenu warfile_type = (HtmlSelectOneMenu) moduleTab.
                    findComponent("clientsamplesTab").
                    findComponent("clientsamplesGrid").
                    findComponent("warfile_type");
            if (warfile_type.getValue().equals("internal")) {
                String fileExistsError = qawebCommon.isFileExists(value.
                        toString());
                if (fileExistsError != null) {
                    FacesMessage msg = new FacesMessage(fileExistsError);
                    FacesContext.getCurrentInstance().addMessage(component.
                            getClientId(FacesContext.getCurrentInstance()),
                            msg);
                }               
            }else if(warfile_type.getValue().equals("external")){                
                String warFileURLError = qawebCommon.accessURL
                        (value.toString());
                if (warFileURLError != null) {
                    FacesMessage msg = new FacesMessage(warFileURLError);
                    FacesContext.getCurrentInstance().addMessage(component.
                            getClientId(FacesContext.getCurrentInstance()),
                            msg);
                }
            }
        }
    }
}
