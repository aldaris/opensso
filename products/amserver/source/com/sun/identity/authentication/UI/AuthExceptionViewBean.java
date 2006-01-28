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
 * $Id: AuthExceptionViewBean.java,v 1.1 2006-01-28 09:15:22 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.authentication.UI;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.iplanet.jato.*;
import com.iplanet.jato.model.*;
import com.iplanet.jato.model.sql.*;
import com.iplanet.jato.util.*;
import com.iplanet.jato.view.*;
import com.iplanet.jato.view.event.*;
import com.iplanet.jato.view.html.*;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.view.event.DisplayEvent;

import com.sun.identity.authentication.server.AuthContextLocal;
import com.sun.identity.authentication.service.AuthD;
import com.sun.identity.authentication.service.AuthUtils;
import com.iplanet.am.util.Debug;
import com.sun.identity.common.L10NMessage;

/**
 *
 *
 *
 */
public class AuthExceptionViewBean extends AuthViewBeanBase {
    /**
     *
     *
     */
    public AuthExceptionViewBean() {
        super(PAGE_NAME);
        exDebug.message("AuthExceptionViewBean() constructor called");
        registerChildren();
    }
    
    /** registers child views */
    protected void registerChildren() {
        super.registerChildren();
        registerChild(URL_LOGIN, StaticTextField.class);
        registerChild(TXT_EXCEPTION, StaticTextField.class);
        registerChild(TXT_GOTO_LOGIN_AFTER_FAIL, StaticTextField.class);
    }
    
    public void forwardTo(RequestContext requestContext) {
        exDebug.message("In forwardTo()");
        if (requestContext!=null) {
            request = requestContext.getRequest();
            response = requestContext.getResponse();
        }
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        if (ad == null ) {
            super.forwardTo(requestContext);
            return;
        }
        
        try {
            ac = au.getAuthContext(request,response,
            au.getSessionIDFromRequest(request),false,false);
        } catch (Exception e) {
            if (e instanceof L10NMessage) {
                java.util.Locale locale =
                com.iplanet.am.util.Locale.getLocale(au.getLocale(ac));
                ResultVal = ((L10NMessage)e).getL10NMessage(locale);
            } else {
                ResultVal = e.getMessage();
            }
        }
        if ((ac==null)||au.sessionTimedOut(ac)) {
            try {
                if (exDebug.messageEnabled()) {
                    exDebug.message("Goto Login URL : " + LOGINURL);
                }
                response.sendRedirect(LOGINURL);
            } catch (Exception e) {}
        } else {
            super.forwardTo(requestContext);
        }
    }
    
    public String getDisplayURL() {
        exDebug.message("In getDisplayURL()");
        
        if (ad == null ) {
            return new StringBuffer().append(File.separator).append("config")
            .append(File.separator).append("auth")
            .append(File.separator).append("default")
            .append(File.separator).append("Exception.jsp")
            .toString();
        }
        // I18N get resource bundle
        java.util.Locale locale =
        com.iplanet.am.util.Locale.getLocale(au.getLocale(ac));
        String client = au.getClientType(request);
        rb = (ResourceBundle)  rbCache.getResBundle("amAuthUI", locale);
        if (rb == null) {
            return au.getFileName(ac, "Exception.jsp");
        } else {
            return au.getFileName(ac, "authException.jsp");
        }
    }
    
    
    protected View createChild(String name) {
        if (exDebug.messageEnabled()) {
            exDebug.message("In createChild() : child name = " + name);
        }
        
        if (name.equals(TXT_EXCEPTION)) {
            return new StaticTextField(this, name, ResultVal);
        } else if (name.equals(TXT_GOTO_LOGIN_AFTER_FAIL)) {
            return new StaticTextField(this, name, "");
        } else if (name.equals(URL_LOGIN)) { // non-cookie support
            String loginURL = au.encodeURL(LOGINURL, ac, response);
            return new StaticTextField(this, name, loginURL);
        } else if (name.equals(HTML_TITLE_AUTH_EXCEPTION)) {
            String exceptionTitle = rb.getString("htmlTitle_AuthException");
            return new StaticTextField(this, name, exceptionTitle);
        } else {
            return super.createChild(name);
        }
    }
    
