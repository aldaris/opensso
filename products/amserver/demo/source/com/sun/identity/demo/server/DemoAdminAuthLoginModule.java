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
 * $Id: DemoAdminAuthLoginModule.java,v 1.1 2005-11-01 00:28:34 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;

import com.iplanet.am.util.Debug;
import com.sun.identity.authentication.internal.AuthPrincipal;
import com.sun.identity.authentication.internal.AuthSubject;
import com.sun.identity.authentication.internal.LoginModule;

public class DemoAdminAuthLoginModule implements LoginModule {
    
    private static final Debug debug = Debug.getInstance("amDemo");

    private AuthSubject authSubject;
    private CallbackHandler callbackHandler;
    private Map sharedState;
    private Map options;
    private String userName;
    private char[] password;
    private boolean loginResult;
    private boolean commitResult;


    public void initialize(AuthSubject subject, CallbackHandler callbackHandler,
                           Map sharedState, Map options) {
        this.authSubject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;
        
        if (debug.messageEnabled()) {
            debug.message("DemoAuthModule: initialized with subject: " +
                                        subject);
        }
    }
    
    public boolean login() throws LoginException {        
        loginResult = false;        
        NameCallback nameCallback = new NameCallback("Username");
        PasswordCallback passwordCallback = 
            new PasswordCallback("Password", false);
        try {
            callbackHandler.handle(
                new Callback[] { nameCallback, passwordCallback });
        } catch (Exception ex) {
            debug.error("callback proecessing failed", ex);
            throw new LoginException("callback processing failed");
        }

        this.userName = nameCallback.getName();
        this.password = passwordCallback.getPassword();            
        passwordCallback.clearPassword();
        
        if (userName == null || userName.trim().length() == 0) { 
            throw new LoginException("Invalid name");
        }
        
        if (password == null || password.length == 0) {
            throw new LoginException("Invalid password");
        }

        // FIXME
        // In a non-demo scenario, this call will be validated against
        // some real data store. But for the purpose of this demo,
        // we dont mind trusting the user anyway!
        loginResult = true;
        
        if (debug.messageEnabled()) {
            debug.message("DemoAuthModule: login status for " + userName 
                    + ": " + loginResult);
        }
        return loginResult;
    }
    
    public boolean commit() throws LoginException {
        commitResult = false;
        if (loginResult) {
            authSubject.getPrincipals().add(new AuthPrincipal(userName));
            commitResult = true;
        }
        
        if (debug.messageEnabled()) {
            debug.message("DemoAuthModule: commitResult for " + userName 
                    + ": " + commitResult);
        }
        
        return commitResult;
    }
    
    public boolean abort() throws LoginException {
        boolean result = true;
        if (loginResult) {
            if (commitResult) {
                result = logout();
            } else {
                loginResult = false;
            }
            
            userName = null;
            password = null;
            authSubject = null;
            sharedState = null;
            options = null;
        }

        return result;
    }

    public boolean logout() throws LoginException {
        return authSubject.getPrincipals().remove(
                new AuthPrincipal(userName));
    }
}
