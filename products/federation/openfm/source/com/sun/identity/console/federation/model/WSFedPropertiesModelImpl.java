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
 * $Id: WSFedPropertiesModelImpl.java,v 1.1 2007-07-26 22:12:32 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;
import com.sun.identity.wsfederation.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.wsfederation.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WSFedPropertiesModelImpl extends AMModelBase
    implements WSFedPropertiesModel {    
    private static Map GEN_DATA_MAP = new HashMap(2);
    private static Map SP_DATA_MAP = new HashMap(2);
    private static Map IDP_DATA_MAP = new HashMap(2);

    static {
        GEN_DATA_MAP.put(TF_NAME, Collections.EMPTY_SET);
        //TBD - will be completed after the api for settting wsfed 
        //attribute values are checked in
    }
    static {
        SP_DATA_MAP.put(TFSSO_NOTIFENDPT, Collections.EMPTY_SET);
        //TBD - will be completed after the api for settting wsfed 
        //attribute values are checked in
    }    
    static {
        IDP_DATA_MAP.put(TFSIGNCERT_ALIAS, Collections.EMPTY_SET);
        //TBD - will be completed after the api for settting wsfed 
        //attribute values are checked in
    }    
    
    /** Creates a new instance of WSFedPropertiesModelImpl */
    public WSFedPropertiesModelImpl(HttpServletRequest req,  Map map) {
        super(req, map);
    }
    
    public String getRealm(String name)
        throws AMConsoleException {
        // TBD -  will remove hard coded value               
        String realm = "/";                          
        return realm;
    }
    
    /**
     * Returns a map with service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the Federation Id otherwise known as the entity id.
     * @return attribute values of SP based on realm and fedid passed.
     * @throws AMConsoleException if unable to retreive the Service Provider 
     *     attrubutes based on the realm and fedid passed.
     */
    public Map getServiceProviderAttributes(String realm, String fedid)
        throws AMConsoleException {
        SPSSOConfigElement spconfig  = null;
        Map SPAttributes = null;
        try {
            spconfig = WSFederationMetaManager.getSPSSOConfig(realm,fedid);
            if (spconfig != null) {
                SPAttributes =  WSFederationMetaUtils.getAttributes(spconfig);
            }
        } catch (WSFederationMetaException e) {
            debug.warning
                ("WSFedPropertiesModelImpl.getServiceProviderAttributes", e);
            throw new AMConsoleException(e.getMessage());
        }
        return (SPAttributes != null) ? SPAttributes : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a map with identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the Federation Id otherwise known as the entity id.
     * @return attribute values of IDP based on realm and fedid passed.
     * @throws AMConsoleException if unable to retreive the Identity Provider 
     *     attrubutes based on the realm and fedid passed.
     */
    public Map getIdentityProviderAttributes(String realm, String fedid)
        throws AMConsoleException {
        IDPSSOConfigElement idpconfig  = null;
       
        Map IDPAttributes = null;
        try {
            idpconfig = WSFederationMetaManager.getIDPSSOConfig(realm,fedid);  
            if (idpconfig != null) {
                IDPAttributes =  WSFederationMetaUtils.getAttributes(idpconfig);
            }
        } catch (WSFederationMetaException e) {
            debug.warning
                ("WSFedPropertiesModelImpl.getIdentityProviderAttributes", e);
            throw new AMConsoleException(e.getMessage());
        }
        return (IDPAttributes != null) ? IDPAttributes : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns FederationElement Object for the realm and fedid passed.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the Federation Id otherwise known as the entity id.
     * @return FederationElement Object for the realm and fedid passed.
     * @throws AMConsoleException if unable to retrieve the FederationElement
     *     Object.
     */
    public FederationElement getEntityDesc(String realm, String fedid)
        throws AMConsoleException {
        FederationElement fedelm = null;
        try {    
         fedelm =  WSFederationMetaManager.getEntityDescriptor(realm, fedid);
         } catch (WSFederationMetaException e) {
             debug.warning("WSFedPropertiesModelImpl.getEntityDesc", e);
             throw new AMConsoleException(e.getMessage());
        }
        return fedelm;
    }
    
    /**
     * Returns TokenIssuerName for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return TokenIssuerName for the FederationElement passed.
     */
    public String getTokenName(FederationElement fedElem) {
        String tkname = null;
        tkname =  WSFederationMetaManager.getTokenIssuerName(fedElem);
        return tkname;
    }
    
    /**
     * Returns TokenIssuerEndPoint for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return TokenIssuerEndPoint for the FederationElement passed.
     */
    public String getTokenEndpoint(FederationElement fedElem) {
        String tkendpt = null;
        tkendpt =  WSFederationMetaManager.getTokenIssuerEndpoint(fedElem);
        return tkendpt;
    }
    
    /**
     * Set attribute values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setAttributeValues(String realm, String fedid, Map values)
        throws AMConsoleException {
        //TBD - will be completed after the api for settting wsfed 
        //attribute values are checked in
    }
    
     /**
     * Returns a map of wsfed general attributes.
     *
     * @return Map of wsfed general attributes.
     */
    public Map getGenDataMap() {
        return GEN_DATA_MAP;
    }
    
    /**
     * Returns a map of Wsfed Service Provider attributes.
     *
     * @return Map of Wsfed Service Provider attributes.
     */
    public Map getSPDataMap() {
        return SP_DATA_MAP;
    }
    
    /**
     * Returns a map of Wsfed Identity Provider attributes.
     *
     * @return Map of Wsfed Identity Provider attributes.
     */
    public Map getIDPDataMap() {
        return IDP_DATA_MAP;
    }
}
