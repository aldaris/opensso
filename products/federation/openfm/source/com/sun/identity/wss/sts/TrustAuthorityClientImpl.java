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
 * $Id: TrustAuthorityClientImpl.java,v 1.2 2008-06-20 20:42:37 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.sts;

import org.w3c.dom.Element;

import com.sun.identity.shared.debug.Debug;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenManager;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.identity.common.SystemConfigurationUtil;
import com.sun.identity.wss.security.SecurityToken;

/**
 * The class <code>TrustAuthorityClientImpl</code> is the implementation of
 * <code>TrustAuthorityClient</code> class.
 * @supported.all.api
 */
public class TrustAuthorityClientImpl {
    
    private static Debug debug = STSUtils.debug;
    private static Class clientTokenClass;
    
    
    /** Creates a new instance of TrustAuthorityClientImpl */
    public TrustAuthorityClientImpl() {
    }
        
    /**
     * Returns security token element obtained from Security Token Service.
     */
    public Element getSTSTokenElement(String wspEndPoint,
                                      String stsEndpoint,
                                      String stsMexAddress,
                                      Object credential) 
                                      throws FAMSTSException {

        STSClientConfiguration config =
            new STSClientConfiguration(stsEndpoint, stsMexAddress);
        if(credential != null) {
           if(credential instanceof Element) {
              Element credE = (Element)credential;
              if(credE.getLocalName().equals("Assertion")) {
                 config.setTokenType(SecurityToken.WSS_FAM_SSO_TOKEN);
              }
           }
           config.setOBOToken(getClientUserToken(credential));
        }
        try {
            IssuedTokenManager manager = IssuedTokenManager.getInstance();
            IssuedTokenContext ctx =
                manager.createIssuedTokenContext(config,wspEndPoint);
            manager.getIssuedToken(ctx);
            Token issuedToken = ctx.getSecurityToken();
            Element element = (Element)issuedToken.getTokenValue();

            return element;
        } catch (Exception ex) {
            debug.error("TrustAuthorityClientImpl.getSTSToken:: Failed in" +
                "obtainining STS Token Element: ", ex);
            // TODO I18n
            throw new FAMSTSException("TrustAuthorityClientImpl:ws trust exception");
        } catch (Throwable ex) {
            ex.printStackTrace();
            return null;
        }
        
    }
    
    /**
     * Returns Client's or End user's token to be converted to Security token.
     */
    private Token getClientUserToken(Object credential) 
                throws FAMSTSException {
        if (clientTokenClass == null) {
            String className =   SystemConfigurationUtil.getProperty(
                STSConstants.STS_CLIENT_USER_TOKEN_PLUGIN, 
                "com.sun.identity.wss.sts.STSClientUserToken");
            try {                
                clientTokenClass = 
                       (Thread.currentThread().getContextClassLoader()).
                        loadClass(className);                               
            } catch (Exception ex) {
                 debug.error("TrustAuthorityClientImpl.getClientUserToken:"
                           +  "Failed in obtaining class", ex);
                 throw new FAMSTSException("initializationFailed");
            }
        }
        
        try {
            ClientUserToken userToken =
                (ClientUserToken) clientTokenClass.newInstance();
            userToken.init(credential);
            if(debug.messageEnabled()) {
                debug.message("TrustAuthorityClientImpl:getClientUserToken: " + 
                    "Client User Token : " + userToken);
            }
            return userToken;

        } catch (Exception ex) {
            debug.error("TrustAuthorityClientImpl.getClientUserToken: " +
                 "Failed in initialization", ex);
             throw new FAMSTSException("initializationFailed");
        }
    }
                 
}
