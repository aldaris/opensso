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
 * $Id: WSFedSPViewBean.java,v 1.1 2007-07-26 22:11:30 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.web.ui.view.alert.CCAlert;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.web.ui.model.CCAddRemoveModel;
import com.sun.web.ui.view.addremove.CCAddRemove;
import com.sun.identity.console.federation.model.WSFedPropertiesModel;
import com.sun.identity.wsfederation.jaxb.entityconfig.SPSSOConfigElement;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

public class WSFedSPViewBean extends WSFedGeneralBase {
    public static final String DEFAULT_DISPLAY_URL =
            "/console/federation/WSFedSP.jsp";
    public static String TAB_TOSAVE ="tabtosave";
    private static final String PROPERTY_ATTRIBUTES = "propertyAttributes";
    
    public WSFedSPViewBean() {
        super("WSFedSP");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }
    
    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        
        WSFedPropertiesModel model = (WSFedPropertiesModel)getModel();
        String ent_name = (String)getPageSessionAttribute
            ("WSFedPropertiesModel.TF_NAME");
        super.beginDisplay(event);
        setPageTitle("wsfedsp.attribute.page.title");
        try{
            String realm = model.getRealm(ent_name);
            
            //TBD-hardcoded value will be removed later on when console allows
            //import of wsfed entity
            String fedid = "http://priya.red.iplanet.com";
            Map attributes = model.getServiceProviderAttributes(realm,fedid);
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
                    }else if (key.equals("assertionTimeSkew")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFASSERT_TIMESKEW, element);
                    } else if (key.equals("wantArtifactResponseSigned")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFARTI_SIGNED, element);
                    } else if (key.equals("spAccountMapper")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPACCT_MAPPER, element);
                    }else if (key.equals("defaultRelayState")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFRELAY_STATE, element);
                    } else if (key.equals("spAuthncontextClassrefMapping")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFAUTHCONTCLASS_REFMAPPING,
                                element);
                    }else if (key.equals("spAuthncontextMapper")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPAUTHCONT_MAPPER,
                                element);
                    }else if (key.equals("autofedEnabled")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPAUTOFED_ENABLED,
                                element);
                    }else if (key.equals("spAttributeMapper")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPATTR_MAPPER, element);
                    }else if (key.equals("autofedAttribute")) {
                        setDisplayFieldValue(
                            WSFedPropertiesModel.TFSPAUTOFED_ATTR, element);
                    }else if (key.equals("assertionEffectiveTime")) {
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
    
    protected void createPropertyModel(String name) {
        psModel = new AMPropertySheetModel(
            getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/propertyWSFedSPView.xml"));
        psModel.clear();
    }
    
   public void handleButton1Request(RequestInvocationEvent event)
    throws ModelControlException {
         try {
             WSFedPropertiesModel model = (WSFedPropertiesModel)getModel();
            //String ent_name = (String)getPageSessionAttribute
            //   ("WSFedPropertiesModel.TF_NAME");
            //TBD-hardcoded value will be removed later on when console allows
            //to import wsfed entity
            String fedId = "http://bbsunil.red.iplanet.com";
            String realm = model.getRealm(fedId);
            AMPropertySheet ps = 
                (AMPropertySheet)getChild(PROPERTY_ATTRIBUTES); 
            Map values =
                    ps.getAttributeValues(model.getGenDataMap(), false,model);
            TAB_TOSAVE = "wsfed.sp.property.updated";
            model.setAttributeValues(realm, fedId, values);
            setInlineAlertMessage(CCAlert.TYPE_INFO, "message.information",
                    TAB_TOSAVE);
         } catch (AMConsoleException e) {
            setInlineAlertMessage(CCAlert.TYPE_ERROR, "message.error",
                    e.getMessage());
         }
         forwardTo();
    }
}
