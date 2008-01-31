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
 * $Id: TrustAuthorityClient.java,v 1.6 2008-01-31 20:01:41 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wss.sts;

import com.sun.identity.wss.security.SecurityMechanism;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import org.w3c.dom.Element;
import javax.servlet.ServletContext;

import com.iplanet.sso.SSOToken;
import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wss.security.SecurityToken;
import com.sun.identity.wss.security.SecurityException;
import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.provider.TrustAuthorityConfig;
import com.sun.identity.wss.provider.STSConfig;
import com.sun.xml.ws.api.security.trust.client.IssuedTokenManager;
import com.sun.xml.ws.security.IssuedTokenContext;
import com.sun.xml.ws.security.Token;
import com.sun.identity.wss.security.AssertionToken;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.identity.common.SystemConfigurationUtil;

import com.sun.identity.classloader.FAMClassLoader;
import java.lang.reflect.Method;


/**
 * The class <code>TrustAuthorityClient</code> is used to obtain the 
 * security tokens from the trusted authority services such as Security
 * Token Service (STS) or Liberty Discovery Service.
 * @supported.all.api
 */
public class TrustAuthorityClient {
    
    private static Debug debug = STSUtils.debug;
    private static Class clientTokenClass;
    
    
    /** Creates a new instance of TrustAuthorityClient */
    public TrustAuthorityClient() {
    }
    
    public SecurityToken getSecurityToken(
            ProviderConfig pc,            
            SSOToken ssoToken) throws FAMSTSException {
        return getSecurityToken(pc,ssoToken,null);
    }

    /**
     * Returns the <code>SecurityToken</code> for the web services client from
     * a trusted authority. The web services client configuation and web service
     * information is identified by the provider configuration.
     *
     * @param pc provider configuration of the web services client.
     * @param ssoToken single sign-on token of the principal.
     * @return SecurityToken security token for the web services consumer.   
     * @exception FAMSTSException if it's unable to retrieve security token.
     */
    public SecurityToken getSecurityToken(
            ProviderConfig pc,            
            SSOToken ssoToken,
            ServletContext context) throws FAMSTSException {
        
        List securityMechanisms = pc.getSecurityMechanisms();
        if(securityMechanisms == null || securityMechanisms.isEmpty()) {
           if(debug.messageEnabled()) {
              debug.message("TrustAuthorityClient.getSecurityToken::"+
                      "Security Mechanisms are not configured");
           }
           return null;
        }
        
        String securityMech = (String)securityMechanisms.get(0);
        if(securityMech.equals(SecurityMechanism.STS_SECURITY_URI)) {
           return getSTSToken(pc, ssoToken, context); 
        } else if (securityMech.equals(
                SecurityMechanism.LIBERTY_DS_SECURITY_URI)) {
           return getLibertyToken(pc, ssoToken);
        } else {
           debug.error("TrustAuthorityClient.getSecurityToken" +
                   "Invalid security mechanism to get token from TA");
           return null;
        }        
        
    }
    
    /**
     * This  method applies only for the STS Tokens.
     * Renew the issued security token that was obtained from previous
     * interactions.
     * @param securityToken security token that needs to be renewed.
     * @param pc provider configuration of the web services client.
     * @param ssoToken single sign-on token of the principal.     
     * @return SecurityToken security token for the web services consumer.   
     * @exception FAMSTSException if it's unable to renew security token or
     *            if the trust authority configuration is not of STS.
     */
    public SecurityToken renewIssuedToken(SecurityToken securityToken,
            ProviderConfig pc,            
            SSOToken ssoToken) throws FAMSTSException {
        //TODO To be implemented
        return null;
        
    }
    
    /**
     * This  method applies only for the STS Tokens.
     * Cancel the issued security token that was obtained from previous interactions.
     * @param securityToken security token that needs to be renewed.
     * @param pc provider configuration of the web services client.
     * @return true if succeed in cancelling the issued token.   
     * @exception FAMSTSException if there is an exception in cancelling
     *            issued security token or if the trust authority configuration
     *            is not of STS.     
     */
    public boolean cancelIssuedToken(SecurityToken securityToken,
            ProviderConfig pc) throws FAMSTSException {
       // TODO - To be implemented
        return false;
    }
    
