/*
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
 * $Id: FAMClientAuthModule.java,v 1.1 2008-05-28 19:50:51 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.sun.identity.wssagents.common.provider;

import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.soap.SOAPMessage;
import java.lang.reflect.Method;
import javax.security.auth.message.module.ClientAuthModule;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.security.trust.WSTrustConstants;
import com.sun.xml.ws.api.message.HeaderList;
import com.sun.identity.classloader.FAMClassLoader;

/**
 * This class <code>FAMClientAuthModule</code> class implements an interface
 * <code>ClientAuthModule</code> defined by the JSR196 and will be invoked
 * by the JSR196 for Securing webservice requests and Validating the
 * webservice responses.
 */

public class FAMClientAuthModule implements ClientAuthModule {
    // Pointer to SOAPRequestHandler class
    private static Class _handler;
    
    // Methods in the class
    private static Method init;
    private static Method secureRequest;
    private static Method print;
    private static Method validateResponse;
    
    // Instance of SOAPRequestHandler
    private Object clientAuthModule;
    private static ClassLoader cls;
    
    /**
     * Initializes the module using configuration defined through deployment
     * descriptors.
     * @param requestPolicy
     * @param responsePolicy
     * @param handler a 
     *             <code>javax.security.auth.callback.CallbackHandler</code>
     * @param options
     * @exception AuthException for any failures.
     */
    public void initialize(MessagePolicy requestPolicy,
               MessagePolicy responsePolicy,
               CallbackHandler handler,
               Map options) throws AuthException {
        
        if(_logger != null) {
            _logger.log(Level.INFO, "FAMClientAuthModule.Init");
        }
        
        ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
        try {            
            if (_handler == null) {
                // Get the FAM Classloader
                cls = FAMClassLoader.getFAMClassLoader(null,jars);
                Thread.currentThread().setContextClassLoader(cls);
                
                // Get the SOAPRequestHandler class
                _handler = cls.loadClass(
                    "com.sun.identity.wss.security.handler.SOAPRequestHandler");
                // Initialize the methods
                Class clsa[] = new Class[1];
                clsa[0] = Class.forName("java.util.Map");
                init = _handler.getDeclaredMethod("init", clsa);
                clsa[0] = Class.forName("org.w3c.dom.Node");
                print = _handler.getDeclaredMethod("print", clsa);
                clsa = new Class[3];
                clsa[0] = Class.forName("javax.xml.soap.SOAPMessage", true, cls);
                clsa[1] = Class.forName("javax.security.auth.Subject");
                clsa[2] = Class.forName("java.util.Map");
                secureRequest = _handler.getDeclaredMethod(
                    "secureRequest", clsa);
                clsa = new Class[2];
                clsa[0] = Class.forName("javax.xml.soap.SOAPMessage", true, cls);
                clsa[1] = Class.forName("java.util.Map");
                validateResponse = _handler.getDeclaredMethod(
                    "validateResponse", clsa);
            }
            // Create an instance of SOAPRequestHandler
            clientAuthModule = _handler.newInstance();
            Object args[] = new Object[1];
            args[0] = options;
            // Initialize SOAPRequestHandler
            init.invoke(clientAuthModule, args);
        } catch (Exception ex) {
            if(_logger != null) {
                _logger.log(Level.SEVERE,
                    "FAMClientAuthModule.initialize failed : " + ex.toString());
            }
            ex.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(oldcc);
        }
    }
    
    /**
     * Secures the outbound request based on the configuration
     * exposed through deployment descriptors.
     * @param messageInfo that has a <code>SOAPMessage</code>.
     * @param clientSubject Client J2EE <code>Subject</code> that will have 
     *        authenticated principal
     * @return AuthStatus if successful
     * @exception AuthException for any failures.
     */
    public AuthStatus secureRequest(MessageInfo messageInfo, 
                      Subject clientSubject) throws AuthException {
        ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cls);
            
            Packet packet = (Packet)messageInfo.getMap().get("REQ_PACKET");
            boolean isTrustMsg = false;
            if ("true".equals(packet.invocationProperties.get(
                    WSTrustConstants.IS_TRUST_MESSAGE))){
                if(_logger != null) {
                    _logger.log(Level.FINE, "FAMClientAuthModule.secureRequest:"
                        + " WS-Trust Message");
                }
                isTrustMsg = true;
                String action = (String)packet.invocationProperties.get(
                    WSTrustConstants.REQUEST_SECURITY_TOKEN_ISSUE_ACTION);
                HeaderList headers = packet.getMessage().getHeaders();
                headers.fillRequestAddressingHeaders(packet,
                   AddressingVersion.W3C, SOAPVersion.SOAP_12,false, action);
            }

