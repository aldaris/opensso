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
 * $Id: STSAgent.java,v 1.5 2007-11-30 19:08:02 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.provider.plugins;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ArrayList;

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wss.provider.STSConfig;
import com.sun.identity.wss.security.PasswordCredential;
import com.sun.identity.wss.provider.ProviderException;
import com.sun.identity.wss.provider.ProviderUtils;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdRepoException;


public class STSAgent extends STSConfig {

    // Initialize the Attributes names set
    private static Set attrNames = new HashSet();;
    
    private static final String ENDPOINT = "STSEndpoint";
    private static final String MEX_ENDPOINT = "STSMexEndpoint";
    private static final String SEC_MECH = "SecurityMech";
    private static final String RESPONSE_SIGN = "isResponseSign";
    private static final String RESPONSE_ENCRYPT = "isResponseEncrypt";
    private static final String REQUEST_SIGN = "isRequestSign";     
    private static final String REQUEST_ENCRYPT = "isRequestEncrypt";
    private static final String REQUEST_HEADER_ENCRYPT = 
                                "isRequestHeaderEncrypt";
    private static final String USER_NAME = "UserName";
    private static final String USER_PASSWORD = "UserPassword";
    private static final String USER_CREDENTIAL = "UserCredential";
    private static final String STS_CONFIG = "STS";
    private static final String PRIVATE_KEY_ALIAS = "privateKeyAlias";
    private static final String PUBLIC_KEY_ALIAS = "publicKeyAlias";
     
    private static Debug debug = ProviderUtils.debug;
    
    private AMIdentityRepository idRepo;
    private boolean profilePresent = false;
    private SSOToken token = null;
    
    static {
        attrNames.add(ENDPOINT);
        attrNames.add(MEX_ENDPOINT);
        attrNames.add(SEC_MECH);
        attrNames.add(RESPONSE_SIGN);
        attrNames.add(RESPONSE_ENCRYPT);
        attrNames.add(REQUEST_SIGN);
        attrNames.add(REQUEST_ENCRYPT);
        attrNames.add(REQUEST_HEADER_ENCRYPT);
        attrNames.add(USER_CREDENTIAL);
        attrNames.add(STS_CONFIG);
        attrNames.add(PRIVATE_KEY_ALIAS);
        attrNames.add(PUBLIC_KEY_ALIAS);
    }

    /** Creates a new instance of STSAgent */
    public STSAgent() {
    }
    
