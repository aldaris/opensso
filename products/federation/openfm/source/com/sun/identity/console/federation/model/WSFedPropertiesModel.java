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
 * $Id: WSFedPropertiesModel.java,v 1.1 2007-07-26 22:12:07 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface WSFedPropertiesModel extends AMModel {
    
     /************************************************************************
     * WSFED General attributes
     ************************************************************************/
    
    // attribute for name of entity
    String TF_NAME = "tfName";
   
    // attribute for token issuer name
    String TFTOKENISSUER_NAME = "tfTokenIssuerName";
    
    // attribute for token issuer end point
    String TFTOKENISSUER_ENDPT = "tfTokenIssuerEndpoint";
    
    // attribute for role of entity
    String TF_ENTROLE = "tfEntRole";
    
    // attribute for protocol of entity
    String TF_ENTPROTOCOL = "tfEntProtocol";
    
    // attribute for realm to which entity belongs
    String TF_REALM  = "tfEntRealm";
    
    // attribute for description of entity
    String TF_DESC = "tfEntDescription";
    
    // attribute for current tab
    String TAB_NOW ="";
    
     /************************************************************************
     * WSFED SP attributes
     ************************************************************************/
    
    // attribute for Single SignOut Notification Endpoint
    String TFSSO_NOTIFENDPT = "tfSPSingleSignOutNotificationEndpoint";
    
    // attribute for AutofedEnabled
    String TFSPAUTOFED_ENABLED = "tfSPAutofedEnabled";
    
    // attribute for ArtificatResponseSigned
    String TFARTI_SIGNED = "tfSPArtificatResponseSigned";
    
    // attribute for AutofedAttribute
    String TFSPAUTOFED_ATTR = "tfSPAutofedAttribute";
    
    // attribute for AssertionEffectiveTime
    String TFASSERTEFFECT_TIME = "tfSPAssertionEffectiveTime";
    
    // attribute for AccountMapper
    String TFSPACCT_MAPPER = "tfSPAccountMapper";
    
    // attribute for description of entity
    String TFSPATTR_MAPPER = "tfSPAttributeMapper";
    
    // attribute for AuthncontextMapper
    String TFSPAUTHCONT_MAPPER = "tfSPAuthncontextMapper";
    
    // attribute for AuthncontextClassrefMapping
    String TFAUTHCONTCLASS_REFMAPPING = "tfSPAuthncontextClassrefMapping";
    
    // attribute for AuthncontextComparisonType
    String TFAUTHCONT_COMPARTYPE = "tfSPAuthncontextComparisonType";
    
    // attribute for AttributeMap
    String TFSPATTR_MAP = "tfSPAttributeMap";
    
    // attribute for DefaultRelayState
    String TFRELAY_STATE = "tfSPDefaultRelayState";
    
    // attribute for assertionTimeSkew
    String TFASSERT_TIMESKEW = "tfSPassertionTimeSkew";
  
    /************************************************************************
    * WSFED IDP attributes
    ************************************************************************/
    
    // attribute for Signing Certificate Alias
    String TFSIGNCERT_ALIAS = "tfSigncertAlias";
    
    // attribute for Claim Types Offered
    String TFCLAIM_TYPES = "tfIDPClaimTypesOffered";
    
    // attribute for AutofedEnabled
    String TFAUTOFED_ENABLED = "tfIDPAutofedEnabled";
    
    // attribute for AutofedAttribute
    String TFIDPAUTOFED_ATTR = "tfIDPAutofedAttribute";
    
    // attribute for AssertionEffectiveTime
    String TFIDPASSERT_TIME = "tfIDPAssertionEffectiveTime";
    
    // attribute for AuthncontextMapper
    String TFIDPAUTH_CONTMAPPER = "tfIdpAuthncontextMapper";
    
    // attribute for AccountMapper
    String TFIDPACCT_MAPPER = "tfIdpAccountMapper";
    
    // attribute for AttributeMapper
    String TFIDPATTR_MAPPER = "tfIdpAttributeMapper";
    
    // attribute for AttributeMap
    String TFIDPATTR_MAP = "tfIDPAttributeMap";
   
    /**
     * Returns realm that has name matching.
     *
     * @param entity name which is the entity id.
     * @return realm based on entity name.
     * @throws AMConsoleException if unable to retreive the Realm to 
     * which the entity belongs.
     */
    String getRealm(String name) throws AMConsoleException;
    
    /**
     * Returns a map with service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @return attribute values of SP based on realm and fedid passed.
     * @throws AMConsoleException if unable to retreive the Service Provider 
     *     attrubutes based on the realm and fedid passed.
     */
    Map getServiceProviderAttributes(String realm, String fedid) 
        throws AMConsoleException;
    
    /**
     * Returns a map with identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @return attribute values of IDP based on realm and fedid passed.
     * @throws AMConsoleException if unable to retreive the Identity Provider 
     *     attrubutes based on the realm and fedid passed.
     */
    Map getIdentityProviderAttributes(String realm, String fedid) 
        throws AMConsoleException;
    
    /**
     * Returns FederationElement Object for the realm and fedid passed.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @return FederationElement Object for the realm and fedid passed.
     * @throws AMConsoleException if unable to retrieve the FederationElement
     *     Object.
     */
    FederationElement getEntityDesc(String realm, String fedid)
        throws AMConsoleException;
    
    /**
     * Returns TokenIssuerName for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return TokenIssuerName for the FederationElement passed.
     */
    String getTokenName(FederationElement fedElem);
    
    /**
     * Returns TokenIssuerEndPoint for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return TokenIssuerEndPoint for the FederationElement passed.
     */
    String getTokenEndpoint(FederationElement fedElem);
    
    /**
     * Saves the attribute values for the entity passed.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param Map containing attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    void setAttributeValues(String realm, String fedid, Map values)
        throws AMConsoleException;
    
    /**
     * Returns a map of wsfed general attribute values.
     *
     * @return Map of wsfed general attribute values.
     */
    Map getGenDataMap();
    
    /**
     * Returns a map of Wsfed Service Provider attribute values.
     *
     * @return Map of Wsfed Service Provider attribute values.
     */
    Map getSPDataMap();
    
    /**
     * Returns a map of Wsfed Identity Provider attribute values.
     *
     * @return Map of Wsfed Identity Provider attribute values.
     */
    Map getIDPDataMap();
}
