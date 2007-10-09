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
 * $Id: SAMLv2ModelImpl.java,v 1.1 2007-10-09 01:11:12 asyhuang Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMAdminUtils;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;
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

import javax.xml.bind.JAXBException;

public class SAMLv2ModelImpl extends AMModelBase implements SAMLv2Model {
    
    private SAML2MetaManager metaManager;
    
    public SAMLv2ModelImpl(HttpServletRequest req, Map map) {
        super(req, map);
    }
    
    /**
     * Returns a Map of PEP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PEP descriptor data.
     */
    public Map getPEPDescriptor(String realm, String entityName){
        Map data = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryDescriptorElement descriptor =
                saml2Manager.getPolicyEnforcementPointDescriptor(
                realm, entityName);
            
            if (descriptor != null) {
                data = new HashMap(10);
                
                //ProtocolSupportEnum
                data.put(ATTR_TXT_PROTOCOL_SUPPORT_ENUM,
                    returnEmptySetIfValueIsNull(
                    descriptor.getProtocolSupportEnumeration()));
                
                if(descriptor.isWantAssertionsSigned())
                    data.put(ATTR_WANT_ASSERTION_SIGNED, "true");
                else{
                    data.put(ATTR_WANT_ASSERTION_SIGNED, "false");
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPEPDescriptor : "  +
                getErrorString(e));
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a Map of PDP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PDP descriptor data.
     */
    
    public Map getPDPDescriptor(String realm, String entityName){
        Map data = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPDescriptorElement descriptor =
                saml2Manager.getPolicyDecisionPointDescriptor(
                realm, 
                entityName);
            
            if (descriptor != null) {
                data = new HashMap(10);
                
                //ProtocolSupportEnum
                data.put(ATTR_TXT_PROTOCOL_SUPPORT_ENUM,
                    returnEmptySetIfValueIsNull(
                    descriptor.getProtocolSupportEnumeration()));
                
                List authzServiceList = descriptor.getXACMLAuthzService();
                if ( authzServiceList.size() != 0) {
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
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPDPDescriptor : "  +
                getErrorString(e));
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    /**
     * Returns a Map of PEP Config data. (Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PEP config data.
     */
    public Map getPEPConfig(String realm, String entityName, String location){
        Map data = null;
        List configList = null;
        String metaAlias = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryConfigElement cfgElement =
                saml2Manager.getPolicyEnforcementPointConfig(realm, entityName);
            
            if (cfgElement != null){
                data = new HashMap();
                configList = cfgElement.getAttribute() ;
                metaAlias = cfgElement.getMetaAlias();
                int size = configList.size();
                for (int i=0; i< size; i++) {
                    AttributeType atype = (AttributeType) configList.get(i);
                    String name = atype.getName();
                    java.util.List value = atype.getValue();
                    debug.error("getPEPConfig :  "+name+"="+value);
                    data.put(atype.getName(),
                        returnEmptySetIfValueIsNull(atype.getValue()));
                }
                data.put("metaAlias", metaAlias);
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPEPConfig : "  +
                getErrorString(e));
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns a Map of PDP Config data. (Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @return key-value pair Map of PPP config data.
     */
    public Map getPDPConfig(String realm, String entityName, String location){
        Map data = null;
        List configList = null;
        String metaAlias = null;
        try {
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPConfigElement cfgElement =
                saml2Manager.getPolicyDecisionPointConfig(realm, entityName);
            if (cfgElement != null){
                data = new HashMap();
                configList = cfgElement.getAttribute() ;
                metaAlias = cfgElement.getMetaAlias();
                int size = configList.size();
                for (int i=0; i< size; i++) {
                    AttributeType atype = (AttributeType) configList.get(i);
                    String name = atype.getName();
                    java.util.List value = atype.getValue();
                    data.put(atype.getName(),
                        returnEmptySetIfValueIsNull(atype.getValue()));
                }
                data.put("metaAlias", metaAlias);
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.getPDPConfig : "  +
                getErrorString(e));
        }
        return (data != null) ? data : Collections.EMPTY_MAP;
    }
    
    /**
     * save data for PDP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PDP standed data.
     * throws AMConsoleException if there is an error
     */
    public void updatePDPDescriptor(
        String realm,
        String entityName,
        Map attrValues
        ) throws AMConsoleException {
        try{
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            EntityDescriptorElement entityDescriptor =
                saml2Manager.getEntityDescriptor(realm, entityName) ;
            XACMLPDPDescriptorElement pdpDescriptor =
                saml2Manager.getPolicyDecisionPointDescriptor(
                realm, 
                entityName);
            
            if (pdpDescriptor != null) {
                List authzServiceList = pdpDescriptor.getXACMLAuthzService();
                if ( authzServiceList.size() != 0) {
                    XACMLAuthzServiceElement authzService =
                        (XACMLAuthzServiceElement) authzServiceList.get(0);
                    authzService.setLocation((String)AMAdminUtils.getValue(
                        (Set)attrValues.get(
                        ATTR_XACML_AUTHZ_SERVICE_LOCATION)));
                }
            }
        } catch (SAML2MetaException e) {
            debug.warning("SAMLv2ModelImpl.updatePDPDescriptor : " +
                getErrorString(e));
        }
    }
    
    /**
     * save data for PDP Config data.(Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     * @param attrValues key-value pair Map of PDP extended config.
     */
    public void updatePDPConfig(
        String realm,
        String entityName,
        String location,
        Map attrValues)
        throws AMConsoleException, JAXBException {
        Map values = convertSetToListInMap(attrValues);
        try {
            ObjectFactory objFactory = new ObjectFactory();
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLPDPConfigElement pdpEntityConfig =
                saml2Manager.getPolicyDecisionPointConfig(realm, entityName);
            
            if (pdpEntityConfig == null) {
                throw new AMConsoleException("updatePDPConfig : " +
                    "invalid XACMLPDPConfigElement : realm = " + realm +
                    ", entityName = "+entityName);
            } else {
                List list = pdpEntityConfig.getAttribute();
                list.clear();
                for (Iterator iter = values.keySet().iterator();
                iter.hasNext(); ) {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)values.get(key));
                    list.add(atype);
                }
            }
        } catch (SAML2MetaException e) {
            throw new AMConsoleException("SAMLv2ModelImpl.updatePDPConfig : " +
                getErrorString(e));
        }
        
    }
    
    
    /**
     * save data for PEP descriptor data.(Standard Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param attrValues key-value pair Map of PEP descriptor data.
     * throws AMConsoleException if there is an error
     */
    public void updatePEPDescriptor(
        String realm,
        String entityName,
        Map attrValues
        ) throws AMConsoleException {
        // TBD : currently, there is nothing to save
    }
    
    /**
     * save data for PEP Config data.(Extended Metadata)
     *
     * @param realm realm of Entity
     * @param entityName entity name of Entity Descriptor.
     * @param location entity is remote or hosted
     * @param attrValues key-value pair Map of PEP extended config.
     */
    public void updatePEPConfig(
        String realm,
        String entityName,
        String location,
        Map attrValues)
        throws AMConsoleException, JAXBException {
        Map values = convertSetToListInMap(attrValues);
        try {
            ObjectFactory objFactory = new ObjectFactory();
            SAML2MetaManager saml2Manager = getSAML2MetaManager();
            XACMLAuthzDecisionQueryConfigElement pepEntityConfig =
                saml2Manager.getPolicyEnforcementPointConfig(realm, entityName);
            
            if (pepEntityConfig == null) {
                throw new AMConsoleException(
                    "SAMLv2ModelImpl.updatePEPConfig : " +
                    "invalid XACMLPDPConfigElement : realm = " + realm +
                    ", entityName = "+entityName);
            } else {
                List list = pepEntityConfig.getAttribute();
                list.clear();
                for (Iterator iter = values.keySet().iterator();
                iter.hasNext(); ) {
                    AttributeType atype = objFactory.createAttributeType();
                    String key = (String)iter.next();
                    atype.setName(key);
                    atype.getValue().addAll((List)values.get(key));
                    list.add(atype);
                }
            }
        } catch (SAML2MetaException e) {
            throw new AMConsoleException("SAMLv2ModelImpl.updatePEPConfig : " +
                getErrorString(e));
        }
    }
    
    protected SAML2MetaManager getSAML2MetaManager()
    throws SAML2MetaException {
        if (metaManager == null) {
            metaManager = new SAML2MetaManager();
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
    
    private Set returnEmptySetIfValueIsNull(List l) {
        Set set = new HashSet();
        int size = l.size();
        for (int i=0;i<size;i++){
            set.add(l.get(i));
        }
        return set;
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
    
    private Map convertSetToListInMap(Map map){
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
