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
 * $Id: FAMServerAuthModule.java,v 1.2 2007-08-28 00:38:45 mallas Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wssagents.glassfish;

import java.util.Map;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URLClassLoader;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.soap.SOAPMessage;

import javax.security.auth.message.module.ServerAuthModule;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import com.sun.xml.ws.api.message.Packet;

import java.lang.reflect.Method;

import com.sun.identity.wssagents.common.FAMClientClassLoader;

/**
 * The <code>FAMServerAuthModule</code> class implements an interface
 * <code>ServerAuthModule</code> defined by the JSR196 and will be invoked
 * by the JSR196 for validating webservice requests and securing the
 * webservice responses.
 */

public class FAMServerAuthModule implements ServerAuthModule {
    // Pointer to SOAPRequestHandler class
    private static Class _handler;
    // Methods in SOAPRequestHandler class
    private static Method init;
    private static Method validateRequest;
    private static Method print;
    private static Method secureResponse;
    
    // Instance of SOAPRequestHandler
    private Object serverAuthModule;
    
    /**
     * Initializes the module using the configuration defined through
     * deployment descriptors.
     * @param requestPolicy
     * @param responsePolicy
     * @param handler a
     * <code>javax.security.auth.callback.CallbackHandler</code>
     * @param options
     */
    
    public void initialize(MessagePolicy requestPolicy,
	       MessagePolicy responsePolicy,
	       CallbackHandler handler,
	       Map options)
	throws AuthException {
        
        if(_logger != null) {
            _logger.log(Level.INFO, "FAMServerAuthModule.Init");
        }
        
        try {
            if (_handler == null) {
                // Obtain the class loader
                URLClassLoader cls =
                    FAMClientClassLoader.getFAMClientClassLoader();
                // Obtain in the instance of class
                _handler = cls.loadClass(
                    "com.sun.identity.wss.security.handler.SOAPRequestHandler");
                // Get the methods
                Class clsa[] = new Class[1];
                clsa[0] = Class.forName("java.util.Map");
                init = _handler.getDeclaredMethod("init", clsa);
                clsa[0] = Class.forName("org.w3c.dom.Node");
                print = _handler.getDeclaredMethod("print", clsa);
                clsa = new Class[5];
                clsa[0] = Class.forName("javax.xml.soap.SOAPMessage");
                clsa[1] = Class.forName("javax.security.auth.Subject");
                clsa[2] = Class.forName("java.util.Map");
                clsa[3] = 
                    Class.forName("javax.servlet.http.HttpServletRequest");
                clsa[4] = 
                    Class.forName("javax.servlet.http.HttpServletResponse");
                validateRequest = _handler.getDeclaredMethod(
                    "validateRequest", clsa);
                clsa = new Class[2];
                clsa[0] = Class.forName("javax.xml.soap.SOAPMessage");
                clsa[1] = Class.forName("java.util.Map");
                secureResponse = _handler.getDeclaredMethod(
                    "secureResponse", clsa);
            }
            // Construct an instance of SOAPRequestHandler & initialize
            serverAuthModule = _handler.newInstance();
            Object args[] = new Object[1];
            args[0] = options;
            init.invoke(serverAuthModule, args);
        } catch(Exception ex) {
            if(_logger != null) {
                _logger.log(Level.SEVERE,
                    "FAMServerAuthModule.initialization failed");
            }
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Validates the SOAP Request based on the configuration
     * enforcement on the webservice endpoint and exposes the principal
     * and credentials for the application.
     *
     * @param param <code>AuthParam</code> that has a <code>SOAPMessage</code>
     * @param subj <code>Principal</code> of the authenticated entity.
     * @param sharedState Any shared state that need to be used by the provider
     *        between request validation and securing the response.
     * @exception AuthException if there is an error occured in validating
     *            the request.
     */
    public AuthStatus validateRequest(MessageInfo messageInfo,
			       Subject clientSubject,
			       Subject serviceSubject) throws AuthException {        
        
        try {
            SOAPMessage soapMessage = (SOAPMessage)messageInfo.getRequestMessage();
            //SOAPMessage soapMessage = ((SOAPAuthParam)param).getRequest();
            Object args[] = new Object[1];
            if(_logger != null) {
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMServerAuthModule.validateRequest: "
                            + "SOAPMessage before validation: " + print.invoke(
                            serverAuthModule, args));
            }
            args = new Object[5];
            args[0] = soapMessage;
            args[1] = clientSubject;
            args[2] = messageInfo.getMap();
            clientSubject = (Subject) validateRequest.invoke(serverAuthModule, args);
            Packet packet = (Packet)messageInfo.getMap().get("REQ_PACKET");
            packet.invocationProperties.put("javax.security.auth.Subject", clientSubject);
            if(_logger != null) {
                args = new Object[1];
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMServerAuthModule.validateRequest: "
                            + "SOAPMessage after validation: " + print.invoke(
                            serverAuthModule, args));
            }
            
        } catch(Exception sbe) {
            if(_logger != null) {
                _logger.log(Level.SEVERE, "AMServerAuthModule.validateRequest:"
                    + " Failed in Securing the Request.");
            }
            sbe.printStackTrace();
            AuthException ae = new AuthException("Validating Request failed");
            ae.initCause(sbe);
            throw ae;
        }
        return AuthStatus.SUCCESS;
    }
    
