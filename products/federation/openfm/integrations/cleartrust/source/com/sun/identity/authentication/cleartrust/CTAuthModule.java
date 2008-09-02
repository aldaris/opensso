/**
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
 * $Id: CTAuthModule.java,v 1.1 2008-09-02 13:39:10 wahmed Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.authentication.cleartrust;

import java.util.Map;
import java.util.Enumeration;
import java.security.Principal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import com.sun.identity.authentication.spi.AMLoginModule;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.shared.debug.Debug;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.util.*;
import java.security.KeyStore;
import java.util.HashMap;
import sirrus.connect.ConnectionDescriptor;
import sirrus.runtime.*;



/**
 * Custom authentication module for validating siteminder user session
 * to enable SSO integration between Federation Access Manager and
 * Siteminder access server.
 */
public class CTAuthModule extends AMLoginModule {

    private static final String COOKIE_NAME = "CTCookieName"; 
    private static final String HOSTNAME = "DispatchServerHostName";
    private static final String SERVER_PORT = "DispatchServerPort"; 
    private static final String REMOTE_USER_HEADER_NAME = "RemoteUserHeaderName";

    private String ctCookieName = null;
    private Set dispatchServerHostname = null;
    private int dispatchServerPort = 5608;
    private String userId = null;
    private Principal userPrincipal = null;
    private String remoteUserHeader = "REMOTE_USER";
    private RuntimeAPI runtimeAPI = null;
 
    protected Debug debug = null;
    protected String CTAuth;
    ResourceBundle bundle = null;
    private Map sharedState;

    public CTAuthModule() throws LoginException{
        CTAuth = "CTAuth";
        debug = Debug.getInstance(CTAuth);
	debug.message("CTAuthModule()");
    }

    /**
     * Initialize the authentication module with it's configuration
     */
    public void init(Subject subject, Map sharedState, Map options) {
	java.util.Locale locale = getLoginLocale();
	bundle = amCache.getResBundle(CTAuth, locale);
	this.sharedState = sharedState;

        if (debug.messageEnabled()) {
	   debug.message("CTAuthModule resbundle locale=" + locale);
        }
        if (debug.messageEnabled()) {
	   debug.message("CTAuthModule initialization" + options);
        }

        ctCookieName = CollectionHelper.getMapAttr(options, 
                       COOKIE_NAME, "CTSESSION");

        dispatchServerHostname = (Set)options.get("DispatchServerHostName");

        try {
            String tmp = CollectionHelper.getMapAttr(options, SERVER_PORT, "5608");
            dispatchServerPort = Integer.parseInt(tmp);
                  
        } catch (Exception e) {
            e.printStackTrace();
        }

        remoteUserHeader = CollectionHelper.getMapAttr(options,
                           REMOTE_USER_HEADER_NAME, "REMOTE_USER");
        
    } 

    /**
     * This method process the login procedure for this authentication
     * module. In this auth module, if the user chooses to just validate
     * the HTTP headers set by the siteminder agent, this will not further
     * validate the SMSESSION by the siteminder SDK since the same thing
     * might have already been validated by the agent.
     */
    public int process(Callback[] callbacks, int state) 
                 throws AuthLoginException {

        HttpServletRequest request = getHttpServletRequest();
        ServerDescriptor dispatcher = null;

        Cookie[] cookies = request.getCookies();
        String CTCookie =  null;
        String principal = null;
        boolean cookieFound = false;
        for (int i=0; i < cookies.length; i++) {
             Cookie cookie = cookies[i];
             if(cookie.getName().equals("CTSESSION")) {
                cookieFound = true;
                String value = cookie.getValue();

                debug.message("CT Session cookie value: " + value);

                // URLDecode the token, if necessary
                   if (value!= null && value.matches(".*%.*")) {
                       try {
                           value = java.net.URLDecoder.decode(value, "UTF-8");
                       } catch (UnsupportedEncodingException ex) {
                          ex.printStackTrace();
                       }
                   }

                   ClearTrust remoteClient = new ClearTrust();
                   remoteClient.connect(value);

            }
        }
      
        return ISAuthConstants.LOGIN_SUCCEED;

    }


  public class ClearTrust  {

      private RuntimeAPI runtimeAPI = null;
      boolean result = false;
       
      private void connect(String value)
      { 

          debug.message("connect() cookie value: " + value);
          ConnectionDescriptor dispatcher = null;

          for (Iterator iter = dispatchServerHostname.iterator();
		 iter.hasNext();) {
             String dispatchServer = (String)iter.next();
             if(dispatchServer == null) {
                debug.message("DispatchServerHostName list is empty!");
                break;
             }

             dispatcher = new ConnectionDescriptor(dispatchServer,
                                          dispatchServerPort,
                                          ServerDescriptor.SSL_ANON);
             debug.message("Trying to connect to " + dispatchServer 
				+ ":" + dispatchServerPort);

             try {
                   runtimeAPI = APIFactory.createFromServerDispatcher(dispatcher);
                   String newToken = runtimeAPI.validateToken(value);
                   userId = runtimeAPI.getTokenValue(newToken, TokenKeys.SC_USER_ID);
                   debug.message("Connected to " + dispatchServer 
				+ ":" + dispatchServerPort);
                   debug.message("result: " + result);
                   debug.message("userId: " + userId);
		   break;
             }
                   catch( RuntimeAPIException e ) {
                   debug.message("Error connecting to dispatch server " +
                        dispatchServer+ ":" + dispatchServerPort);
                   debug.message("Error connecting to dispatch server " +
                        dispatchServer + ":" + dispatchServerPort);
                   e.printStackTrace();
             }

          }
      }
  }


   private void disconnect()
    {
        if (runtimeAPI != null)
            runtimeAPI.close();
    }



    /**
     * Returns the authenticated principal.
     * This is consumed by the authentication framework to set the 
     * principal
     */
    public java.security.Principal getPrincipal() {
        if (userPrincipal != null) {
            return userPrincipal;
        } else if (userId != null) {
            userPrincipal = new CTPrincipal(userId);
            return userPrincipal;
        } else {
            return null;
        }
    }
}