            //set the isTrustProperty into MessageInfo
            messageInfo.getMap().put("IS_TRUST_MSG", Boolean.valueOf(isTrustMsg));

            SOAPMessage soapMessage = 
                (SOAPMessage)messageInfo.getRequestMessage();
            Object args[];
            if(_logger != null) {
                args = new Object[1];
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMClientAuthModule.secureRequest: " +
                    "SOAPMessage before securing: " + print.invoke(
                    clientAuthModule, args));
            }
            
            // Invoke the secure request method
            args = new Object[3];
            args[0] = soapMessage;
            args[1] = clientSubject;
            args[2] = messageInfo.getMap();
            soapMessage = (SOAPMessage) secureRequest.invoke(
                clientAuthModule, args);
            
            if(_logger != null) {
                args = new Object[1];
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMClientAuthModule.secureRequest: " +
                    "SOAPMessage after securing: " + print.invoke(
                    clientAuthModule, args));
            }
            return AuthStatus.SUCCESS;
            
        } catch (Exception ex) {
            if(_logger != null) {
                _logger.log(Level.SEVERE, "FAMClientAuthModule.secureRequest: "
                            + " Failed in Securing the Request.");
            }
            ex.printStackTrace();
            AuthException ae = new AuthException("Securing Request Failed");
            ae.initCause(ex);
            throw ae;
        } finally {
            Thread.currentThread().setContextClassLoader(oldcc);
        }
    }
    
    /**
     * Validates the response from the server.
     * @param messageInfo that has a inbound <code>SOAPMessage</code>
     * @param clientSubject Client J2EE <code>Subject</code>
     * @param serviceSubject Service J2EE <code>Subject</code>
     * @return AuthStatus if successful in validating the response.
     * @exception AuthException for any error occured during validation of
     *        the message.
     */
    public AuthStatus validateResponse(MessageInfo messageInfo, 
        Subject clientSubject, Subject serviceSubject) throws AuthException {
        ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cls);
            
            Packet packet = (Packet)messageInfo.getMap().get("RES_PACKET");
            Boolean trustMsgProp = 
                (Boolean) messageInfo.getMap().get("IS_TRUST_MSG");
            boolean isTrustMsg = 
                (trustMsgProp != null) ? trustMsgProp.booleanValue() : false;
            if (isTrustMsg) {
                HeaderList hl = packet.getMessage().getHeaders();
                String action = 
                    hl.getAction(AddressingVersion.W3C, SOAPVersion.SOAP_12);
            }
            
            SOAPMessage soapMessage = 
                (SOAPMessage)messageInfo.getResponseMessage();
            Object args[];
            if(_logger != null) {
                args = new Object[1];
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMClientAuthModule.validateResponse:"
                    + "SOAPMessage before validation: " + print.invoke(
                    clientAuthModule, args));
            }
            
            // Invoke validate response
            args = new Object[2];
            args[0] = soapMessage;
            args[1] = messageInfo.getMap();
            validateResponse.invoke(clientAuthModule, args);
            
            if(_logger != null) {
                args = new Object[1];
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMClientAuthModule.validateResponse:"
                            + " SOAPMessage after validation: " + print.invoke(
                            clientAuthModule, args));
            }
            return AuthStatus.SUCCESS;
        } catch (Exception ex) {
            if(_logger != null) {
                _logger.log(Level.SEVERE, 
                            "FAMClientAuthModule.validateResponse:"
                            + " Failed in validating the response.");
            }
            ex.printStackTrace();
            AuthException ae = new AuthException("Response Validation Failed");
            ae.initCause(ex);
            throw ae;
        } finally {
            Thread.currentThread().setContextClassLoader(oldcc);
        }
    }
    
    /**
     * Clears the subject and shared state.
     * @param messageInfo
     * @param subject
     * @exception AuthException
     */
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws
          AuthException {
        
        if (subject == null) {
            return;
        }
        
    }

    public Class[] getSupportedMessageTypes() {
        return null;
    }
    
    private static Logger _logger = null;
    static {
        LogManager logManager = LogManager.getLogManager();
        _logger = logManager.getLogger(
            "javax.enterprise.system.core.security");
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