    /**
     * Secures the response sending by the application by reading
     * the configuration from the application deployment descriptors.
     *
     * @param param <code>AuthParam</code> that has a <code>SOAPMessage<code>
     * (<code>SOAPResponse</code>);
     * @param sub Principal's subject
     * @param sharedState sharedMap between the request and response.
     * @exception AuthException for any error while securing the response.
     */
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject)
	throws AuthException {
        try {
            SOAPMessage soapMessage = (SOAPMessage)messageInfo.getResponseMessage();
            //SOAPMessage soapMessage = ((SOAPAuthParam)param).getResponse();
            Object[] args = new Object[1];
            args[0] = soapMessage.getSOAPPart().getEnvelope();
            if(_logger != null) {
                _logger.log(Level.FINE, "FAMServerAuthModule.secureResponse: " 
                            + "SOAPMessage before securing: " + print.invoke(
                            serverAuthModule, args));
            }
            args = new Object[2];
            args[0] = soapMessage;
            args[1] = messageInfo.getMap();
            soapMessage = (SOAPMessage) secureResponse.invoke(
                serverAuthModule, args);
            if(_logger != null) {
                args = new Object[1];
                args[0] = soapMessage.getSOAPPart().getEnvelope();
                _logger.log(Level.FINE, "FAMServerAuthModule.secureResponse: " 
                            + "SOAPMessage after securing: " + print.invoke(
                            serverAuthModule, args));
            }
            
        } catch(Exception ie) {
            if(_logger != null) {
                _logger.log(Level.SEVERE, "FAMClientAuthModule.secureResponse:"
                            + " Failed in securing the response.");
            }
            ie.printStackTrace();
            AuthException ae = new AuthException("Securing response failed");
            ae.initCause(ie);
            throw ae;
        }
        return AuthStatus.SUCCESS;
    }
    
    /**
     * Diposes the subject and security credentials.
     * @param subject <code>Subject</code> that was set during the message
     *                 authentication.
     * @param sharedState Any shared state that was set during the message
     *                    authentication.
     * @exception if there is an error occured while disposing the subject.
     */
    
    public void cleanSubject(MessageInfo messageInfo, Subject subject)
	throws AuthException {
        
        if(subject == null) {
            throw new AuthException("nullSubject");
        }
    }
    
    
    private static Logger _logger = null;
    
    static {
        LogManager logManager = LogManager.getLogManager();
        _logger = logManager.getLogger(
            "javax.enterprise.system.core.security");
    }
    
     public Class[] getSupportedMessageTypes() {
         return null;
     }
}