    /**
     * Returns security token obtained from Security Token Service.
     */
    private SecurityToken getSTSToken(ProviderConfig pc, 
            SSOToken ssoToken, ServletContext context) throws FAMSTSException {
        
        STSConfig stsConfig = null;
        TrustAuthorityConfig taconfig = pc.getTrustAuthorityConfig();
        if(taconfig instanceof STSConfig) {
           stsConfig = (STSConfig)taconfig;
        } else {
           throw new FAMSTSException("invalid trust authorityconfig");
        }
        
        String stsEndpoint = stsConfig.getEndpoint();        
        String stsMexAddress = stsConfig.getMexEndpoint();
        STSClientConfiguration config = 
                new STSClientConfiguration(stsEndpoint, stsMexAddress);
        
        if(ssoToken != null) {
           config.setOBOToken(getClientUserToken(ssoToken));
        }

        if (context != null) {
            ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
            Object issuedTokenManager;
            Object issuedTokenContext;
            Object token;
            Class _handlerManager;
            Class _handlerTokenContext;
            Class _handlerToken;
            try {
                ClassLoader cls = FAMClassLoader.getFAMClassLoader(context);
                _handlerManager = cls.loadClass(
                    "com.sun.xml.ws.api.security.trust.client.IssuedTokenManager");
                
                Method getInstance = _handlerManager.getDeclaredMethod("getInstance");
                
                Class clsa[] = new Class[2];
                clsa[0] = Class.forName("com.sun.identity.wss.sts.STSClientConfiguration");
                clsa[1] = Class.forName("java.util.String");
                Method createIssuedTokenContext = _handlerManager.getDeclaredMethod(
                    "createIssuedTokenContext", clsa);

                clsa = new Class[1];
                clsa[0] = Class.forName("com.sun.xml.ws.security.IssuedTokenContext");
                Method getIssuedToken = _handlerManager.getDeclaredMethod("getIssuedToken", clsa);
                
                issuedTokenManager = _handlerManager.newInstance();
                
                getInstance.invoke(issuedTokenManager);

                _handlerTokenContext = cls.loadClass(
                    "com.sun.xml.ws.security.IssuedTokenContext");

                Method getSecurityToken = _handlerTokenContext.getDeclaredMethod("getSecurityToken");

                issuedTokenContext = _handlerTokenContext.newInstance();

                Object args[] = new Object[2];
                args[0] = config;
                args[1] = pc.getWSPEndpoint();
                issuedTokenContext = (IssuedTokenContext) createIssuedTokenContext.invoke(
                issuedTokenManager, args);

                args = new Object[1];
                args[0] = issuedTokenContext;
                getIssuedToken.invoke(issuedTokenManager, args);

                _handlerToken = cls.loadClass("com.sun.xml.ws.security.Token");

                Method getTokenValue = _handlerToken.getDeclaredMethod("getTokenValue");

                token = _handlerToken.newInstance();

                token = (Token) getSecurityToken.invoke(issuedTokenContext);

                Element element = (Element)getTokenValue.invoke(token);

                if(debug.messageEnabled()) {
                debug.message("TrustAuthorityClient.getSTSToken:: Assertion" +
                       "obtained from STS" + XMLUtils.print(element));
                }            
                return new AssertionToken(element);

            } catch (Exception ex) {
                debug.error("TrustAuthorityClient.getSTSToken:: Failed in" +
                    "obtainining STS Token : ", ex);
                // TODO I18n
                throw new FAMSTSException("ws trust exception");
            } catch (Throwable ex) {
                ex.printStackTrace();
                return null;
            } finally {
                Thread.currentThread().setContextClassLoader(oldcc);
            }

        } else {
            try {
                IssuedTokenManager manager = IssuedTokenManager.getInstance();            
                IssuedTokenContext ctx = 
                    manager.createIssuedTokenContext(config, pc.getWSPEndpoint());          
                manager.getIssuedToken(ctx);
                Token issuedToken = ctx.getSecurityToken();            
            
               Element element = (Element)issuedToken.getTokenValue();
               if(debug.messageEnabled()) {
                  debug.message("TrustAuthorityClient.getSTSToken:: Assertion" +
                       "obtained from STS" + XMLUtils.print(element));
               }            
               return new AssertionToken(element);
            } catch (WSTrustException wte) {
               debug.error("TrustAuthorityClient.getSTSToken:: Failed in" +
                    "obtainining STS Token", wte);
               //TODO I18n
               throw new FAMSTSException ("ws trust exception");
            } catch (Exception ex) {
                debug.error("TrustAuthorityClient.getSTSToken:: Failed in" +
                    "parsing SAML Assertion", ex);
               // TODO I18n
               throw new FAMSTSException("SAML exception");
            }
        }
        
    }
    
    /**
     * Returns Liberty token by quering Liberty discovery service
     */
    private SecurityToken getLibertyToken(ProviderConfig pc,
            SSOToken ssoToken) throws FAMSTSException {
        
        // TODO - to be implemented
        return null;
    }

    private static Token getClientUserToken(SSOToken ssoToken) 
                throws FAMSTSException {
                
        if (clientTokenClass == null) {
            String className =   SystemConfigurationUtil.getProperty(
                STSConstants.STS_CLIENT_USER_TOKEN_PLUGIN, 
                "com.sun.identity.wss.sts.STSClientUserToken");
            try {                
                clientTokenClass = Class.forName(className);
 //                       Thread.currentThread().
 //                       getContextClassLoader().loadClass(className);                                
            } catch (Exception ex) {
                 debug.error("TrustAuthorityClient.getClientUserToken:"
                           +  "Failed in obtaining class", ex);
                 throw new FAMSTSException("initializationFailed");
            }
        }
        
        try {
            ClientUserToken userToken = 
                    (ClientUserToken) clientTokenClass.newInstance();
            userToken.init(ssoToken);
            return userToken;
        } catch (Exception ex) {
            debug.error("TrustAuthorityClient.getClientUserToken: " +
                 "Failed in initialization", ex);
             throw new FAMSTSException("initializationFailed");
        }
    }
            
            
}
