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
 * $Id: IDFFEntityProviderModelImpl.java,v 1.1 2007-08-01 22:13:16 asyhuang Exp $
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
    extends AMModelBase
    implements IDFFEntityProviderModel 
{
    
    private IDFFMetaManager metaManager;
    
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
    public String getProviderType(String name, String role) {
        String type = null;
        try {
            IDFFMetaManager mgr = getIDFFMetaManager();
            EntityConfigElement entityConfig = mgr.getEntityConfig(name);
            if (entityConfig != null) {
                if(entityConfig.isHosted()){
                    type = IFSConstants.PROVIDER_HOSTED;
                }else{
                    type = IFSConstants.PROVIDER_REMOTE;
                }
            }
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModelImpl.getProviderType", e);
        }
        return type;
    }
    
    /**
     * Returns IDP Descriptor
     *
     * @param name of Entity Descriptor.
     * @return the handler of IDP Descriptor
     */
    public IDPDescriptorType getIdentityProvider(String name) {
        IDPDescriptorType pdesc = null;
        try {
            IDFFMetaManager mgr = getIDFFMetaManager();
            pdesc = mgr.getIDPDescriptor(name);
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModelImpl.getIdentityProvider", e);
        }
        return pdesc;
    }
    
    /**
     * Returns SP Descriptor
     *
     * @param name of Entity Descriptor.
     * @return the handler of SP Descriptor
     */
    public SPDescriptorType getServiceProvider(String name) {
        SPDescriptorType pdesc = null;
        try {
            IDFFMetaManager mgr = getIDFFMetaManager();
            pdesc = mgr.getSPDescriptor(name);
        } catch (IDFFMetaException e) {
            debug.error("IDFFEntityProviderModelImpl.getServiceProvider", e);
        }
        return pdesc;
    }
    /**
     * Returns a map of IDP key/value pairs
     *
     * @param name of Entity Descriptor.
     * @return map of IDP key/value pairs
     */
    public Map getEntityIDPDescriptor(String entityDescriptorName) {
        Map map = new HashMap();
        IDPDescriptorType  pDesc = getIdentityProvider(entityDescriptorName);
        
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
            convertListToSet(
            pDesc.getFederationTerminationNotificationProtocolProfile()));
        map.put(ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE,
            convertListToSet(pDesc.getSingleLogoutProtocolProfile()));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE,
            convertListToSet(pDesc.getRegisterNameIdentifierProtocolProfile()));
        map.put(ATTR_SINGLE_SIGN_ON_PROTOCOL_PROFILE,
            convertListToSet(pDesc.getSingleSignOnProtocolProfile()));
        
        return map;
    }
    
    /**
     * Returns a map of SP key/value pairs
     *
     * @param name of Entity Descriptor.
     * @return map of SP key/value pairs
     */
    public Map getEntitySPDescriptor(String entityDescriptorName) {
        Map map = new HashMap();
        SPDescriptorType  pDesc = getServiceProvider(entityDescriptorName);
        
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
            convertListToSet(
            pDesc.getFederationTerminationNotificationProtocolProfile()));
        map.put(ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE,
            convertListToSet(pDesc.getSingleLogoutProtocolProfile()));
        map.put(ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE,
            convertListToSet(pDesc.getRegisterNameIdentifierProtocolProfile()));
        
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
     * @param entityDescriptorName Name of Entity Descriptor.
     * @param role Role of provider. (idp or sp)
     * @param type Type of provider such as Hosted or Remote.
     * @return attributes values of provider.
     */
    public Map getEntityConfig (
        String entityDescriptorName,
        String role,
        String type) 
    {
        IDFFMetaManager manager;
        Map map = new HashMap();
        Map tmpMap = new HashMap();
        try{
            manager = getIDFFMetaManager();
            String metaAlias = null;
            if (role.equals(IFSConstants.IDP)) {
                IDPDescriptorType  pDesc =
                    getIdentityProvider(entityDescriptorName);
                BaseConfigType  idpConfig=
                    manager.getIDPDescriptorConfig(entityDescriptorName);
                map = IDFFMetaUtils.getAttributes(idpConfig) ;
                metaAlias = idpConfig.getMetaAlias();
            } else if (role.equals(IFSConstants.SP)) {
                SPDescriptorType  pDesc =
                    getServiceProvider(entityDescriptorName);
                BaseConfigType spConfig =
                    manager.getSPDescriptorConfig(entityDescriptorName);
                map = IDFFMetaUtils.getAttributes(spConfig) ;
                metaAlias = spConfig.getMetaAlias();
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
        }
        return tmpMap;
    }
    
    
    /**
     * updateEntityDescriptor
     * Modifies a provider's standard metadata.
     *
     * @param entityDescriptorName Name of Entity Descriptor.
     * @param role Role of provider. (SP or IDP)
     * @param attrValues Map of attribute name to set of values.
     * @throws AMConsoleException if provider cannot be modified.
     */
    
    
    public void updateEntityDescriptor (
        String entityName,
        String role,
        Map attrValues
        ) throws AMConsoleException 
    {
        if (role.equals(IFSConstants.SP)) {
            updateEntitySPDescriptor(entityName, attrValues);
        } else {
            updateEntityIDPDescriptor(entityName, attrValues);
        }
    }
    
    private void updateEntitySPDescriptor (
        String entityName,
        Map attrValues
        ) throws AMConsoleException 
    {
        try{
            IDFFMetaManager idffManager = getIDFFMetaManager();
            EntityDescriptorElement entityDescriptor =
                idffManager.getEntityDescriptor(entityName) ;
            SPDescriptorType pDesc = idffManager.getSPDescriptor(entityName);
            
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
            pDesc.getSingleLogoutProtocolProfile().clear();
            pDesc.getSingleLogoutProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE)));
            pDesc.getRegisterNameIdentifierProtocolProfile().clear();
            pDesc.getRegisterNameIdentifierProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE)));
            
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
            idffManager.setEntityDescriptor(entityDescriptor);
        } catch (IDFFMetaException e) {
            debug.error("IDFFMetaException, updateEntitySPDescriptor");
        } catch (JAXBException e){
            debug.error("JAXBException, updateEntitySPDescriptor");
        }
        
    }
    
    private void updateEntityIDPDescriptor (
        String entityName,
        Map attrValues
        ) throws AMConsoleException 
    {
        try{
            IDFFMetaManager idffManager = getIDFFMetaManager();
            EntityDescriptorElement entityDescriptor =
                idffManager.getEntityDescriptor(entityName) ;
            IDPDescriptorType pDesc = idffManager.getIDPDescriptor(entityName);
            
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
            pDesc.getSingleLogoutProtocolProfile().clear();
            pDesc.getSingleLogoutProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_LOGOUT_PROTOCOL_PROFILE)));
            pDesc.getRegisterNameIdentifierProtocolProfile().clear();
            pDesc.getRegisterNameIdentifierProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_REGISTRATION_NAME_IDENTIFIER_PROFILE_PROFILE)));
            pDesc.getSingleSignOnProtocolProfile().clear();
            pDesc.getSingleSignOnProtocolProfile().add(
                (String)AMAdminUtils.getValue((Set)attrValues.get(
                ATTR_SINGLE_SIGN_ON_PROTOCOL_PROFILE)));
            
            entityDescriptor.getIDPDescriptor().clear();
            entityDescriptor.getIDPDescriptor().add(pDesc);
            idffManager.setEntityDescriptor(entityDescriptor);
        } catch (IDFFMetaException e) {
            debug.error("IDFFMetaException , updateEntityIDPDescriptor");
        }
        
    }
    
    private void updateAttrInConfig(List configList,Map values,
        EntityConfigElement entityConfig,
        ObjectFactory objFactory,
        IDFFMetaManager idffMetaMgr)
        throws AMConsoleException 
    {
        try{
            for (Iterator iter = configList.iterator(); iter.hasNext();) {
                BaseConfigType bConfig = (BaseConfigType)iter.next();
                List list = bConfig.getAttribute();
                list.clear();
                // add all new attrubutes
                for (Iterator iter2 = values.keySet().iterator();
                iter2.hasNext(); ) {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter2.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)values.get(key));
                    list.add(atype);
                }
                idffMetaMgr.setEntityConfig(entityConfig);
            }
        } catch (IDFFMetaException e) {
            throw new AMConsoleException(getErrorString(e));
        } catch(JAXBException e) {
            throw new AMConsoleException(getErrorString(e));
        }
    }
    
    /**
     * updateEntityConfig
     * Modifies a provider's extended metadata.
     *
     * @param entityDescriptorName Name of Entity Descriptor.
     * @param role Role of provider. (SP or IDP)
     * @param attrValues Map of attribute name to set of values.
     * @throws AMConsoleException if provider cannot be modified.
     */
    
    public void updateEntityConfig(
        String entityID,
        String role,
        Map attrValues)
        throws AMConsoleException, JAXBException 
    {
        String classMethod = "IDFFEntityProviderModelImpl.updateEntityConfig:";
        Map values = convertSetToListInMap(attrValues);
        try {
            IDFFMetaManager idffMetaMgr = getIDFFMetaManager();
            ObjectFactory objFactory = new ObjectFactory();
            // Check whether the entity id existed in the DS
            EntityDescriptorElement entityDesc =
                idffMetaMgr.getEntityDescriptor(entityID);
            
            if (entityDesc == null) {
                debug.error(classMethod +" No such entity: " + entityID);
                //String[] data = {entityID};
                throw new AMConsoleException("invalidEntityID");
            }
            EntityConfigElement entityConfig =
                idffMetaMgr.getEntityConfig(entityID);
            if (entityConfig == null) {
                // TBD: create extended metadata object (entityConfig)
                // for new remote entity.
                // create entity config and add the attributes
                BaseConfigType baseCfgType = null;

                // add to entityConfig
                entityConfig = objFactory.createEntityConfigElement();
                entityConfig.setEntityID(entityID);
                entityConfig.setHosted(false);

                // Decide which role EntityDescriptorElement includes
                // It could have one sp and one idp.
                if ((role.equals(IFSConstants.SP)) &&
                    (IDFFMetaUtils.getSPDescriptor(entityDesc) != null)) {
                    baseCfgType = objFactory.createSPDescriptorConfigElement();
                    //add attribute
                    for (Iterator iter = values.keySet().iterator(); iter.hasNext(); ) {
                        AttributeType atype = objFactory.createAttributeType();
                        String key = (String)iter.next();
                        atype.setName(key);
                        atype.getValue().addAll((List)values.get(key));
                        baseCfgType.getAttribute().add(atype);
                    }
                    entityConfig.getSPDescriptorConfig().add(baseCfgType);
                } else if ((role.equals(IFSConstants.IDP)) &&
                    (IDFFMetaUtils.getIDPDescriptor(entityDesc) != null)) {
                    baseCfgType = objFactory.createIDPDescriptorConfigElement();
                    //add attribute
                    for (Iterator iter = values.keySet().iterator(); iter.hasNext(); ) {
                        AttributeType atype = objFactory.createAttributeType();
                        String key = (String)iter.next();
                        atype.setName(key);
                        atype.getValue().addAll((List)values.get(key));
                        baseCfgType.getAttribute().add(atype);
                    }
                    entityConfig.getIDPDescriptorConfig().add(baseCfgType);
                }
                idffMetaMgr.setEntityConfig(entityConfig);                
            } else {
                // update the sp and idp entity config
                if (role.equals(IFSConstants.SP)) {
                    List spConfigList = entityConfig.getSPDescriptorConfig();
                    updateAttrInConfig(
                        spConfigList,
                        values,
                        entityConfig,
                        objFactory,
                        idffMetaMgr);
                } else if (role.equals(IFSConstants.IDP)) {
                    List idpConfigList = entityConfig.getIDPDescriptorConfig();
                    updateAttrInConfig(
                        idpConfigList,
                        values,
                        entityConfig,
                        objFactory,
                        idffMetaMgr);
                }
            }
        } catch (IDFFMetaException e) {
            throw new AMConsoleException(classMethod + getErrorString(e));
        }
    }
    
    protected IDFFMetaManager getIDFFMetaManager()
        throws IDFFMetaException 
    {
        if (metaManager == null) {
            metaManager = new IDFFMetaManager(getUserSSOToken());
        }
        return metaManager;
    }
    
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
    
    private List returnEmptyListIfValueIsNull(List list){
        return (list != null) ? list : Collections.EMPTY_LIST;
    }
    
    public Set convertListToSet(List list) {
        Set s = new HashSet();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            s.add(it.next());
        }
        return s;
    }
    
    public List convertSetToList(Set set) {
        List list = new ArrayList();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    
    public Map convertSetToListInMap(Map map){
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
}
