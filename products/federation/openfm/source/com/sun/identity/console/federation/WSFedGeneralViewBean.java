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
 * $Id: WSFedGeneralViewBean.java,v 1.1 2007-07-26 22:10:48 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.console.federation;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.identity.console.base.AMPropertySheet;
import com.sun.identity.console.federation.model.WSFedPropertiesModel;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.identity.console.base.model.AMPropertySheetModel;
import java.util.Map;

public class WSFedGeneralViewBean
    extends WSFedGeneralBase {
    public static final String DEFAULT_DISPLAY_URL =
        "/console/federation/WSFedGeneral.jsp";
    public static String TAB_TOSAVE ="tabtosave";
    private static final String PROPERTY_ATTRIBUTES = "propertyAttributes";
    
    public WSFedGeneralViewBean() {
        super("WSFedGeneral");
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
    }
    
    public void beginDisplay(DisplayEvent event)
        throws ModelControlException {
            super.beginDisplay(event);
            WSFedPropertiesModel model = (WSFedPropertiesModel)getModel();
            String ent_name = (String)getPageSessionAttribute
                ("WSFedPropertiesModel.TF_NAME");
            setDisplayFieldValue(WSFedPropertiesModel.TF_NAME, ent_name);
            String ent_role = (String)getPageSessionAttribute
                ("WSFedPropertiesModel.TF_ENTROLE");
            setDisplayFieldValue(WSFedPropertiesModel.TF_ENTROLE, ent_role);
            String ent_protocol = (String)getPageSessionAttribute
                ("WSFedPropertiesModel.TF_ENTPROTOCOL");
            setDisplayFieldValue(WSFedPropertiesModel.TF_ENTPROTOCOL, 
                ent_protocol);
            
            //TBD-hardcoded value will be removed later on when console allows
            //to import wsfed entity
            String ent_new = "http://amy.red.iplanet.com";
            try {
                String realm = model.getRealm(ent_name);
                setDisplayFieldValue(WSFedPropertiesModel.TF_REALM, realm);
                FederationElement fed_elem =
                    model.getEntityDesc(realm, ent_new);
                String tok_issuer_name = model.getTokenName(fed_elem);
                setDisplayFieldValue(WSFedPropertiesModel.TFTOKENISSUER_NAME,
                    tok_issuer_name);
                String tok_issuer_endpt = model.getTokenEndpoint(fed_elem);
                setDisplayFieldValue(WSFedPropertiesModel.TFTOKENISSUER_ENDPT,
                    tok_issuer_endpt);            
            } catch (AMConsoleException e) {
                debug.error("WSFedGeneralViewBean.beginDispaly", e);
            }
            setPageTitle("wsfed.attribute.page.title");
    }
    
    protected void createPropertyModel(String name) {
        psModel = new AMPropertySheetModel(
                getClass().getClassLoader().getResourceAsStream(
                "com/sun/identity/console/propertyWSFedGeneralView.xml"));
        psModel.clear();
    }
    public void handleButton1Request(RequestInvocationEvent event)
    throws ModelControlException {
        WSFedPropertiesModel model = (WSFedPropertiesModel)getModel();
        try {
            //String ent_name = (String)getPageSessionAttribute
            //   ("WSFedPropertiesModel.TF_NAME");
            //TBD-hardcoded value will be removed later on when console allows
            //to import wsfed entity
            String fedId = "http://bbsunil.red.iplanet.com";
            String realm = model.getRealm(fedId);
            Map values = null;
            AMPropertySheet ps = 
                (AMPropertySheet)getChild(PROPERTY_ATTRIBUTES);            
            values =
                    ps.getAttributeValues(model.getGenDataMap(), false,model);
            TAB_TOSAVE = "wsfed.general.property.updated";
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
