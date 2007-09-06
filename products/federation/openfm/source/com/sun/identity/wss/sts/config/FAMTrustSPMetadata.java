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
 * $Id: FAMTrustSPMetadata.java,v 1.2 2007-09-06 07:42:10 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wss.sts.config;

import com.sun.xml.ws.api.security.trust.config.TrustSPMetadata;

import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.provider.ProviderException;
import com.sun.identity.wss.sts.STSConstants;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wss.sts.STSUtils;
import com.sun.identity.wss.security.SecurityMechanism;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class FAMTrustSPMetadata implements TrustSPMetadata {
    
    private String endpoint;
    private String spName;
    private String tokenType;
    private String keyType;
    private String certAlias;
    private Map<String, Object> otherOptions = new HashMap<String, Object>();
    private static Debug debug = STSUtils.debug;

    /** Creates a new instance of FAMTrustSPMetedata */
    public FAMTrustSPMetadata(String spName) {
        this.spName = spName;
        ProviderConfig config = getWSPConfig(spName);
        if (config != null) {
            this.endpoint = config.getWSPEndpoint();
            this.certAlias = config.getKeyAlias();
            this.keyType = STSConstants.PUBLIC_KEY;

            List list = config.getSecurityMechanisms();
            if (list != null) {
                if( (list.contains(SecurityMechanism.WSS_NULL_SAML_HK_URI)) ||
                    (list.contains(SecurityMechanism.WSS_TLS_SAML_HK_URI)) ||
                    (list.contains(
                        SecurityMechanism.WSS_CLIENT_TLS_SAML_HK_URI))) {

                    this.tokenType = STSConstants.SAML11_ASSERTION_TOKEN_TYPE;
                } else if( (list.contains(
                    SecurityMechanism.WSS_NULL_SAML2_HK_URI)) ||
                    (list.contains(SecurityMechanism.WSS_TLS_SAML2_HK_URI)) ||
                    (list.contains(
                        SecurityMechanism.WSS_CLIENT_TLS_SAML2_HK_URI))) {

                    this.tokenType = STSConstants.SAML20_ASSERTION_TOKEN_TYPE;
                }
            }
        }
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

    // Get WSP configuration.
    private static ProviderConfig getWSPConfig(String providerName) {

        ProviderConfig config = null;
        try {
            config = ProviderConfig.getProvider(providerName, 
                          ProviderConfig.WSP);
            if(config == null) {
               debug.error("FAMTrustSPMetadata.getWSPConfig:: Provider" +
               " configuration is null");
            }

        } catch (ProviderException pe) {
            debug.error("FAMTrustSPMetadata.getWSPConfig:: Provider" +
               " configuration read failure", pe);
        } catch (Exception e) {
            debug.error("FAMTrustSPMetadata.getWSPConfig:: Provider" +
               " configuration read failure", e);
        }
        return config;
    }
}