    public void beginDisplay(DisplayEvent event)
        throws ModelControlException {
        if (ad != null ) {
            try {
                String cookieDomain = null;
                Set cookieDomainSet = au.getCookieDomains();
                Cookie cookie;
                setPageEncoding(request,response);

                // No cookie domain specified in profile
                if (cookieDomainSet.isEmpty()) {
                    cookie = au.getLogoutCookie(ac, null);
                    response.addCookie(cookie);
                    // clear Persistent Cookie
                    if (au.getPersistentCookieMode(ac)) {
                        cookie = au.clearPersistentCookie(null, ac);
                        if (exDebug.messageEnabled()) {
                            exDebug.message(
                              "Clearing persistent cookie: null cookie domain");
                            exDebug.message("Persistent cookie: " + cookie);
                        }
                        response.addCookie(cookie);
                    }
                } else {
                    Iterator iter = cookieDomainSet.iterator();
                    while (iter.hasNext()) {
                        cookieDomain = (String)iter.next();
                        cookie = au.getLogoutCookie(ac, cookieDomain);
                        response.addCookie(cookie);
                        // clear Persistent Cookie
                        if (au.getPersistentCookieMode(ac)) {
                            cookie = au.clearPersistentCookie(cookieDomain, ac);
                            if (exDebug.messageEnabled()) {
                                exDebug.message("Clearing persistent cookie: "
                                + cookieDomain);
                                exDebug.message("Persistent cookie: " + cookie);
                            }
                            response.addCookie(cookie);
                        }
                    }
                }
                au.clearlbCookie(response);
                ResultVal = rb.getString("uncaught_exception");
                
            } catch (Exception e) {
                e.printStackTrace();
                if (exDebug.messageEnabled()) {
                    exDebug.message("error in getting Exception : " +
                        e.getMessage());
                }
                
                ResultVal = rb.getString("uncaught_exception") + " : " +
                    e.getMessage();
            }
        }
        
    }
    
    /**
     *
     *
     */
    public void handleHrefExceptionRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        ViewBean targetView = getViewBean(LoginViewBean.class);
        targetView.forwardTo(getRequestContext());
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Display cycle events:
    // If the fireDisplayEvents attribute in a display field tag is set to true,
    // then the begin/endDisplay events will fire for that display field.
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Using the display cycle event to adjust the value of a given field
     *
     */
    // HrefLogout ( Return to login )
    public boolean beginHrefExceptionDisplay(ChildDisplayEvent event) {
        return true;
    }
    
    public boolean beginContentHrefExceptionDisplay(ChildDisplayEvent event) {
        setDisplayFieldValue(
        TXT_GOTO_LOGIN_AFTER_FAIL,
        rb.getString("gotoLoginAfterFail"));
        return true;
    }
    
    // StaticTextLogout
    public boolean beginStaticTextExceptionDisplay(ChildDisplayEvent event) {
        return true;
    }
    
    public String getTileIndex() {
        return "";
    }
    
    public boolean beginContentStaticTextExceptionDisplay(
        ChildDisplayEvent event
    ) {
        return true;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Class variables
    ////////////////////////////////////////////////////////////////////////////
    
    public static final String PAGE_NAME="AuthException";
    
    static Debug exDebug = Debug.getInstance("amAuthExceptionViewBean");
    
    ////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////
    
    HttpServletRequest request;
    HttpServletResponse response;
    AuthContextLocal ac = null;
    public String ResultVal = "";
    private static String LOGINURL = "";
    public ResourceBundle rb = null;
    static AuthD ad = AuthD.getAuth();
    
    public static final String URL_LOGIN = "urlLogin";
    public static final String TXT_EXCEPTION = "StaticTextException";
    public static final String TXT_GOTO_LOGIN_AFTER_FAIL =
        "txtGotoLoginAfterFail";
    public static final String HTML_TITLE_AUTH_EXCEPTION =
        "htmlTitle_AuthException";
    static {
        LOGINURL = serviceUri + "/UI/Login";
    }
}

