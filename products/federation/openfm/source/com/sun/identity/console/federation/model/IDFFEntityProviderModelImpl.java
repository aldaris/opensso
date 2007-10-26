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
 * $Id: IDFFEntityProviderModelImpl.java,v 1.6 2007-10-26 00:08:11 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMConsoleException;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaUtils;
import com.sun.identity.liberty.ws.meta.jaxb.AffiliationDescriptorType;
import com.sun.identity.federation.jaxb.entityconfig.AttributeType;
import com.sun.identity.federation.jaxb.entityconfig.BaseConfigType;
import com.sun.identity.federation.jaxb.entityconfig.EntityConfigElement;
import com.sun.identity.federation.jaxb.entityconfig.ObjectFactory;
import com.sun.identity.liberty.ws.meta.jaxb.EntityDescriptorElement;
import com.sun.identity.liberty.ws.meta.jaxb.IDPDescriptorType;
import com.sun.identity.liberty.ws.meta.jaxb.SPDescriptorType;
import com.sun.identity.federation.common.IFSConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

public class IDFFEntityProviderModelImpl
    extends EntityModelImpl
    implements IDFFEntityProviderModel {
    
    private IDFFMetaManager metaManager;
    private static Map extendedMetaMap = new HashMap(24);
    private static Map extendedMetaIdpMap = new HashMap(9);
    private static Map extendedMetaSpMap = new HashMap(13);

    private static List federationTerminationProfileList = new ArrayList(2);
    static {
     federationTerminationProfileList.add("http://projectliberty.org/profiles/fedterm-sp-http");
     federationTerminationProfileList.add("http://projectliberty.org/profiles/fedterm-sp-soap");
    }

    private static List singleLogoutProfileList = new ArrayList(3);
    static {
        singleLogoutProfileList.add("http://projectliberty.org/profiles/slo-sp-http");
        singleLogoutProfileList.add("http://projectliberty.org/profiles/slo-idp-http-get");
        singleLogoutProfileList.add("http://projectliberty.org/profiles/slo-sp-soap");
    }

    private static List nameRegistrationProfileList = new ArrayList(2);
    static {
        nameRegistrationProfileList.add("http://projectliberty.org/profiles/rni-sp-http");
        nameRegistrationProfileList.add("http://projectliberty.org/profiles/rni-sp-soap");
    }

    private static List federationProfileList = new ArrayList(3);
    static {
        federationProfileList.add("http://projectliberty.org/profiles/brws-post");
        federationProfileList.add("http://projectliberty.org/profiles/brws-art");
        federationProfileList.add("http://projectliberty.org/profiles/lecp");
    }    
    
    // BOTH idp AND SP extended metadata
    static{
        extendedMetaMap.put(ATTR_DO_FEDERATION_PAGE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_ATTRIBUTE_MAPPER_CLASS,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_ENABLE_AUTO_FEDERATION,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_REGISTERATION_DONE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_COT_LIST,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_RESPONSD_WITH,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_ENABLE_NAME_ID_ENCRYPTION,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_SSO_FAILURE_REDIRECT_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_LIST_OF_COTS_PAGE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_DEFAULT_AUTHN_CONTEXT,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_SIGNING_CERT_ALIAS,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_REALM_NAME,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_USER_PROVIDER_CLASS,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_NAME_ID_IMPLEMENETATION_CLASS,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_FEDERATION_DONE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_AUTH_TYPE,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_ENCRYPTION_CERT_ALIAS,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_TERMINATION_DONE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_AUTO_FEDERATION_ATTRIBUTE,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_ERROR_PAGE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_PROVIDER_STATUS,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_PROVIDER_DESCRIPTION,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_LOGOUT_DONE_URL,
            Collections.EMPTY_LIST);
        extendedMetaMap.put(ATTR_PROVIDER_HOME_PAGE_URL,
            Collections.EMPTY_LIST);
    }
    
    // IDP extend meta attribute ONLY IDP
    static {
        extendedMetaIdpMap.put(ATTR_ASSERTION_LIMIT,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_ATTRIBUTE_PLUG_IN,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_IDP_ATTRIBUTE_MAP,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_ASSERTION_ISSUER,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_CLEANUP_INTERVAL,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_IDP_AUTHN_CONTEXT_MAPPING,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_GERNERATE_BOOT_STRAPPING,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_ARTIFACT_TIMEOUT,
            Collections.EMPTY_LIST);
        extendedMetaIdpMap.put(ATTR_ASSERTION_INTERVAL,
            Collections.EMPTY_LIST);
    }
    
    // SP extend meta attribute.. ONLY SP
    static {
        extendedMetaSpMap.put(ATTR_IS_PASSIVE,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_SP_ATTRIBUTE_MAP,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_SP_AUTHN_CONTEXT_MAPPING,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_IDP_PROXY_LIST,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_ENABLE_IDP_PROXY,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_NAME_ID_POLICY,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_FEDERATION_SP_ADAPTER_ENV,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_ENABLE_AFFILIATION,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_FORCE_AUTHN,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_IDP_PROXY_COUNT,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_FEDERATION_SP_ADAPTER,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_USE_INTRODUCTION_FOR_IDP_PROXY,
            Collections.EMPTY_LIST);
        extendedMetaSpMap.put(ATTR_SUPPORTED_SSO_PROFILE,
            Collections.EMPTY_LIST);
    }
    /**
     * Creates a simple model using default resource bundle.
     *
     * @param req HTTP Servlet Request
     * @param map of user information
     */
    public IDFFEntityProviderModelImpl(HttpServletRequest req,  Map map) {
        super(req, map);
    }
    
    /**
     * Returns the type of a provider such as hosted or remote.
     *
     * @param name of Entity Descriptor.
     * @param role Provider's role.
     * @return the type of a provider such as hosted or remote.
     */
    public String getProviderType(String name, String realm, String role) {
        String type = null;
        try {
            IDFFMetaManager mgr = getIDFFMetaManager();
            EntityConfigElement entityConfig = mgr.getEntityConfig(realm, name);
            if (entityConfig != null) {
                if(entityConfig.isHosted()){
                    type = IFSConstants.PROVIDER_HOSTED;
                }else{
                    type = IFSConstants.PROVIDER_REMOTE;
                }
            }
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModel.getProviderType", e);
        }
        return type;
    }
    
    /**
     * Returns IDP Descriptor
     *
     * @param name of Entity Descriptor.
     * @return the handler of IDP Descriptor
     */
    public IDPDescriptorType getIdentityProvider(String name, String realm) {
        IDPDescriptorType pdesc = null;
        try {
            IDFFMetaManager mgr = getIDFFMetaManager();
            pdesc = mgr.getIDPDescriptor(realm, name);
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModel.getIdentityProvider", e);
        }
        return pdesc;
    }
    
    /**
     * Returns SP Descriptor
     *
     * @param name of Entity Descriptor.
     * @return the handler of SP Descriptor
     */
    public SPDescriptorType getServiceProvider(String name, String realm) {
        SPDescriptorType pdesc = null;
        try {
            IDFFMetaManager mgr = getIDFFMetaManager();
            pdesc = mgr.getSPDescriptor(realm, name);
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModel.getServiceProvider", e);
        }
        return pdesc;
    }
    
    /**
     * Returns a map of IDP key/value pairs.
     *
     * @param entityName of entity descriptor.
     * @param realm where the entity exists.
     * @return map of IDP key/value pairs
     */
    public Map getEntityIDPDescriptor(String entityName, String realm) {
        Map map = new HashMap();
        IDPDescriptorType  pDesc = getIdentityProvider(entityName, realm);
        
        // common attributes
        map.put(ATTR_PROTOCOL_SUPPORT_ENUMERATION,
            convertListToSet(pDesc.getProtocolSupportEnumeration()));
        
        //communication URLs
        map.put(ATTR_SOAP_END_POINT,
            returnEmptySetIfValueIsNull(pDesc.getSoapEndpoint()));
        map.put(ATTR_SINGLE_SIGN_ON_SERVICE_URL,
            returnEmptySetIfValueIsNull(pDesc.getSingleSignOnServiceURL()));
        map.put(ATTR_SINGLE_LOGOUT_SERVICE_URL,
            returnEmptySetIfValueIsNull(pDesc.getSingleLogoutServiceURL()));
        map.put(ATTR_SINGLE_LOGOUT_SERVICE_RETURN_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getSingleLogoutServiceReturnURL()));
        map.put(ATTR_FEDERATION_TERMINATION_SERVICES_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getFederationTerminationServiceURL()));
        map.put(ATTR_FEDERATION_TERMINATION_SERVICE_RETURN_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getFederationTerminationServiceReturnURL()));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getRegisterNameIdentifierServiceURL()));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_RETURN_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getRegisterNameIdentifierServiceReturnURL()));
        
        // communication profiles        
        map.put(ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE,
            returnEmptySetIfValueIsNull(
            (String)pDesc.getFederationTerminationNotificationProtocolProfile().get(0)));
        map.put(ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE,
            returnEmptySetIfValueIsNull((String)pDesc.getSingleLogoutProtocolProfile().get(0)));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE,
            returnEmptySetIfValueIsNull((String)pDesc.getRegisterNameIdentifierProtocolProfile().get(0)));
        map.put(ATTR_SINGLE_SIGN_ON_PROTOCOL_PROFILE,
            returnEmptySetIfValueIsNull((String)pDesc.getSingleSignOnProtocolProfile().get(0)));
        
        return map;
    }
    
    /**
     * Returns a map of an SP entity descriptors key/value pairs.
     *
     * @param entityName name of entity descriptor.
     * @param realm where the entity exists.
     * @return map of SP key/value pairs
     */
    public Map getEntitySPDescriptor(String entityName, String realm) {
        Map map = new HashMap();
        SPDescriptorType  pDesc = getServiceProvider(entityName, realm);
        
        // common attributes
        map.put(ATTR_PROTOCOL_SUPPORT_ENUMERATION,
            convertListToSet(pDesc.getProtocolSupportEnumeration()));
        
        //communication URLs
        map.put(ATTR_SOAP_END_POINT,
            returnEmptySetIfValueIsNull(pDesc.getSoapEndpoint()));
        map.put(ATTR_SINGLE_LOGOUT_SERVICE_URL,
            returnEmptySetIfValueIsNull(pDesc.getSingleLogoutServiceURL()));
        map.put(ATTR_SINGLE_LOGOUT_SERVICE_RETURN_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getSingleLogoutServiceReturnURL()));
        map.put(ATTR_FEDERATION_TERMINATION_SERVICES_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getFederationTerminationServiceURL()));
        map.put(ATTR_FEDERATION_TERMINATION_SERVICE_RETURN_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getFederationTerminationServiceReturnURL()));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getRegisterNameIdentifierServiceURL()));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_RETURN_URL,
            returnEmptySetIfValueIsNull(
            pDesc.getRegisterNameIdentifierServiceReturnURL()));
        
        // communication profiles
        map.put(ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE,
            returnEmptySetIfValueIsNull(
            (String)pDesc.getFederationTerminationNotificationProtocolProfile().get(0)));
        map.put(ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE,
            returnEmptySetIfValueIsNull((String)pDesc.getSingleLogoutProtocolProfile().get(0)));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE,
            returnEmptySetIfValueIsNull((String)pDesc.getRegisterNameIdentifierProtocolProfile().get(0)));
        
        // only for Service Provider
        com.sun.identity.liberty.ws.meta.jaxb.SPDescriptorType.AssertionConsumerServiceURLType
            assertionType =
            (com.sun.identity.liberty.ws.meta.jaxb.SPDescriptorType.AssertionConsumerServiceURLType)
            ((List) pDesc.getAssertionConsumerServiceURL()).get(0);
        if (assertionType != null) {
            map.put(ATTR_ASSERTION_CUSTOMER_SERVICE_URIID,
                returnEmptySetIfValueIsNull(assertionType.getId()));
            map.put(ATTR_ASSERTION_CUSTOMER_SERVICE_URL,
                returnEmptySetIfValueIsNull(assertionType.getValue()));
            map.put(ATTR_ASSERTION_CUSTOMER_SERVICE_URL_AS_DEFAULT,
                returnEmptySetIfValueIsNull(assertionType.isIsDefault()));
        }else{
            map.put(ATTR_ASSERTION_CUSTOMER_SERVICE_URIID,
                Collections.EMPTY_SET);
            map.put(ATTR_ASSERTION_CUSTOMER_SERVICE_URL,
                Collections.EMPTY_SET);
            map.put(ATTR_ASSERTION_CUSTOMER_SERVICE_URL_AS_DEFAULT,
                Collections.EMPTY_SET);
        }
        
        map.put(ATTR_AUTHN_REQUESTS_SIGNED,
            returnEmptySetIfValueIsNull(pDesc.isAuthnRequestsSigned()));
        return map;
    }
    
    /**
     * Returns attributes values in extended metadata.
     *
     * @param entityName Name of Entity Descriptor.
     * @param role Role of provider. (idp or sp)
     * @param location Location of provider such as Hosted or Remote.
     * @return attributes values of provider.
     */
    public Map getEntityConfig(
        String entityName,
        String realm,
        String role,
        String location
    ) {
        IDFFMetaManager manager;
        Map map = new HashMap();
        Map tmpMap = new HashMap();
        try{
            manager = getIDFFMetaManager();
            String metaAlias = null;
            if (role.equals(IFSConstants.IDP)) {
                IDPDescriptorType  pDesc =
                    getIdentityProvider(entityName, realm);
                BaseConfigType  idpConfig=
                    manager.getIDPDescriptorConfig(realm, entityName);
                if (idpConfig != null){
                    map = IDFFMetaUtils.getAttributes(idpConfig) ;
                    metaAlias = idpConfig.getMetaAlias();
                } else {
                    createEntityConfig(entityName, realm, role, location);
                }
            } else if (role.equals(IFSConstants.SP)) {
                SPDescriptorType  pDesc =
                    getServiceProvider(entityName, realm);
                BaseConfigType spConfig =
                    manager.getSPDescriptorConfig(realm, entityName);
                if (spConfig != null) {
                    map = IDFFMetaUtils.getAttributes(spConfig) ;
                    metaAlias = spConfig.getMetaAlias();
                } else {
                    createEntityConfig(entityName, realm, role, location);
                }
            }
            
            Set entries = map.entrySet();
            Iterator iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                tmpMap.put((String)entry.getKey(),
                    returnEmptySetIfValueIsNull(
                    convertListToSet((List)entry.getValue())));
            }
            tmpMap.put(ATTR_PROVIDER_ALIAS,
                returnEmptySetIfValueIsNull(metaAlias));
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModelImpl",e);
        } catch (AMConsoleException e) {
            debug.error("IDFFEntityProviderModelImpl",e);
        } catch (JAXBException e) {
            debug.error("IDFFEntityProviderModelImpl",e);
        }
        return tmpMap;
    }
    
    /**
     * updateEntityDescriptor
     * Modifies a provider's standard metadata.
     *
     * @param entityName Name of Entity Descriptor.
     * @param realm Realm of entity
     * @param role Role of provider. (SP or IDP)
     * @param attrValues Map of attribute name to set of values.
     * @throws AMConsoleException if provider cannot be modified.
     */
    public void updateEntityDescriptor(
        String entityName,
        String realm,
        String role,
        Map attrValues
        ) throws AMConsoleException {
        if (role.equals(IFSConstants.SP)) {
            updateEntitySPDescriptor(realm, entityName, attrValues);
        } else {
            updateEntityIDPDescriptor(realm, entityName, attrValues);
        }
    }
    
    private void updateEntitySPDescriptor(
        String realm,
        String entityName,
        Map attrValues
        ) throws AMConsoleException {
        try{
            IDFFMetaManager idffManager = getIDFFMetaManager();
            EntityDescriptorElement entityDescriptor =
                idffManager.getEntityDescriptor(realm, entityName) ;
            SPDescriptorType pDesc = idffManager.getSPDescriptor(
                realm, entityName);
            
            //Protocol Support Enumeration
            pDesc.getProtocolSupportEnumeration().clear();
            pDesc.getProtocolSupportEnumeration().add(
                (String)AMAdminUtils.getValue(
                (Set)attrValues.get(ATTR_PROTOCOL_SUPPORT_ENUMERATION)));
            
            //communication URLs
            pDesc.setSoapEndpoint(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SOAP_END_POINT)));
            pDesc.setSingleLogoutServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_SERVICE_URL)));
            pDesc.setSingleLogoutServiceReturnURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_SERVICE_RETURN_URL)));
            pDesc.setFederationTerminationServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_FEDERATION_TERMINATION_SERVICES_URL)));
            pDesc.setFederationTerminationServiceReturnURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_FEDERATION_TERMINATION_SERVICE_RETURN_URL)));
            pDesc.setRegisterNameIdentifierServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_URL)));
            pDesc.setRegisterNameIdentifierServiceReturnURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_RETURN_URL)));
            
            // communication profiles
            pDesc.getFederationTerminationNotificationProtocolProfile().clear();
            pDesc.getFederationTerminationNotificationProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE)));
            int size = federationTerminationProfileList.size();            
            for (int i=0; i< size; i++) {                
                if(!federationTerminationProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE)))) 
                {            
                    pDesc.getFederationTerminationNotificationProtocolProfile().add(                
                     federationTerminationProfileList.get(i));
                }
            }
            
            pDesc.getSingleLogoutProtocolProfile().clear();
            pDesc.getSingleLogoutProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE)));
            size = singleLogoutProfileList.size();         
            for (int i=0; i< size; i++) {              
                if(!singleLogoutProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE))))
                {
                    pDesc.getSingleLogoutProtocolProfile().add(
                        singleLogoutProfileList.get(i));
                }
            }
            
            pDesc.getRegisterNameIdentifierProtocolProfile().clear();
            pDesc.getRegisterNameIdentifierProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE)));
            size = nameRegistrationProfileList.size();
            for (int i=0; i< size; i++) {              
                if(!nameRegistrationProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE))))
                {
                    pDesc.getRegisterNameIdentifierProtocolProfile().add(
                        nameRegistrationProfileList.get(i));
                }
            }
            
            // only for sp
            String id =  (String) AMAdminUtils.getValue(
                (Set)attrValues.get(ATTR_ASSERTION_CUSTOMER_SERVICE_URIID));
            String value =  (String) AMAdminUtils.getValue(
                (Set)attrValues.get(ATTR_ASSERTION_CUSTOMER_SERVICE_URL));
            String isDefault =  (String) AMAdminUtils.getValue(
                (Set)attrValues.get(
                ATTR_ASSERTION_CUSTOMER_SERVICE_URL_AS_DEFAULT));
            String authnRequestsSigned = (String) AMAdminUtils.getValue(
                (Set)attrValues.get(ATTR_AUTHN_REQUESTS_SIGNED));
            com.sun.identity.liberty.ws.meta.jaxb.ObjectFactory objFactory =
                new com.sun.identity.liberty.ws.meta.jaxb.ObjectFactory();
            com.sun.identity.liberty.ws.meta.jaxb.SPDescriptorType.AssertionConsumerServiceURLType
                assertionType =
                objFactory.createSPDescriptorTypeAssertionConsumerServiceURLType();
            assertionType.setId(id);
            assertionType.setValue(value);
            if (isDefault.equals("true")) {
                assertionType.setIsDefault(true);
            } else {
                assertionType.setIsDefault(false);
            }
            pDesc.getAssertionConsumerServiceURL().clear();
            pDesc.getAssertionConsumerServiceURL().add(assertionType);
            if (authnRequestsSigned.equals("true")){
                pDesc.setAuthnRequestsSigned(true);
            } else {
                pDesc.setAuthnRequestsSigned(false);
            }
            
            entityDescriptor.getSPDescriptor().clear();
            entityDescriptor.getSPDescriptor().add(pDesc);
            idffManager.setEntityDescriptor(realm, entityDescriptor);
        } catch (IDFFMetaException e) {
            debug.error("IDFFMetaException, updateEntitySPDescriptor");
        } catch (JAXBException e){
            debug.error("JAXBException, updateEntitySPDescriptor");
        }
    }
    
    private void updateEntityIDPDescriptor(
        String realm,
        String entityName,
        Map attrValues
        ) throws AMConsoleException {
        try{
            IDFFMetaManager idffManager = getIDFFMetaManager();
            EntityDescriptorElement entityDescriptor =
                idffManager.getEntityDescriptor(realm, entityName) ;
            IDPDescriptorType pDesc = idffManager.getIDPDescriptor(
                realm, entityName);
            
            //Protocol Support Enumeration
            pDesc.getProtocolSupportEnumeration().clear();
            pDesc.getProtocolSupportEnumeration().add(
                (String)AMAdminUtils.getValue(
                (Set)attrValues.get(ATTR_PROTOCOL_SUPPORT_ENUMERATION)));
            
            //communication URLs
            pDesc.setSoapEndpoint(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SOAP_END_POINT)));
            pDesc.setSingleSignOnServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_SIGN_ON_SERVICE_URL)));
            pDesc.setSingleLogoutServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_SERVICE_URL)));
            pDesc.setSingleLogoutServiceReturnURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_SERVICE_RETURN_URL)));
            pDesc.setFederationTerminationServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_FEDERATION_TERMINATION_SERVICES_URL)));
            pDesc.setFederationTerminationServiceReturnURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_FEDERATION_TERMINATION_SERVICE_RETURN_URL)));
            pDesc.setRegisterNameIdentifierServiceURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_URL)));
            pDesc.setRegisterNameIdentifierServiceReturnURL(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_SERVICE_RETURN_URL)));
                        
            // communication profiles
            pDesc.getFederationTerminationNotificationProtocolProfile().clear();
            pDesc.getFederationTerminationNotificationProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE)));
            int size = federationTerminationProfileList.size();           
            for (int i=0; i< size; i++) {                
                if(!federationTerminationProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_FEDERATION_TERMINATION_NOTIFICATION_PROTOCOL_PROFILE)))) 
                {            
                    pDesc.getFederationTerminationNotificationProtocolProfile().add(                
                     federationTerminationProfileList.get(i));
                }
            }

            pDesc.getSingleLogoutProtocolProfile().clear();
            pDesc.getSingleLogoutProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE)));
            size = singleLogoutProfileList.size();          
            for (int i=0; i< size; i++) {               
                if(!singleLogoutProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE))))
                {
                    pDesc.getSingleLogoutProtocolProfile().add(
                        singleLogoutProfileList.get(i));
                }
            }

            pDesc.getRegisterNameIdentifierProtocolProfile().clear();
            pDesc.getRegisterNameIdentifierProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE)));        
            size = nameRegistrationProfileList.size();
            for (int i=0; i< size; i++) {               
                if(!nameRegistrationProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE))))
                {
                    pDesc.getRegisterNameIdentifierProtocolProfile().add(
                        nameRegistrationProfileList.get(i));
                }
            }

            pDesc.getSingleSignOnProtocolProfile().clear();
            pDesc.getSingleSignOnProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_SIGN_ON_PROTOCOL_PROFILE)));         
            size = federationProfileList.size();
            for (int i=0; i< size; i++) {               
                if(!federationProfileList.get(i).equals(
                    (String)AMAdminUtils.getValue((Set)attrValues.get(
                    ATTR_SINGLE_SIGN_ON_PROTOCOL_PROFILE))))
                {
                    pDesc.getSingleSignOnProtocolProfile().add(
                        federationProfileList.get(i));
                }

            }
            
            entityDescriptor.getIDPDescriptor().clear();
            entityDescriptor.getIDPDescriptor().add(pDesc);
            idffManager.setEntityDescriptor(realm, entityDescriptor);
        } catch (IDFFMetaException e) {
            debug.error("IDFFMetaException , updateEntityIDPDescriptor", e);
        }
    }
    
    private void updateAttrInConfig(
        String realm,
        List configList,
        Map values,
        EntityConfigElement entityConfig,
        ObjectFactory objFactory,
        IDFFMetaManager idffMetaMgr
    ) throws AMConsoleException {
        try {
            for (Iterator iter = configList.iterator(); iter.hasNext();) {
                BaseConfigType bConfig = (BaseConfigType)iter.next();
                List list = bConfig.getAttribute();
                list.clear();
                // add all new attrubutes
                for (Iterator iter2 = values.keySet().iterator();
                    iter2.hasNext(); ) 
                {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter2.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)values.get(key));
                    list.add(atype);
                }
                idffMetaMgr.setEntityConfig(realm, entityConfig);
            }
        } catch (IDFFMetaException e) {
            throw new AMConsoleException(getErrorString(e));
        } catch(JAXBException e) {
            throw new AMConsoleException(getErrorString(e));
        }
    }
    
    /**
     * Modifies a provider's extended metadata.
     *
     * @param name of Entity Descriptor.
     * @param realm where entity exists.
     * @param role specifies if SP or IDP.
     * @param attrValues Map of attribute name to set of values.
     * @throws AMConsoleException if provider cannot be modified.
     * @throws JAXBException if provider cannot be retrieved.
     */
    public void updateEntityConfig(
        String name,
        String realm,
        String role,
        Map attrValues
    ) throws AMConsoleException, JAXBException {
        Map values = convertSetToListInMap(attrValues);
       
        try {
            IDFFMetaManager idffMetaMgr = getIDFFMetaManager();
            ObjectFactory objFactory = new ObjectFactory();
            // Check whether the entity id existed in the DS
            EntityDescriptorElement entityDesc =
                idffMetaMgr.getEntityDescriptor(realm, name);

            if (entityDesc == null) {
                throw new AMConsoleException("invalid.entity.name");
            }
            
            EntityConfigElement entityConfig =
                idffMetaMgr.getEntityConfig(realm, name);
            
            if (entityConfig == null) {
                throw new AMConsoleException("invalid.config.element");
            } else {
                // update the sp and idp entity config
                if (role.equals(IFSConstants.SP)) {
                    List spConfigList = entityConfig.getSPDescriptorConfig();
                    updateAttrInConfig(
                        realm,
                        spConfigList,
                        values,
                        entityConfig,
                        objFactory,
                        idffMetaMgr);
                } else if (role.equals(IFSConstants.IDP)) {
                    List idpConfigList = entityConfig.getIDPDescriptorConfig();
                    updateAttrInConfig(
                        realm,
                        idpConfigList,
                        values,
                        entityConfig,
                        objFactory,
                        idffMetaMgr);
                } else {
                    debug.error("updateEntityConfig()," +
                        "never get here, neither idp nor sp");
                }
            }
            
        } catch (IDFFMetaException e) {
            throw new AMConsoleException(getErrorString(e));
        }
    }
    
    public void createEntityConfig(
        String entityName,
        String realm,
        String role,
        String location
    ) throws AMConsoleException, JAXBException {
        try {
            IDFFMetaManager idffMetaMgr = getIDFFMetaManager();
            ObjectFactory objFactory = new ObjectFactory();
            // Check whether the entity id existed in the DS
            EntityDescriptorElement entityDesc =
                idffMetaMgr.getEntityDescriptor(realm, entityName);
            
            if (entityDesc == null) {
                throw new AMConsoleException("invalid.entity.name");
            }
            EntityConfigElement entityConfig =
                idffMetaMgr.getEntityConfig(realm, entityName);
            if (entityConfig == null) {
                entityConfig =
                    objFactory.createEntityConfigElement();
                // add to entityConfig
                entityConfig.setEntityID(entityName);
                if (location.equals("remote")) {
                    entityConfig.setHosted(false);
                } else {
                    entityConfig.setHosted(true);
                }
            }
            
            // create entity config and add the attribute
            BaseConfigType baseCfgType = null;
            
            // Decide which role EntityDescriptorElement includes
            // It could have one sp and one idp.
            if ((role.equals(IFSConstants.SP)) &&
                (IDFFMetaUtils.getSPDescriptor(entityDesc) != null)) 
            {
                baseCfgType = objFactory.createSPDescriptorConfigElement();
                
                for (Iterator iter = extendedMetaMap.keySet().iterator();
                iter.hasNext(); ) {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)extendedMetaMap.get(key));
                    baseCfgType.getAttribute().add(atype);
                }
                
                for (Iterator iter = extendedMetaSpMap.keySet().iterator();
                iter.hasNext(); ) {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)extendedMetaSpMap.get(key));
                    baseCfgType.getAttribute().add(atype);
                }
                entityConfig.getSPDescriptorConfig().add(baseCfgType);
            } else if ((role.equals(IFSConstants.IDP)) &&
                (IDFFMetaUtils.getIDPDescriptor(entityDesc) != null)) 
            {
                baseCfgType = objFactory.createIDPDescriptorConfigElement();
                
                for (Iterator iter = extendedMetaMap.keySet().iterator();
                    iter.hasNext(); ) 
                {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)extendedMetaMap.get(key));
                    baseCfgType.getAttribute().add(atype);
                }
                
                for (Iterator iter = extendedMetaIdpMap.keySet().iterator();
                    iter.hasNext(); ) 
                {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)extendedMetaIdpMap.get(key));
                    baseCfgType.getAttribute().add(atype);
                }
                entityConfig.getIDPDescriptorConfig().add(baseCfgType);
            }
            idffMetaMgr.setEntityConfig(realm, entityConfig);
        } catch (IDFFMetaException e){
            debug.error("IDFFEntityProviderModel", e);
        }
    }
    
    protected IDFFMetaManager getIDFFMetaManager() throws IDFFMetaException {
        if (metaManager == null) {
            metaManager = new IDFFMetaManager(getUserSSOToken());
        }
        return metaManager;
    }

    /**
     * Returns true if entity descriptor is an affiliate.
     *
     * @param name of entity descriptor.
     * @return true if entity descriptor is an affiliate.
     * @throws AMConsoleException if entity cannot be retrieved.
     */
    public boolean isAffiliate(String realm, String name) 
        throws AMConsoleException 
    {
        boolean isAffiliate = false;
        try {
            IDFFMetaManager idffManager = getIDFFMetaManager();
            AffiliationDescriptorType ad = (AffiliationDescriptorType)
            idffManager.getAffiliationDescriptor(realm, name);
            if (ad != null) {
                isAffiliate = true;
            }
        } catch (IDFFMetaException  e) {            
            debug.warning("IDFFEntityProviderModel.isAffiliate", e);
            throw new AMConsoleException(getErrorString(e));
        }
        
        return isAffiliate;
    }
    
    /**
     * Returns affiliate profile attribute values.
     *
     * @param realm the realm in which the entity resides.
     * @param name of Entity Descriptor.
     * @return affiliate profile attribute values.
     * @throws AMConsoleException if attribute values cannot be obtained.
     */
    public Map getAffiliateProfileAttributeValues(String realm, String name)
        throws AMConsoleException
    {
        Map values = new HashMap();
        try {          
            IDFFMetaManager idffManager = getIDFFMetaManager();
            AffiliationDescriptorType aDesc = (AffiliationDescriptorType)
                idffManager.getAffiliationDescriptor(realm, name);
            
            if (aDesc != null) {
                values.put(ATTR_AFFILIATE_ID,
                    returnEmptySetIfValueIsNull(aDesc.getAffiliationID()));
                
                values.put(ATTR_AFFILIATE_OWNER_ID,
                    returnEmptySetIfValueIsNull(aDesc.getAffiliationOwnerID()));
                
                //TBD : common attributes which may be added here later
                /*ATTR_AFFILIATE_VALID_UNTIL,
                  ATTR_AFFILIATE_CACHE_DURATION
                  ATTR_AFFILIATE_SIGNING_KEY_ALIAS
                  ATTR_AFFILIATE_ENCRYPTION_KEY_ALIAS
                  ATTR_AFFILIATE_ENCRYPTION_KEY_SIZE
                  ATTR_AFFILIATE_ENCRYPTION_KEY_METHOD
                 */                
                
            } else {
                values.put(ATTR_AFFILIATE_ID, Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_OWNER_ID, Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_VALID_UNTIL, Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_CACHE_DURATION,
                    Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_SIGNING_KEY_ALIAS, 
                    Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_ENCRYPTION_KEY_ALIAS, 
                    Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_ENCRYPTION_KEY_SIZE, 
                    Collections.EMPTY_SET);
                values.put(ATTR_AFFILIATE_ENCRYPTION_KEY_METHOD, 
                    Collections.EMPTY_SET);                
            }         
        } catch (IDFFMetaException e) {
            debug.warning(
               "IDFFEntityProviderModel.getAffiliateProfileAttributeValues", e);
            throw new AMConsoleException(e.getMessage());
        }                
        return (values != null) ? values : Collections.EMPTY_MAP;
    }
    
    /**
     * Modifies affiliate profile.
     *
     * @param realm the realm in which the entity resides.
     * @param name Name of entity descriptor.
     * @param values Map of attribute name/value pairs.
     * @param members Set of affiliate members
     * @throws AMConsoleException if profile cannot be modified.
     */
    public void updateAffiliateProfile(
        String realm, 
        String name, 
        Map values, 
        Set members
    ) throws AMConsoleException{
        try {
            IDFFMetaManager idffManager = getIDFFMetaManager();
            EntityDescriptorElement entityDescriptor =
                idffManager.getEntityDescriptor(realm, name) ;
            AffiliationDescriptorType aDesc =
                entityDescriptor.getAffiliationDescriptor();
            
            aDesc.setAffiliationID(
                (String)AMAdminUtils.getValue((Set)values.get(
                ATTR_AFFILIATE_ID)));
            
            aDesc.setAffiliationOwnerID(
                (String)AMAdminUtils.getValue((Set)values.get(
                ATTR_AFFILIATE_OWNER_ID)));
            
            //TBD : common attributes which may be added here later
            /*ATTR_AFFILIATE_VALID_UNTIL,
             ATTR_AFFILIATE_CACHE_DURATION
             ATTR_AFFILIATE_SIGNING_KEY_ALIAS
             ATTR_AFFILIATE_ENCRYPTION_KEY_ALIAS
             ATTR_AFFILIATE_ENCRYPTION_KEY_SIZE
             ATTR_AFFILIATE_ENCRYPTION_KEY_METHOD
             */
            
            // add affilliate members
            Iterator it = members.iterator();
            while (it.hasNext()) {
                String newMember = (String)it.next();
                aDesc.getAffiliateMember().add(newMember);
            }
            
            entityDescriptor.setAffiliationDescriptor(aDesc);
            idffManager.setEntityDescriptor(realm, entityDescriptor);
            
        } catch (IDFFMetaException e) {
            debug.warning("IDFFEntityProviderModel.updateAffiliateProfile", e);
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    /**
     * Returns a <code>Set</code> of entity descriptor names.
     * 
     * @param realm the realm in which the entity resides.
     * @return the IDFF entity descriptor 
     * @throws AMConsoleException 
     */
    public Set getAllEntityDescriptorNames(String realm)
        throws AMConsoleException 
    {
        Set entitySet = null;
        try {
            IDFFMetaManager idffManager = getIDFFMetaManager();
            entitySet = idffManager.getAllEntities(realm);
        } catch (IDFFMetaException e) {
            debug.warning(
                "IDFFEntityProviderModel.getAllEntityDescriptorNames", e);
            throw new AMConsoleException(e.getMessage());
        }        
        return (entitySet != null) ? entitySet : Collections.EMPTY_SET;
    }
        
    /**
     * @return a Set of all the idff Affiliate entities.
     */
    public Set getAllAffiliateEntityDescriptorNames(String realm)
        throws AMConsoleException 
    {
        Set entitySet = new HashSet();
        try {
            IDFFMetaManager idffManager = getIDFFMetaManager();
            Set allEntities = idffManager.getAllEntities(realm);
            Iterator it = allEntities.iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                if (isAffiliate(realm, name)) {
                    entitySet.add(name);
                }
            }
        } catch (IDFFMetaException e) {
            debug.warning(
             "IDFFEntityProviderModel.getAllAffiliateEntityDescriptorNames", e);
            throw new AMConsoleException(e.getMessage());
        }
        return (entitySet != null) ? entitySet : Collections.EMPTY_SET;
    }
    
    /** 
     * Returns a Set of all the affiliate members
     *
     * @param realm the realm in which the entity resides.
     * @param entityID name of the Entity Descriptor.
     * @throws AMConsoleException if values cannot be obtained.
     */
    public Set getAllAffiliateMembers(String realm, String entityID)
        throws AMConsoleException 
    {
        Set memberSet = null;
        try {
            IDFFMetaManager idffManager = getIDFFMetaManager();
            AffiliationDescriptorType aDesc = (AffiliationDescriptorType)
                idffManager.getAffiliationDescriptor(realm, entityID);
            memberSet = convertListToSet(aDesc.getAffiliateMember());
        } catch (IDFFMetaException e) {
            debug.warning("IDFFEntityProviderModel.getAllAffiliateMembers", e);
            throw new AMConsoleException(e.getMessage());
        }
        
        return (memberSet != null) ? memberSet : Collections.EMPTY_SET;
    }
  /*  
    private Set returnEmptySetIfValueIsNull(boolean b) {
        Set set = new HashSet(2);
        set.add(Boolean.toString(b));
        return set;
    }
    
    private Set returnEmptySetIfValueIsNull(String str) {
        Set set = Collections.EMPTY_SET;
        if (str != null) {
            set = new HashSet(2);
            set.add(str);
        }
        return set;
    }
        
    private Set returnEmptySetIfValueIsNull(Set set) {
        return (set != null) ? set : Collections.EMPTY_SET;
    }
    
    private List returnEmptyListIfValueIsNull(String str) {
        List list = Collections.EMPTY_LIST;
        if (str != null) {
            list = new ArrayList(2);
            list.add(str);
        }
        return list;
    }
    
  
    private List returnEmptyListIfValueIsNull(List list) {
        return (list != null) ? list : Collections.EMPTY_LIST;
    }
    
    private Set convertListToSet(List list) {
        Set s = new HashSet();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            s.add(it.next());
        }
        return s;
    }
    
    private List convertSetToList(Set set) {
        List list = new ArrayList();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    
    private Map convertSetToListInMap(Map map) {
        Map tmpMap = new HashMap();
        Set entries = map.entrySet();
        Iterator iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            tmpMap.put((String)entry.getKey(),
                returnEmptyListIfValueIsNull(
                convertSetToList((Set)entry.getValue())));
        }
        return tmpMap;
    }
    */
}
