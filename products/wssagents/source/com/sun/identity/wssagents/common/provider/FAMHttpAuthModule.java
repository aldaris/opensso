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
 * $Id: FAMHttpAuthModule.java,v 1.1 2008-05-29 06:46:01 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.sun.identity.wssagents.common.provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.net.URLClassLoader;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.soap.SOAPMessage;
import javax.security.auth.message.module.ServerAuthModule;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.sun.identity.classloader.FAMClassLoader;

/**
 * The <code>FAMHttpAuthModule</code> class implements an interface
 * <code>ServerAuthModule</code> defined by the JSR196 and will be invoked
 * by the JSR196 for Validating webservice requests and Securing the
 * webservice responses.
 */
public class FAMHttpAuthModule implements ServerAuthModule {
    // Pointer to HttpRequestHandler
    private static Class _handler;
    
    // Methods in HttpRequestHandler
    private static Method init;
    private static Method shouldAuthenticate;
    private static Method getLoginURL;
    
    // Instance of HttpRequestHandler
    private Object httpAuthModule;
    private static ClassLoader cls;
    
    /**
     * Initializes the module using the configuration defined through
     * deployment descriptors.
     * @param requestPolicy
     * @param responsePolicy
     * @param handler a
     *     <code>javax.security.auth.callback.CallbackHandler</code>
     * @param options
     * @exception AuthException for any failures.
     */
    public void initialize(MessagePolicy requestPolicy,
	       MessagePolicy responsePolicy,
	       CallbackHandler handler,
	       Map options) throws AuthException {
        /*
        if(_logger != null) {
            _logger.log(Level.INFO, "FAMHttpAuthModule.Init");
        }
        
        ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
        try {
            if (_handler == null) {
                // Get the FAM Classloader
                cls = FAMClassLoader.getFAMClassLoader(null,jars);
                Thread.currentThread().setContextClassLoader(cls);
                
                // Get a pointer to the class
                _handler = cls.loadClass(
                    "com.sun.identity.wss.security.handler.HTTPRequestHandler");
                // Get the methods
                Class clsa[] = new Class[1];
                clsa[0] = Class.forName("java.util.Map");
                init = _handler.getDeclaredMethod("init", clsa);
                clsa[0] = 
                    Class.forName("javax.servlet.http.HttpServletRequest");
                getLoginURL = _handler.getDeclaredMethod("getLoginURL", clsa);
                clsa = new Class[2];
                clsa[0] = Class.forName("javax.security.auth.Subject");
                clsa[1] = 
                    Class.forName("javax.servlet.http.HttpServletRequest");
                shouldAuthenticate = _handler.getDeclaredMethod(
                    "shouldAuthenticate", clsa);
            }
            // Instantiate HttpRequestHandler & initialize
            httpAuthModule = _handler.newInstance();
            Object[] args = new Object[1];
            args[0] = options;
            init.invoke(httpAuthModule, args);
        } catch (Exception ex) {
            if(_logger != null) {
                _logger.log(Level.SEVERE,
                    "FAMHttpAuthModule.initialize failed");
            }
            ex.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(oldcc);
        }*/
    }
    
    /**
     * Validates the SOAP Request based on the configuration
     * enforcement on the webservice endpoint and exposes the principal
     * and credentials for the application.
     *
     * @param messageInfo that has a inbound <code>SOAPMessage</code>
     * @param clientSubject Client J2EE <code>Subject</code>
     * @param serviceSubject Service J2EE <code>Subject</code>
     * @return AuthStatus if successful in validating the request.
     * @exception AuthException if there is an error occured in validating
     *            the request.
     */
    public AuthStatus validateRequest(MessageInfo messageInfo,
			       Subject clientSubject,
			       Subject serviceSubject) throws AuthException {
        
        /*ClassLoader oldcc = Thread.currentThread().getContextClassLoader();
        
        HttpServletAuthParam httpAuthParam = (HttpServletAuthParam)param;
        HttpServletRequest request = httpAuthParam.getRequest();
        HttpServletResponse response = httpAuthParam.getResponse();
        
        // Check if you needs to authenticate
        Object[] args = new Object[2];
        args[0] = subject;
        args[1] = request;
        Boolean authN = Boolean.FALSE;
        try {
            Thread.currentThread().setContextClassLoader(cls);
            authN = (Boolean) shouldAuthenticate.invoke(httpAuthModule, args);
        } catch (Exception ex) {
            if(_logger != null) {
                _logger.log(Level.SEVERE, "FAMHttpAuthModule.validateRequest " 
                            + "shouldAuthenticate failed");
            }
            ex.printStackTrace();
            AuthException ae = new AuthException(ex.getMessage());
            ae.initCause(ex);
            throw (ae);
        } finally {
            Thread.currentThread().setContextClassLoader(oldcc);
        }
        
        if (authN.booleanValue()) {
            args = new Object[1];
            args[0] = request;
            String loginURL = null;
            try {
                Thread.currentThread().setContextClassLoader(cls);
                loginURL = (String) getLoginURL.invoke(httpAuthModule, args);
            } catch (Exception ex) {
                if(_logger != null) {
                    _logger.log(Level.SEVERE, 
                                "FAMHttpAuthModule.validateRequest " +
                                "getLoginURL failed");
                }
                ex.printStackTrace();
                AuthException ae = new AuthException(ex.getMessage());
                ae.initCause(ex);
                throw (ae);
            } finally {
                Thread.currentThread().setContextClassLoader(oldcc);
            }
            
            if(_logger != null) {
                _logger.log(Level.FINE,
                    "FAMHttpAuthModule.validateRequest: LoginURL :" 
                    + loginURL);
            }
            try {
                response.sendRedirect(loginURL);
            } catch (IOException ie) {
                throw new AuthException(ie.getMessage());
            } catch (UnsupportedOperationException uae) {
                try {
                    response.sendRedirect(loginURL);
                } catch (IOException ie) {
                    AuthException ae = new AuthException(ie.getMessage());
                    ae.initCause(ie);
                    throw (ae);
                }
            }
        }*/
        return AuthStatus.SUCCESS;
    }
    
    /**
     * Secures the response sending by the application by reading
     * the configuration from the application deployment descriptors.
     *
     * @param messageInfo that has a <code>SOAPMessage</code>.
     * @param serviceSubject Service J2EE <code>Subject</code> that will have 
     *        authenticated principal
     * @return AuthStatus if successful
     * @exception AuthException for any error while securing the response.
     */
    public AuthStatus secureResponse(MessageInfo messageInfo, 
        Subject serviceSubject) throws AuthException {
        return AuthStatus.SUCCESS;
    }
    
    /**
     * Diposes the subject and security credentials.
     * @param messageInfo
     * @param subject
     * @exception AuthException if there is an error occured while disposing 
     *     the subject.
     */
    public void cleanSubject(MessageInfo messageInfo, Subject subject)
	throws AuthException {
        
        if(subject == null) {
            throw new AuthException("nullSubject");
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
