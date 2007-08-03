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
 * "Portions Copyrighted [year] [name of copyright owner]
 *
 * $Id: WSFedSPViewBean.java,v 1.2 2007-08-03 22:29:03 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import com.sun.identity.console.federation.model.WSFedPropertiesModel;
import com.sun.web.ui.view.alert.CCAlert;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

public class WSFedSPViewBean extends WSFedGeneralBase {
    public static final String DEFAULT_DISPLAY_URL =
            "/console/federation/WSFedSP.jsp";
    
    public WSFedSPViewBean() {
        super("WSFedSP");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }
    
    public void beginDisplay(DisplayEvent event)
        throws ModelControlException 
    {
        super.beginDisplay(event);
        
        try {                    
            WSFedPropertiesModel model = (WSFedPropertiesModel)getModel();
            Map attributes = model.getServiceProviderAttributes(
                realm, entityName);
            Iterator it = attributes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                Object key = entry.getKey();
                List vallist = (List)entry.getValue();
                if (key.equals("attributeMap")) {
                    setDisplayFieldValue(
                        WSFedPropertiesModel.TFSPATTR_MAP, vallist);
                }
                for (Iterator itlist = vallist.iterator(); itlist.hasNext(); ) {
                    Object element = itlist.next();
                    if (key.equals("spAuthncontextComparisonType")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFAUTHCONT_COMPARTYPE,
                            element);
                    } else if (key.equals("assertionTimeSkew")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFASSERT_TIMESKEW, element);
                    } else if (key.equals("wantArtifactResponseSigned")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFARTI_SIGNED, element);
                    } else if (key.equals("spAccountMapper")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPACCT_MAPPER, element);
                    } else if (key.equals("defaultRelayState")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFRELAY_STATE, element);
                    } else if (key.equals("spAuthncontextClassrefMapping")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFAUTHCONTCLASS_REFMAPPING,
                            element);
                    } else if (key.equals("spAuthncontextMapper")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPAUTHCONT_MAPPER,
                            element);
                    } else if (key.equals("autofedEnabled")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPAUTOFED_ENABLED,
                            element);
                    } else if (key.equals("spAttributeMapper")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPATTR_MAPPER, element);
                    } else if (key.equals("autofedAttribute")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPAUTOFED_ATTR, element);
                    } else if (key.equals("assertionEffectiveTime")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFASSERTEFFECT_TIME, element);
                    }
                }
            }
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage() );
        }
    }
    
    protected void createPropertyModel() {
        psModel = new AMPropertySheetModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/propertyWSFedSPView.xml"));
        psModel.clear();
    }
    
    public void handleButton1Request(RequestInvocationEvent event)
        throws ModelControlException 
    {
        retrieveCommonProperties();
        try {    
            WSFedPropertiesModel model = (WSFedPropertiesModel)getModel();
            AMPropertySheet ps = 
                (AMPropertySheet)getChild(PROPERTY_ATTRIBUTES); 
            Map values =
                ps.getAttributeValues(model.getGenDataMap(), false, model);
            model.setAttributeValues(realm, entityName, values);
            setInlineAlertMessage(CCAlert.TYPE_INFO, "message.information",
                "wsfed.sp.property.updated");
        } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                e.getMessage());
        }
        forwardTo();
    }

}
