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
 * $Id: WSFedPropertiesModel.java,v 1.2 2007-08-14 21:56:02 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModel;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import java.util.Map;
import java.util.List;
import java.util.Set;

public interface WSFedPropertiesModel extends AMModel {
    
    /************************************************************************
     * WSFED General attributes
     ************************************************************************/
    
    // attribute for name of entity
    String TF_NAME = "tfName";
    
    // attribute for display name of entity
    String TF_DISPNAME ="tfDispName";
    
    // attribute for token issuer name
    String TFTOKENISSUER_NAME = "TokenIssuerName";
    
    // attribute for token issuer end point
    String TFTOKENISSUER_ENDPT = "TokenIssuerEndpoint";
    
    // attribute for role of entity
    String TF_ENTROLE = "tfEntRole";
    
    // attribute for protocol of entity
    String TF_ENTPROTOCOL = "tfEntProtocol";
    
    // attribute for realm to which entity belongs
    String TF_REALM  = "tfEntRealm";
    
    // attribute for description of entity
    String TF_DESC = "tfEntDescription";
    
    /************************************************************************
     * WSFED SP attributes
     ************************************************************************/
    
    // attribute for Single SignOut Notification Endpoint
    String TFSSO_NOTIFENDPT = "SingleSignOutNotificationEndpoint";
    
    // attribute for AutofedEnabled
    String TFSPAUTOFED_ENABLED = "autofedEnabled";
    
    // attribute for ArtificatResponseSigned
    String TFARTI_SIGNED = "wantArtifactResponseSigned";
    
    // attribute for AutofedAttribute
    String TFSPAUTOFED_ATTR = "autofedAttribute";
    
    // attribute for AssertionEffectiveTime
    String TFASSERTEFFECT_TIME = "assertionEffectiveTime";
    
    // attribute for AccountMapper
    String TFSPACCT_MAPPER = "spAccountMapper";
    
    // attribute for description of entity
    String TFSPATTR_MAPPER = "spAttributeMapper";
    
    // attribute for AuthncontextMapper
    String TFSPAUTHCONT_MAPPER = "spAuthncontextMapper";
    
    // attribute for AuthncontextClassrefMapping
    String TFAUTHCONTCLASS_REFMAPPING = "spAuthncontextClassrefMapping";
    
    // attribute for AuthncontextComparisonType
    String TFAUTHCONT_COMPARTYPE = "spAuthncontextComparisonType";
    
    // attribute for AttributeMap
    String TFSPATTR_MAP = "attributeMap";
    
    // attribute for DefaultRelayState
    String TFRELAY_STATE = "defaultRelayState";
    
    // attribute for assertionTimeSkew
    String TFASSERT_TIMESKEW = "assertionTimeSkew";
    
    /************************************************************************
     * WSFED IDP attributes
     ************************************************************************/
    
    // attribute for Signing Certificate Alias
    String TFSIGNCERT_ALIAS = "signingCertAlias";
    
    // attribute for Claim Types Offered
    String TFCLAIM_TYPES = "UriNamedClaimTypesOffered";
    
    // attribute for AutofedEnabled
    String TFAUTOFED_ENABLED = "autofedEnabled";
    
    // attribute for AutofedAttribute
    String TFIDPAUTOFED_ATTR = "autofedAttribute";
    
    // attribute for AssertionEffectiveTime
    String TFIDPASSERT_TIME = "assertionEffectiveTime";
    
    // attribute for AuthncontextMapper
    String TFIDPAUTH_CONTMAPPER = "idpAuthncontextMapper";
    
    // attribute for AccountMapper
    String TFIDPACCT_MAPPER = "idpAccountMapper";
    
    // attribute for AttributeMapper
    String TFIDPATTR_MAPPER = "idpAttributeMapper";
    
    // attribute for AttributeMap
    String TFIDPATTR_MAP = "attributeMap";
    
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
     * @param fedElem the FederationElement Object.
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
     * Returns UriNamedClaimTypesOffered for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return UriNamedClaimTypesOffered for the FederationElement passed.
     */
    List getClaimType(FederationElement fedElem);
    
    /**
     * Returns Signing Certificate for the FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return Signing Certificate for the FederationElement passed.
     */
    byte[] getSignCert(FederationElement fedElem);
    
    /**
     * Returns SingleSignoutNotificationEndPoint for FederationElement passed.
     *
     * @param fedElem is the FederationElement Object.
     * @return SingleSignoutNotificationEndPoint for FederationElement passed.
     */
    String getSingleSignoutNotificationEndPoint(FederationElement fedElem);
    
    /**
     * Saves the extended metadata attribute values for the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param Map spExtvalues contain the extended attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    void setSPExtAttributeValues(String realm, String fedId, Map spExtvalues)
    throws AMConsoleException;
    
    /**
     * Saves the standard attribute values for the SP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param Map spStdValues contain standard attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    void setSPSTDAttributeValues(String realm, String fedId, Map spStdValues)
    throws AMConsoleException;
    
    /**
     * Saves the extended metadata attribute values for the IDP.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param Map idpExtValues contain attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    void setIDPExtAttributeValues(String realm, String fedId, Map idpExtValues)
    throws AMConsoleException;
    
    /**
     * Saves the standard attribute values from the General page.
     *
     * @param realm to which the entity belongs.
     * @param fedid is the entity id.
     * @param Map idpStdValues contain standard attribute values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    void setGenAttributeValues(String realm, String fedId, Map idpStdValues)
    throws AMConsoleException;
    
    /**
     * Returns a map of wsfed general attribute values.
     *
     * @return Map of wsfed general attribute values.
     */
    Map getGenDataMap();
    
    /**
     * Returns a map of Wsfed Extended Service Provider attribute values.
     *
     * @return Map of Wsfed Extended Service Provider attribute values.
     */
    Map getSPEXDataMap();
    
    /**
     * Returns a map of Wsfed Standard Service Provider attribute values.
     *
     * @return Map of Wsfed Standard Service Provider attribute values.
     */
    Map getSPSTDDataMap();
    
    /**
     * Returns a map of Wsfed Extended Identity Provider attribute values.
     *
     * @return Map of Wsfed Extended Identity Provider attribute values.
     */
    Map getIDPEXDataMap();
    
    /**
     * Returns a map of Wsfed Standard Identity Provider attribute values.
     *
     * @return Map of Wsfed Standard Identity Provider attribute values.
     */
    Map getIDPSTDDataMap();
}
