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
 * $Id: SAMLv2ModelImpl.java,v 1.21 2008-05-28 18:31:17 babysunil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import javax.servlet.http.HttpServletRequest;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaSecurityUtils;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.IDPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.AttributeElement;
import com.sun.identity.saml2.jaxb.metadata.SingleSignOnServiceElement;
import com.sun.identity.saml2.jaxb.metadata.ArtifactResolutionServiceElement;
import com.sun.identity.saml2.jaxb.metadata.EndpointType;
import com.sun.identity.saml2.jaxb.metadata.SingleLogoutServiceElement;
import com.sun.identity.saml2.jaxb.metadata.ManageNameIDServiceElement;
import com.sun.identity.saml2.jaxb.metadata.SPSSODescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.jaxb.metadata.SSODescriptorType;
import com.sun.identity.saml2.jaxb.metadata.AssertionConsumerServiceElement;
import com.sun.identity.saml2.jaxb.metadata.XACMLAuthzDecisionQueryDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.XACMLPDPDescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.XACMLAuthzDecisionQueryConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.XACMLPDPConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.AttributeType;
import com.sun.identity.saml2.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.saml2.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.ObjectFactory;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.XACMLAuthzServiceElement;
import com.sun.identity.console.federation.model.EntityModel;
import com.sun.identity.saml2.jaxb.metadata.NameIDMappingServiceElement;
import com.sun.identity.saml2.jaxb.metadata.AuthnAuthorityDescriptorElement;
import com.sun.identity.saml2.jaxb.entityconfig.AuthnAuthorityConfigElement;
import com.sun.identity.saml2.jaxb.metadata.AttributeAuthorityDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.AttributeServiceElement;
import com.sun.identity.saml2.jaxb.metadata.AssertionIDRequestServiceElement;
import com.sun.identity.saml2.jaxb.entityconfig.AttributeAuthorityConfigElement;
import com.sun.identity.saml2.jaxb.entityconfig.AttributeQueryConfigElement;
import com.sun.identity.saml2.jaxb.metadata.AuthnQueryServiceElement;
import com.sun.identity.saml2.jaxb.metadataextquery.AttributeQueryDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.AffiliationDescriptorType;
import com.sun.identity.saml2.jaxb.entityconfig.AffiliationConfigElement;
import com.sun.identity.saml2.jaxb.metadata.KeyDescriptorElement;
import com.sun.identity.saml2.jaxb.metadata.EncryptionMethodElement;
import com.sun.identity.saml2.jaxb.xmlenc.EncryptionMethodType;
import com.sun.identity.saml2.jaxb.xmlenc.EncryptionMethodType.KeySize;
import com.sun.identity.console.federation.SAMLv2AuthContexts;
import javax.xml.bind.JAXBException;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.BigInteger;

public class SAMLv2ModelImpl extends EntityModelImpl implements SAMLv2Model {
    private SAML2MetaManager metaManager;
    private static Map extendedMetaIdpMap = new HashMap(46);
    private static Map extendedACMetaIdpMap = new HashMap(34);
    private static Map extendedAPMetaIdpMap = new HashMap(12);
    private static Map extendedSMetaIdpMap = new HashMap(2);
    private static Map extendedAdMetaIdpMap = new HashMap(6);
    private static Map extendedMetaSpMap = new HashMap(54);
    private static Map extendedACMetaSpMap = new HashMap(34);
    private static Map extendedAPMetaSpMap = new HashMap(24);
    private static Map extendedSMetaSpMap = new HashMap(2);
    private static Map extendedAdMetaSpMap = new HashMap(20);
    private static Map xacmlPDPExtendedMeta = new HashMap(18);
    private static Map xacmlPEPExtendedMeta = new HashMap(18);
    private static Map extAttrAuthMap = new HashMap(12);
    private static Map extAuthnAuthMap = new HashMap(6);
    private static Map extattrQueryMap = new HashMap(4);
    