    public STSAgent(AMIdentity amIdentity) throws ProviderException {
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
    
    public void init(String name, String type, SSOToken token) 
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
            debug.error("STSAgent.init: Unable to get idRepo", e);
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
        
        if(attr.equals(ENDPOINT)) {
            this.endpoint = value;
        } else if(attr.equals(MEX_ENDPOINT)) {
            this.mexEndpoint = value;
        } else if(attr.equals(SEC_MECH)) {
           if (secMech == null) {
               secMech = new ArrayList();
           }
           StringTokenizer st = new StringTokenizer(value, ","); 
           while(st.hasMoreTokens()) {
               secMech.add(st.nextToken());
           }
        } else if(attr.equals(RESPONSE_SIGN)) {
           this.isResponseSigned = Boolean.valueOf(value).booleanValue();
        } else if(attr.equals(RESPONSE_ENCRYPT)) {
           this.isResponseEncrypted = Boolean.valueOf(value).booleanValue();
        } else if(attr.equals(REQUEST_SIGN)) {
           this.isRequestSigned = Boolean.valueOf(value).booleanValue();
        } else if(attr.equals(REQUEST_ENCRYPT)) {
           this.isRequestEncrypted = Boolean.valueOf(value).booleanValue();
        } else if(attr.equals(REQUEST_HEADER_ENCRYPT)) {
           this.isRequestHeaderEncrypted = Boolean.valueOf(value).booleanValue();
        } else if(attr.equals(PRIVATE_KEY_ALIAS)) {
           this.privateKeyAlias = value;
        } else if(attr.equals(STS_CONFIG)) {
           this.stsConfigName = value;
        } else if(attr.equals(PUBLIC_KEY_ALIAS)) {
           this.publicKeyAlias = value;
        } else if(attr.equals(USER_CREDENTIAL)) {
            if(usercredentials == null) {
                usercredentials = new ArrayList();
            }
            StringTokenizer stVal = new StringTokenizer(value, ","); 
            while(stVal.hasMoreTokens()) {
                String tmpVal = (String)stVal.nextToken();
                int index = tmpVal.indexOf("|");
                if(index == -1) {
                    return;
                }
                String usertmp = tmpVal.substring(0, index);
                String passwordtmp = tmpVal.substring(index+1, tmpVal.length()); 

                String user = null;
                String password = null;
                StringTokenizer st = new StringTokenizer(usertmp, ":"); 
                if(USER_NAME.equals(st.nextToken())) {
                    if(st.hasMoreTokens()) {
                        user = st.nextToken();
                    }               
                }
                StringTokenizer st1 = new StringTokenizer(passwordtmp, ":"); 
                if(USER_PASSWORD.equals(st1.nextToken())) {
                    if(st1.hasMoreTokens()) {
                        password = st1.nextToken();
                    }              
                }

                if((user != null) && (password != null)) {
                    PasswordCredential credential = 
                        new PasswordCredential(user, password);
                    usercredentials.add(credential);
                }
            }
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
            debug.error("STSAgent.delete: Unable to get idRepo", e);
            throw (new ProviderException("idRepo exception: "+ e.getMessage()));
        }
        
    }
    
    public void store() throws ProviderException {
        
        Map config = new HashMap();
        
        if(endpoint != null) {
           config.put(ENDPOINT, endpoint);
        }        

        if(mexEndpoint != null) {
           config.put(MEX_ENDPOINT, mexEndpoint);
        }

        if(privateKeyAlias != null) {
           config.put(PRIVATE_KEY_ALIAS, privateKeyAlias);
        }

        if(publicKeyAlias != null) {
           config.put(PUBLIC_KEY_ALIAS, publicKeyAlias);
        }

        if(stsConfigName != null) {
           config.put(STS_CONFIG, stsConfigName);
        }
        
        Set secMechSet = new HashSet();
        if(secMech != null) {
           Iterator iter = secMech.iterator();
           while(iter.hasNext()) {
               secMechSet.add((String)iter.next());
           }
        }
        
        config.put(RESPONSE_SIGN, 
                            Boolean.toString(isResponseSigned));
        config.put(RESPONSE_ENCRYPT, 
                            Boolean.toString(isResponseEncrypted));
        config.put(REQUEST_SIGN, 
                            Boolean.toString(isRequestSigned));
        config.put(REQUEST_ENCRYPT, 
                            Boolean.toString(isRequestEncrypted));
        config.put(REQUEST_HEADER_ENCRYPT,
                            Boolean.toString(isRequestHeaderEncrypted));
        
        if(usercredentials != null) {
           Iterator iter = usercredentials.iterator();
           StringBuffer sb =  new StringBuffer(100);
           while(iter.hasNext()) {
              PasswordCredential cred = (PasswordCredential)iter.next();
              String user = cred.getUserName();
              String password = cred.getPassword();
              if(user == null || password == null) {
                 continue;
              }
              
              sb.append(USER_NAME).append(":").append(user)
                .append("|").append(USER_PASSWORD).append(":").append(password).append(",");
           }
           sb = sb.deleteCharAt(sb.length() - 1);
           config.put(USER_CREDENTIAL, sb.toString());
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
            if (secMechSet != null) {
                attributes.put(SEC_MECH, secMechSet);
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
            debug.error("STSAgent.store: Unable to get idRepo", e);
            throw (new ProviderException("idRepo exception: "+ e.getMessage()));
        }
        
    }
    
    private String getKeyValue(String key, String value) {
        return key + "=" + value;
    }

}
