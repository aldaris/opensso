/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: TrustAuthorityClient.java,v 1.13 2008-07-02 16:57:23 mallas Exp $
 *
 */

package com.sun.identity.wss.sts;

import com.sun.identity.wss.security.SecurityMechanism;
import java.util.List;
import org.w3c.dom.Element;
import javax.servlet.ServletContext;

import com.sun.identity.shared.xml.XMLUtils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wss.security.SecurityToken;
import com.sun.identity.wss.provider.ProviderConfig;
import com.sun.identity.wss.provider.TrustAuthorityConfig;
import com.sun.identity.wss.provider.STSConfig;
import com.sun.identity.wss.security.AssertionToken;
import com.sun.identity.wss.security.FAMSecurityToken;
import com.sun.identity.wss.security.SAML2Token;
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
    
    /** 
     * Creates a new instance of TrustAuthorityClient.
     */
    public TrustAuthorityClient() {
    }
    
    /**
     * Returns the <code>SecurityToken</code> for the web services client from
     * a trusted authority, which is Security Token Service (STS). 
     * The web services client configuation and web service
     * information is identified by the provider configuration.
     *
     * @param pc Provider configuration of the web services client.
     * @param credential User's credential. The user's credential could be
     *        Single Sign-On Token or a SAML Assertion or any other object.
     * @return SecurityToken security token for the web services consumer.   
     * @exception FAMSTSException if it's unable to retrieve security token.
     */
    public SecurityToken getSecurityToken(
            ProviderConfig pc,            
            Object credential) throws FAMSTSException {
        return getSecurityToken(pc,null,null,null,credential,null,null);
    }
    
    /**
     * Returns the <code>SecurityToken</code> for the web services client from
     * a trusted authority, which is Security Token Service (STS). 
     * The web services client configuation and web service
     * information is identified by the provider configuration.
     *
     * @param pc Provider configuration of the web services client.
     * @param credential User's credential. The user's credential could be
     *        Single Sign-On Token or a SAML Assertion or any other object.
     * @param context Web context under which this class is running.
     * @return SecurityToken security token for the web services consumer.   
     * @exception FAMSTSException if it's unable to retrieve security token.
     */
    public SecurityToken getSecurityToken(
            ProviderConfig pc,            
            Object credential,
            ServletContext context) throws FAMSTSException {
        return getSecurityToken(pc,null,null,null,credential,null,context);
    }
    
    /**
     * Returns the <code>SecurityToken</code> for the web services client from
     * a trusted authority, which is Security Token Service (STS). 
     *
     * @param wspEndPoint Web Service Provider end point.
     * @param stsEndPoint Security Token Service end point.
     * @param stsMexEndPoint Security Token Service MEX end point.
     * @param credential user's credential.
     * @param securityMech Required Security Mechanism by Web Service Client.
     * @param context web context under which this class is running.
     * @return SecurityToken security token for the web services consumer.   
     * @exception FAMSTSException if it's unable to retrieve security token.
     */
    public SecurityToken getSecurityToken(
            String wspEndPoint,
            String stsEndPoint,
            String stsMexEndPoint,
            Object credential,
            String securityMech,
            ServletContext context) throws FAMSTSException {
        return getSecurityToken(null,wspEndPoint,stsEndPoint,stsMexEndPoint,
                credential,securityMech,context);
    }

    // Gets Security Token from Security Token Service.
    private SecurityToken getSecurityToken(
            ProviderConfig pc,
            String wspEndPoint,
            String stsEndPoint,
            String stsMexEndPoint,
            Object credential,
            String securityMech,
            ServletContext context) throws FAMSTSException {
        String keyType = STSConstants.PUBLIC_KEY;
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
            
            STSConfig stsConfig = null;
            TrustAuthorityConfig taconfig = pc.getTrustAuthorityConfig();
            if(taconfig instanceof STSConfig) {
               stsConfig = (STSConfig)taconfig;
            } else {
               throw new FAMSTSException(
                       STSUtils.bundle.getString("invalidtaconfig"));
            }

            stsEndPoint = stsConfig.getEndpoint();        
            stsMexEndPoint = stsConfig.getMexEndpoint();
            String stsSecMech = (String)stsConfig.getSecurityMech().get(0);
            if(stsSecMech.equals(
                    SecurityMechanism.WSS_NULL_KERBEROS_TOKEN_URI) ||
               stsSecMech.equals(
                    SecurityMechanism.WSS_NULL_USERNAME_TOKEN_PLAIN_URI) ||
               stsSecMech.equals(SecurityMechanism.STS_SECURITY_URI)) {               
               keyType = STSConstants.BEARER_KEY;
            }
            wspEndPoint = pc.getWSPEndpoint();
        }
        
        if(securityMech.equals(SecurityMechanism.STS_SECURITY_URI)) {
           return getSTSToken(wspEndPoint,stsEndPoint,stsMexEndPoint,
                   credential,keyType, context); 
        } else if (securityMech.equals(
                SecurityMechanism.LIBERTY_DS_SECURITY_URI)) {
           return getLibertyToken(pc, credential);
        } else {
           debug.error("TrustAuthorityClient.getSecurityToken" +
                   "Invalid security mechanism to get token from TA");
           return null;
        }        
        
    }
    
    /**
     * Renews the issued security token that was obtained from previous
     * interactions.
     * This method applies only for the STS Tokens.
     * @param securityToken security token that needs to be renewed.
     * @param pc provider configuration of the web services client.
     * @param ssoToken single sign-on token of the principal.     
     * @return SecurityToken security token for the web services consumer.   
     * @exception FAMSTSException if it's unable to renew security token or
     *            if the trust authority configuration is not of STS.
     */
    public SecurityToken renewIssuedToken(SecurityToken securityToken,
            ProviderConfig pc,            
            Object ssoToken) throws FAMSTSException {
        //TODO To be implemented
        throw new FAMSTSException("unsupported");
        
    }
    
    /**
     * Cancels the issued security token that was obtained from previous 
     * interactions.
     * This method applies only for the STS Tokens.
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
        throw new FAMSTSException("unsupported");
    }
    
    /**
     * Returns security token obtained from Security Token Service.
     */
    private SecurityToken getSTSToken(String wspEndPoint,
                                      String stsEndpoint,
                                      String stsMexAddress,
                                      Object credential,
                                      String keyType,
                                      ServletContext context) 
                                      throws FAMSTSException {
        
        if(debug.messageEnabled()) {
            debug.message("TrustAuthorityClient.getSTSToken:: stsEndpoint : " 
                + stsEndpoint);
            debug.message("TrustAuthorityClient.getSTSToken:: stsMexAddress : " 
                + stsMexAddress);
            debug.message("TrustAuthorityClient.getSTSToken:: wsp end point : " 
                + wspEndPoint);
        }

        ClassLoader oldcc = Thread.currentThread().getContextClassLoader();        
        try {                       
            ClassLoader cls = 
                       FAMClassLoader.getFAMClassLoader(context,jars);
            Thread.currentThread().setContextClassLoader(cls);

            Class _handlerTrustAuthorityClient = cls.loadClass(
                       "com.sun.identity.wss.sts.TrustAuthorityClientImpl");

            Constructor taClientCon = 
                        _handlerTrustAuthorityClient.getConstructor();                

            Object stsClient = taClientCon.newInstance();

            Class clsa[] = new Class[5];
            clsa[0] = Class.forName("java.lang.String");
            clsa[1] = Class.forName("java.lang.String");
            clsa[2] = Class.forName("java.lang.String");
            clsa[3] = Class.forName("java.lang.Object");
            clsa[4] = Class.forName("java.lang.String");

            Method getSTSTokenElement = 
                      stsClient.getClass().getDeclaredMethod(
                      "getSTSTokenElement", clsa);

            Object args[] = new Object[5];
            args[0] = wspEndPoint;
            args[1] = stsEndpoint;
            args[2] = stsMexAddress;
            args[3] = credential;
            args[4] = keyType;
            Element element = (Element)getSTSTokenElement.invoke(stsClient, args);
            String type = getTokenType(element);
            
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
                } else if (type.equals(SecurityToken.WSS_FAM_SSO_TOKEN)) {
                    return new FAMSecurityToken(element);    
                } else {
                    throw new FAMSTSException (
                            STSUtils.bundle.getString("unsupportedtokentype"));
                }
            } else {
               throw new FAMSTSException (
                       STSUtils.bundle.getString("nulltokentype"));
            }

        } catch (Exception ex) {
            debug.error("TrustAuthorityClient.getSTSToken:: Failed in" +
                "obtainining STS Token : ", ex);            
            throw new FAMSTSException(
                    STSUtils.bundle.getString("wstrustexception"));
        } finally {
            Thread.currentThread().setContextClassLoader(oldcc);
        }

    }
    
    /**
     * Returns Liberty token by quering Liberty discovery service
     */
    private SecurityToken getLibertyToken(ProviderConfig pc,
            Object ssoToken) throws FAMSTSException {
        
        // TODO - to be implemented
        throw new FAMSTSException(
                STSUtils.bundle.getString("unsupportedoperation"));
    }
    
    private String getTokenType (Element element) throws FAMSTSException {
        String elemName = element.getLocalName(); 
        if (elemName == null) {
            throw new FAMSTSException(
                    STSUtils.bundle.getString("invalidelementname"));
        }

        if (elemName.equals(STSConstants.ASSERTION_ELEMENT)) {
            String attrValue = 
                element.getAttribute(STSConstants.SAML20_NAMESPACE);
            if ((attrValue != null) && (attrValue.length() != 0) 
                && (attrValue.equals(STSConstants.SAML20_ASSERTION_TOKEN_TYPE)) ) {
                return STSConstants.SAML20_ASSERTION_TOKEN_TYPE;
            }
            attrValue = element.getAttribute(STSConstants.SAML10_NAMESPACE);
            if ((attrValue != null) && (attrValue.length() != 0) 
                && (attrValue.equals(STSConstants.SAML10_ASSERTION)) ) {
                return STSConstants.SAML11_ASSERTION_TOKEN_TYPE;
            }
        } else if(elemName.equals("FAMToken")) {
            return SecurityToken.WSS_FAM_SSO_TOKEN;
        } else {
            // TBD for other token types.
            return "getTokenType:NOT IMPLEMENTED TOKEN TYPE";
        }
        return null;
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
        "openssoclientsdk.jar",
        "openssowssproviders.jar",
        "xalan.jar",
        "xercesImpl.jar"
    };        
}