    //extended metadata attributes for idp only
    static {
        extendedMetaIdpMap.put(IDP_SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_BASIC_AUTH_ON, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_BASIC_AUTH_USER, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_BASIC_AUTH_PWD, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_AUTO_FED_ENABLED, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_AUTO_FED_ATTR, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_ATTR_MAP, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_NAMEID_ENCRYPTED, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_LOGOUT_REQ_SIGN, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_LOGOUT_RESP_SIGN, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_MNI_REQ_SIGN, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_MNI_RESP_SIGN, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(ASSERT_EFFECT_TIME, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_ACCT_MAPPER, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_AUTHN_CONTEXT_MAPPER, 
                Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_AUTHN_CONTEXT_CLASS_REF_MAPPING,
                Collections.EMPTY_SET);
        extendedMetaIdpMap.put(IDP_ATTR_MAPPER, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(ASSERT_NOT_BEFORE_TIMESKEW,
                Collections.EMPTY_SET);
        extendedMetaIdpMap.put(BOOT_STRAP_ENABLED, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(ARTIF_RESOLVE_SIGN, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(AUTH_URL, Collections.EMPTY_SET);
        extendedMetaIdpMap.put(ASSERTION_CACHE_ENABLED,
                Collections.EMPTY_SET);
        
        // ECP
        extendedMetaIdpMap.put(ATTR_IDP_ECP_SESSION_MAPPER,
                Collections.EMPTY_SET);
        
        //SAE
        extendedMetaIdpMap.put(ATTR_SAE_IDP_APP_SECRET_LIST,
                Collections.EMPTY_SET);
        extendedMetaIdpMap.put(ATTR_SAE_IDP_URL,
                Collections.EMPTY_SET);
    }
    
    //extended metadata attributes for sp only
    static {
        extendedMetaSpMap.put(SP_SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_BASIC_AUTH_ON, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_BASIC_AUTH_USER, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_BASIC_AUTH_PWD, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_AUTO_FED_ENABLED, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_AUTO_FED_ATTR, Collections.EMPTY_SET);
        
        extendedMetaSpMap.put(SP_ATTR_MAP, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_NAMEID_ENCRYPTED, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_LOGOUT_REQ_SIGN, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_LOGOUT_RESP_SIGN, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_MNI_REQ_SIGN, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_MNI_RESP_SIGN, Collections.EMPTY_SET);
        
        extendedMetaSpMap.put(TRANSIENT_USER, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_ACCT_MAPPER, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_AUTHN_CONTEXT_MAPPER, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_ATTR_MAPPER, Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_AUTHN_CONTEXT_CLASS_REF_MAPPING,
                Collections.EMPTY_SET);
        extendedMetaSpMap.put(SP_AUTHN_CONTEXT_COMPARISON,
                Collections.EMPTY_SET);
        extendedMetaSpMap.put(SAML2_AUTH_MODULE, Collections.EMPTY_SET);
        extendedMetaSpMap.put(LOCAL_AUTH_URL, Collections.EMPTY_SET);
        extendedMetaSpMap.put(INTERMEDIATE_URL, Collections.EMPTY_SET);
        extendedMetaSpMap.put(DEFAULT_RELAY_STATE, Collections.EMPTY_SET);
        extendedMetaSpMap.put(ASSERT_TIME_SKEW, Collections.EMPTY_SET);
        extendedMetaSpMap.put(WANT_ATTR_ENCRYPTED, Collections.EMPTY_SET);
        extendedMetaSpMap.put(WANT_ASSERTION_ENCRYPTED,
                Collections.EMPTY_SET);
        extendedMetaSpMap.put(WANT_ARTIF_RESP_SIGN, Collections.EMPTY_SET);
        
               
        //IDP PROXY
        extendedMetaSpMap.put(ENABLE_IDP_PROXY, Collections.EMPTY_SET);
        extendedMetaSpMap.put(IDP_PROXY_LIST, Collections.EMPTY_SET);
        extendedMetaSpMap.put(IDP_PROXY_COUNT, Collections.EMPTY_SET);
        extendedMetaSpMap.put(IDP_PROXY_INTROD, Collections.EMPTY_SET);
        
        //ECP
        extendedMetaSpMap.put(ATTR_ECP_REQUEST_IDP_LIST_FINDER_IMPL,
                Collections.EMPTY_SET);
        extendedMetaSpMap.put(ATTR_ECP_REQUEST_IDP_LIST,
                Collections.EMPTY_SET);
        extendedMetaSpMap.put(ATTR_ECP_REQUEST_IDP_LIST_GET_COMPLETE,
                Collections.EMPTY_SET);
        
        //SAE
        extendedMetaSpMap.put(ATTR_SAE_SP_APP_SECRET_LIST, 
                Collections.EMPTY_SET);
        extendedMetaSpMap.put(ATTR_SAE_SP_URL, Collections.EMPTY_SET);
        extendedMetaSpMap.put(ATTR_SAE_LOGOUT_URL, Collections.EMPTY_SET);
        
        //spAdapter
        extendedMetaSpMap.put(ATTR_SP_ADAPTER, Collections.EMPTY_SET);
        extendedMetaSpMap.put(ATTR_SP_ADAPTER_ENV, Collections.EMPTY_SET);
    }
    
    //extended Assertion Content metadata attributes for idp only    
    static {
        extendedACMetaIdpMap.put(ARTIF_RESOLVE_SIGN, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_LOGOUT_REQ_SIGN, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_LOGOUT_RESP_SIGN, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_MNI_REQ_SIGN, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_MNI_RESP_SIGN, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_NAMEID_ENCRYPTED, Collections.EMPTY_SET);    
        extendedACMetaIdpMap.put(IDP_AUTHN_CONTEXT_MAPPER, 
                Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_AUTHN_CONTEXT_CLASS_REF_MAPPING,
                Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(ASSERT_EFFECT_TIME, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(ASSERT_NOT_BEFORE_TIMESKEW,
                Collections.EMPTY_SET);        
        extendedACMetaIdpMap.put(IDP_BASIC_AUTH_ON, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_BASIC_AUTH_USER, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(IDP_BASIC_AUTH_PWD, Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(ASSERTION_CACHE_ENABLED,
                Collections.EMPTY_SET);
        extendedACMetaIdpMap.put(BOOT_STRAP_ENABLED, Collections.EMPTY_SET);
    }
    
    //extended Assertion Processing metadata attributes for idp only    
    static {
        extendedAPMetaIdpMap.put(IDP_ATTR_MAP, Collections.EMPTY_SET);
        extendedAPMetaIdpMap.put(IDP_ATTR_MAPPER, Collections.EMPTY_SET);
        extendedAPMetaIdpMap.put(IDP_AUTO_FED_ENABLED, Collections.EMPTY_SET);
        extendedAPMetaIdpMap.put(IDP_AUTO_FED_ATTR, Collections.EMPTY_SET);
        extendedAPMetaIdpMap.put(IDP_ACCT_MAPPER, Collections.EMPTY_SET);
        extendedAPMetaIdpMap.put(AUTH_URL, Collections.EMPTY_SET);
    }
    
    //extended Services metadata attributes for idp only    
    static {
        extendedSMetaIdpMap.put(IDP_META_ALIAS, Collections.EMPTY_SET);
    }
    
    //extended Advanced metadata attributes for idp only    
    static {
        extendedAdMetaIdpMap.put(ATTR_IDP_ECP_SESSION_MAPPER,
                Collections.EMPTY_SET);
        extendedAdMetaIdpMap.put(ATTR_SAE_IDP_APP_SECRET_LIST,
                Collections.EMPTY_SET);
        extendedAdMetaIdpMap.put(ATTR_SAE_IDP_URL,
                Collections.EMPTY_SET);
    }
    
    //extended Assertion Content metadata attributes for sp only    
    static {
        extendedACMetaSpMap.put(SP_SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);        
        extendedACMetaSpMap.put(SP_NAMEID_ENCRYPTED, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(WANT_ATTR_ENCRYPTED, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(WANT_ASSERTION_ENCRYPTED,
                Collections.EMPTY_SET);       
        extendedACMetaSpMap.put(SP_LOGOUT_REQ_SIGN, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_LOGOUT_RESP_SIGN, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_MNI_REQ_SIGN, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_MNI_RESP_SIGN, Collections.EMPTY_SET);        
        extendedACMetaSpMap.put(WANT_ARTIF_RESP_SIGN, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_AUTHN_CONTEXT_MAPPER,
                Collections.EMPTY_SET);        
        extendedACMetaSpMap.put(SP_AUTHN_CONTEXT_CLASS_REF_MAPPING,
                Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_AUTHN_CONTEXT_COMPARISON,
                Collections.EMPTY_SET);
        extendedACMetaSpMap.put(ASSERT_TIME_SKEW, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_BASIC_AUTH_ON, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_BASIC_AUTH_USER, Collections.EMPTY_SET);
        extendedACMetaSpMap.put(SP_BASIC_AUTH_PWD, Collections.EMPTY_SET);
        
    }
    
    //extended Assertion Processing metadata attributes for sp only    
    static {
       extendedAPMetaSpMap.put(SP_ATTR_MAP, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(SP_ATTR_MAPPER, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(SP_AUTO_FED_ENABLED, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(SP_AUTO_FED_ATTR, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(SP_ACCT_MAPPER, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(TRANSIENT_USER, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(LOCAL_AUTH_URL, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(INTERMEDIATE_URL, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(DEFAULT_RELAY_STATE, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(SAML2_AUTH_MODULE, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(ATTR_SP_ADAPTER, Collections.EMPTY_SET);
       extendedAPMetaSpMap.put(ATTR_SP_ADAPTER_ENV, Collections.EMPTY_SET);
    }
    
    //extended Services metadata attributes for sp only    
    static {
        extendedSMetaSpMap.put(SP_META_ALIAS, Collections.EMPTY_SET);        
    }
    
    //extended Advanced metadata attributes for sp only    
    static {
        //IDP PROXY
        extendedAdMetaSpMap.put(ENABLE_IDP_PROXY, Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(IDP_PROXY_LIST, Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(IDP_PROXY_COUNT, Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(IDP_PROXY_INTROD, Collections.EMPTY_SET);
        
        //ECP
        extendedAdMetaSpMap.put(ATTR_ECP_REQUEST_IDP_LIST_FINDER_IMPL,
                Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(ATTR_ECP_REQUEST_IDP_LIST,
                Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(ATTR_ECP_REQUEST_IDP_LIST_GET_COMPLETE,
                Collections.EMPTY_SET);
        
        //SAE
        extendedAdMetaSpMap.put(ATTR_SAE_SP_APP_SECRET_LIST,
                Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(ATTR_SAE_SP_URL, Collections.EMPTY_SET);
        extendedAdMetaSpMap.put(ATTR_SAE_LOGOUT_URL, Collections.EMPTY_SET);        
    }
    
    static {
        xacmlPDPExtendedMeta.put(ATTR_WANT_ASSERTION_SIGNED,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_SIGNING_CERT_ALIAS,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_ENCRYPTION_CERT_ALIAS,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_BASIC_AUTH_ON,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_BASIC_AUTH_USER,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_BASIC_AUTH_PASSWORD,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_WANT_XACML_AUTHZ_DECISION_QUERY_SIGNED,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_WANT_ASSERTION_ENCRYPTED,
                Collections.EMPTY_LIST);
        xacmlPDPExtendedMeta.put(ATTR_COTLIST,
                Collections.EMPTY_LIST);
    }
    static {
        xacmlPEPExtendedMeta.put(ATTR_WANT_ASSERTION_SIGNED,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_SIGNING_CERT_ALIAS,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_ENCRYPTION_CERT_ALIAS,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_BASIC_AUTH_ON,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_BASIC_AUTH_USER,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_BASIC_AUTH_PASSWORD,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_WANT_XACML_AUTHZ_DECISION_QUERY_SIGNED,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_WANT_ASSERTION_ENCRYPTED,
                Collections.EMPTY_LIST);
        xacmlPEPExtendedMeta.put(ATTR_COTLIST,
                Collections.EMPTY_LIST);
    }
    
    //attributes for attribute authority
    static {
        extAttrAuthMap.put(SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extAttrAuthMap.put(ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);
        extAttrAuthMap.put(DEF_AUTH_MAPPER, Collections.EMPTY_SET);
        extAttrAuthMap.put(X509_AUTH_MAPPER, Collections.EMPTY_SET);
        extAttrAuthMap.put(SUB_DATA_STORE, Collections.EMPTY_SET);
        extAttrAuthMap.put(ASSERTION_ID_REQ_MAPPER,
               Collections.EMPTY_SET);
    }
    
    //attributes for authn authority
    static {
        extAuthnAuthMap.put(SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extAuthnAuthMap.put(ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);
        extAuthnAuthMap.put(ASSERTION_ID_REQ_MAPPER, Collections.EMPTY_SET);
    }
    
    //attributes for attribute query
    static {
        extattrQueryMap.put(SIGN_CERT_ALIAS, Collections.EMPTY_SET);
        extattrQueryMap.put(ENCRYPT_CERT_ALIAS, Collections.EMPTY_SET);
    }
    
    public SAMLv2ModelImpl(HttpServletRequest req, Map map) {
        super(req, map);
    }
    
    /**
     * Returns a map with standard identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with standard attribute values of Identity Provider.
     * @throws AMConsoleException if unable to retrieve the Identity Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getStandardIdentityProviderAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "IDP-Standard"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        Map map = new HashMap();
        IDPSSODescriptorElement idpssoDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            idpssoDescriptor =
                    samlManager.getIDPSSODescriptor(realm,entityName);
            if (idpssoDescriptor != null) {
                
                // retrieve WantAuthnRequestsSigned
                map.put(WANT_AUTHN_REQ_SIGNED,returnEmptySetIfValueIsNull(
                        idpssoDescriptor.isWantAuthnRequestsSigned()));
                
                //retrieve ArtifactResolutionService
                List artList =
                        idpssoDescriptor.getArtifactResolutionService();
                if (!artList.isEmpty()) {
                    ArtifactResolutionServiceElement key =
                            (ArtifactResolutionServiceElement)artList.get(0);
                    map.put(ART_RES_LOCATION,
                            returnEmptySetIfValueIsNull(key.getLocation()));
                    map.put(ART_RES_INDEX,
                            returnEmptySetIfValueIsNull(Integer.toString(
                            key.getIndex())));
                    map.put(ART_RES_ISDEFAULT,
                            returnEmptySetIfValueIsNull(key.isIsDefault()));
                }
                
                //retrieve SingleLogoutService
                map.put(SINGLE_LOGOUT_HTTP_LOCATION, Collections.EMPTY_SET);
                map.put(SINGLE_LOGOUT_HTTP_RESP_LOCATION,
                    Collections.EMPTY_SET);
                map.put(SLO_POST_LOC, Collections.EMPTY_SET);
                map.put(SLO_POST_RESPLOC, Collections.EMPTY_SET);
                map.put(SINGLE_LOGOUT_SOAP_LOCATION, Collections.EMPTY_SET);
                
                List logoutList = idpssoDescriptor.getSingleLogoutService();                
                for (int i=0; i<logoutList.size(); i++) {
                    SingleLogoutServiceElement spslsElem = 
                            (SingleLogoutServiceElement) logoutList.get(i);
                    String tmp = spslsElem.getBinding();
                    if (tmp.contains(httpRedirect)) {
                        map.put(SINGLE_LOGOUT_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getLocation()));
                        map.put(SINGLE_LOGOUT_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getResponseLocation()));
                    } else if (tmp.contains(httpPost)) {
                        map.put(SLO_POST_LOC,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getLocation()));
                        map.put(SLO_POST_RESPLOC,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getResponseLocation()));
                    } else if (tmp.contains(soap)) {
                        map.put(SINGLE_LOGOUT_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getLocation()));
                    }
                }
                
                //retrieve ManageNameIDService
                map.put(MANAGE_NAMEID_HTTP_LOCATION,Collections.EMPTY_SET);
                map.put(MANAGE_NAMEID_HTTP_RESP_LOCATION,
                    Collections.EMPTY_SET);
                map.put(MNI_POST_LOC,Collections.EMPTY_SET);
                map.put(MNI_POST_RESPLOC,Collections.EMPTY_SET);
                map.put(MANAGE_NAMEID_SOAP_LOCATION,Collections.EMPTY_SET);
                List manageNameIdList =
                        idpssoDescriptor.getManageNameIDService();
                
                for (int i=0; i<manageNameIdList.size(); i++) {
                    ManageNameIDServiceElement mniElem = 
                        (ManageNameIDServiceElement) manageNameIdList.get(i);
                    String tmp = mniElem.getBinding();
                    if (tmp.contains(httpRedirect)) {
                        map.put(MANAGE_NAMEID_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem.getLocation()));
                        map.put(MANAGE_NAMEID_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem.getResponseLocation()));
                    } else if (tmp.contains(httpPost)) {
                        map.put(MNI_POST_LOC,
                            returnEmptySetIfValueIsNull(
                            mniElem.getLocation()));
                        map.put(MNI_POST_RESPLOC,
                            returnEmptySetIfValueIsNull(
                            mniElem.getResponseLocation()));
                    } else if (tmp.contains(soap)) {
                        map.put(MANAGE_NAMEID_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem.getLocation()));
                    }
                }
                
                //retrieve nameid mapping service
                List nameIDmappingList =
                        idpssoDescriptor.getNameIDMappingService();
                if (!nameIDmappingList.isEmpty()) {
                    NameIDMappingServiceElement namidElem1 =
                        (NameIDMappingServiceElement)nameIDmappingList.get(0);
                    map.put(NAME_ID_MAPPPING,
                            returnEmptySetIfValueIsNull(
                            namidElem1.getLocation()));
                }
                
                //retrieve nameid format
                List NameIdFormatList = idpssoDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    map.put(NAMEID_FORMAT, returnEmptySetIfValueIsNull(
                            convertListToSet(NameIdFormatList)));
                }
                
                //retrieve SingleSignOnService
                map.put(SINGLE_SIGNON_HTTP_LOCATION, Collections.EMPTY_SET);
                map.put(SINGLE_SIGNON_SOAP_LOCATION, Collections.EMPTY_SET);
                map.put(SSO_SOAPS_LOC, Collections.EMPTY_SET);
                
                List signonList = idpssoDescriptor.getSingleSignOnService();
                
                for (int i=0; i<signonList.size(); i++) {
                    SingleSignOnServiceElement signElem = 
                            (SingleSignOnServiceElement) signonList.get(i);
                    String tmp = signElem.getBinding();
                    if (tmp.contains(httpRedirect)) {
                        map.put(SINGLE_SIGNON_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            signElem.getLocation()));
                    } else if (tmp.contains(httpPost)) {
                        map.put(SINGLE_SIGNON_SOAP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            signElem.getLocation()));
                    } else if (tmp.contains(soap)) {
                        map.put(SSO_SOAPS_LOC,
                            returnEmptySetIfValueIsNull(
                            signElem.getLocation()));
                    }
                }
                
                //retrieve key descriptor encryption details if present
                if (idpssoDescriptor.getKeyDescriptor() != null ) {
                    getKeyandAlgorithm(idpssoDescriptor, map);
                }
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getIdentityProviderAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Standard", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return map;
    }
    
    /**
     * Returns a map with extended identity provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended attribute values of Identity Provider.
     * @throws AMConsoleException if unable to retrieve the Identity Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getExtendedIdentityProviderAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "IDP-Extended"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        Map map = null;
        IDPSSOConfigElement idpssoConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            idpssoConfig = samlManager.getIDPSSOConfig(realm,entityName);
            if (idpssoConfig != null) {
                BaseConfigType baseConfig = (BaseConfigType)idpssoConfig;
                map = SAML2MetaUtils.getAttributes(baseConfig);
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getExtIdentityProviderAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     *Returns the metaAlias of the entity.
     *
     *@param realm to which the entity belongs.
     *@param entityName is the entity id.
     *@param role the Role of entity.
     *@return the metaAlias of the entity.
     *@throws AMConsoleException if unable to retrieve metaAlias.
     */
    public String getMetaalias(
            String realm,
            String entityName,
            String role
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "Extended"};
        logEvent("ATTEMPT_GET_METAALIAS", params);
        String metaAlias = null;
        IDPSSOConfigElement idpssoConfig = null;
        SPSSOConfigElement spssoConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            if (role.equals(EntityModel.IDENTITY_PROVIDER)) {
                idpssoConfig = samlManager.getIDPSSOConfig(realm,entityName);
                if (idpssoConfig != null) {
                    BaseConfigType baseConfig = (BaseConfigType)idpssoConfig;
                    metaAlias = baseConfig.getMetaAlias();
                }
            } else if (role.equals(EntityModel.SERVICE_PROVIDER)) {
                spssoConfig = samlManager.getSPSSOConfig(realm,entityName);
                if (spssoConfig != null) {
                    BaseConfigType baseConfig = (BaseConfigType)spssoConfig;
                    metaAlias = baseConfig.getMetaAlias();
                }
            }
            logEvent("SUCCEED_GET_METAALIAS", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getMetaalias:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "Extended", strError};
            logEvent("FEDERATION_EXCEPTION_GET_METAALIAS",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return metaAlias;
    }
    
    /**
     * Returns a map with standard service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with standard attribute values of Service Provider.
     * @throws AMConsoleException if unable to retrieve the Service Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getStandardServiceProviderAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "SP-Standard"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        Map map = new HashMap();
        SPSSODescriptorElement spssoDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            spssoDescriptor = samlManager.getSPSSODescriptor(realm,entityName);
            if (spssoDescriptor != null) {
                
                // retrieve WantAuthnRequestsSigned
                map.put(IS_AUTHN_REQ_SIGNED,
                        returnEmptySetIfValueIsNull(
                        spssoDescriptor.isAuthnRequestsSigned()));
                map.put(WANT_ASSERTIONS_SIGNED,
                        returnEmptySetIfValueIsNull(
                        spssoDescriptor.isWantAssertionsSigned()));
                
                //retrieve SingleLogoutService
                map.put(SP_SINGLE_LOGOUT_HTTP_LOCATION, Collections.EMPTY_SET);
                map.put(SP_SINGLE_LOGOUT_HTTP_RESP_LOCATION,
                    Collections.EMPTY_SET);
                map.put(SP_SLO_POST_LOC, Collections.EMPTY_SET);
                map.put(SP_SLO_POST_RESPLOC, Collections.EMPTY_SET);
                map.put(SP_SINGLE_LOGOUT_SOAP_LOCATION, Collections.EMPTY_SET);
                List splogoutList = spssoDescriptor.getSingleLogoutService();
                for (int i=0; i<splogoutList.size(); i++) {
                    SingleLogoutServiceElement spslsElem = 
                            (SingleLogoutServiceElement) splogoutList.get(i);
                    String tmp = spslsElem.getBinding();
                    if (tmp.contains(httpRedirect)) {
                        map.put(SP_SINGLE_LOGOUT_HTTP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getLocation()));
                        map.put(SP_SINGLE_LOGOUT_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getResponseLocation()));
                    } else if (tmp.contains(httpPost)) {
                        map.put(SP_SLO_POST_LOC,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getLocation()));
                        map.put(SP_SLO_POST_RESPLOC,
                            returnEmptySetIfValueIsNull(
                            spslsElem.getResponseLocation()));
                    } else if (tmp.contains(soap)) {
                        map.put(SP_SINGLE_LOGOUT_SOAP_LOCATION,
                        returnEmptySetIfValueIsNull(spslsElem.getLocation()));
                    }
                }
                
                //retrieve ManageNameIDService
                map.put(SP_MANAGE_NAMEID_HTTP_LOCATION, Collections.EMPTY_SET);
                map.put(SP_MANAGE_NAMEID_HTTP_RESP_LOCATION,
                    Collections.EMPTY_SET);
                map.put(SP_MNI_POST_LOC, Collections.EMPTY_SET);
                map.put(SP_MNI_POST_RESPLOC, Collections.EMPTY_SET);
                map.put(SP_MANAGE_NAMEID_SOAP_LOCATION, Collections.EMPTY_SET);
                map.put(SP_MANAGE_NAMEID_SOAP_RESP_LOCATION,
                    Collections.EMPTY_SET);
                        
                List manageNameIdList =
                        spssoDescriptor.getManageNameIDService();                
                for (int i=0; i<manageNameIdList.size(); i++) {
                    ManageNameIDServiceElement mniElem = 
                        (ManageNameIDServiceElement) manageNameIdList.get(i);
                    String tmp = mniElem.getBinding();
                    if (tmp.contains(httpRedirect)) {
                        map.put(SP_MANAGE_NAMEID_HTTP_LOCATION,
                        returnEmptySetIfValueIsNull(mniElem.getLocation()));
                        map.put(SP_MANAGE_NAMEID_HTTP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem.getResponseLocation()));
                    } else if (tmp.contains(httpPost)) {
                        map.put(SP_MNI_POST_LOC,
                            returnEmptySetIfValueIsNull(
                            mniElem.getLocation()));
                        map.put(SP_MNI_POST_RESPLOC,
                            returnEmptySetIfValueIsNull(
                            mniElem.getResponseLocation()));
                    } else if (tmp.contains(soap)) {
                        map.put(SP_MANAGE_NAMEID_SOAP_LOCATION,
                        returnEmptySetIfValueIsNull(mniElem.getLocation()));
                        map.put(SP_MANAGE_NAMEID_SOAP_RESP_LOCATION,
                            returnEmptySetIfValueIsNull(
                            mniElem.getResponseLocation()));
                    }
                }
                
                //retrieve AssertionConsumerService                
                map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_DEFAULT,
                                returnEmptySetIfValueIsNull(false));
                map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_INDEX,
                            Collections.EMPTY_SET);
                map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_LOCATION,
                            Collections.EMPTY_SET);
                map.put(HTTP_POST_ASSRT_CONS_SERVICE_INDEX,
                            Collections.EMPTY_SET);
                map.put(HTTP_POST_ASSRT_CONS_SERVICE_LOCATION,
                        Collections.EMPTY_SET);
                map.put(PAOS_ASSRT_CONS_SERVICE_INDEX,
                        Collections.EMPTY_SET);
                map.put(PAOS_ASSRT_CONS_SERVICE_LOCATION,
                        Collections.EMPTY_SET);
                List asconsServiceList =
                        spssoDescriptor.getAssertionConsumerService();
                
                for (int i=0; i<asconsServiceList.size(); i++) {
                    AssertionConsumerServiceElement acsElem = 
                    (AssertionConsumerServiceElement) asconsServiceList.get(i);
                    String tmp = acsElem.getBinding();                    
                    if (tmp.contains(artifact)) {
                        map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_DEFAULT,
                        returnEmptySetIfValueIsNull(acsElem.isIsDefault()));
                        map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_INDEX,
                                returnEmptySetIfValueIsNull(
                                Integer.toString(acsElem.getIndex())));
                        map.put(HTTP_ARTI_ASSRT_CONS_SERVICE_LOCATION,
                            returnEmptySetIfValueIsNull(acsElem.getLocation()));
                    } else if (tmp.contains(post)) {
                        map.put(HTTP_POST_ASSRT_CONS_SERVICE_INDEX,
                                returnEmptySetIfValueIsNull(
                                Integer.toString(acsElem.getIndex())));
                        map.put(HTTP_POST_ASSRT_CONS_SERVICE_LOCATION,
                            returnEmptySetIfValueIsNull(acsElem.getLocation()));
                    } else if (tmp.contains(paos)) {
                        map.put(PAOS_ASSRT_CONS_SERVICE_INDEX,
                                returnEmptySetIfValueIsNull(
                                Integer.toString(acsElem.getIndex())));
                        map.put(PAOS_ASSRT_CONS_SERVICE_LOCATION,
                            returnEmptySetIfValueIsNull(acsElem.getLocation()));
                    }
                }
                
                //retrieve nameid format
                List NameIdFormatList = spssoDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    map.put(NAMEID_FORMAT, returnEmptySetIfValueIsNull(
                            convertListToSet(NameIdFormatList)));
                }
                
                //retrieve key descriptor encryption details if present
                if (spssoDescriptor.getKeyDescriptor() != null ) {
                    getKeyandAlgorithm(spssoDescriptor, map);
                }
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getStandardServiceProviderAttribute:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP-Standard", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return map;
    }
    
    /**
     * Returns a map with extended service provider attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended attribute values of Service Provider.
     * @throws AMConsoleException if unable to retrieve the Service Provider
     *     attrubutes based on the realm and entityName passed.
     */
    public Map getExtendedServiceProviderAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "SP-Extended"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        Map map = null;
        SPSSOConfigElement spssoConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            spssoConfig = samlManager.getSPSSOConfig(realm,entityName);
            if (spssoConfig != null) {
                BaseConfigType baseConfig = (BaseConfigType)spssoConfig;
                map = SAML2MetaUtils.getAttributes(baseConfig);
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning(
                    "SAMLv2ModelImpl.getExtendedServiceProviderAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     * Saves the standard attribute values for the Identiy Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param idpStdValues Map which contains the standard attribute values.
     * @param idpExtValues Map which contain the extended attribute values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPStdAttributeValues(
            String realm,
            String entityName,
            Map idpStdValues,
            Map idpExtValues,
            String location
            )  throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "IDP-Standard"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        IDPSSODescriptorElement idpssoDescriptor = null;
        com.sun.identity.saml2.jaxb.metadata.ObjectFactory objFact = new 
                com.sun.identity.saml2.jaxb.metadata.ObjectFactory();
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            idpssoDescriptor =
                    samlManager.getIDPSSODescriptor(realm,entityName);
            if (idpssoDescriptor != null) {
                
                // save for WantAuthnRequestsSigned 
                if (idpStdValues.keySet().contains(WANT_AUTHN_REQ_SIGNED)) {
                    boolean value = setToBoolean(
                            idpStdValues, WANT_AUTHN_REQ_SIGNED);
                    idpssoDescriptor.setWantAuthnRequestsSigned(value); 
                }
                
                // save for Artifact Resolution Service
                if (idpStdValues.keySet().contains(ART_RES_LOCATION)) {
                    String artLocation = getResult(
                            idpStdValues, ART_RES_LOCATION);
                    String indexValue = getResult(idpStdValues, ART_RES_INDEX);
                    boolean isDefault =
                            setToBoolean(idpStdValues, ART_RES_ISDEFAULT);
                    List artList =
                            idpssoDescriptor.getArtifactResolutionService();
                    if (!artList.isEmpty()) {
                        ArtifactResolutionServiceElement elem =
                            (ArtifactResolutionServiceElement)artList.get(0);
                        elem.setLocation(artLocation);
                        elem.setIndex(Integer.parseInt(indexValue));
                        elem.setIsDefault(isDefault);
                        idpssoDescriptor.
                                getArtifactResolutionService().clear();
                        idpssoDescriptor.
                                getArtifactResolutionService().add(elem);
                    }
                }
                
                // save for Single Logout Service - Http-Redirect
                if (idpStdValues.keySet().contains(
                        SINGLE_LOGOUT_HTTP_LOCATION)) {
                    String lohttpLocation = getResult(
                            idpStdValues, SINGLE_LOGOUT_HTTP_LOCATION);
                    String lohttpRespLocation = getResult(
                            idpStdValues, SINGLE_LOGOUT_HTTP_RESP_LOCATION);
                    String postLocation = getResult(
                            idpStdValues, SLO_POST_LOC);
                    String postRespLocation = getResult(
                            idpStdValues, SLO_POST_RESPLOC);
                    String losoapLocation = getResult(
                            idpStdValues, SINGLE_LOGOUT_SOAP_LOCATION);
                    
                    List logList = idpssoDescriptor.getSingleLogoutService();
                    
                    if (!logList.isEmpty()) {
                        logList.clear();                        
                    }
                    
                    if (lohttpLocation != null && lohttpLocation.length() > 0) {
                        SingleLogoutServiceElement slsElemRed = 
                                objFact.createSingleLogoutServiceElement();   
                        slsElemRed.setBinding(httpRedirectBinding);
                        slsElemRed.setLocation(lohttpLocation);
                        slsElemRed.setResponseLocation(lohttpRespLocation);
                        logList.add(slsElemRed);
                    }
                    
                    if (postLocation != null && postLocation.length() > 0) {
                        SingleLogoutServiceElement slsElemPost = 
                                objFact.createSingleLogoutServiceElement();
                        slsElemPost.setBinding(httpPostBinding);
                        slsElemPost.setLocation(postLocation);
                        slsElemPost.setResponseLocation(postRespLocation);
                        logList.add(slsElemPost);
                    }
                    
                    if (losoapLocation != null && losoapLocation.length() > 0) {
                        SingleLogoutServiceElement slsElemSoap = 
                                objFact.createSingleLogoutServiceElement();
                        slsElemSoap.setBinding(soapBinding);
                        slsElemSoap.setLocation(losoapLocation);
                        logList.add(slsElemSoap);
                    }
                }
                
                // save for Manage Name ID Service
                if (idpStdValues.keySet().contains(
                        MANAGE_NAMEID_HTTP_LOCATION)) {
                    String mnihttpLocation = getResult(
                            idpStdValues, MANAGE_NAMEID_HTTP_LOCATION);
                    String mnihttpRespLocation = getResult(
                            idpStdValues, MANAGE_NAMEID_HTTP_RESP_LOCATION);
                    String mnipostLocation = getResult(
                            idpStdValues, MNI_POST_LOC);
                    String mnipostRespLocation = getResult(
                            idpStdValues, MNI_POST_RESPLOC);                
                    String mnisoapLocation = getResult(
                            idpStdValues, MANAGE_NAMEID_SOAP_LOCATION);
                    List manageNameIdList =
                            idpssoDescriptor.getManageNameIDService();
                    
                    if (!manageNameIdList.isEmpty()) {
                        manageNameIdList.clear();                        
                    }
                    
                    if (mnihttpLocation != null &&
                            mnihttpLocation.length() > 0) {
                        ManageNameIDServiceElement mniElemRed = 
                                objFact.createManageNameIDServiceElement(); 
                        mniElemRed.setBinding(httpRedirectBinding);
                        mniElemRed.setLocation(mnihttpLocation);
                        mniElemRed.setResponseLocation(mnihttpRespLocation);
                        manageNameIdList.add(mniElemRed);
                    }
                    if (mnipostLocation != null && 
                            mnipostLocation.length() > 0) {
                        ManageNameIDServiceElement mniElemPost = 
                                objFact.createManageNameIDServiceElement();
                        mniElemPost.setBinding(httpPostBinding);
                        mniElemPost.setLocation(mnipostLocation);                        
                        mniElemPost.setResponseLocation(mnipostRespLocation);
                        manageNameIdList.add(mniElemPost);
                    }
                    if (mnisoapLocation != null && 
                            mnisoapLocation.length() > 0) {
                        ManageNameIDServiceElement mniElemSoap = 
                                objFact.createManageNameIDServiceElement();
                        mniElemSoap.setBinding(soapBinding);
                        mniElemSoap.setLocation(mnisoapLocation);
                        manageNameIdList.add(mniElemSoap);
                    }
                }
                
                //save nameid mapping
                if (idpStdValues.keySet().contains(NAME_ID_MAPPPING)) {
                    String nameIDmappingloc = getResult(
                            idpStdValues, NAME_ID_MAPPPING);
                    List nameIDmappingList =
                            idpssoDescriptor.getNameIDMappingService();
                    if (!nameIDmappingList.isEmpty()) {
                        NameIDMappingServiceElement namidElem1 =
                        (NameIDMappingServiceElement)nameIDmappingList.get(0);
                        namidElem1.setLocation(nameIDmappingloc);
                        idpssoDescriptor.getNameIDMappingService().clear();
                        idpssoDescriptor.getNameIDMappingService().add(
                                namidElem1);
                    }
                }
                
                //save nameid format                
                if (idpStdValues.keySet().contains(NAMEID_FORMAT)) {
                    List NameIdFormatList = idpssoDescriptor.getNameIDFormat();
                    if (!NameIdFormatList.isEmpty()) {
                        saveNameIdFormat(idpssoDescriptor, idpStdValues);
                    }
                }
                
                //save for SingleSignOnService
                if (idpStdValues.keySet().contains(
                        SINGLE_SIGNON_HTTP_LOCATION)) 
                {
                    String ssohttpLocation = getResult(
                            idpStdValues, SINGLE_SIGNON_HTTP_LOCATION);
                    String ssopostLocation = getResult(
                            idpStdValues, SINGLE_SIGNON_SOAP_LOCATION);
                    String ssoSoapLocation = getResult(
                            idpStdValues, SSO_SOAPS_LOC);
                    List signonList = idpssoDescriptor.getSingleSignOnService();
                    
                    if (!signonList.isEmpty()) {
                        signonList.clear();                        
                    }
                    
                    if (ssohttpLocation != null && 
                            ssohttpLocation.length() > 0)
                    {
                        SingleSignOnServiceElement slsElemRed = 
                                objFact.createSingleSignOnServiceElement();  
                        slsElemRed.setBinding(httpRedirectBinding);
                        slsElemRed.setLocation(ssohttpLocation);
                        signonList.add(slsElemRed);
                    }
                    
                    if (ssopostLocation != null &&
                            ssopostLocation.length() > 0) 
                    {
                        SingleSignOnServiceElement slsElemPost = 
                                objFact.createSingleSignOnServiceElement();
                        slsElemPost.setBinding(httpPostBinding);
                        slsElemPost.setLocation(ssopostLocation);
                        signonList.add(slsElemPost);
                    }
                    
                    if (ssoSoapLocation != null && 
                            ssoSoapLocation.length() > 0) 
                    {
                        SingleSignOnServiceElement slsElemSoap = 
                                objFact.createSingleSignOnServiceElement();
                        slsElemSoap.setBinding(soapBinding);
                        slsElemSoap.setLocation(ssoSoapLocation);
                        signonList.add(slsElemSoap);
                    }
                }
                
                //save key and encryption details if present for hosted
                if (location.equals("hosted") && 
                    idpStdValues.keySet().contains(TF_KEY_NAME)) 
                {
                    String keysize = getResult(idpStdValues, TF_KEY_NAME);
                    String algorithm = getResult(idpStdValues, TF_ALGORITHM);      
                    String e_certAlias = getResult(idpExtValues, 
                            IDP_ENCRYPT_CERT_ALIAS);
                    String s_certAlias = getResult(idpExtValues,
                            IDP_SIGN_CERT_ALIAS); 
                    SAML2MetaSecurityUtils.updateProviderKeyInfo(realm,
                      entityName, e_certAlias, false, true, algorithm, 
                            Integer.parseInt(keysize));
                    SAML2MetaSecurityUtils.updateProviderKeyInfo(realm,
                      entityName, s_certAlias, true, true, algorithm,
                            Integer.parseInt(keysize)); 
                }
                
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.setIDPStdAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Standard", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        } catch (JAXBException e) {
            debug.warning("SAMLv2ModelImpl.setIDPStdAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Standard", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Saves the extended attribute values for the Identiy Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param idpExtValues Map which contains the standard attribute values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setIDPExtAttributeValues(
            String realm,
            String entityName,
            Map idpExtValues,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "IDP-Extended"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        String role = EntityModel.IDENTITY_PROVIDER;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            
            //for remote cases
            if (entityConfig == null) {
                createExtendedObject(realm, entityName, location, role);
                entityConfig =
                        samlManager.getEntityConfig(realm,entityName);
            }
            IDPSSOConfigElement  idpssoConfig =
                    samlManager.getIDPSSOConfig(realm,entityName);
            if (idpssoConfig != null) {
                updateBaseConfig(idpssoConfig, idpExtValues, role);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.error("SAMLv2ModelImpl.setIDPExtAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (JAXBException e) {
            debug.error("SAMLv2ModelImpl.setIDPExtAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (AMConsoleException e) {
            debug.error("SAMLv2ModelImpl.setIDPExtAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "IDP-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Saves the standard attribute values for the Service Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param spStdValues Map which contains the standard attribute values.
     * @param spExtValues Map which contain the extended attribute values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPStdAttributeValues(
            String realm,
            String entityName,
            Map spStdValues,
            Map spExtValues,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "SP-Standard"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        SPSSODescriptorElement spssoDescriptor = null;
        com.sun.identity.saml2.jaxb.metadata.ObjectFactory objFact = new 
                com.sun.identity.saml2.jaxb.metadata.ObjectFactory(); 
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            spssoDescriptor =
                    samlManager.getSPSSODescriptor(realm,entityName);
            if (spssoDescriptor != null) {
                
                // save for Single Logout Service - Http-Redirect
                if (spStdValues.keySet().contains(
                        SP_SINGLE_LOGOUT_HTTP_LOCATION)) 
                {
                    String lohttpLocation = getResult(
                            spStdValues, SP_SINGLE_LOGOUT_HTTP_LOCATION);
                    String lohttpRespLocation = getResult(
                            spStdValues, SP_SINGLE_LOGOUT_HTTP_RESP_LOCATION);
                    String lopostLocation = getResult(
                            spStdValues, SP_SLO_POST_LOC);
                    String lopostRespLocation = getResult(
                            spStdValues, SP_SLO_POST_RESPLOC);                
                    String losoapLocation = getResult(
                            spStdValues, SP_SINGLE_LOGOUT_SOAP_LOCATION);
                    List logList = spssoDescriptor.getSingleLogoutService();
                    
                    if (!logList.isEmpty()) {
                        logList.clear();                        
                    }
                    
                    if (lohttpLocation != null && 
                            lohttpLocation.length() > 0) 
                    {                    
                        SingleLogoutServiceElement slsElemRed = 
                                objFact.createSingleLogoutServiceElement();   
                        slsElemRed.setBinding(httpRedirectBinding);
                        slsElemRed.setLocation(lohttpLocation);
                        slsElemRed.setResponseLocation(lohttpRespLocation);
                        logList.add(slsElemRed);
                    }
                    
                    if (lopostLocation != null && 
                            lopostLocation.length() > 0) 
                    {
                        SingleLogoutServiceElement slsElemPost = 
                                objFact.createSingleLogoutServiceElement();
                        slsElemPost.setBinding(httpPostBinding);
                        slsElemPost.setLocation(lopostLocation);
                        slsElemPost.setResponseLocation(lopostRespLocation);
                        logList.add(slsElemPost);
                    }
                    
                    if (losoapLocation != null && 
                            losoapLocation.length() > 0) 
                    {
                        SingleLogoutServiceElement slsElemSoap = 
                                objFact.createSingleLogoutServiceElement();
                        slsElemSoap.setBinding(soapBinding);
                        slsElemSoap.setLocation(losoapLocation);
                        logList.add(slsElemSoap);
                    }
                }
                
                // save for Manage Name ID Service
                if (spStdValues.keySet().contains(
                        SP_MANAGE_NAMEID_HTTP_LOCATION)) 
                {
                    String mnihttpLocation = getResult(
                            spStdValues, SP_MANAGE_NAMEID_HTTP_LOCATION);
                    String mnihttpRespLocation = getResult(
                            spStdValues, SP_MANAGE_NAMEID_HTTP_RESP_LOCATION);
                    String mnipostLocation = getResult(
                            spStdValues, SP_MNI_POST_LOC);
                    String mnipostRespLocation = getResult(
                            spStdValues, SP_MNI_POST_RESPLOC);                
                    String mnisoapLocation = getResult(
                            spStdValues, SP_MANAGE_NAMEID_SOAP_LOCATION);
                    String mnisoapResLocation = getResult(
                            spStdValues, SP_MANAGE_NAMEID_SOAP_RESP_LOCATION);
                    List manageNameIdList =
                            spssoDescriptor.getManageNameIDService();
                      
                    if (!manageNameIdList.isEmpty()) {
                        manageNameIdList.clear();                        
                    }
                    
                    if (mnihttpLocation != null && 
                            mnihttpLocation.length() > 0) 
                    {
                        ManageNameIDServiceElement mniElemRed = 
                                objFact.createManageNameIDServiceElement();
                        mniElemRed.setBinding(httpRedirectBinding);
                        mniElemRed.setLocation(mnihttpLocation);
                        mniElemRed.setResponseLocation(mnihttpRespLocation);
                        manageNameIdList.add(mniElemRed);
                    }
                    
                    if (mnipostLocation != null && 
                            mnipostLocation.length() > 0) 
                    {
                        ManageNameIDServiceElement mniElemPost = 
                                objFact.createManageNameIDServiceElement();
                        mniElemPost.setBinding(httpPostBinding);
                        mniElemPost.setLocation(mnipostLocation);
                        mniElemPost.setResponseLocation(mnipostRespLocation);
                        manageNameIdList.add(mniElemPost);
                    }
                    
                    if (mnisoapLocation != null && 
                            mnisoapLocation.length() > 0) 
                    {
                        ManageNameIDServiceElement mniElemSoap = 
                                objFact.createManageNameIDServiceElement();
                        mniElemSoap.setBinding(soapBinding);
                        mniElemSoap.setLocation(mnisoapLocation);
                        mniElemSoap.setResponseLocation(mnisoapResLocation);
                        manageNameIdList.add(mniElemSoap);
                    }
                }
                
                //save for artifact and post Assertion Consumer Service                
                if (spStdValues.keySet().contains(
                        HTTP_ARTI_ASSRT_CONS_SERVICE_DEFAULT)) 
                {
                    boolean isassertDefault = setToBoolean(
                            spStdValues, HTTP_ARTI_ASSRT_CONS_SERVICE_DEFAULT);
                    String httpIndex = getResult(
                            spStdValues, HTTP_ARTI_ASSRT_CONS_SERVICE_INDEX);
                    String httpLocation = getResult(
                        spStdValues, HTTP_ARTI_ASSRT_CONS_SERVICE_LOCATION);
                    String paosIndex = null;
                    String paosLocation = null;
                    String postIndex =  getResult(
                            spStdValues, HTTP_POST_ASSRT_CONS_SERVICE_INDEX);
                    String postLocation = getResult(
                        spStdValues, HTTP_POST_ASSRT_CONS_SERVICE_LOCATION);                    
                    
                    List asconsServiceList =
                            spssoDescriptor.getAssertionConsumerService();
                    
                    //to retain the value of ecp assertion consumer service
                    if (!asconsServiceList.isEmpty()) {
                        for (int i=0; i<asconsServiceList.size(); i++) {
                            AssertionConsumerServiceElement acsElem =
                                (AssertionConsumerServiceElement)
                                asconsServiceList.get(i);
                            String tmp = acsElem.getBinding();
                            if (tmp.contains(paos)) {
                                paosIndex = 
                                        Integer.toString(acsElem.getIndex());
                                paosLocation = acsElem.getLocation();
                            }
                        }
                    }
                    
                    if (!asconsServiceList.isEmpty()) {
                        asconsServiceList.clear();
                    }

                    AssertionConsumerServiceElement acsElemArti = null;                                          
                    AssertionConsumerServiceElement acsElemPost = null;                           
                    AssertionConsumerServiceElement acsElemPaos = null;
                    
                    if (httpLocation != null && httpLocation.length() > 0 ) {
                        acsElemArti = 
                                objFact.createAssertionConsumerServiceElement();
                        acsElemArti.setBinding(httpartifactBinding);
                        acsElemArti.setIsDefault(isassertDefault);                    
                        acsElemArti.setIndex(Integer.parseInt(httpIndex));
                        acsElemArti.setLocation(httpLocation);
                        if (isassertDefault) {
                            asconsServiceList.add(acsElemArti);
                        }
                    }
                    if (postIndex != null && postIndex.length() > 0 ) {
                        acsElemPost = 
                                objFact.createAssertionConsumerServiceElement();
                        acsElemPost.setBinding(httpPostBinding);
                        acsElemPost.setIndex(Integer.parseInt(postIndex));
                        acsElemPost.setLocation(postLocation);
                        asconsServiceList.add(acsElemPost);
                        if (acsElemArti!=null && !isassertDefault) {
                          asconsServiceList.add(acsElemArti);  
                        }
                    }
                  
                    if (paosLocation != null && paosLocation.length() > 0 ) {
                        acsElemPaos = 
                            objFact.createAssertionConsumerServiceElement();
                        acsElemPaos.setBinding(paosBinding);
                        acsElemPaos.setIndex(Integer.parseInt(paosIndex));
                        acsElemPaos.setLocation(paosLocation);
                        asconsServiceList.add(acsElemPaos);
                    }
                }
                
                //save for ECP Assertion Consumer Service                
                if (spStdValues.keySet().contains(
                        PAOS_ASSRT_CONS_SERVICE_INDEX)) {
                    String paosIndex = getResult(
                            spStdValues, PAOS_ASSRT_CONS_SERVICE_INDEX);
                    String paosLocation = getResult(
                            spStdValues, PAOS_ASSRT_CONS_SERVICE_LOCATION);
                    List asconsServiceList =
                            spssoDescriptor.getAssertionConsumerService();
                    
                    boolean isassertDefault = false;
                    String httpIndex = null;
                    String httpLocation = null;
                    String postIndex = null;
                    String postLocation = null;
                    
                    // to retain the value of artifact and post assertions
                    if (!asconsServiceList.isEmpty()) {
                        for (int i=0; i<asconsServiceList.size(); i++) {
                            AssertionConsumerServiceElement acsElem =
                                    (AssertionConsumerServiceElement)
                                    asconsServiceList.get(i);
                            String tmp = acsElem.getBinding();
                            if (tmp.contains(httpArtifact)) {
                                httpIndex = Integer.toString(
                                        acsElem.getIndex());
                                httpLocation = acsElem.getLocation();                                
                                if (acsElem.isIsDefault()) {
                                    isassertDefault = acsElem.isIsDefault();
                                }
                            } else if (tmp.contains(httpPost)) {
                                postIndex = Integer.toString(
                                        acsElem.getIndex());
                                postLocation = acsElem.getLocation();
                            }
                            
                        }
                    }
                    
                    if (!asconsServiceList.isEmpty()) {
                        asconsServiceList.clear();
                    }
                    
                    AssertionConsumerServiceElement acsElemArti = null;
                    AssertionConsumerServiceElement acsElemPost = null;
                    AssertionConsumerServiceElement acsElemPaos = null;
                    
                    if (httpLocation != null && httpLocation.length()>0) {
                        acsElemArti = 
                            objFact.createAssertionConsumerServiceElement();
                        acsElemArti.setBinding(httpartifactBinding);
                        acsElemArti.setIsDefault(isassertDefault);
                        acsElemArti.setIndex(Integer.parseInt(httpIndex));
                        acsElemArti.setLocation(httpLocation);
                        if (isassertDefault) {
                            asconsServiceList.add(acsElemArti);
                        }
                    }
                    
                    if (postLocation != null && postLocation.length()>0) {
                        acsElemPost =
                            objFact.createAssertionConsumerServiceElement();
                        acsElemPost.setBinding(httpPostBinding);
                        acsElemPost.setIndex(Integer.parseInt(postIndex));
                        acsElemPost.setLocation(postLocation);
                        asconsServiceList.add(acsElemPost);
                        if(acsElemArti!=null && !isassertDefault) {
                            asconsServiceList.add(acsElemArti);
                        }
                    }
                    
                    if (paosLocation != null && paosLocation.length()>0) {
                        acsElemPaos =
                            objFact.createAssertionConsumerServiceElement();
                        acsElemPaos.setBinding(paosBinding);
                        acsElemPaos.setIndex(Integer.parseInt(paosIndex));
                        acsElemPaos.setLocation(paosLocation);
                        asconsServiceList.add(acsElemPaos);
                    }
                    
                }
                
                //save nameid format
                if (spStdValues.keySet().contains(NAMEID_FORMAT)) {
                    List NameIdFormatList = spssoDescriptor.getNameIDFormat();
                    if (!NameIdFormatList.isEmpty()) {
                        saveNameIdFormat(spssoDescriptor, spStdValues);
                    }
                }
                
                //save AuthenRequestsSigned
                if (spStdValues.keySet().contains(IS_AUTHN_REQ_SIGNED)) {
                    boolean authnValue = setToBoolean(
                            spStdValues, IS_AUTHN_REQ_SIGNED);
                    spssoDescriptor.setAuthnRequestsSigned(authnValue);
                }
                
                //save WantAssertionsSigned
                if (spStdValues.keySet().contains(WANT_ASSERTIONS_SIGNED)) {
                    boolean assertValue = setToBoolean(
                            spStdValues, WANT_ASSERTIONS_SIGNED);
                    spssoDescriptor.setWantAssertionsSigned(assertValue);
                }
                
                //save key descriptor encryption details if present
                if (location.equals("hosted") && 
                    spStdValues.keySet().contains(TF_KEY_NAME))
                {
                    String keysize = getResult(spStdValues, TF_KEY_NAME);
                    String algorithm = getResult(spStdValues, TF_ALGORITHM);      
                    String e_certAlias = getResult(spExtValues, 
                            SP_ENCRYPT_CERT_ALIAS);
                    String s_certAlias = getResult(spExtValues,
                            SP_SIGN_CERT_ALIAS);
                    SAML2MetaSecurityUtils.updateProviderKeyInfo(realm,
                      entityName, e_certAlias, false, false, algorithm, 
                            Integer.parseInt(keysize));
                    SAML2MetaSecurityUtils.updateProviderKeyInfo(realm,
                      entityName, s_certAlias, true, false, algorithm,
                            Integer.parseInt(keysize));

                }
                
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.setSPStdAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP-Standard", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        } catch (JAXBException e) {
            debug.error("SAMLv2ModelImpl.setSPStdAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP-Standard", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Saves the extended attribute values for the Service Provider.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param spExtValues Map which contains the standard attribute values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setSPExtAttributeValues(
            String realm,
            String entityName,
            Map spExtValues,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "SP-Extended"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        String role = EntityModel.SERVICE_PROVIDER;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            
            //for remote cases
            if (entityConfig == null) {
                createExtendedObject(realm, entityName, location, role);
                entityConfig =
                        samlManager.getEntityConfig(realm,entityName);
            }
            SPSSOConfigElement  spssoConfig = samlManager.getSPSSOConfig(
                    realm,entityName);
            if (spssoConfig != null){
                updateBaseConfig(spssoConfig, spExtValues, role);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.error("SAMLv2ModelImpl.setSPExtAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (JAXBException e) {
            debug.error("SAMLv2ModelImpl.setSPExtAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (AMConsoleException e) {
            debug.error("SAMLv2ModelImpl.setSPExtAttributeValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "SP Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Updates the BaseConfigElement.
     *
     * @param baseConfig is the BaseConfigType passed.
     * @param values the Map which contains the new attribute/value pairs.
     * @param role the role of entity.
     * @throws AMConsoleException if update of baseConfig object fails.
     */
    private void updateBaseConfig(
            BaseConfigType baseConfig,
            Map values,
            String role
            ) throws JAXBException, AMConsoleException {
            List attrList = baseConfig.getAttribute();
            
            //to handle cases from common tasks page
            if ((attrList.size() == 1) && 
                    (role.equals(EntityModel.IDENTITY_PROVIDER))) 
            {
                baseConfig = addAttributeType(extendedMetaIdpMap, baseConfig);
                attrList = baseConfig.getAttribute();
            } else if ((attrList.size() == 1) && 
                    (role.equals(EntityModel.SERVICE_PROVIDER))) 
            {
                baseConfig = addAttributeType(extendedMetaSpMap, baseConfig);
                attrList = baseConfig.getAttribute();
            }
            if (attrList.size() > 1) {
                for (Iterator it = attrList.iterator(); it.hasNext(); ) {
                    AttributeElement avpnew = (AttributeElement)it.next();
                    String name = avpnew.getName();
                    if (values.keySet().contains(name)) {
                        Set set = (Set)values.get(name);
                        if (set != null) {
                            avpnew.getValue().clear();
                            avpnew.getValue().addAll(set);
                        }
                    }
                }
            }
    }
    
    
    /**
     * Updates the BaseConfigElement.
     *
     * @param baseConfig is the BaseConfigType passed.
     * @param attributeName is the attribute name
     * @param list the list which contains the new values.
     * @throws AMConsoleException if update of baseConfig object fails.
     */
    private void updateBaseConfig(
            BaseConfigType baseConfig,
            String attributeName,
            List list
            ) throws AMConsoleException {
        List attrList = baseConfig.getAttribute();
        
        for (Iterator it = attrList.iterator(); it.hasNext(); ) {
            AttributeElement avpnew = (AttributeElement)it.next();
            String name = avpnew.getName();
            if(name.equals(attributeName)){
                avpnew.getValue().clear();
                avpnew.getValue().addAll(list);
            }
        }
        
    }
    
    /**
     * Saves the NameIdFormat.
     *
     * @param ssodescriptor is the SSODescriptorType which can be idpsso/spsso.
     * @param values the Map which contains the new attribute/value pairs.
     * @throws AMConsoleException if save fails.
     */
    private void saveNameIdFormat(
            SSODescriptorType ssodescriptor,
            Map values
            ) throws AMConsoleException {
        List listtoSave = convertSetToList(
                (Set)values.get(NAMEID_FORMAT));
        ssodescriptor.getNameIDFormat().clear();
        Iterator itt = listtoSave.listIterator();
        while (itt.hasNext()) {
            String name =(String) itt.next();
            ssodescriptor.getNameIDFormat().add(name);
        }
    }
    
    /**
     * retrieves the encryption key size and algorithm
     *
     * @param ssodescriptor the SSODescriptorType which can be idpsso/spsso.
     * @param map the Map which contains the attribute/value pairs.
     */
    private void getKeyandAlgorithm(
            SSODescriptorType ssodescriptor,
            Map map
            )  {
                List keyList = ssodescriptor.getKeyDescriptor();
                for (int i=0; i<keyList.size(); i++) {
                    KeyDescriptorElement keyOne =
                            (KeyDescriptorElement)keyList.get(i);
                    String type = keyOne.getUse();
                    if (type.equals("encryption")) {
                        List encryptMethod = keyOne.getEncryptionMethod();
                        if (!encryptMethod.isEmpty()) {
                            EncryptionMethodElement encrptElement = 
                            (EncryptionMethodElement)encryptMethod.get(0);
                            String alg = encrptElement.getAlgorithm();
                            String size = null;
                            List keySizeList = encrptElement.getContent();
                            if (!keySizeList.isEmpty()) {
                                EncryptionMethodType.KeySize keysizeElem =
                                   (EncryptionMethodType.KeySize)
                                   keySizeList.get(0);
                                BigInteger keysize = keysizeElem.getValue();
                                size = Integer.toString(keysize.intValue());
                            }                    

                            map.put(TF_KEY_NAME, 
                                returnEmptySetIfValueIsNull(size));
                            map.put(TF_ALGORITHM, 
                                returnEmptySetIfValueIsNull(alg));

                        }
                    }
                }
    }
    
    /**
     * Creates the extended config object when it does not exist.
     * @param realm the realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param location indicates whether hosted or remote
     * @param role can be SP, IDP or SP/IDP.
     * @throws SAML2MetaException, JAXBException,
     *     AMConsoleException if saving of attribute value fails.
     */
    private void createExtendedObject(
            String realm,
            String entityName,
            String location,
            String role
            ) throws SAML2MetaException, JAXBException, AMConsoleException {
        SAML2MetaManager samlManager = new SAML2MetaManager();
        EntityDescriptorElement entityDescriptor =
                samlManager.getEntityDescriptor(realm, entityName);
        ObjectFactory objFactory = new ObjectFactory();
        EntityConfigElement entityConfigElement =
                objFactory.createEntityConfigElement();
        entityConfigElement.setEntityID(entityName);
        if (location.equals("remote")) {
            entityConfigElement.setHosted(false);
        } else {
            entityConfigElement.setHosted(true);
        }
        List configList =
                entityConfigElement.
                getIDPSSOConfigOrSPSSOConfigOrAuthnAuthorityConfig();
        
        BaseConfigType baseConfigIDP = null;
        BaseConfigType baseConfigSP = null;
        BaseConfigType baseConfigAuth = null;        
        AttributeAuthorityDescriptorElement attrauthDescriptor =
                samlManager.getAttributeAuthorityDescriptor(realm,entityName);
        AuthnAuthorityDescriptorElement authnauthDescriptor  = 
                samlManager.getAuthnAuthorityDescriptor(realm,entityName);
        AttributeQueryDescriptorElement attrQueryDescriptor =
                samlManager.getAttributeQueryDescriptor(realm,entityName);
        IDPSSODescriptorElement idpssoDesc = 
                samlManager.getIDPSSODescriptor(realm,entityName);
        SPSSODescriptorElement spssoDesc = 
                samlManager.getSPSSODescriptor(realm,entityName);                
       XACMLAuthzDecisionQueryDescriptorElement xacmlAuthzDescriptor =
                    samlManager.getPolicyEnforcementPointDescriptor(
                    realm, entityName);
        XACMLPDPDescriptorElement xacmlPDPDescriptor =
                    samlManager.getPolicyDecisionPointDescriptor(
                    realm, entityName);

        if (isDualRole(entityDescriptor)) {
            baseConfigIDP = objFactory.createIDPSSOConfigElement();
            baseConfigSP = objFactory.createSPSSOConfigElement();
            baseConfigIDP = addAttributeType(extendedMetaIdpMap, baseConfigIDP);
            baseConfigSP = addAttributeType(extendedMetaSpMap, baseConfigSP);
            configList.add(baseConfigIDP);
            configList.add(baseConfigSP);
        }else if (role.equals(EntityModel.IDENTITY_PROVIDER) ||
                (idpssoDesc != null)) 
        {
            baseConfigIDP = objFactory.createIDPSSOConfigElement();
            baseConfigIDP = addAttributeType(extendedMetaIdpMap, baseConfigIDP);
            configList.add(baseConfigIDP);
        } else if (role.equals(EntityModel.SERVICE_PROVIDER) || 
                (spssoDesc  != null)) 
        {
            baseConfigSP = objFactory.createSPSSOConfigElement();
            baseConfigSP = addAttributeType(extendedMetaSpMap, baseConfigSP);
            configList.add(baseConfigSP);
        }
        if (role.equals(EntityModel.SAML_ATTRAUTHORITY) || 
                (attrauthDescriptor != null))
        {
            baseConfigAuth =
                    objFactory.createAttributeAuthorityConfigElement();
            baseConfigAuth = addAttributeType(extAttrAuthMap, baseConfigAuth);
            configList.add(baseConfigAuth);
        }
        if (role.equals(EntityModel.SAML_AUTHNAUTHORITY) ||
                (authnauthDescriptor != null)) 
        {
            baseConfigAuth =
                    objFactory.createAuthnAuthorityConfigElement();
            baseConfigAuth = addAttributeType(extAuthnAuthMap, baseConfigAuth);
            configList.add(baseConfigAuth);
        }
        if (role.equals(EntityModel.SAML_ATTRQUERY) || 
                (attrQueryDescriptor != null))
        {
            baseConfigAuth =
                    objFactory.createAttributeQueryConfigElement();
            baseConfigAuth = addAttributeType(extattrQueryMap, baseConfigAuth);
            configList.add(baseConfigAuth);
        }
        if (role.equals(EntityModel.POLICY_DECISION_POINT_DESCRIPTOR) ||
                (xacmlPDPDescriptor != null)) 
        {
            baseConfigAuth =
                    objFactory.createXACMLPDPConfigElement();
            baseConfigAuth = addAttributeType(
                    xacmlPDPExtendedMeta, baseConfigAuth);
            configList.add(baseConfigAuth);
        }
        if (role.equals(EntityModel.POLICY_ENFORCEMENT_POINT_DESCRIPTOR) ||
                (xacmlAuthzDescriptor != null)) 
        {
            baseConfigAuth =
                    objFactory.createXACMLAuthzDecisionQueryConfigElement();
            baseConfigAuth = addAttributeType(
                    xacmlPEPExtendedMeta, baseConfigAuth);
            configList.add(baseConfigAuth);
        }
        
        samlManager.setEntityConfig(realm, entityConfigElement);
    }
    
    private BaseConfigType addAttributeType(Map values, BaseConfigType bctype)
    throws JAXBException{
        ObjectFactory objFactory = new ObjectFactory();
        for (Iterator iter = values.keySet().iterator();
        iter.hasNext(); ) {
            AttributeType avp = objFactory.createAttributeElement();
            String key = (String)iter.next();
            avp.setName(key);
            avp.getValue().addAll(Collections.EMPTY_LIST);
            bctype.getAttribute().add(avp);
        }
        return bctype;
    }
    
    /**
     * Retrieves information whether entity has dual role or not.
     * @param entityDescriptor is the standard metadata object.
     *
     * @return a boolean value which indicates entity has dual role or not.
     */
    private boolean isDualRole(EntityDescriptorElement entityDescriptor) {
        List roles = new ArrayList();
        boolean dual = false;
        if (entityDescriptor != null) {
            if ( (SAML2MetaUtils.getSPSSODescriptor(
                    entityDescriptor) != null) && (
                    SAML2MetaUtils.getIDPSSODescriptor(
                    entityDescriptor) != null) ) {
                dual = true;
            }
        }
        return dual;
    }
    
    /**
     * Returns a Map of PEP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PEP descriptor data.
     * @throws AMConsoleException if unable to retrieve the PEP
     *         standard metadata attributes
     */
    public Map getPEPDescriptor(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "XACML PEP"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        
        Map data = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryDescriptorElement xacmlAuthzDescriptor =
                    saml2Manager.getPolicyEnforcementPointDescriptor(
                    realm, entityName);
            if (xacmlAuthzDescriptor != null) {
                data = new HashMap(10);
                
                //ProtocolSupportEnum
                data.put(ATTR_TXT_PROTOCOL_SUPPORT_ENUM,
                        returnEmptySetIfValueIsNull(
                        xacmlAuthzDescriptor.getProtocolSupportEnumeration()));
                if (xacmlAuthzDescriptor.isWantAssertionsSigned()) {
                    data.put(ATTR_WANT_ASSERTION_SIGNED, "true");
                } else {
                    data.put(ATTR_WANT_ASSERTION_SIGNED, "false");
                }
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PEP", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a Map of PDP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PDP descriptor data.
     * @throws AMConsoleException if unable to retrieve the PDP
     *         standard metadata attribute
     */
    public Map getPDPDescriptor(String realm, String entityName)
        throws AMConsoleException 
    {
        String[] params = {realm, entityName,"SAMLv2", "XACML PDP"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        
        Map data = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPDescriptorElement xacmlPDPDescriptor =
                    saml2Manager.getPolicyDecisionPointDescriptor(
                    realm,
                    entityName);
            if (xacmlPDPDescriptor != null) {
                data = new HashMap(10);
                
                //ProtocolSupportEnum
                data.put(ATTR_TXT_PROTOCOL_SUPPORT_ENUM,
                        returnEmptySetIfValueIsNull(
                        xacmlPDPDescriptor.getProtocolSupportEnumeration()));
                List authzServiceList =
                        xacmlPDPDescriptor.getXACMLAuthzService();
                if (authzServiceList.size() != 0) {
                    XACMLAuthzServiceElement authzService =
                            (XACMLAuthzServiceElement) authzServiceList.get(0);
                    data.put(ATTR_XACML_AUTHZ_SERVICE_BINDING,
                            returnEmptySetIfValueIsNull(
                            authzService.getBinding()));
                    data.put(ATTR_XACML_AUTHZ_SERVICE_LOCATION,
                            returnEmptySetIfValueIsNull(
                            authzService.getLocation()));
                }
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PDP", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", 
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a <code>Map</code> containing the extended metadata for the PEP.
     *
     * @param realm where entity exists.
     * @param entityName name of entity descriptor.
     * @param location if the entity is remote or hosted.
     * @return key-value pair Map of PEP config data.
     * @throws AMConsoleException if unable to retrieve the PEP
     *         extended metadata attribute
     */
    public Map getPEPConfig(
            String realm,
            String entityName,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "XACML PEP"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        String role = EntityModel.POLICY_ENFORCEMENT_POINT_DESCRIPTOR;
        Map data = null;
        List configList = null;
        String metaAlias = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryConfigElement xacmlAuthzConfigElement =
                    saml2Manager.getPolicyEnforcementPointConfig(
                    realm, entityName);
            
            if (xacmlAuthzConfigElement != null) {
                data = new HashMap();
                configList = xacmlAuthzConfigElement.getAttribute();
                metaAlias = xacmlAuthzConfigElement.getMetaAlias();
                int size = configList.size();
                for (int i=0; i< size; i++) {
                    AttributeType atype = (AttributeType) configList.get(i);
                    String name = atype.getName();
                    java.util.List value = atype.getValue();
                    data.put(atype.getName(),
                            returnEmptySetIfValueIsNull(atype.getValue()));
                }
                data.put("metaAlias", metaAlias);
            }  else {
                createExtendedObject(realm, entityName, location, role);
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        } catch (JAXBException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PEP", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", 
                    paramsEx);
            throw new AMConsoleException(strError);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PEP", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a Map of PDP Config data. (Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor
     * @param location location of entity(hosted or remote)
     * @return key-value pair Map of PPP config data.
     * @throws AMConsoleException if unable to retrieve the PDP
     *         extended metadata attribute
     */
    public Map getPDPConfig(
            String realm,
            String entityName,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "XACML PDP"};
        logEvent("ATTEMPT_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
        String role =  EntityModel.POLICY_DECISION_POINT_DESCRIPTOR;
        Map data = null;
        List configList = null;
        String metaAlias = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPConfigElement xacmlPDPConfigElement =
                    saml2Manager.getPolicyDecisionPointConfig(
                    realm, entityName);
            if (xacmlPDPConfigElement != null) {
                data = new HashMap();
                configList = xacmlPDPConfigElement.getAttribute() ;
                metaAlias = xacmlPDPConfigElement.getMetaAlias();
                int size = configList.size();
                for (int i=0; i< size; i++) {
                    AttributeType atype = (AttributeType) configList.get(i);
                    String name = atype.getName();
                    java.util.List value = atype.getValue();
                    data.put(atype.getName(),
                            returnEmptySetIfValueIsNull(atype.getValue()));
                }
                data.put("metaAlias", metaAlias);
            } else {
                createExtendedObject(realm, entityName, location, role);
            }
            logEvent("SUCCEED_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", params);
            } catch (JAXBException e) {
             String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PDP", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PDP", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ENTITY_DESCRIPTOR_ATTR_VALUES", 
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Save standard metadata for PDP descriptor.
     *
     * @param realm realm of Entity.
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PDP standed data.
     * @throws AMConsoleException if fails to modify/save the PDP
     *         standard metadata attribute
     */
    public void updatePDPDescriptor(
            String realm,
            String entityName,
            Map attrValues
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "XACML PDP"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    saml2Manager.getEntityDescriptor(realm, entityName) ;
            XACMLPDPDescriptorElement pdpDescriptor =
                    saml2Manager.getPolicyDecisionPointDescriptor(
                    realm,
                    entityName);
            
            if (pdpDescriptor != null) {
                List authzServiceList = pdpDescriptor.getXACMLAuthzService();
                if (authzServiceList.size() != 0) {
                    XACMLAuthzServiceElement authzService =
                            (XACMLAuthzServiceElement)authzServiceList.get(0);
                    authzService.setLocation((String)AMAdminUtils.getValue(
                            (Set)attrValues.get(
                            ATTR_XACML_AUTHZ_SERVICE_LOCATION)));
                }
            }
            saml2Manager.setEntityDescriptor(realm, entityDescriptor);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PDP", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    
    /**
     * Save extended metadata for PDP Config.
     *
     * @param realm realm of Entity.
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted.
     * @param attrValues key-value pair Map of PDP extended config.
     * @throws AMConsoleException if fails to modify/save the PDP
     *         extended metadata attribute
     */
    public void updatePDPConfig(
            String realm,
            String entityName,
            String location,
            Map attrValues
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "XACML PDP"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        String role = EntityModel.POLICY_DECISION_POINT_DESCRIPTOR;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    saml2Manager.getEntityConfig(realm,entityName);
            
            if (entityConfig == null) {
                throw new AMConsoleException("invalid.xacml.configuration");
            }
            XACMLPDPConfigElement pdpEntityConfig =
                    saml2Manager.getPolicyDecisionPointConfig(
                    realm, entityName);
            if (pdpEntityConfig == null) {
                throw new AMConsoleException("invalid.xacml.configuration");
            } else {
                updateBaseConfig(pdpEntityConfig, attrValues, role);
            }
            
            //saves the attributes by passing the new entityConfig object
            saml2Manager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PDP", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", paramsEx);
            throw new AMConsoleException(strError);
        } catch (JAXBException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PDP", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    /**
     * Save the standard metadata for PEP descriptor.
     *
     * @param realm realm of Entity.
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PEP descriptor data.
     * throws AMConsoleException if there is an error.
     */
    public void updatePEPDescriptor(
            String realm,
            String entityName,
            Map attrValues
            ) throws AMConsoleException {
        // TBD : currently, there is nothing to save
    }
    
    /**
     * Save the extended metadata for PEP Config.
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     * @param attrValues key-value pair Map of PEP extended config.
     * @throws AMConsoleException if fails to modify/save the PEP
     *         extended metadata attributes
     */
    public void updatePEPConfig(
            String realm,
            String entityName,
            String location,
            Map attrValues
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "XACML PEP"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        String role = EntityModel.POLICY_ENFORCEMENT_POINT_DESCRIPTOR;
        
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    saml2Manager.getEntityConfig(realm,entityName);
            
            if (entityConfig == null) {
                throw new AMConsoleException("invalid.xacml.configuration");
            }            
            XACMLAuthzDecisionQueryConfigElement pepEntityConfig =
                    saml2Manager.getPolicyEnforcementPointConfig(
                    realm, entityName);
            if (pepEntityConfig == null) {
                throw new AMConsoleException("invalid.xacml.configuration");
            } else {
                updateBaseConfig(pepEntityConfig, attrValues, role);
            }
            
            //saves the attributes by passing the new entityConfig object
            saml2Manager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PEP", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", paramsEx);
            throw new AMConsoleException(strError);
        } catch (JAXBException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "XACML PEP", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    
    /**
     * Returns the object of Auththentication Contexts in IDP.
     *
     * @param realm Realm of Entity
     * @param entityName Name of Entity Descriptor.
     * @return SAMLv2AuthContexts contains IDP authContexts values.
     * @throws AMConsoleException if unable to retrieve the IDP
     *         Authentication Contexts
     */
    public SAMLv2AuthContexts getIDPAuthenticationContexts(
            String realm,
            String entityName
            ) throws AMConsoleException {
        SAMLv2AuthContexts cxt = new SAMLv2AuthContexts();
        
        try {
            List tmpList = new ArrayList();
            SAML2MetaManager  saml2MetaManager = getSAML2MetaManager();
            Map map = new HashMap();
            
            BaseConfigType  idpConfig=
                    saml2MetaManager.getIDPSSOConfig(realm, entityName);
            if (idpConfig != null){
                map = SAML2MetaUtils.getAttributes(idpConfig) ;
            } else {
                throw new AMConsoleException("invalid.entity.name");
            }
            List list = (List) map.get(IDP_AUTHN_CONTEXT_CLASS_REF_MAPPING);
            
            for (int i=0; i<list.size();i++) {
                String tmp = (String) list.get(i);
                int index = tmp.lastIndexOf("|");
                boolean isDefault = false;
                String defaultValue = tmp.substring(index+1);
                if(defaultValue.equals("default")){
                    isDefault = true;
                }
                
                tmp = tmp.substring(0, index);
                index = tmp.lastIndexOf("|");
                
                String authScheme = tmp.substring(index+1);
                tmp = tmp.substring(0, index);
                
                index = tmp.indexOf("|");
                String level = tmp.substring(index + 1);
                String name = tmp.substring(0,index);   
             
                cxt.put(name, "true", authScheme, level, isDefault);
            }
            
        } catch (SAML2MetaException e) {
            throw new AMConsoleException(getErrorString(e));
        } catch (AMConsoleException e) {
            throw new AMConsoleException(getErrorString(e));
        }
        return (cxt != null) ? cxt : new SAMLv2AuthContexts();
    }
    
    /**
     * Returns  the object of Auththentication Contexts in SP.
     *
     * @param realm Realm of Entity
     * @param entityName Name of Entity Descriptor.
     * @return SAMLv2AuthContexts contains SP authContexts values.
     * @throws AMConsoleException if unable to retrieve the SP
     *         Authentication Contexts
     */
    public SAMLv2AuthContexts getSPAuthenticationContexts(
            String realm,
            String entityName
            ) throws AMConsoleException {
        SAMLv2AuthContexts cxt = new SAMLv2AuthContexts();
        
        try{
            List tmpList = new ArrayList();
            SAML2MetaManager  saml2MetaManager = getSAML2MetaManager();
            Map map = new HashMap();
            
            BaseConfigType  spConfig=
                    saml2MetaManager.getSPSSOConfig(realm, entityName);
            if (spConfig != null){
                map = SAML2MetaUtils.getAttributes(spConfig) ;
            } else {
                throw new AMConsoleException("invalid.entity.name");
            }
            
            List list = (List) map.get(SP_AUTHN_CONTEXT_CLASS_REF_MAPPING);
            
            for (int i=0; i<list.size(); i++){
                String tmp = (String) list.get(i);
                int index = tmp.lastIndexOf("|");
                
                boolean isDefault = false;
                String defaultValue = tmp.substring(index+1);
                if(defaultValue.equals("default")){
                    isDefault = true;
                }
                tmp = tmp.substring(0, index);
                index = tmp.indexOf("|");
                String level = tmp.substring(index + 1);
                String name = tmp.substring(0,index);
                cxt.put(name, "true", level, isDefault);
            }
            
        } catch (SAML2MetaException e) {
            throw new AMConsoleException(getErrorString(e));
        } catch (AMConsoleException e) {
            throw new AMConsoleException(getErrorString(e));
        }
        
        return (cxt != null) ? cxt : new SAMLv2AuthContexts();
        
    }
    
    /**
     * update IDP Authentication Contexts
     *
     * @param realm Realm of Entity
     * @param entityName Name of Entity Descriptor.
     * @param cxt SAMLv2AuthContexts object contains IDP
     *        Authentication Contexts values
     * @throws AMConsoleException if fails to update IDP
     *         Authentication Contexts.
     */
    public void updateIDPAuthenticationContexts(
            String realm,
            String entityName,
            SAMLv2AuthContexts cxt
            ) throws AMConsoleException {
        List list = cxt.toIDPAuthContextInfo();
        String[] params = {realm, entityName,"SAMLv2",
            "IDP-updateIDPAuthenticationContexts"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        
        try {
            SAML2MetaManager saml2MetaManager = getSAML2MetaManager();
            EntityConfigElement entityConfig =
                    saml2MetaManager.getEntityConfig(realm,entityName);
            if (entityConfig == null) {
                throw new AMConsoleException("invalid.entity.name");
            }
            
            IDPSSOConfigElement	idpDecConfigElement =
                    saml2MetaManager.getIDPSSOConfig(realm, entityName);
            if (idpDecConfigElement == null) {
                throw new AMConsoleException("invalid.config.element");
            } else {
                updateBaseConfig(
                        idpDecConfigElement,
                        IDP_AUTHN_CONTEXT_CLASS_REF_MAPPING,
                        list
                        );
            }
            
            //saves the attributes by passing the new entityConfig object
            saml2MetaManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx = {realm, entityName, "SAMLv2", 
             "IDP-updateIDPAuthenticationContexts", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR", 
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        
        return;
    }
    
    /**
     * update SP Authentication Contexts
     *
     * @param realm Realm of Entity
     * @param entityName Name of Entity Descriptor.
     * @param cxt SAMLv2AuthContexts object contains SP
     *        Authentication Contexts values
     * @throws AMConsoleException if fails to update SP
     *         Authentication Contexts.
     */
    public void updateSPAuthenticationContexts(
            String realm,
            String entityName,
            SAMLv2AuthContexts cxt
            ) throws AMConsoleException {
        List list = cxt.toSPAuthContextInfo();
        String[] params = {realm, entityName,"SAMLv2", 
            "SP-updateSPAuthenticationContexts"};
        logEvent("ATTEMPT_MODIFY_ENTITY_DESCRIPTOR", params);
        
        try {
            SAML2MetaManager saml2MetaManager = getSAML2MetaManager();
            EntityConfigElement entityConfig =
                    saml2MetaManager.getEntityConfig(realm,entityName);
            if (entityConfig == null) {
                throw new AMConsoleException("invalid.entity.name");
            }
            
            SPSSOConfigElement spDecConfigElement =
                    saml2MetaManager.getSPSSOConfig(realm, entityName);
            if (spDecConfigElement == null) {
                throw new AMConsoleException("invalid.config.element");
            } else {
                // update sp entity config
                updateBaseConfig(
                        spDecConfigElement,
                        SP_AUTHN_CONTEXT_CLASS_REF_MAPPING,
                        list
                        );
            }
            
            //saves the attributes by passing the new entityConfig object
            saml2MetaManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", 
             "SP-updateSPAuthenticationContexts", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return;
    }
        
    /**
     * Returns a map with standard AttributeAuthority attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with AttributeAuthority values.
     * @throws AMConsoleException if unable to retrieve std AttributeAuthority
     *       values based on the realm and entityName passed.
     */
    public Map getStandardAttributeAuthorityAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "AttribAuthority-Std"};
        logEvent("ATTEMPT_GET_ATTR_AUTH_ATTR_VALUES", params);
        Map map = new HashMap();
        AttributeAuthorityDescriptorElement attrauthDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            attrauthDescriptor =
                samlManager.getAttributeAuthorityDescriptor(realm,entityName);
            if (attrauthDescriptor != null) {
                List artServiceList =
                        attrauthDescriptor.getAttributeService();
                if (!artServiceList.isEmpty()) {
                    AttributeServiceElement key =
                            (AttributeServiceElement)artServiceList.get(0);
                    map.put(ATTR_SEFVICE_DEFAULT_LOCATION,
                            returnEmptySetIfValueIsNull(key.getLocation()));
                    AttributeServiceElement key2 =
                            (AttributeServiceElement)artServiceList.get(1);
                    map.put(SUPPORTS_X509,
                            returnEmptySetIfValueIsNull(
                            key2.isSupportsX509Query()));
                    map.put(ATTR_SEFVICE_LOCATION,
                            returnEmptySetIfValueIsNull(key2.getLocation()));
                }
                List assertionIDReqList =
                        attrauthDescriptor.getAssertionIDRequestService();
                if (!assertionIDReqList.isEmpty()) {
                    AssertionIDRequestServiceElement elem1 =
                            (AssertionIDRequestServiceElement)
                            assertionIDReqList.get(0);
                    map.put(ASSERTION_ID_SAOP_LOC,
                        returnEmptySetIfValueIsNull(elem1.getLocation()));
                    AssertionIDRequestServiceElement elem2 =
                        (AssertionIDRequestServiceElement)
                        assertionIDReqList.get(0);
                    map.put(ASSERTION_ID_URI_LOC,
                        returnEmptySetIfValueIsNull(elem2.getLocation()));
                }
                
                List attrProfileList =
                        attrauthDescriptor.getAttributeProfile();
                if (!attrProfileList.isEmpty()) {
                    String key =
                            (String)attrProfileList.get(0);
                    map.put(ATTRIBUTE_PROFILE,
                            returnEmptySetIfValueIsNull(key));
                    
                }
                
            }
            
            logEvent("SUCCEED_GET_ATTR_AUTH_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                ("SAMLv2ModelImpl.getStandardAttributeAuthorityAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribAuthority-Std", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ATTR_AUTH_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return map;
    }
    
    /**
     * Returns a map with extended AttributeAuthority attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended AttributeAuthority values.
     * @throws AMConsoleException if unable to retrieve ext AttributeAuthority
     *     attributes based on the realm and entityName passed.
     */
    public Map getExtendedAttributeAuthorityAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "AttribAuthority-Ext"};
        logEvent("ATTEMPT_GET_ATTR_AUTH_ATTR_VALUES", params);
        Map map = null;
        AttributeAuthorityConfigElement attributeAuthorityConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            attributeAuthorityConfig =
                    samlManager.getAttributeAuthorityConfig(
                    realm,entityName);
            if (attributeAuthorityConfig != null) {
                BaseConfigType baseConfig =
                        (BaseConfigType)attributeAuthorityConfig;
                map = SAML2MetaUtils.getAttributes(baseConfig);
            }
            logEvent("SUCCEED_GET_ATTR_AUTH_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                ("SAMLv2ModelImpl.getExtendedAttributeAuthorityAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribAuthority-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_ATTR_AUTH_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a map with standard AuthnAuthority attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with AuthnAuthority values.
     * @throws AMConsoleException if unable to retrieve std AuthnAuthority
     *       values based on the realm and entityName passed.
     */
    public Map getStandardAuthnAuthorityAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "AuthnAuthority-Std"};
        logEvent("ATTEMPT_GET_AUTHN_AUTH_ATTR_VALUES", params);
        Map map = new HashMap();
        AuthnAuthorityDescriptorElement authnauthDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            authnauthDescriptor =
                    samlManager.getAuthnAuthorityDescriptor(realm,entityName);
            if (authnauthDescriptor != null) {
                List authQueryServiceList =
                        authnauthDescriptor.getAuthnQueryService();
                if (!authQueryServiceList.isEmpty()) {
                    AuthnQueryServiceElement key =
                        (AuthnQueryServiceElement)authQueryServiceList.get(0);
                    map.put(AUTHN_QUERY_SERVICE,
                            returnEmptySetIfValueIsNull(key.getLocation()));
                }
                List assertionIDReqList =
                        authnauthDescriptor.getAssertionIDRequestService();
                if (!assertionIDReqList.isEmpty()) {
                    AssertionIDRequestServiceElement elem1 =
                            (AssertionIDRequestServiceElement)
                            assertionIDReqList.get(0);
                    map.put(ASSERTION_ID_SAOP_LOC,
                        returnEmptySetIfValueIsNull(elem1.getLocation()));
                    AssertionIDRequestServiceElement elem2 =
                            (AssertionIDRequestServiceElement)
                            assertionIDReqList.get(0);
                    map.put(ASSERTION_ID_URI_LOC,
                       returnEmptySetIfValueIsNull(elem2.getLocation()));
                }
                
            }
            logEvent("SUCCEED_GET_AUTHN_AUTH_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getStandardAuthnAuthorityAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AuthnAuthority-Std", strError};
            logEvent("FEDERATION_EXCEPTION_GET_AUTHN_AUTH_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return map;
        
    }
    
    /**
     * Returns a map with extended AuthnAuthority attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended AuthnAuthority values.
     * @throws AMConsoleException if unable to retrieve ext AuthnAuthority
     *     attributes based on the realm and entityName passed.
     */
    public Map getExtendedAuthnAuthorityAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "AuthnAuthority-Ext"};
        logEvent("ATTEMPT_GET_AUTHN_AUTH_VALUES", params);
        Map map = null;
        AuthnAuthorityConfigElement authnAuthorityConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            authnAuthorityConfig = samlManager.getAuthnAuthorityConfig(
                    realm,entityName);
            if (authnAuthorityConfig != null) {
                BaseConfigType baseConfig =
                        (BaseConfigType)authnAuthorityConfig;
                map = SAML2MetaUtils.getAttributes(baseConfig);
            }
            logEvent("SUCCEED_GET_AUTHN_AUTH_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                ("SAMLv2ModelImpl.getExtendedAuthnAuthorityAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AuthnAuthority-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_AUTHN_AUTH_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a map with standard AttrQuery attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with AttrQuery values.
     * @throws AMConsoleException if unable to retrieve std AttrQuery
     *       values based on the realm and entityName passed.
     */
    public Map getStandardAttrQueryAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException  {
        String[] params = {realm, entityName,"SAMLv2", "AttrQuery-Std"};
        logEvent("ATTEMPT_GET_ATTR_QUERY_ATTR_VALUES", params);
        Map map = new HashMap();
        AttributeQueryDescriptorElement attrQueryDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            attrQueryDescriptor =
                    samlManager.getAttributeQueryDescriptor(realm,entityName);
            if (attrQueryDescriptor != null) {
                //retrieve nameid format
                List NameIdFormatList = attrQueryDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {
                    map.put(ATTR_NAMEID_FORMAT, returnEmptySetIfValueIsNull(
                            convertListToSet(NameIdFormatList)));
                }
                
            }
            logEvent("SUCCEED_GET_ATTR_QUERY_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getStandardAttrQueryAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttrQuery-Std", strError};
            logEvent("FEDERATION_EXCEPTION_GET_ATTR_QUERY_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return map;
        
    }
    
    /**
     * Returns a map with extended AttrQuery attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended AttrQuery values.
     * @throws AMConsoleException if unable to retrieve ext AttrQuery
     *     attributes based on the realm and entityName passed.
     */
    public Map getExtendedAttrQueryAttributes(
            String realm,
            String entityName
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "AttrQuery-Ext"};
        logEvent("ATTEMPT_GET_ATTR_QUERY_VALUES", params);
        Map map = null;
        AttributeQueryConfigElement attrQueryConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            attrQueryConfig = samlManager.getAttributeQueryConfig(
                    realm,entityName);
            if (attrQueryConfig != null) {
                BaseConfigType baseConfig =
                        (BaseConfigType)attrQueryConfig;
                map = SAML2MetaUtils.getAttributes(baseConfig);
            }
            logEvent("SUCCEED_GET_ATTR_QUERY_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getExtendedAttrQueryAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttrQuery-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_ATTR_QUERY_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
    }
    
    /**
     * Saves the standard attribute values for Attribute Authority.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param attrAuthValues Map which contains standard attribute auth values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setStdAttributeAuthorityValues(
            String realm,
            String entityName,
            Map attrAuthValues
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "AttribAuthority-Std"};
        logEvent("ATTEMPT_MODIFY_ATTR_AUTH_ATTR_VALUES", params);
        Map map = new HashMap();
        AttributeAuthorityDescriptorElement attrauthDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            attrauthDescriptor =
                samlManager.getAttributeAuthorityDescriptor(realm,entityName);
            if (attrauthDescriptor != null) {
                
                //save attribute Service
                String defLocation = getResult(
                        attrAuthValues, ATTR_SEFVICE_DEFAULT_LOCATION);
                boolean is509 =
                        setToBoolean(attrAuthValues, SUPPORTS_X509);
                String x509Location = getResult(
                        attrAuthValues, ATTR_SEFVICE_LOCATION);
                List artServiceList =
                        attrauthDescriptor.getAttributeService();
                if (!artServiceList.isEmpty()) {
                    AttributeServiceElement key1 =
                            (AttributeServiceElement)artServiceList.get(0);
                    AttributeServiceElement key2 =
                            (AttributeServiceElement)artServiceList.get(1);
                    key1.setLocation(defLocation);
                    key2.setLocation(x509Location);
                    key2.setSupportsX509Query(is509);
                    attrauthDescriptor.getAttributeService().clear();
                    attrauthDescriptor.getAttributeService().add(key1);
                    attrauthDescriptor.getAttributeService().add(key2);
                }
                
                //save assertion ID request
                String soapLocation = getResult(
                        attrAuthValues, ASSERTION_ID_SAOP_LOC);
                String uriLocation = getResult(
                        attrAuthValues, ASSERTION_ID_URI_LOC);
                List assertionIDReqList =
                        attrauthDescriptor.getAssertionIDRequestService();
                if (!assertionIDReqList.isEmpty()) {
                    AssertionIDRequestServiceElement elem1 =
                        (AssertionIDRequestServiceElement)
                        assertionIDReqList.get(0);
                    AssertionIDRequestServiceElement elem2 =
                            (AssertionIDRequestServiceElement)
                            assertionIDReqList.get(0);
                    elem1.setLocation(soapLocation);
                    elem2.setLocation(uriLocation);
                    attrauthDescriptor.
                       getAssertionIDRequestService().clear();
                    attrauthDescriptor.
                       getAssertionIDRequestService().add(elem1);
                    attrauthDescriptor.
                       getAssertionIDRequestService().add(elem2);
                }
                
                //save attribute profile
                String attrProfile = getResult(
                        attrAuthValues, ATTRIBUTE_PROFILE);
                List attrProfileList =
                        attrauthDescriptor.getAttributeProfile();
                if (!attrProfileList.isEmpty()) {                    
                    attrauthDescriptor.getAttributeProfile().clear();
                    attrauthDescriptor.getAttributeProfile().
                       add(attrProfile);
                    
                }
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
            
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.setStdAttributeAuthorityValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribAuthority-Std", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    
    /**
     * Saves the extended attribute values for Attribute Authority.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param attrAuthExtValues Map which contains the extended values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setExtAttributeAuthorityValues(
            String realm,
            String entityName,
            Map attrAuthExtValues,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "AttribAuthority-Ext"};
        logEvent("ATTEMPT_MODIFY_ATTR_AUTH_ATTR_VALUES", params);
        String role = EntityModel.SAML_ATTRAUTHORITY;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
                        
            //for remote cases
            if (entityConfig == null) {
                createExtendedObject(realm, entityName, location, role);
                entityConfig =
                        samlManager.getEntityConfig(realm,entityName);
            }
            AttributeAuthorityConfigElement attributeAuthorityConfig =
                    samlManager.getAttributeAuthorityConfig(
                    realm,entityName);
            if (attributeAuthorityConfig != null) {
                updateBaseConfig(attributeAuthorityConfig, 
                        attrAuthExtValues,role);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.error("SAMLv2ModelImpl.setExtAttributeAuthorityValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribAuthority-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (JAXBException e) {
            debug.error("SAMLv2ModelImpl.setExtAttributeAuthorityValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribAuthority-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (AMConsoleException e) {
            debug.error("SAMLv2ModelImpl.setExtAttributeAuthorityValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribAuthority-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Saves the standard attribute values for Authn Authority.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param authnAuthValues Map which contains standard authn authority values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setStdAuthnAuthorityValues(
            String realm,
            String entityName,
            Map authnAuthValues
            ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "AuthnAuthority-Std"};
        logEvent("ATTEMPT_MODIFY_AUTHN_AUTH_ATTR_VALUES", params);
        Map map = new HashMap();
        AuthnAuthorityDescriptorElement authnauthDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            authnauthDescriptor =
                    samlManager.getAuthnAuthorityDescriptor(realm,entityName);
            if (authnauthDescriptor != null) {
                String queryService = getResult(
                        authnAuthValues, AUTHN_QUERY_SERVICE);
                
                //save query service
                List authQueryServiceList =
                        authnauthDescriptor.getAuthnQueryService();
                if (!authQueryServiceList.isEmpty()) {
                    AuthnQueryServiceElement key =
                       (AuthnQueryServiceElement)authQueryServiceList.get(0);
                    key.setLocation(queryService);
                    authnauthDescriptor.getAuthnQueryService().clear();
                    authnauthDescriptor.getAuthnQueryService().add(key);
                }
                
                //save assertion ID request
                String soapLocation = getResult(
                        authnAuthValues, ASSERTION_ID_SAOP_LOC);
                String uriLocation = getResult(
                        authnAuthValues, ASSERTION_ID_URI_LOC);
                List assertionIDReqList =
                        authnauthDescriptor.getAssertionIDRequestService();
                if (!assertionIDReqList.isEmpty()) {
                    AssertionIDRequestServiceElement elem1 =
                            (AssertionIDRequestServiceElement)
                            assertionIDReqList.get(0);
                    AssertionIDRequestServiceElement elem2 =
                            (AssertionIDRequestServiceElement)
                            assertionIDReqList.get(0);
                    elem1.setLocation(soapLocation);
                    elem2.setLocation(uriLocation);
                    authnauthDescriptor.
                       getAssertionIDRequestService().clear();
                    authnauthDescriptor.
                            getAssertionIDRequestService().add(elem1);
                    authnauthDescriptor.
                            getAssertionIDRequestService().add(elem2);
                }
                
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.setStdAuthnAuthorityValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AuthnAuthority-Std", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    
    /**
     * Saves the extended attribute values for Authn Authority.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param authnAuthExtValues Map which contains the extended values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setExtauthnAuthValues(
            String realm,
            String entityName,
            Map authnAuthExtValues,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "AuthnAuthority-Ext"};
        logEvent("ATTEMPT_MODIFY_AUTHN_AUTH_ATTR_VALUES", params);
        String role = EntityModel.SAML_AUTHNAUTHORITY;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            
            //for remote cases
            if (entityConfig == null) {
                createExtendedObject(realm, entityName, location, role);
                entityConfig =
                        samlManager.getEntityConfig(realm,entityName);
            }
            AuthnAuthorityConfigElement authnAuthorityConfig  =
                    samlManager.getAuthnAuthorityConfig(
                    realm,entityName);
            if (authnAuthorityConfig != null) {
                updateBaseConfig(authnAuthorityConfig, 
                        authnAuthExtValues, role);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.error("SAMLv2ModelImpl.setExtauthnAuthValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AuthnAuthority-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (JAXBException e) {
            debug.error("SAMLv2ModelImpl.setExtauthnAuthValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AuthnAuthority-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (AMConsoleException e) {
            debug.error("SAMLv2ModelImpl.setExtauthnAuthValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AuthnAuthority-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Saves the standard attribute values for Attribute Query.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param attrQueryValues Map which contains standard attribute query values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setStdAttributeQueryValues(
            String realm,
            String entityName,
            Map attrQueryValues
            ) throws AMConsoleException  {
        String[] params = {realm, entityName,"SAMLv2", "AttribQuery-Std"};
        logEvent("ATTEMPT_MODIFY_ATTR_QUERY_VALUES", params);
        Map map = new HashMap();
        AttributeQueryDescriptorElement attrQueryDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            attrQueryDescriptor =
                    samlManager.getAttributeQueryDescriptor(realm,entityName);
            if (attrQueryDescriptor != null) {
                
                //save nameid format
                List NameIdFormatList = 
                   attrQueryDescriptor.getNameIDFormat();
                if (!NameIdFormatList.isEmpty()) {                    
                    List listtoSave = convertSetToList(
                            (Set)attrQueryValues.get(ATTR_NAMEID_FORMAT));
                    attrQueryDescriptor.getNameIDFormat().clear();
                    Iterator itt = listtoSave.listIterator();
                    while (itt.hasNext()) {
                        String name =(String) itt.next();
                        attrQueryDescriptor.getNameIDFormat().add(name);
                    }
                }
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
            
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.setStdAttributeQueryValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribQuery-Std", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    
    /**
     * Saves the extended attribute values for Attribute Query.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param attrQueryExtValues Map which contains the extended values.
     * @param location has the information whether remote or hosted.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setExtAttributeQueryValues(
            String realm,
            String entityName,
            Map attrQueryExtValues,
            String location
            ) throws AMConsoleException {
        String[] params = {realm, entityName, "SAMLv2", "AttribQuery-Ext"};
        logEvent("ATTEMPT_MODIFY_ATTR_QUERY_VALUES", params);
        String role = EntityModel.SAML_ATTRQUERY;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            
            //entityConfig is the extended entity configuration object
            EntityConfigElement entityConfig =
                    samlManager.getEntityConfig(realm,entityName);
            
            //for remote cases
            if (entityConfig == null) {
                createExtendedObject(realm, entityName, location, role);
                entityConfig =
                        samlManager.getEntityConfig(realm,entityName);
            }
            AttributeQueryConfigElement attrQueryConfig =
                    samlManager.getAttributeQueryConfig(
                    realm,entityName);
            if (attrQueryConfig != null) {
                updateBaseConfig(attrQueryConfig, attrQueryExtValues, role);
            }
            
            //saves the attributes by passing the new entityConfig object
            samlManager.setEntityConfig(realm,entityConfig);
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.error("SAMLv2ModelImpl.setExtAttributeQueryValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribQuery-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (JAXBException e) {
            debug.error("SAMLv2ModelImpl.setExtAttributeQueryValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribQuery-Extended", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        } catch (AMConsoleException e) {
            debug.error("SAMLv2ModelImpl.setExtAttributeQueryValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "AttribQuery-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
        }
    }
    
    /**
     * Returns a map with standard Affiliation attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with Affiliation values.
     * @throws AMConsoleException if unable to retrieve std Affiliation 
     *       values based on the realm and entityName passed.
     */
    public Map getStandardAffiliationAttributes(
        String realm,
        String entityName
        ) throws AMConsoleException {
        String[] params = {realm, entityName,"SAMLv2", "Affiliation-Std"};
        logEvent("ATTEMPT_GET_AFFILIATION_ATTR_VALUES", params);
        Map map = new HashMap();
        AffiliationDescriptorType affiliationDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            affiliationDescriptor =
                    samlManager.getAffiliationDescriptor(realm,entityName);
            if (affiliationDescriptor != null) {
                
                //retrieve member list
                List membList = affiliationDescriptor.getAffiliateMember();
                if (!membList.isEmpty()) {
                    map.put(AFFILIATE_MEMBER, returnEmptySetIfValueIsNull(
                            convertListToSet(membList)));
                }                
            }
            logEvent("SUCCEED_GET_AFFILIATION_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getStandardAffiliationAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "Affiliation-Std", strError};
            logEvent("FEDERATION_EXCEPTION_GET_AFFILIATION_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return map;        
    }
    
    /**
     * Returns a map with extended Affiliation attributes and values.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @return Map with extended Affiliation values.
     * @throws AMConsoleException if unable to retrieve ext Affiliation
     *     attributes based on the realm and entityName passed.
     */
    public Map getExtendedAffiliationyAttributes(
        String realm,
        String entityName
        ) throws AMConsoleException {;
        String[] params = {realm, entityName, "SAMLv2", "Affiliation-Ext"};
        logEvent("ATTEMPT_GET_AFFILIATION_VALUES", params);
        Map map = null;
        AffiliationConfigElement atffilConfig = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            atffilConfig = samlManager.getAffiliationConfig(
                    realm,entityName);
            if (atffilConfig != null) {
                BaseConfigType baseConfig =
                        (BaseConfigType)atffilConfig;
                map = SAML2MetaUtils.getAttributes(baseConfig);
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                }
                String metalias = baseConfig.getMetaAlias();
                List list = new ArrayList();
                list.add(metalias);
                map.put("metaAlias",list);
            }
            logEvent("SUCCEED_GET_AFFILIATION_ATTR_VALUES", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.getExtendedAffiliationyAttributes:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "Affiliation-Ext", strError};
            logEvent("FEDERATION_EXCEPTION_AFFILIATION_ATTR_VALUES",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
        return (map != null) ? map : Collections.EMPTY_MAP;
        
    }
    
    /**
     * Saves the standard attribute values for Affilaition.
     *
     * @param realm to which the entity belongs.
     * @param entityName is the entity id.
     * @param affilaitionValues Map which contains standard affiliation values.
     * @throws AMConsoleException if saving of attribute value fails.
     */
    public void setStdAffilationValues(
        String realm,
        String entityName,
        Map affilaitionValues
        ) throws AMConsoleException {
        
        String[] params = {realm, entityName,"SAMLv2", "Affiliation-Std"};
        logEvent("ATTEMPT_MODIFY_AFFILIATION_VALUES", params);
        Map map = new HashMap();
        AffiliationDescriptorType affiliationDescriptor = null;
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                    samlManager.getEntityDescriptor(realm,entityName);
            affiliationDescriptor =
                    samlManager.getAffiliationDescriptor(realm,entityName);
            if (affiliationDescriptor != null) {
                
                //save memberlist
                List memberList = 
                   affiliationDescriptor.getAffiliateMember();
                if (!memberList.isEmpty()) {                    
                    List listtoSave = convertSetToList(
                            (Set)affilaitionValues.get(AFFILIATE_MEMBER));
                    affiliationDescriptor.getAffiliateMember().clear();
                    Iterator itt = listtoSave.listIterator();
                    while (itt.hasNext()) {
                        String name =(String) itt.next();
                        affiliationDescriptor.getAffiliateMember().add(name);
                    }
                }
                samlManager.setEntityDescriptor(realm, entityDescriptor);
            }
            
            logEvent("SUCCEED_MODIFY_ENTITY_DESCRIPTOR", params);
        } catch (SAML2MetaException e) {
            debug.warning
                    ("SAMLv2ModelImpl.setStdAffilationValues:", e);
            String strError = getErrorString(e);
            String[] paramsEx =
            {realm, entityName, "SAMLv2", "Affilaition-Std", strError};
            logEvent("FEDERATION_EXCEPTION_MODIFY_ENTITY_DESCRIPTOR",
                    paramsEx);
            throw new AMConsoleException(strError);
        }
    }
    
    protected SAML2MetaManager getSAML2MetaManager() throws SAML2MetaException {
        if (metaManager == null) {
            metaManager = new SAML2MetaManager();
        }
        return metaManager;
    }
    
    private boolean setToBoolean(Map map, String value) {
        Set set = (Set)map.get(value);
        return ((set != null) && !set.isEmpty()) ?
            Boolean.parseBoolean((String)set.iterator().next()) : false;
    }
    
    private String getResult(Map map, String value) {
        Set set = (Set)map.get(value);
        String val = null;
        if (set != null  && !set.isEmpty() ) {
            Iterator  i = set.iterator();
            while ((i !=  null) && (i.hasNext())) {
                val = (String)i.next();
            }
        }
        return val;
    }
    
    /**
     * Returns SAMLv2 Extended Service Provider attribute values.
     *
     * @return SAMLv2 Extended Service Provider attribute values.
     */
    public Map getSPEXDataMap() {
        return  extendedMetaSpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Identity Provider attribute values.
     *
     * @return SAMLv2 Extended Identity Provider attribute values.
     */
    public Map getIDPEXDataMap() {
        return extendedMetaIdpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Service Provider attribute for Assertion Content.
     *
     * @return SAMLv2 Extended Service Provider attribute for Assertion Content.
     */
    public Map getSPEXACDataMap() {
        return  extendedACMetaSpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Service Provider values for Assertion Processing.
     *
     * @return SAMLv2 Extended Service Provider values for Assertion Processing.
     */
    public Map getSPEXAPDataMap() {
        return  extendedAPMetaSpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Service Provider attribute values for Services.
     *
     * @return SAMLv2 Extended Service Provider attribute values for Services.
     */
    public Map getSPEXSDataMap() {
        return  extendedSMetaSpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Service Provider attribute values for Advanced.
     *
     * @return SAMLv2 Extended Service Provider attribute values for Advanced.
     */
    public Map getSPEXAdDataMap() {
        return  extendedAdMetaSpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Identity Provider values for Assertion Content.
     *
     * @return SAMLv2 Extended Identity Provider values for Assertion Content.
     */
    public Map getIDPEXACDataMap() {
        return extendedACMetaIdpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Identity Provider values for Assertion Processing.
     *
     * @return SAMLv2 Extended Identity Provider values for Assertion Processing.
     */
    public Map getIDPEXAPDataMap() {
        return extendedAPMetaIdpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Identity Provider attribute values for Services.
     *
     * @return SAMLv2 Extended Identity Provider attribute values for Services.
     */
    public Map getIDPEXSDataMap() {
        return extendedSMetaIdpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Identity Provider attribute values for Advanced.
     *
     * @return SAMLv2 Extended Identity Provider attribute values for Advanced.
     */
    public Map getIDPEXAdDataMap() {
        return extendedAdMetaIdpMap;
    }
    
    /**
     * Returns SAMLv2 Extended Attribute Authority values.
     *
     * @return SAMLv2 Extended Attribute Authority values.
     */
    public Map getattrAuthEXDataMap() {
        return extAttrAuthMap;
    }
    
    /**
     * Returns SAMLv2 Extended Authn Authority values.
     *
     * @return SAMLv2 Extended Authn Authority values.
     */
    public Map getauthnAuthEXDataMap() {
        return extAuthnAuthMap;
    }
    
    /**
     * Returns SAMLv2 Extended Attribute Query values.
     *
     * @return SAMLv2 Extended Attribute Query values.
     */
    public Map getattrQueryEXDataMap() {
        return extattrQueryMap;
    }    
    
}
