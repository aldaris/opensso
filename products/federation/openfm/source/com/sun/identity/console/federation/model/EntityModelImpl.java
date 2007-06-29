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
 * $Id: EntityModelImpl.java,v 1.1 2007-06-29 20:23:23 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.federation.model;

import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.console.base.model.AMConsoleException;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.federation.meta.IDFFMetaManager;
import com.sun.identity.federation.meta.IDFFMetaException;

import com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement;


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
        //allEntities.putAll(getWSFedEntities());
        
        return allEntities;
    }
     
    private Map getSAMLv2Entities() 
        throws AMConsoleException 
    {
        Map samlv2Map = new HashMap();
        SAML2MetaManager samlManager = null;
        List hostedEntities = null;
        Set samlEntities = null;
        
        try {
            samlManager = new SAML2MetaManager();   
            for (Iterator i = realms.iterator(); i.hasNext(); ) {
                String realmName = (String)i.next();
    
                hostedEntities = samlManager.getAllHostedEntities(realmName);
                samlEntities = samlManager.getAllEntities(realmName);
           
                for (Iterator j = samlEntities.iterator(); j.hasNext();) {
                    String entityName = (String)j.next();

                    com.sun.identity.saml2.jaxb.metadata.EntityDescriptorElement d =
                        samlManager.getEntityDescriptor(realmName, entityName);

                    if (d != null) {           
                        Map data = new HashMap(8);
                        data.put(PROTOCOL, "SAMLv2");

                        // find out what role this dude is playing
                        StringBuffer role = new StringBuffer(32);
                        if (SAML2MetaUtils.getSPSSODescriptor(d) != null) {
                            addRole(role, "SP");
                        }
                        if (SAML2MetaUtils.getIDPSSODescriptor(d) != null) {
                            addRole(role, "IDP");
                        }
                        if (SAML2MetaUtils.getPolicyDecisionPointDescriptor(d) != null) {
                            addRole(role, "PDP");
                        }
                        if (SAML2MetaUtils.getPolicyEnforcementPointDescriptor(d) != null) {
                            addRole(role, "PEP");
                        }
                        
                        data.put(ROLE, role.toString());

                        if (hostedEntities.contains(entityName)) {
                            data.put(LOCATION, "Hosted");
                        } else {
                            data.put(LOCATION, "Remote");
                        }

                        samlv2Map.put(entityName, (HashMap)data);            
                    }                   
                }
            }
        } catch (SAML2MetaException e) {
            debug.error("EntityModel.getSAMLv2Entities", e);                           
        }
        
        return (samlv2Map != null) ? samlv2Map : Collections.EMPTY_MAP;
    }
    
    private Map getIDFFEntities()
        throws AMConsoleException
    {
        Map idffMap = new HashMap();
        try {
            IDFFMetaManager idffManager = new IDFFMetaManager(
                getUserSSOToken());            

            Set entities = idffManager.getAllEntities(); 
            List hostedEntities = idffManager.getAllHostedEntities();
            
            for (Iterator i = entities.iterator(); i.hasNext();) {
                String name = (String)i.next();

                Map data = new HashMap(8);
                data.put(PROTOCOL, "IDFF");
                
                StringBuffer role = new StringBuffer(20);
                if (idffManager.getIDPDescriptor(name) != null) {
                    addRole(role, "IDP");
                }
                if (idffManager.getSPDescriptor(name) != null) {
                    addRole(role, "SP");
                }
                data.put(ROLE, role.toString());
                
                if (hostedEntities.contains(name)) {
                    data.put(LOCATION, "Hosted");
                } else {
                    data.put(LOCATION, "Remote");
                }
                                
                idffMap.put(name, (HashMap)data);
            }
        } catch (IDFFMetaException e) {
            debug.warning("EntityModel.getIDFFEntities", e);
            throw new AMConsoleException(e.getMessage());
        }
        
        return (idffMap != null) ? idffMap : Collections.EMPTY_MAP;
    }
    
    /*
     * This is used to build up a string that will look like
     *  SP; IDP; PEP
     * or
     *  IDP
     */
    private void addRole(StringBuffer sb, String role) {
        if (sb.length() > 0) {
            sb.append("; ");
        }
        sb.append(getLocalizedString(role));
    }
    
    public void createEntity(Map data) throws AMConsoleException {
        String protocol = (String)data.remove("protocol");
        if (protocol.equals("saml2")) {
            createSAMLv2Provider(data);
        } else if (protocol.equals("wsfed")) {
            createWSFedProvider(data);             
        } else if (protocol.equals("idff")) {
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
            String type = (String)data.get(name);
              
            if (type.equals("IDFF")) {
                deleteIDFFEntity(name);
            } else if (type.equals("WSFED")) {
                deleteWSFedEntity(name);           
            } else {
                deleteSAMLv2Entity(name);
            }
        }
    }
    
    private void deleteSAMLv2Entity(String entityID)
        throws AMConsoleException
    {
        //
        // TBD the realm should be pulled from the entity descriptor element
        //
        String realm = "/";
        try {
            SAML2MetaManager metaManager = new SAML2MetaManager();          
            metaManager.deleteEntityDescriptor(realm, entityID);           
        } catch (SAML2MetaException e) {
            throw new AMConsoleException("delete.entity.exists.error");
        }
    }
    
    private void deleteIDFFEntity(String entityID)
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
    
    private void deleteWSFedEntity(String entityID) 
        throws AMConsoleException 
    {
        throw new AMConsoleException("TBD");
    }
}