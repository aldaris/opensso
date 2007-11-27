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
 * $Id: DiscoveryAgent.java,v 1.3 2007-11-27 22:03:39 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.wss.provider.plugins; 

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Enumeration;

import com.iplanet.sso.SSOToken;
import com.sun.identity.shared.debug.Debug;
import com.iplanet.sso.SSOException;
import com.sun.identity.wss.provider.DiscoveryConfig;
import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.provider.ProviderUtils;
import com.sun.identity.wss.provider.TrustAuthorityConfig;
import com.sun.identity.wss.provider.ProviderException;
import com.sun.identity.wss.security.PasswordCredential;

import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdRepoException;


public class DiscoveryAgent extends DiscoveryConfig {

     private static final String AGENT_CONFIG_ATTR = 
                       "sunIdentityServerDeviceKeyValue";
     private static final String NAME = "Name";
     private static final String TYPE = "Type";
     private static final String ENDPOINT = "Endpoint";
     private static final String KEY_ALIAS = "KeyAlias";

     private AMIdentityRepository idRepo;
     private static Set agentConfigAttribute;
     private static Debug debug = ProviderUtils.debug;

     // Instance variables
     private SSOToken token;
     private boolean profilePresent;
     
     public DiscoveryAgent() {
         
     }
     
     public DiscoveryAgent(AMIdentity amIdentity) throws ProviderException {
        try {
            Set attributeValues = amIdentity.getAttribute(AGENT_CONFIG_ATTR);
            if(attributeValues != null && !attributeValues.isEmpty()) {
               profilePresent = true;
               parseAgentKeyValues(attributeValues);
            }
        } catch (IdRepoException ire) {
            debug.error("STSAgent.constructor: Idrepo exception", ire);
            throw new ProviderException(ire.getMessage());            
        } catch (SSOException se) {
            debug.error("STSAgent.constructor: SSO exception", se);
            throw new ProviderException(se.getMessage());            
        }
         
     }

     public void init (String name, String type, SSOToken token) 
               throws ProviderException {

        this.name = name;
        this.type = type;
        this.token = token;

        // Obtain the provider from Agent profile
        try {
            if (idRepo == null) {
                idRepo = new AMIdentityRepository(token, "/");
            }

            if (agentConfigAttribute == null) {
                agentConfigAttribute = new HashSet();
                agentConfigAttribute.add(AGENT_CONFIG_ATTR);
            }
            IdSearchControl control = new IdSearchControl();
            control.setReturnAttributes(agentConfigAttribute);
            IdSearchResults results = idRepo.searchIdentities(IdType.AGENT,
               name, control);
            Set agents = results.getSearchResults();
            if (!agents.isEmpty()) {
                Map attrs = (Map) results.getResultAttributes();
                AMIdentity provider = (AMIdentity) agents.iterator().next();
                profilePresent = true;
                Map attributes = (Map) attrs.get(provider);
                Set attributeValues = (Set) attributes.get(
                          AGENT_CONFIG_ATTR.toLowerCase());
                if (attributeValues != null) {
                    // Get the values and initialize the properties
                    parseAgentKeyValues(attributeValues);
                }
            }
        } catch (Exception e) {
            debug.error("DiscoveryAgent.init: Unable to get idRepo", e);
            throw (new ProviderException("idRepo exception: "+ e.getMessage()));
        }
    }

    private void parseAgentKeyValues(Set keyValues) throws ProviderException {
        if(keyValues == null || keyValues.isEmpty()) {
           return;
        }
        Iterator iter = keyValues.iterator(); 
        while(iter.hasNext()) {
           String entry = (String)iter.next();
           StringTokenizer st = new StringTokenizer(entry, "=");
           if(st.countTokens() != 2) {
              continue;
           }
           setConfig(st.nextToken(), st.nextToken());
        }
    }

    private void setConfig(String attr, String value) {
 
        debug.message("Attribute name: " + attr + "Value: "+ value);

        if(attr.equals(NAME)) {
           this.name = value;
        } else if(attr.equals(TYPE)) {
           this.type = value; 
        } else if(attr.equals(ENDPOINT)) {
           this.endpoint = value;
        } else if(attr.equals(KEY_ALIAS)) {
           this.privateKeyAlias = value;
        } else {
           if(ProviderUtils.debug.messageEnabled()) {
              ProviderUtils.debug.message("DiscoveryAgent.setConfig: Invalid " +
              "Attribute configured." + attr);
           }
        }
    }

    public void store() throws ProviderException {

        Set set = new HashSet();
        
        if(name != null) {
           set.add(getKeyValue(NAME, name)); 
        }
        
        if(type != null) { 
           set.add(getKeyValue(TYPE, type));
        }

        if(endpoint != null) {
           set.add(getKeyValue(ENDPOINT, endpoint));
        }
        if(privateKeyAlias != null) {
           set.add(getKeyValue(KEY_ALIAS, privateKeyAlias));
        }

        // Save the entry in Agent's profile
        try {
            Map attributes = new HashMap();
            attributes.put(AGENT_CONFIG_ATTR, set);
            if (profilePresent) {
                // Construct AMIdentity object and save
                AMIdentity id = new AMIdentity(token,
                   name, IdType.AGENT, "/", null);
                debug.message("Attributes to be stored: " + attributes);
                id.setAttributes(attributes);
                id.store();
            } else {
                // Create a new Agent profile
                if (idRepo == null) {
                    idRepo = new AMIdentityRepository(token, "/");
                }
                idRepo.createIdentity(IdType.AGENT, name, attributes);
            }
        } catch (Exception e) {
            debug.error("DiscoveryAgent.store: Unable to get idRepo", e);
            throw (new ProviderException("idRepo exception: "+ e.getMessage()));
        }
    }

    public void delete() throws ProviderException {
        if (!profilePresent) {
            return;
        }

        // Delete the agent profile
        try {
            if (idRepo == null) {
                idRepo = new AMIdentityRepository(token, "/");
            }
            // Construct AMIdentity object to delete
            AMIdentity id = new AMIdentity(token,name, IdType.AGENT, "/", null);
            Set identities = new HashSet();
            identities.add(id);
            idRepo.deleteIdentities(identities);
        } catch (Exception e) {
            debug.error("DiscoveryAgent.delete: Unable to get idRepo", e);
            throw (new ProviderException("idRepo exception: "+ e.getMessage()));
        }
    }

    private String getKeyValue(String key, String value) {
        return key + "=" + value;
    }
   

}
