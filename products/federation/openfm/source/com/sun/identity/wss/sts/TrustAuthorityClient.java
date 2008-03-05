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
 * $Id: TrustAuthorityClient.java,v 1.8 2008-03-05 18:24:25 mrudul_uchil Exp $
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
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.identity.wss.security.AssertionToken;
import com.sun.identity.wss.security.SAML2Token;
import com.sun.xml.ws.api.security.trust.WSTrustException;
import com.sun.identity.common.SystemConfigurationUtil;

import com.sun.identity.classloader.FAMClassLoader;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

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
        return getSecurityToken(pc,null,null,null,ssoToken,null,null);
    }
    
    public SecurityToken getSecurityToken(
            ProviderConfig pc,            
            SSOToken ssoToken,
            ServletContext context) throws FAMSTSException {
        return getSecurityToken(pc,null,null,null,ssoToken,null,context);
    }
    
    public SecurityToken getSecurityToken(
            String wspEndPoint,
            String stsEndPoint,
            String stsMexEndPoint,
            SSOToken ssoToken,
            String securityMech,
            ServletContext context) throws FAMSTSException {
        return getSecurityToken(null,wspEndPoint,stsEndPoint,stsMexEndPoint,
                ssoToken,securityMech,context);
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
            String wspEndPoint,
            String stsEndPoint,
            String stsMexEndPoint,
            SSOToken ssoToken,
            String securityMech,
            ServletContext context) throws FAMSTSException {
        if (pc != null) {
            List securityMechanisms = pc.getSecurityMechanisms();
            if(securityMechanisms == null || securityMechanisms.isEmpty()) {
               if(debug.messageEnabled()) {
                  debug.message("TrustAuthorityClient.getSecurityToken::"+
                          "Security Mechanisms are not configured");
               }
               return null;
            }
            securityMech = (String)securityMechanisms.get(0);
        }
        
        if(securityMech.equals(SecurityMechanism.STS_SECURITY_URI)) {
           return getSTSToken(pc,wspEndPoint,stsEndPoint,stsMexEndPoint,
                   ssoToken,context); 
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
                                      String wspEndPoint,
                                      String stsEndpoint,
                                      String stsMexAddress,
                                      SSOToken ssoToken, 
                                      ServletContext context) 
                                      throws FAMSTSException {
        if (pc != null) {
            STSConfig stsConfig = null;
            TrustAuthorityConfig taconfig = pc.getTrustAuthorityConfig();
            if(taconfig instanceof STSConfig) {
               stsConfig = (STSConfig)taconfig;
            } else {
               throw new FAMSTSException("invalid trust authority config");
            }

            stsEndpoint = stsConfig.getEndpoint();        
            stsMexAddress = stsConfig.getMexEndpoint();
            wspEndPoint = pc.getWSPEndpoint();
        }
        
        if(debug.messageEnabled()) {
            debug.message("TrustAuthorityClient.getSTSToken:: stsEndpoint : " 
                + stsEndpoint);
            debug.message("TrustAuthorityClient.getSTSToken:: stsMexAddress : " 
                + stsMexAddress);
            debug.message("TrustAuthorityClient.getSTSToken:: wsp end point : " 
                + wspEndPoint);
        }

        ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
        if (context != null) {
            Object issuedTokenManager;
            Object stsClient;
            Object issuedTokenContext;
            Object token;
            Class _handlerSTSClient;
            Class _handlerManager;
            Class _handlerTokenContext;
            Class _handlerToken;
            try {
                ClassLoader cls = 
                    FAMClassLoader.getFAMClassLoader(context,jars);
                Thread.currentThread().setContextClassLoader(cls);
                _handlerSTSClient = 
                    cls.loadClass(
                    "com.sun.identity.wss.sts.STSClientConfiguration");
                
                Class clsaC[] = new Class[2];
                clsaC[0] = Class.forName("java.lang.String");
                clsaC[1] = Class.forName("java.lang.String");
                Constructor stsClientCon = 
                    _handlerSTSClient.getConstructor(clsaC);                
                
                Object argsC[] = new Object[2];
                argsC[0] = stsEndpoint;
                argsC[1] = stsMexAddress;
                stsClient = stsClientCon.newInstance(argsC);
               
                Method getTokenType = 
                    stsClient.getClass().getDeclaredMethod("getTokenType");
                String type =  (String)getTokenType.invoke(stsClient);
                
                if(ssoToken != null) {
                    Class clsaM[] = new Class[1];
                    clsaM[0] = 
                        Class.forName("com.sun.xml.ws.security.Token",
                            true, cls);
                    Method setOBOToken = 
                        stsClient.getClass().getDeclaredMethod(
                            "setOBOToken", clsaM);
                
                    Object argsM[] = new Object[1];
                    argsM[0] = getClientUserToken(ssoToken,cls);
                    setOBOToken.invoke(stsClient, argsM);          
                }        
                
                _handlerManager = 
                    cls.loadClass(
                    "com.sun.xml.ws.api.security.trust.client.IssuedTokenManager");
                
                Method getInstance = 
                    _handlerManager.getDeclaredMethod("getInstance");              
                                
                issuedTokenManager = getInstance.invoke(_handlerManager);
                
                Class clsa[] = new Class[2];
                clsa[0] = 
                    Class.forName(
                    "com.sun.xml.ws.api.security.trust.client.IssuedTokenConfiguration",
                    true, cls);
                clsa[1] = Class.forName("java.lang.String");
                Method createIssuedTokenContext = 
                    issuedTokenManager.getClass().getDeclaredMethod(
                    "createIssuedTokenContext", clsa);

                clsa = new Class[1];
                clsa[0] = 
                    Class.forName("com.sun.xml.ws.security.IssuedTokenContext",
                    true, cls);
                Method getIssuedToken = 
                    issuedTokenManager.getClass().getDeclaredMethod(
                    "getIssuedToken", clsa);              
                
                _handlerTokenContext = cls.loadClass(
                    "com.sun.xml.ws.security.IssuedTokenContext");
         
                Object args[] = new Object[2];
                args[0] = stsClient;
                args[1] = wspEndPoint;
                issuedTokenContext = 
                    createIssuedTokenContext.invoke(issuedTokenManager, args);

                args = new Object[1];
                args[0] = issuedTokenContext;
                getIssuedToken.invoke(issuedTokenManager, args);

                Method getSecurityToken = 
                    _handlerTokenContext.getDeclaredMethod("getSecurityToken");
                
                _handlerToken = cls.loadClass("com.sun.xml.ws.security.Token");

                token = getSecurityToken.invoke(issuedTokenContext);

                Method getTokenValue = 
                    _handlerToken.getDeclaredMethod("getTokenValue");

                Element element = (Element)getTokenValue.invoke(token);

                if(debug.messageEnabled()) {
                    debug.message("TrustAuthorityClient.getSTSToken:: Token "
                        + "type : " + type);
                    debug.message("TrustAuthorityClient.getSTSToken:: Assertion"
                        + " obtained from STS : " + XMLUtils.print(element));
                }
                
                if (type != null) {
                    if (type.equals(STSConstants.SAML20_ASSERTION_TOKEN_TYPE)) {
                        return new SAML2Token(element);
                    } else if (
                        type.equals(STSConstants.SAML11_ASSERTION_TOKEN_TYPE)) {
                        return new AssertionToken(element);    
                    } else {
                        throw new FAMSTSException ("Token type not supported.");
                    }
                } else {
                   throw new FAMSTSException ("Token type is NULL.");
                }

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
            STSClientConfiguration config = 
                new STSClientConfiguration(stsEndpoint, stsMexAddress);
        
            String type = config.getTokenType();
            if(ssoToken != null) {
                config.setOBOToken(getClientUserToken(ssoToken, oldcc));
            }
            try {
                IssuedTokenManager manager = IssuedTokenManager.getInstance();            
                IssuedTokenContext ctx = 
                    manager.createIssuedTokenContext(config,wspEndPoint);          
                manager.getIssuedToken(ctx);
                Token issuedToken = ctx.getSecurityToken();            
            
               Element element = (Element)issuedToken.getTokenValue();
               if(debug.messageEnabled()) {
                    debug.message("TrustAuthorityClient.getSTSToken:: Token "
                        + "type : " + type);
                    debug.message("TrustAuthorityClient.getSTSToken:: Assertion"
                        + " obtained from STS : " + XMLUtils.print(element));
                }
                if (type != null) {
                    if (type.equals(STSConstants.SAML20_ASSERTION_TOKEN_TYPE)) {
                        return new SAML2Token(element);
                    } else if (
                        type.equals(STSConstants.SAML11_ASSERTION_TOKEN_TYPE)) {
                        return new AssertionToken(element);    
                    } else {
                        throw new FAMSTSException ("Token type not supported.");
                    }
                } else {
                   throw new FAMSTSException ("Token type is NULL.");
                }
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

    private static Token getClientUserToken(SSOToken ssoToken, ClassLoader cls) 
                throws FAMSTSException {
                
        if (clientTokenClass == null) {
            String className =   SystemConfigurationUtil.getProperty(
                STSConstants.STS_CLIENT_USER_TOKEN_PLUGIN, 
                "com.sun.identity.wss.sts.STSClientUserToken");
            try {                
                //clientTokenClass = Class.forName(className, true, cls);
                clientTokenClass = cls.loadClass(className);                               
            } catch (Exception ex) {
                 debug.error("TrustAuthorityClient.getClientUserToken:"
                           +  "Failed in obtaining class", ex);
                 throw new FAMSTSException("initializationFailed");
            }
        }
        
        try {
            Constructor stsClientToken = 
                    clientTokenClass.getConstructor();               
            Object stsClientTokenObj = stsClientToken.newInstance();
            
            Class _handlerclientTokenClass = 
                cls.loadClass("com.sun.identity.wss.sts.ClientUserToken");
            Object clientUserToken = 
                _handlerclientTokenClass.cast(stsClientTokenObj);
            
            Class clsaM[] = new Class[1];
            clsaM[0] = Class.forName("java.lang.Object");
            Method init = clientUserToken.getClass().getDeclaredMethod(
                            "init", clsaM);
            Object argsM[] = new Object[1];
            argsM[0] = ssoToken;
            
            clientUserToken = init.invoke(clientUserToken, argsM);
            ClientUserToken clUserToken = (ClientUserToken) clientUserToken;
            if(debug.messageEnabled()) {
                debug.message("getClientUserToken:Client User Token : " + 
                    clUserToken);
            }
            return clUserToken;
        } catch (Exception ex) {
            debug.error("TrustAuthorityClient.getClientUserToken: " +
                 "Failed in initialization", ex);
             throw new FAMSTSException("initializationFailed");
        }
    }
            
    /**
     * The list of jar files to be loaded by FAMClassLoader.
     */
    public static String[] jars = new String[]{
        "webservices-api.jar",
        "webservices-rt.jar",
        "webservices-tools.jar",
        "webservices-extra-api.jar",
        "webservices-extra.jar",
        "openssoclientsdk.jar"
    };        
}
