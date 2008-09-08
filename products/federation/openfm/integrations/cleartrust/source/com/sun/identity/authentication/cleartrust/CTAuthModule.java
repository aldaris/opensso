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
 * $Id: CTAuthModule.java,v 1.2 2008-09-08 19:58:53 wahmed Exp $
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
     * module. 
     */
    public int process(Callback[] callbacks, int state) 
                 throws AuthLoginException {

        HttpServletRequest request = getHttpServletRequest();
        ServerDescriptor dispatcher = null;

        Cookie[] cookies = request.getCookies();
        String CTCookie =  null;
        String principal = null;
        boolean cookieFound = false;
        boolean ctsessionValid = false;
	//// GAPA -- instead of for loop, just use a while loop to exit upon finding CTcookie
	int i = 0;  ////Gapa
	while((!cookieFound) && (i < cookies.length)){ 		////Gapa
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
                   ctsessionValid = remoteClient.connect(value);

                   if(!ctsessionValid)  {
		         debug.message("CTSESSION cookie validation failed");	
		         throw new AuthLoginException("CTSESSION cookie validation failed");      
                   }
		
            }// end if cookieFound
	    i=i+1;	////Gapa
        } //end while loop



	//// GAPA -- What if cookies.length is  0 or cookie is not found,
	//// 		do you still return LOGIN_SUCCEED?	
	//// GAPA -- If CTCookie not found, throw AuthLoginException
	if (!cookieFound) {
		debug.message("CTCookie not found in request");	
		throw new AuthLoginException("No CTCookie found in request");      
	}
        return ISAuthConstants.LOGIN_SUCCEED;

    }



  public class ClearTrust  {

      private RuntimeAPI runtimeAPI = null;
      boolean result = false;
       
      private boolean connect(String value)
      { 

          debug.message("connect() cookie value: " + value);
          ConnectionDescriptor dispatcher = null;
	  //// GAPA -- do we need to validate session on all listed dispatch host
	  //// It is enough to check atleast on one server the session is valid
	  ////	Exist the loop when a valid userId found in CT Token
	  //// Use a While loop instead of for loop
	  Iterator iter = dispatchServerHostname.iterator(); 	////Gapa
	  boolean foundValidCTSession= false; 			////Gapa
          
	  while ((!foundValidCTSession) && (iter.hasNext())) { 	////Gapa
             String dispatchServer = (String)iter.next();
             if(dispatchServer == null) {
                debug.message("DispatchServerHostName list is empty");
             } else { 
             	dispatcher = new ConnectionDescriptor(dispatchServer,
                                          dispatchServerPort,
                                          ServerDescriptor.SSL_ANON);

                try {
                   runtimeAPI = APIFactory.createFromServerDispatcher(dispatcher);
                   String newToken = runtimeAPI.validateToken(value);
                   userId = runtimeAPI.getTokenValue(newToken, TokenKeys.SC_USER_ID);
                   debug.message("result: " + result);
                   debug.message("userId: " + userId);
		   foundValidCTSession = true;  ////Gapa
                }
                catch( RuntimeAPIException e ) {
                   debug.message("Error connecting to dispatch server " +
                        dispatchServer+ ":" + dispatchServerPort);
		   e.printStackTrace();
                } //end try
                finally {   
                          // Disconnect from Dispatch Server - WXA
			if (runtimeAPI != null)
                	 	runtimeAPI.close();
                } 
	     } ////Gapa-added for the new else condition
          } // end while
	  
	  //// Gapa - if valid token not found, display a message
	  if (!foundValidCTSession)   		//// Gapa
		debug.message("Unable to find valid CTSESSION Token");  /// Gapa

          return foundValidCTSession;

      }

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
