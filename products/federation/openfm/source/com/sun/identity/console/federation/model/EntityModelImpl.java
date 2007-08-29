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
 * $Id: EntityModelImpl.java,v 1.6 2007-08-29 05:51:17 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaException;
import com.sun.identity.liberty.ws.meta.jaxb.AffiliationDescriptorType;
import com.sun.identity.liberty.ws.meta.jaxb.ObjectFactory;

import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;

import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaException;
import com.sun.identity.wsfederation.jaxb.wsfederation.FederationElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.UriNamedClaimTypesOfferedElement;
import com.sun.identity.wsfederation.jaxb.wsfederation.TokenIssuerEndpointElement;

public class EntityModelImpl extends AMModelBase implements EntityModel {
    
    private Set realms = null;
    
    public EntityModelImpl(HttpServletRequest req, Map map) {
        super(req, map);
        try {
            realms = getRealmNames(getStartDN(), "*");
        } catch (AMConsoleException a) {
            debug.warning("EntityModel problem getting realm names");
            realms = Collections.EMPTY_SET;
        }
    }
    
    public Map getEntities()
        throws AMConsoleException 
    {
        Map allEntities = getSAMLv2Entities();
        allEntities.putAll(getIDFFEntities());
        allEntities.putAll(getWSFedEntities());
        
        return allEntities;
    }
    
    /*
     * Returns a map of all the samlv2 entities including data about
     * what realm, the roles, and location of each entity.
     */
    private Map getSAMLv2Entities()
        throws AMConsoleException 
    {
        Map samlv2Map = new HashMap();
        
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            for (Iterator i = realms.iterator(); i.hasNext(); ) {
                String realmName = (String)i.next();
                
                Set samlEntities = samlManager.getAllEntities(realmName);
                List hostedEntities =
                    samlManager.getAllHostedEntities(realmName);
                for (Iterator j = samlEntities.iterator(); j.hasNext();) {
                    String entityName = (String)j.next();
                    
                    Map data = new HashMap(8);
                    data.put(REALM, realmName);
                    // get the roles this entity is acting in
                    data.put(ROLE,
                        listToString(getSAMLv2Roles(entityName, realmName)));
                    
                    data.put(PROTOCOL, SAMLV2);
                                        
                    if ((hostedEntities != null) &&
                        hostedEntities.contains(entityName)) 
                    {
                        data.put(LOCATION, "hosted");
                    } else {
                        data.put(LOCATION, "remote");
                    }
                    
                    samlv2Map.put(entityName, (HashMap)data);
                }
            }
        } catch (SAML2MetaException e) {
            debug.error("EntityModel.getSAMLv2Entities", e);
            throw new AMConsoleException(e.getMessage());
        }
        
