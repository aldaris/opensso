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
 * $Id: DiscoveryAgent.java,v 1.5 2007-11-30 19:08:02 mrudul_uchil Exp $
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

     // Initialize the Attributes names set
     private static Set attrNames = new HashSet();;

     private static final String ENDPOINT = "DiscoveryEndpoint";
     private static final String KEY_ALIAS = "privateKeyAlias";
     private static final String AUTHN_ENDPOINT = "AuthNServiceEndpoint";

     private AMIdentityRepository idRepo;
     private static Set agentConfigAttribute;
     private static Debug debug = ProviderUtils.debug;

     // Instance variables
     private SSOToken token;
     private boolean profilePresent;

     static {
         attrNames.add(ENDPOINT);
         attrNames.add(AUTHN_ENDPOINT);
         attrNames.add(KEY_ALIAS);
     }
     
     public DiscoveryAgent() {
         
     }
     
     public DiscoveryAgent(AMIdentity amIdentity) throws ProviderException {
        try {
            this.name = amIdentity.getName();
            this.type = amIdentity.getType().getName();
            Map attributes = (Map) amIdentity.getAttributes(attrNames);
            parseAgentKeyValues(attributes);
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

             IdSearchControl control = new IdSearchControl();
             control.setAllReturnAttributes(true);
             IdSearchResults results = idRepo.searchIdentities(IdType.AGENTONLY,
                 name, control);
             Set agents = results.getSearchResults();
             if (!agents.isEmpty()) {
                 //Map attrs = (Map) results.getResultAttributes();
                 AMIdentity provider = (AMIdentity) agents.iterator().next();
                 profilePresent = true;
                 //Map attributes = (Map) attrs.get(provider);
                 Map attributes = (Map) provider.getAttributes(attrNames);
                 parseAgentKeyValues(attributes);
             }
        } catch (Exception e) {
            debug.error("DiscoveryAgent.init: Unable to get idRepo", e);
            throw (new ProviderException("idRepo exception: "+ e.getMessage()));
        }
    }

    private void parseAgentKeyValues(Map attributes) throws ProviderException {
        if(attributes == null || attributes.isEmpty()) {
           return;
        }

        for (Iterator i = attributes.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            Set valSet = (Set)attributes.get(key);
            String value = null;
            if ((valSet != null) && (valSet.size() > 0)) {
                Iterator iter = valSet.iterator();
                StringBuffer sb =  new StringBuffer(100);
                while(iter.hasNext()) {
                   sb.append((String)iter.next()).append(",");
                }
                sb = sb.deleteCharAt(sb.length() - 1);
                value = sb.toString();
            }
            setConfig(key, value);
        }

    }

    private void setConfig(String attr, String value) {
 
        debug.message("Attribute name: " + attr + " Value: "+ value);

        if (attr.equals(AUTHN_ENDPOINT)) {
           this.authServiceEndpoint = value; 
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

        Map config = new HashMap();
        
        if(authServiceEndpoint != null) { 
           config.put(AUTHN_ENDPOINT, authServiceEndpoint);
        }

        if(endpoint != null) {
           config.put(ENDPOINT, endpoint);
        }

        if(privateKeyAlias != null) {
           config.put(KEY_ALIAS, privateKeyAlias);
        }

        // Save the entry in Agent's profile
        try {
            Map attributes = new HashMap();
            Set values = null ;

            for (Iterator i = config.keySet().iterator(); i.hasNext(); ) {
                String key = (String)i.next();
                String value = (String)config.get(key);
                values = new HashSet();
                values.add(value);
                attributes.put(key, values);
            }
            if (profilePresent) {
                // Construct AMIdentity object and save
                AMIdentity id = new AMIdentity(token,
                    name, IdType.AGENTONLY, "/", null);
                debug.message("Attributes to be stored: " + attributes);
                id.setAttributes(attributes);
                id.store();
            } else {
                // Create a new Agent profile
                if (idRepo == null) {
                    idRepo = new AMIdentityRepository(token, "/");
                }
                idRepo.createIdentity(IdType.AGENTONLY, name, attributes);
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
            AMIdentity id = new AMIdentity(token, name,
                            IdType.AGENTONLY, "/", null);
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
