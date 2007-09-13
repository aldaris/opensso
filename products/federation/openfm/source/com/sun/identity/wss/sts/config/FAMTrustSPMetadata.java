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
 * $Id: FAMTrustSPMetadata.java,v 1.3 2007-09-13 07:24:22 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wss.sts.config;

import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.provider.ProviderException;
import com.sun.identity.wss.sts.STSConstants;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.Constants;
import com.sun.identity.common.SystemConfigurationUtil;
import com.sun.identity.wss.sts.STSUtils;
import com.sun.identity.wss.security.SecurityMechanism;
import com.sun.identity.wss.security.WSSUtils;

import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.iplanet.sso.SSOToken;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class FAMTrustSPMetadata implements TrustSPMetadata {
    
    private String endpoint;
    private String spName;
    private String tokenType;
    private String keyType;
    private String certAlias;
    private Map<String, Object> otherOptions = new HashMap<String, Object>();
    private static Debug debug = STSUtils.debug;
    private List secMech = null;

    private static final String AGENT_CONFIG_ATTR = 
                       "sunIdentityServerDeviceKeyValue";
    private static final String WSP_ENDPOINT = "WSPEndpoint";
    private static final String TYPE = "Type";
    private static final String NAME = "Name";
    private static final String SEC_MECH = "SecurityMech";
    private static final String KEY_ALIAS = "keyAlias";

    /** Creates a new instance of FAMTrustSPMetedata */
    public FAMTrustSPMetadata(String spEndPoint) {
        this.endpoint = spEndPoint;

        getAndProcessWSPKeyValues(spName);

        this.certAlias = 
            SystemConfigurationUtil.getProperty(
                Constants.SAML_XMLSIG_CERT_ALIAS);
        this.keyType = STSConstants.PUBLIC_KEY;

    }

    public String getSPEndPoint(){
        return this.endpoint;
    }
        
    public void setCertAlias(final String certAlias){
        this.certAlias = certAlias;
    }
        
    public String getCertAlias(){
        return this.certAlias;
    }
        
    public void setTokenType(final String tokenType){
        this.tokenType = tokenType;
    }
    
     public String getTokenType(){
        return this.tokenType;
    }
     
    public void setKeyType(final String keyType){
        this.keyType = keyType;
    }
    
    public String getKeyType(){
        return this.keyType;
    }
    
    public Map<String, Object> getOtherOptions(){
        return this.otherOptions;
    }

    // Get WSP configuration and process Key/Value pairs.
    private void getAndProcessWSPKeyValues(String providerEndPoint) {
        Set agentConfigAttribute = new HashSet();
        agentConfigAttribute.add(AGENT_CONFIG_ATTR);

        // Obtain the provider configuration from Agent profile
        try {
            SSOToken adminToken = WSSUtils.getAdminToken();
            AMIdentityRepository idRepo = 
                new AMIdentityRepository(adminToken, "/");

            IdSearchControl control = new IdSearchControl();
            control.setReturnAttributes(agentConfigAttribute);
            control.setTimeOut(0);
            control.setMaxResults(2);

            Map kvPairMap = new HashMap();
            Set set = new HashSet();
            set.add(TYPE + "=" + "WSP");
            set.add(WSP_ENDPOINT + "=" + providerEndPoint);
            kvPairMap.put(AGENT_CONFIG_ATTR, set);

            control.setSearchModifiers(IdSearchOpModifier.OR, kvPairMap);

            IdSearchResults results = idRepo.searchIdentities(IdType.AGENT,
                "*", control);
            Set agents = results.getSearchResults();
            if (!agents.isEmpty()) {
                Map attrs = (Map) results.getResultAttributes();
                AMIdentity provider = (AMIdentity) agents.iterator().next();
                Map attributes = (Map) attrs.get(provider);
                if(debug.messageEnabled()) {
                   debug.message("FAMTrustSPMetadata.getAndProcessWSPKeyValues:"
                                  + " SP Attributes: " + attributes);
                }
                Set attributeValues = (Set) attributes.get(
                          AGENT_CONFIG_ATTR.toLowerCase());
                if (attributeValues != null) {
                    // Get the values and initialize the properties
                    parseAgentKeyValues(attributeValues);
                }
            }
        } catch (Exception e) {
            debug.error("FAMTrustSPMetadata.getAndProcessWSPKeyValues:ERROR: "
                        , e);
        }

    }

    private void parseAgentKeyValues(Set keyValues) {
        if(keyValues == null || keyValues.isEmpty()) {
           return;
        }
        Iterator iter = keyValues.iterator(); 
        while(iter.hasNext()) {
           String entry = (String)iter.next();
           int index = entry.indexOf("=");
           if(index == -1) {
              continue;
           }
           setConfig(entry.substring(0, index),
                      entry.substring(index+1, entry.length()));
        }
    }

    private void setConfig(String attr, String value) {
 
        if(attr.equals(NAME)) {
           this.spName = value;
        } else if(attr.equals(SEC_MECH)) {
            if (secMech == null) {
                secMech = new ArrayList();
            }

            StringTokenizer st = new StringTokenizer(value, ","); 
            while(st.hasMoreTokens()) {
                secMech.add(st.nextToken());
            }
           
            if (secMech != null) {
                if( (secMech.contains(SecurityMechanism.WSS_NULL_SAML_HK_URI))
                     || (secMech.contains(
                         SecurityMechanism.WSS_TLS_SAML_HK_URI)) || 
                    (secMech.contains(
                        SecurityMechanism.WSS_CLIENT_TLS_SAML_HK_URI))) {

                    this.tokenType = STSConstants.SAML11_ASSERTION_TOKEN_TYPE;
                } else if( (secMech.contains(
                    SecurityMechanism.WSS_NULL_SAML2_HK_URI)) ||
                    (secMech.contains(SecurityMechanism.WSS_TLS_SAML2_HK_URI))
                     || (secMech.contains(
                        SecurityMechanism.WSS_CLIENT_TLS_SAML2_HK_URI))) {

                    this.tokenType = STSConstants.SAML20_ASSERTION_TOKEN_TYPE;
                }
            }

        } else if(attr.equals(KEY_ALIAS)) {
           //this.certAlias = value;
        }
    }

}