        return (samlv2Map != null) ? samlv2Map : Collections.EMPTY_MAP;
    }
    
    /*
     * Returns a map of all the idff entities including data about
     * what realm, the roles, and location of each entity.
     */
    private Map getIDFFEntities()
        throws AMConsoleException 
    {
        Map idffMap = new HashMap();
        try {
            IDFFMetaManager idffManager = new IDFFMetaManager(
                getUserSSOToken());
            
            for (Iterator j = realms.iterator(); j.hasNext(); ) {
                String realm = (String)j.next();
                
// TBD pass the realm when support is added from api
                Set entities = idffManager.getAllEntities();
                List hostedEntities = idffManager.getAllHostedEntities();
                
                for (Iterator i = entities.iterator(); i.hasNext();) {
                    String name = (String)i.next();
                    
                    Map data = new HashMap(8);
                    
// TBD Uncomment when realm support is added in the api
// default to root realm for now.
                    data.put(REALM, realm);
                    
                    data.put(PROTOCOL, IDFF);
                    data.put(ROLE, listToString(getIDFFRoles(name, realm)));
                    if (hostedEntities.contains(name)) {
                        data.put(LOCATION, "hosted");
                    } else {
                        data.put(LOCATION, "remote");
                    }
                    
                    idffMap.put(name, (HashMap)data);
                }
            }
        } catch (IDFFMetaException e) {
            debug.warning("EntityModel.getIDFFEntities", e);
            throw new AMConsoleException(e.getMessage());
        }
        
        return (idffMap != null) ? idffMap : Collections.EMPTY_MAP;
    }
    
    /*
     * Returns a map of all the idff entities including data about
     * what realm, the roles, and location of each entity.
     */
    private Map getWSFedEntities()
        throws AMConsoleException 
    {
        Map wsfedMap = new HashMap();
        for (Iterator i = realms.iterator(); i.hasNext(); ) {
            String realm = (String)i.next();
            
            try {
                Set wsfedEntities =
                    WSFederationMetaManager.getAllEntities(realm);
                List hosted =
                    WSFederationMetaManager.getAllHostedEntities(realm);
                
                for (Iterator j = wsfedEntities.iterator(); j.hasNext(); ) {
                    String entity = (String)j.next();
                    Map data = new HashMap(8);
                    data.put(REALM, realm);
                    data.put(PROTOCOL, WSFED);
                    data.put(ROLE, listToString(getWSFedRoles(entity, realm)));
                    if ((hosted != null) && (hosted.contains(entity))) {
                        data.put(LOCATION, "hosted");
                    } else {
                        data.put(LOCATION, "remote");
                    }
                    
                    wsfedMap.put(entity, (HashMap)data);
                }
            } catch (WSFederationMetaException e) {
                debug.error("EntityModel.getWSFedEntities", e);
                throw new AMConsoleException(e.getMessage());
            }
        }
        
        return (wsfedMap != null) ?
            wsfedMap : Collections.EMPTY_MAP;
    }
    
    /**
     * This is a convenience routine that can be used
     * to convert a List of String objects to a single String in the format of
     *     "one; two; three"
     */
    private String listToString(List roleNames) {
        StringBuffer sb = new StringBuffer();
        for (Iterator i = roleNames.iterator(); i.hasNext(); ) {
            String role = (String)i.next();
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(getLocalizedString(role + ".label"));
        }
        return sb.toString();
    }
    
    public void createEntity(Map data) throws AMConsoleException {
        String protocol = (String)data.remove("protocol");
        if (protocol.equals(SAMLV2)) {
            createSAMLv2Provider(data);
        } else if (protocol.equals(WSFED)) {
            createWSFedProvider(data);
        } else if (protocol.equals(IDFF)) {
            createIDFFProvider(data);
        }
    }
    
    /*
     * TBD what is the best approach for creating a new provider with
     * minimal input from the user
     *
     */
    
    private void createSAMLv2Provider(Map data) throws AMConsoleException {
        throw new AMConsoleException("create SAML not implemented yet");
    }
    
    private void createWSFedProvider(Map data) throws AMConsoleException {
        throw new AMConsoleException("create WSFed not implemented yet");
    }
    
    private void createIDFFProvider(Map data) throws AMConsoleException {
        throw new AMConsoleException("create IDFF not implemented yet");
    }
    
    public void deleteEntities(Map data)
        throws AMConsoleException 
    {
        if (data == null || data.isEmpty()) {
            throw new AMConsoleException("delete.entity.invalid.data");
        }
        
        Set entities = data.keySet();                
        for (Iterator i = entities.iterator(); i.hasNext();) {
            String name = (String)i.next();      
            
            // the format of string s is <type>|<realm>|<location>
            String s = (String)data.get(name);
            int pos = s.indexOf("|");
            String type = s.substring(0, pos);
            String realm = s.substring(pos+1, s.lastIndexOf("|"));
            
            if (type.equals(IDFF)) {
                deleteIDFFEntity(name, realm);
            } else if (type.equals(WSFED)) {
                deleteWSFedEntity(name,realm);
            } else {
                deleteSAMLv2Entity(name,realm);
            }
        }
    }
    
    private void deleteSAMLv2Entity(String entityID, String realm)
        throws AMConsoleException 
    {
        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();
            metaManager.deleteEntityDescriptor(realm, entityID);
        } catch (SAML2MetaException e) {
            throw new AMConsoleException("delete.entity.exists.error");
        }
    }
    
    private void deleteIDFFEntity(String entityID, String realm)
        throws AMConsoleException 
    {
        try {
            IDFFMetaManager metaManager = new IDFFMetaManager(
                getUserSSOToken());
            
            metaManager.deleteEntityDescriptor(entityID);
            
        } catch (IDFFMetaException e) {
            throw new AMConsoleException(e.getMessage());
        }
    }
    
    private void deleteWSFedEntity(String entityID, String realm)
        throws AMConsoleException 
    {
        try {
            WSFederationMetaManager.deleteFederation(realm, entityID); 
        } catch (WSFederationMetaException w) {
            debug.warning("EntityModel.deleteWSFedEntity", w);
            throw new AMConsoleException(w.getMessage());
        }
    }
    
    /*
     * This is used to determine what 'roles' a particular entity is
     * acting as. It will producs a list of role names which can then
     * be used by the calling routine for whatever purpose it needs.
     */
    private List getIDFFRoles(String entity, String realm) {
        List roles = new ArrayList(6);
        
        try {
            IDFFMetaManager idffManager = new IDFFMetaManager(
                getUserSSOToken());
            
            // find out what role this dude is playing
            if (idffManager.getIDPDescriptor(entity) != null) {
                roles.add("IDP");
            }
            if (idffManager.getSPDescriptor(entity) != null) {
                roles.add("SP");
            }
            if(idffManager.getAffiliationDescriptor(entity) != null) {
                roles.add("Affiliate");
            }
        } catch (IDFFMetaException s) {
            if (debug.warningEnabled()) {
                debug.warning("EntityModel.getIDFFRoles() - " +
                    "Couldn't get SAMLMetaManager");
            }
        }
        
        return roles;
    }
    
    private List getWSFedRoles(String entity, String realm) 
     {
        String classMethod = "EntityModelImpl.getWSFedRoles:";
        List roles = new ArrayList(4);
        boolean isSP = true;
        int cnt = 0;
        try {
            if (WSFederationMetaManager.getIDPSSOConfig(realm, entity) != null) {
                roles.add("IDP");
            }
            if (WSFederationMetaManager.getSPSSOConfig(realm, entity) != null) {
                roles.add("SP");
            }
            
            //to handle dual roles specifically for WSFED
            if (roles.isEmpty()) {
                FederationElement fedElem =
                    WSFederationMetaManager.getEntityDescriptor(realm, entity);
                if (fedElem != null) {
                    for (Iterator iter = fedElem.getAny().iterator();                  
                      iter.hasNext(); ) {
                          Object o = iter.next();
                          if (o instanceof UriNamedClaimTypesOfferedElement) {
                              roles.add("IDP");
                              isSP = false; 
                          } else if (o instanceof TokenIssuerEndpointElement) {
                              cnt++;
                          }
                    }
                    if ((isSP) || (cnt >1)) {  
                        roles.add("SP");
                    } 
                }
            }
        } catch (WSFederationMetaException w) {
            debug.warning(classMethod + w); 
        }
        return (roles != null) ?
            roles : Collections.EMPTY_LIST;
    }
    
    /*
     * This is used to determine what 'roles' a particular entity is
     * acting as. It will producs a list of role names which can then
     * be used by the calling routine for whatever purpose it needs.
     */
    private List getSAMLv2Roles(String entity, String realm) {
        List roles = new ArrayList();
        
        try {
            SAML2MetaManager samlManager = new SAML2MetaManager();
            EntityDescriptorElement d =
                samlManager.getEntityDescriptor(realm, entity);
            
            if (d != null) {
                // find out what role this dude is playing
                StringBuffer role = new StringBuffer(32);
                if (SAML2MetaUtils.getSPSSODescriptor(d) != null) {
                    roles.add("SP");
                }
                if (SAML2MetaUtils.getIDPSSODescriptor(d) != null) {
                    roles.add("IDP");
                }
                if (SAML2MetaUtils.getPolicyDecisionPointDescriptor(d) != null) {
                    roles.add("PDP");
                }
                if (SAML2MetaUtils.getPolicyEnforcementPointDescriptor(d) != null) {
                    roles.add("PEP");
                }
            }
        } catch (SAML2MetaException s) {
            if (debug.warningEnabled()) {
                debug.warning("EntityModel.getSAMLv2Roles() - " +
                    "Couldn't get SAMLMetaManager");
            }
        }
        
        return (roles != null) ?
            roles : Collections.EMPTY_LIST;
    }
    
    private Map createTabEntry(String type) {
        Map tab = new HashMap(10);
        tab.put("label", "federation." + type + ".label");
        tab.put("status", "federation." + type + ".status");
        tab.put("tooltip", "federation." + type + ".tooltip");
        tab.put("url", "../federation/" + type);
        tab.put("viewbean", "com.sun.identity.console.federation." + type + "ViewBean");
        tab.put("permissions", "sunAMRealmService");
        
        return tab;
    }
    
    /*
     * Creates a list of tab entries dynamically based on the roles supported
     * for an entity.
     */
    public List getTabMenu(String protocol, String entity, String realm) {
        List entries = new ArrayList();
        List roles = new ArrayList();
        
        // do not localize General. Its the name of a class file.
        roles.add("General");
        
        if (protocol.equals(SAMLV2)) {
            roles.addAll(getSAMLv2Roles(entity, realm));
        } else if (protocol.equals(IDFF)) {
            roles.addAll(getIDFFRoles(entity, realm));
        } else {
            roles.addAll(getWSFedRoles(entity, realm));
        }
        
        // create a tab for each role type
        for (Iterator type = roles.iterator(); type.hasNext(); ) {
            String name = protocol + (String)type.next();
            entries.add(createTabEntry(name));
        }
        
        return entries;
    }
    
    /**
     * Returns true if entity descriptor is an affiliate.
     *
     * @param name Name of entity descriptor.
     * @return true if entity descriptor is an affiliate.
     */
    public boolean isAffiliate(String name) throws AMConsoleException {
        boolean isAffiliate = false;
        try {
            IDFFMetaManager idffManager = new IDFFMetaManager(
                getUserSSOToken());
            AffiliationDescriptorType ad = (AffiliationDescriptorType)
            idffManager.getAffiliationDescriptor(name);
            if (ad != null) {
                isAffiliate = true;
            }
        } catch (IDFFMetaException  e) {           
            debug.warning("EntityModelImpl.isAffiliate", e);
            throw new AMConsoleException(getErrorString(e));
        }
        return isAffiliate;
    }
}
