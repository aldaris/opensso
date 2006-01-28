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
 * $Id: LoginServlet.java,v 1.1 2006-01-28 09:15:26 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.authentication.UI;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.URL;

import com.iplanet.jato.*;
import com.iplanet.jato.util.*;
import com.iplanet.jato.view.*;

import com.sun.identity.authentication.service.AuthUtils;
import com.sun.identity.common.ISLocaleContext;
import com.sun.identity.common.L10NMessageImpl;
import com.sun.identity.common.RequestUtils;
import com.sun.identity.common.Constants;
import com.iplanet.am.util.Debug;
import com.iplanet.dpro.session.SessionID;
import com.iplanet.dpro.session.Session;
import com.iplanet.am.util.SystemProperties;

/**
 *
 *
 *
 */
public class LoginServlet
extends com.sun.identity.authentication.UI.AuthenticationServletBase {
    /**
     *
     *
     */
    
    public LoginServlet() {
        super();
        
    }
    
    /**
     *
     *
     */
    protected void initializeRequestContext(RequestContext requestContext) {
        super.initializeRequestContext(requestContext);
        
        // Set a view bean manager in the request context.  This must be
        // done at the module level because the view bean manager is
        // module specifc.
        ViewBeanManager viewBeanManager =
        new ViewBeanManager(requestContext,PACKAGE_NAME);
        
        ((RequestContextImpl)requestContext).setViewBeanManager(
        viewBeanManager);
        
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        
        // Check content length
        try {
            RequestUtils.checkContentLength(request);
        } catch (L10NMessageImpl e) {
            if (debug.messageEnabled()) {
                ISLocaleContext localeContext = new ISLocaleContext();
                localeContext.setLocale(request);
                java.util.Locale locale = localeContext.getLocale();
                debug.message("LoginServlet: " + e.getL10NMessage(locale));
            }
            AuthExceptionViewBean vb = (AuthExceptionViewBean)
            viewBeanManager.getViewBean(
            com.sun.identity.authentication.UI.AuthExceptionViewBean.class);
            vb.forwardTo(requestContext);
            throw new CompleteRequestException();
        }
        
        // Check if the hostname in the URL is an FQDN else
        // redirect to the fqdn
        AuthUtils au = new AuthUtils();
        String client_type = au.getClientType(request);
        if (debug.messageEnabled()) {
            debug.message("Client Type = " + client_type);
        }
        String hostName = AuthUtils.getHostName(request);
        if (!AuthUtils.isValidFQDNRequest(hostName)) {
            try {
                String newHN =
                AuthUtils.getValidFQDNResource(hostName,request);
                if (debug.messageEnabled()) {
                    debug.message("FQDN = " + newHN);
                }
                if (au.isGenericHTMLClient(client_type)) {
                    debug.message("This is HTML");
                    response.sendRedirect(newHN);
                } else {
                    String fileName = au.getDefaultFileName(
                        request, REDIRECT_JSP);
                    if (debug.messageEnabled()) {
                        debug.message("Forward to : " + fileName);
                    }
                    RequestDispatcher dispatcher =
                    request.getRequestDispatcher(fileName);
                    dispatcher.forward(request, response);
                }
            } catch (Exception e) {
                // came here continue
            }
            throw new CompleteRequestException();
        }
        
        // Check whether this is the correct server to accept the client
        // response.
        String authCookieValue = au.getAuthCookieValue(request);
        if ((authCookieValue != null) && (authCookieValue.length() != 0) &&
            (!authCookieValue.equalsIgnoreCase("LOGOUT"))) {
            //if cookie server does not match to this local server then
            //send Auth request to cookie (original) server
            String cookieURL = null;
            try {
                SessionID sessionID = new SessionID(authCookieValue);
                URL sessionServerURL = Session.getSessionServiceURL(sessionID);
                cookieURL = sessionServerURL.getProtocol()
                + "://" + sessionServerURL.getHost() + ":"
                    + Integer.toString(sessionServerURL.getPort()) + serviceURI;
            } catch (Exception e) {
                if (debug.messageEnabled()) {
                    debug.message("LoginServlet error in Session : "
                        + e.toString());
                }
            }
            if (debug.messageEnabled()) {
                debug.message("cookieURL : " + cookieURL);
            }
            if ((cookieURL != null) && (cookieURL.length() != 0) &&
                (!au.isLocalServer(cookieURL,true))) {
                debug.message("Routing the request to Original Auth server");
                try {
                    HashMap origRequestData =
                        au.sendAuthRequestToOrigServer(
                        request,response,cookieURL);                    
                    String redirect_url = null;
                    String clientType = null;
                    String output_data = null;
                    if (origRequestData != null && !origRequestData.isEmpty()) {
                        redirect_url =
                            (String)origRequestData.get("AM_REDIRECT_URL");
                        output_data =
                            (String)origRequestData.get("OUTPUT_DATA");
                        clientType =
                            (String)origRequestData.get("AM_CLIENT_TYPE");
                    }
                    if (debug.messageEnabled()) {
                        debug.message("redirect_url : " + redirect_url);
                        debug.message("clientType : " + clientType);
                    }
                    if (((redirect_url != null) && !redirect_url.equals("")) &&
                        (au.isGenericHTMLClient(clientType))
                    ) {
                        debug.message("Redirecting the response");
                        response.sendRedirect(redirect_url);
                    }
                    if ((output_data != null) && (!output_data.equals(""))) {
                        debug.message("Printing the forwarded response");
                        java.io.PrintWriter outP = response.getWriter();
                        outP.println(output_data);
                    }
                } catch (Exception e) {
                    if (debug.messageEnabled()) {
                        debug.message("LoginServlet error in Request Routing : "
                            + e.toString());
                    }
                }
                throw new CompleteRequestException();
            }
        }
    }    
    
    /**
     *
     *
     */
    public String getModuleURL() {
        // The superclass can be configured from init params specified at
        // deployment time.  If the superclass has been configured with
        // a different module URL, it will return a non-null value here.
        // If it has not been configured with a different URL, we use our
        // (hopefully) sensible default.
        String result = super.getModuleURL();
        if (result != null)
            return result;
        else
            return DEFAULT_MODULE_URL;
    }
    
    /**
     *
     *
     */
    protected void onSessionTimeout(RequestContext requestContext)
    throws ServletException {
        // Do nothing
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Class variables
    ////////////////////////////////////////////////////////////////////////////
    
    public static final String DEFAULT_MODULE_URL="../UI";
    public static String PACKAGE_NAME=
    getPackageName(LoginServlet.class.getName());
    
    private static final String REDIRECT_JSP = "Redirect.jsp";
    
    // the debug file
    private static Debug debug = Debug.getInstance("amLoginServlet");
    
    private static String serviceURI = SystemProperties.get(
        Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR) + "/UI/Login";    
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////
}

