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
 * $Id: STSClientConfiguration.java,v 1.4 2008-03-04 23:57:46 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.sts;

import java.util.List;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.security.Token;
import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.security.SecurityMechanism;

/**
 * This class implements WSIT <code>STSIssuedTokenConfiguration</code> to
 * give the appropriate run time configuration for the WSIT client to
 * obtain the security tokens from a trusted authority.
 */
public class STSClientConfiguration extends STSIssuedTokenConfiguration {
           
    private String tokenType = null;
    
    private String keyType = null;
    
    private long keySize = -1;
    
    private String signatureAlg = null;
    
    private String encAlg = null;
    
    private String canAlg = null;
    
    private String keyWrapAlg = null;
    
    private Token userToken = null;
    
    
    
    public STSClientConfiguration(String stsEndpoint, String stsMEXAddress){
        super(stsEndpoint, stsMEXAddress);
        // We need public key value in the STS issued assertion for signing
        // purposes
        // TODO - we will have to see for Symmetric key
        this.keyType = STSConstants.PUBLIC_KEY;
    }
    
    public STSClientConfiguration(String stsEndpoint,
          String stsWSDLLocation, 
          String stsServiceName,
          String stsPortName,
          String stsNamespace){
        super(stsEndpoint, stsWSDLLocation, stsServiceName, stsPortName,
              stsNamespace);
        
        keyType = STSConstants.PUBLIC_KEY;
    }
    
    public void setOBOToken(Token userToken) {
        this.userToken = userToken;
    }
        
    public String getTokenType() {
        // This is temp till WSIT trust client code is corrected to 
        // not set token type as SAML 1.1 by default.
        try {
            ProviderConfig pc = 
                ProviderConfig.getProvider("wsp", ProviderConfig.WSP);
            List secMech = pc.getSecurityMechanisms();
            if (secMech != null) {
                if( (secMech.contains(
                    SecurityMechanism.WSS_NULL_SAML2_HK_URI)) ||
                    (secMech.contains(SecurityMechanism.WSS_TLS_SAML2_HK_URI))
                     || (secMech.contains(
                        SecurityMechanism.WSS_CLIENT_TLS_SAML2_HK_URI))) {

                    this.tokenType = STSConstants.SAML20_ASSERTION_TOKEN_TYPE;
                } else if( (secMech.contains(SecurityMechanism.WSS_NULL_SAML_HK_URI))
                     || (secMech.contains(
                         SecurityMechanism.WSS_TLS_SAML_HK_URI)) || 
                    (secMech.contains(
                        SecurityMechanism.WSS_CLIENT_TLS_SAML_HK_URI))) {

                    this.tokenType = STSConstants.SAML11_ASSERTION_TOKEN_TYPE;
                }
            }
        } catch (Exception e) {
            // Ignore Exception.
        }
        
        return tokenType;
    }
    
    public String getKeyType() {
        return keyType;
    }
    
    public long getKeySize() {
        return keySize;
    }
    
    public String getSignatureAlgorithm() {
        return signatureAlg;
    }
    
    public String getEncryptionAlgorithm() {
        return encAlg;
    }
    
    public String getCanonicalizationAlgorithm() {
        return canAlg;
    }
    
    public String getKeyWrapAlgorithm() {
        return keyWrapAlg;
    }
    
    public Token getOBOToken() {
        return userToken;
    }
    
}
