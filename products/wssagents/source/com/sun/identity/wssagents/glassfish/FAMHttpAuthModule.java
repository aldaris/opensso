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
 * $Id: FAMHttpAuthModule.java,v 1.1 2007-06-08 06:39:01 mrudul_uchil Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */
 
package com.sun.identity.wssagents.glassfish;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.net.URLClassLoader;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.enterprise.security.jauth.AuthParam;
import com.sun.enterprise.security.jauth.AuthPolicy;
import com.sun.enterprise.security.jauth.HttpServletAuthParam;
import com.sun.enterprise.security.jauth.AuthException;
import com.sun.enterprise.security.jauth.ServerAuthModule;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.sun.identity.wssagents.common.FAMClientClassLoader;

/**
 * This interface describes a module that can be configured
 * for a ServerAuthContext. The main purpose of this module
 * is to validate client requests and to secure responses back to the client.
 *
 * <p> A module implementation must assume it may be shared
 * across different requests from different clients.
 * It is the module implementation's responsibility to properly
 * store and restore any state necessary to associate new requests
 * with previous responses.  A module that does not need to do so
 * may remain completely stateless.
 *
 * <p> Modules are passed a shared state Map that can be used
 * to save state across a sequence of calls from <code>validateRequest</code>
 * to <code>secureResponse</code> to <code>disposeSubject</code>.
 * The same Map instance is guaranteed to be passed to all methods
 * in the call sequence.  Furthermore, it should be assumed that
 * each call sequence is passed its own unique shared state Map instance.
 *
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
    
    /**
     * Initializes this module with a policy to enforce,
     * a CallbackHandler, and administrative options.
     *
     * <p> Either the the request policy or the response policy (or both)
     * must be non-null.
     *
     * @param requestPolicy the request policy this module is to enforce,
     *		which may be null.
     *
     * @param responsePolicy the response policy this module is to enforce,
     *		which may be null.
     *
     * @param handler CallbackHandler used to request information
     *		from the caller.
     *
     * @param options administrative options.
     */
    public void initialize(AuthPolicy requestPolicy,
        AuthPolicy responsePolicy,
        CallbackHandler handler,
        Map options) {
        
        if(_logger != null) {
            _logger.log(Level.INFO, "FAMHttpAuthModule.Init");
        }
        
        try {
            if (_handler == null) {
                // Get the class loader
                URLClassLoader cls =
                    FAMClientClassLoader.getFAMClientClassLoader();
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
        }
    }
    
    /**
     * Authenticates a client request.
     *
     * <p> The AuthParam input parameter encapsulates the client request and
     * server response objects.  This ServerAuthModule validates the client
     * request object (decrypts content and verifies a signature, for example).
     *
     * @param param an authentication parameter that encapsulates the
     *          client request and server response objects.
     *
     * @param subject the subject may be used by configured modules
     *		to store and Principals and credentials validated
     *		in the request.
     *
     * @param sharedState a Map for modules to save state across
     *		a sequence of calls from <code>validateRequest</code>
     *		to <code>secureResponse</code> to <code>disposeSubject</code>.
     *
     * @exception AuthException if the operation failed.
     */
    public void validateRequest(AuthParam param,
        Subject subject,
        Map sharedState)
        throws AuthException {
        
        HttpServletAuthParam httpAuthParam = (HttpServletAuthParam)param;
        HttpServletRequest request = httpAuthParam.getRequest();
        HttpServletResponse response = httpAuthParam.getResponse();
        
        // Check if you needs to authenticate
        Object[] args = new Object[2];
        args[0] = subject;
        args[1] = request;
        Boolean authN = Boolean.FALSE;
        try {
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
        }
        
        if (authN.booleanValue()) {
            args = new Object[1];
            args[0] = request;
            String loginURL = null;
            try {
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
        }
    }
    
    /**
     * Secures the response to the client
     * (sign and encrypt the response, for example).
     *
     * @param param an authentication parameter that encapsulates the
     *          client request and server response objects.
     *
     * @param subject the subject may be used by configured modules
     *		to obtain credentials needed to secure the response, or null.
     *		If null, the module may use a CallbackHandler to obtain
     *		the necessary information.
     *
     * @param sharedState a Map for modules to save state across
     *		a sequence of calls from <code>validateRequest</code>
     *		to <code>secureResponse</code> to <code>disposeSubject</code>.
     *
     * @exception AuthException if the operation failed.
     */
    public void secureResponse(AuthParam param,
        Subject subject,
        Map sharedState)
        throws AuthException {
    }
    
    /**
     * Disposes of the Subject.
     *
     * <p> Remove Principals or credentials from the Subject object
     * that were stored during <code>validateRequest</code>.
     *
     * @param subject the Subject instance to be disposed.
     *
     * @param sharedState a Map for modules to save state across
     *		a sequence of calls from <code>validateRequest</code>
     *		to <code>secureResponse</code> to <code>disposeSubject</code>.
     *
     * @exception AuthException if the operation failed.
     */
    public void disposeSubject(Subject subject, Map sharedState)
    throws AuthException {
    }
    
    private static Logger _logger = null;
    
    static {
        LogManager logManager = LogManager.getLogManager();
        _logger = logManager.getLogger(
            "javax.enterprise.system.core.security");
    }
    
}
