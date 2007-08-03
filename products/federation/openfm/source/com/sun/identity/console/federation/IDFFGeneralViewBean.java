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
 * $Id: IDFFGeneralViewBean.java,v 1.2 2007-08-03 22:29:02 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.federation.model.IDFFEntityModel;
import com.sun.identity.console.federation.model.IDFFEntityModelImpl;
import com.sun.web.ui.view.alert.CCAlert;
import java.util.Map;

public class IDFFGeneralViewBean
    extends IDFFViewBeanBase 
{
    public static final String DEFAULT_DISPLAY_URL =
        "/console/federation/IDFFGeneral.jsp";
    
    public IDFFGeneralViewBean() {
        super("IDFFGeneral");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }
    
    public void beginDisplay(DisplayEvent event)
        throws ModelControlException 
    {
        super.beginDisplay(event);        
  
        psModel.setValue(TF_NAME, entityName);
        psModel.setValue(TXT_TYPE, "idff.entityDescriptor.type.provider.label");
        populateValue(entityName);
    }
    
    private void populateValue(String name) {
        IDFFEntityModel model = (IDFFEntityModel)getModel();
        try {
            Map values = model.getCommonAttributeValues(name);
            AMPropertySheet ps = (AMPropertySheet)getChild(PROPERTY_ATTRIBUTES);
            ps.setAttributeValues(values, model);
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage());
        }        
    }
    
    protected void createPropertyModel() {
        psModel = new AMPropertySheetModel(
            getClass().getClassLoader().getResourceAsStream(
            "com/sun/identity/console/propertyIDFFGeneral.xml"));
        psModel.clear();
    }
    
    /**
     * Handles save
     *
     * @param event Request invocation event
     */
    public void handleButton1Request(RequestInvocationEvent event)
       throws ModelControlException 
    {        
        retrieveCommonProperties();
        IDFFEntityModel model = (IDFFEntityModel)getModel();
        
        try {
            Map orig = model.getCommonAttributeValues(entityName);
            AMPropertySheet ps = (AMPropertySheet)getChild(PROPERTY_ATTRIBUTES);
            Map values = ps.getAttributeValues(orig, false, model);
            model.modifyEntityProfile(entityName, values);
            setInlineAlertMessage(CCAlert.TYPE_INFO,
                "message.information",
                "idff.entityDescriptor.provider.general.updated");
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage());          
        }
        forwardTo();
    }             
}

