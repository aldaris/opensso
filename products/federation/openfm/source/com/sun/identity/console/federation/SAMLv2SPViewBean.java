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
 * $Id: SAMLv2SPViewBean.java,v 1.2 2007-10-16 20:14:34 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.federation.model.SAMLv2Model;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class SAMLv2SPViewBean extends SAMLv2Base {
    public static final String DEFAULT_DISPLAY_URL =
            "/console/federation/SAMLv2SP.jsp";
    protected static final String PROPERTIES =
            "propertyAttributes";
    
    public SAMLv2SPViewBean() {
        super("SAMLv2SP");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }
    
    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        AMPropertySheet ps = (AMPropertySheet) getChild(PROPERTIES);
        ps.init();
        SAMLv2Model model = (SAMLv2Model)getModel();
        ps.setAttributeValues(getStandardValues(), model);
        ps.setAttributeValues(getExtendedValues(), model);
        super.beginDisplay(event);
    }
    
    protected void createPropertyModel() {
        retrieveCommonProperties();
        if (isHosted()) {
            psModel = new AMPropertySheetModel(
                    getClass().getClassLoader().getResourceAsStream(
                    "com/sun/identity/console/propertySAMLv2SPHosted.xml"));
        } else {
            psModel = new AMPropertySheetModel(
                    getClass().getClassLoader().getResourceAsStream(
                    "com/sun/identity/console/propertySAMLv2SPRemote.xml"));
        }
    }
    
    public void handleButton1Request(RequestInvocationEvent event)
    throws ModelControlException {
        try {
            
            SAMLv2Model model = (SAMLv2Model)getModel();
            AMPropertySheet ps =
                    (AMPropertySheet)getChild(PROPERTY_ATTRIBUTES);
            
            //retrieve the standard metadata values from the property sheet
            Map spStdValues = ps.getAttributeValues(
                    model.getStandardServiceProviderAttributes(
                    realm, entityName), false, model);
            
            //save the standard metadata values for the Idp
            model.setSPStdAttributeValues(realm, entityName, spStdValues);
            
            //retrieve the extended metadata values from the property sheet
            Map spExtValues = ps.getAttributeValues(
                    getExtendedValues(), false, model);
            
            //save the extended metadata values for the Idp
            model.setSPExtAttributeValues(realm, entityName, spExtValues,
                    location);
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                    e.getMessage());
        }
        forwardTo();
    }
    
    private Map getStandardValues() {
        Map map = new HashMap();
        try {
            
            //gets standard metadata values
            SAMLv2Model model = (SAMLv2Model)getModel();
            map = model.getStandardServiceProviderAttributes(
                    realm, entityName);
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                    e.getMessage() );
        }
        return map;
    }
    
    private Map getExtendedValues() {
        Map extendedValues = new HashMap();
        try {
            
            //gets extended metadata values
            SAMLv2Model model = (SAMLv2Model)getModel();
            Map attrs = model.getExtendedServiceProviderAttributes(
                    realm, entityName);
            Set entries = attrs.entrySet();
            Iterator iterator = entries.iterator();
            
            //the list of values is converted to a set
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                extendedValues.put(
                        (String)entry.getKey(),
                        convertListToSet((List)entry.getValue()) );
            }
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                    e.getMessage() );
        }
        return extendedValues;
    }
}