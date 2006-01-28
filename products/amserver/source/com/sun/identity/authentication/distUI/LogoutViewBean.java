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
 * $Id: LogoutViewBean.java,v 1.1 2006-01-28 09:15:39 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.authentication.distUI;

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

import com.iplanet.am.util.SystemProperties;
import com.iplanet.am.util.Debug;
import com.iplanet.dpro.session.SessionID;
import com.iplanet.dpro.session.Session;
import com.iplanet.dpro.session.SessionException;
import com.iplanet.dpro.session.service.SessionService;
import com.sun.identity.common.Constants;
import com.sun.identity.common.L10NMessage;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.service.AuthUtils;
import com.sun.identity.common.ISLocaleContext;

/**
 *
 *
 *
 */
public class LogoutViewBean 
extends com.sun.identity.authentication.UI.AuthViewBeanBase {
    
    /**
     *
     *
     */
    public LogoutViewBean() {
        super(PAGE_NAME);
        logoutDebug.message("LogoutViewBean() constructor called");
        registerChildren();
    }
    
    /** registers child views */
    protected void registerChildren() {
        super.registerChildren();
        registerChild(URL_LOGIN, StaticTextField.class);
        registerChild(TXT_LOGOUT, StaticTextField.class);
        registerChild(TXT_GOTO_LOGIN_AFTER_LOGOUT, StaticTextField.class);
    }
    
    public void forwardTo(RequestContext requestContext) {        
        logoutDebug.message("In forwardTo()");
        
        if (requestContext != null) {
            request = requestContext.getRequest();
            response = requestContext.getResponse();
            servletContext = requestContext.getServletContext();
            session = request.getSession();
        }
        
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        gotoUrl = request.getParameter("goto");
        if (logoutDebug.messageEnabled()) {
            logoutDebug.message("Goto query param : " + gotoUrl);
        }
        
        try {            
            cookieSupported = au.checkForCookies(request);
            
            if (session.isNew()) {
                logoutDebug.message("New Request");
                client_type = au.getClientType(request);
                ISLocaleContext localeContext = new ISLocaleContext();
                localeContext.setLocale(request);
                locale = localeContext.getLocale();
            } else {
                logoutDebug.message("Existing Request");
                client_type = (String) session.getAttribute("Client_Type");
                locale = (java.util.Locale) session.getAttribute("Locale");
                loginURL = (String) session.getAttribute("LoginURL");
                orgName = (String) session.getAttribute("OrgName");
                indexType =
                    au.getIndexType((String) session.getAttribute("IndexType"));
                indexName = (String) session.getAttribute("IndexName");
                ac = (AuthContext) session.getAttribute("AuthContext");
            }
            
            if (logoutDebug.messageEnabled()) {
                logoutDebug.message("Client Type is: " + client_type);
                logoutDebug.message("JSPLocale = " + locale);
                logoutDebug.message("loginURL : " + loginURL);
                logoutDebug.message("AuthContext : " + ac);
            }
            
            fallbackLocale = locale;
            rb =  rbCache.getResBundle(bundleName, locale);
            
        } catch (Exception e) {
            logoutDebug.message("Retrieve AuthContext Error : ", e);
            ResultVal = getL10NMessage(e, locale);
        }
        
        if (cookieSupported) {
            logoutDebug.message("Cookie is supported");
            clearAllCookies();
        } else {
            logoutDebug.message("Cookie is not supported");            
            logoutCookie = LOGOUTCOOKIEVAULE;
            if (logoutDebug.messageEnabled()) {
                logoutDebug.message("Logout Cookie is " + logoutCookie);
            }            
        }
        
        // Invalidate HttpSession
        session.invalidate();
        
        // get the Logout JSP page path
        jsp_page = appendLogoutCookie(getFileName(LOGOUT_JSP));
        
        if (ac == null) {
            /*
            if (SessionService.getSessionService().isSessionFailoverEnabled()) {
                try {
                    Session session = Session.getSession(sid);
                    session.logout();
                    logoutDebug.message(
                        "logout successfully in Session failover mode");
                    ResultVal = rb.getString("logout.successful");
                } catch (SessionException se) {
                    try {
                        if (logoutDebug.messageEnabled()) {
                            logoutDebug.message("Exception during logout", se);
                            logoutDebug.message("Goto Login URL : "+ LOGINURL);
                        }
                        if (doSendRedirect(LOGINURL)) {
                            response.sendRedirect(appendLogoutCookie(LOGINURL));
                            return;
                        } else {
                            jsp_page = appendLogoutCookie(
                                getFileName(LOGIN_JSP));
                        }
             
                    } catch (Exception e) {
                        if (logoutDebug.messageEnabled()) {
                            logoutDebug.message(
                                "Redirect failed:" + LOGINURL ,e);
                        }
                        ResultVal = e.getMessage();
                    }
                    super.forwardTo(requestContext);
                    return;
                }
            } else {   */
            if (!isGotoSet()) {
                    /*
                    String originalLoginURL = au.getOrigRedirectURL(
                        request, sid);
                    if (originalLoginURL != null) {
                        try {
                            if (logoutDebug.messageEnabled()) {
                                logoutDebug.message("Original Login URL: " +
                                originalLoginURL);
                            }
                            int index = originalLoginURL.indexOf("/Login");
                            String originalLogoutURL =
                            originalLoginURL.substring(0,index) + "/Logout";
                            if (logoutDebug.messageEnabled()) {
                                logoutDebug.message(
                                    "Redirect to Original Logout URL : " +
                                        originalLogoutURL);
                            }
                            if (doSendRedirect(originalLogoutURL)) {
                                response.sendRedirect(
                                    appendLogoutCookie(originalLogoutURL));
                                return;
                            }
                        } catch (Exception e) {
                            ResultVal = getL10NMessage(e, locale);
                        }
                    } else { */
                try {
                    if (logoutDebug.messageEnabled()) {
                        logoutDebug.message("AuthContext is NULL");
                        logoutDebug.message("Goto LOGINURL : "+ LOGINURL);
                    }
                    if (doSendRedirect(LOGINURL)) {
                        response.sendRedirect(appendLogoutCookie(LOGINURL));
                        return;
                    } else {
                        jsp_page = appendLogoutCookie(getFileName(LOGIN_JSP));
                    }
                } catch (Exception e) {
                    ResultVal = getL10NMessage(e, locale);
                }
                //}
            }
            //}
        } else {
            try {
                ac.logout();
                logoutDebug.message("logout successfully");
                ResultVal = rb.getString("logout.successful");
            } catch (Exception e) {
                if (logoutDebug.messageEnabled()) {
                    logoutDebug.message(
                        "error in logout : " + e.getMessage(), e);
                }
                
                ResultVal = rb.getString("logout.failure")
                + " : " + getL10NMessage(e, locale);
                super.forwardTo(requestContext);
                return;
            }
        }
        
        if (!redirectToGoto(locale)) {
            super.forwardTo(requestContext);
        }
    }
    
    private String getFileName(String fileName) {
        String relativeFileName = null;
        if (ac != null) {
            relativeFileName = au.getFileName(
                fileName, locale.toString(), orgName, request, servletContext,
                indexType, indexName);
        } else {
            relativeFileName =
            au.getDefaultFileName(request,fileName,locale,servletContext);
        }
        if (logoutDebug.messageEnabled()) {
            logoutDebug.message("fileName is : " + fileName);
            logoutDebug.message("relativeFileName is : " + relativeFileName);
        }
        
        return relativeFileName;
    }
    
    private void clearAllCookies() {
        Set cookieDomainSet =  au.getCookieDomains();

        // No cookie domain specified in profile
        if (cookieDomainSet.isEmpty()) {
            clearAllCookiesByDomain(null);
        } else {
            Iterator iter = cookieDomainSet.iterator();
            while (iter.hasNext()) {
                clearAllCookiesByDomain((String)iter.next());
            }
        }
        au.clearlbCookie(response);
        clearHostUrlCookie(response);
    }
    
    private void clearAllCookiesByDomain(String cookieDomain) {
        Cookie cookie = au.createCookie(LOGOUTCOOKIEVAULE, cookieDomain);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        if (au.getAuthCookieValue(request) != null) {
            cookie = au.createCookie(AuthUtils.getAuthCookieName(),
                LOGOUTCOOKIEVAULE, cookieDomain);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
    
    public String getDisplayURL() {
        if (logoutDebug.messageEnabled()) {
            logoutDebug.message("In getDisplayURL() jsp_page " + jsp_page);
        }
        return jsp_page;
    }
    
    /**
     *
     *
     */
    protected View createChild(String name) {
        if (logoutDebug.messageEnabled()) {
            logoutDebug.message("In createChild() : child name = " + name);
        }
        
        if (name.equals(TXT_LOGOUT)) {
            return new StaticTextField(this, name, ResultVal);
        } else if (name.equals(TXT_GOTO_LOGIN_AFTER_LOGOUT)) {
            return new StaticTextField(this, name, "");
        } else if (name.equals(URL_LOGIN)) {
            if ((loginURL==null)||(loginURL.length() == 0)) {
                loginURL = LOGINURL;
            }
            loginURL = appendLogoutCookie(loginURL);
            return new StaticTextField(this, name, loginURL);
        } else if (name.equals(HTML_TITLE_LOGOUT)) {
            String logoutTitle = rb.getString("htmlTitle_Logout");
            return new StaticTextField(this, name, logoutTitle);
        } else {
            return super.createChild(name);
        }
    }
    
    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        logoutDebug.message("In beginDisplay()");
        setPageEncoding(request,response);
    }
    
    
    /* returns the url encoded with the logout cookie string */
    
    private String appendLogoutCookie(String url) {
        return AuthUtils.addLogoutCookieToURL(url,logoutCookie,cookieSupported);
    }
    
    /* Checks if request should use sendRedirect */
    private boolean doSendRedirect(String redirectURL) {
        return        ((redirectURL != null) && (redirectURL.length() != 0)
        && (au.isGenericHTMLClient(clientType))) ;
        
    }
    
    // Check whether the 'goto' query parameter value exists or not
    private boolean isGotoSet() {
        if ((gotoUrl != null) && (gotoUrl.length() != 0)) {
            return true;
        } else {
            return false;
        }
    }
    
    // Redirect to the 'goto' query parameter value
    private boolean redirectToGoto(java.util.Locale locale) {
        if (isGotoSet()) {
            if (logoutDebug.messageEnabled()) {
                logoutDebug.message("Redirect to 'goto' URL : " + gotoUrl);
            }
            try {
                if (doSendRedirect(gotoUrl)) {
                    response.sendRedirect(appendLogoutCookie(gotoUrl));
                    return true;
                }
            } catch (Exception e) {
                if (logoutDebug.messageEnabled()) {
                    logoutDebug.message(
                        "'goto' Redirect failed : " + gotoUrl, e);
                }
                ResultVal = getL10NMessage(e, locale);
            }
        }
        return false;
    }
    
    /**
     *
     *
     */
    public void handleHrefLogoutRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        //ViewBean targetView = getViewBean(LoginViewBean.class);
        //targetView.forwardTo(getRequestContext());
        forwardTo();
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
    public boolean beginHrefLogoutDisplay(ChildDisplayEvent event) {
        return true;
    }
    
    public boolean beginContentHrefLogoutDisplay(ChildDisplayEvent event) {
        setDisplayFieldValue(
        TXT_GOTO_LOGIN_AFTER_LOGOUT,
        rb.getString("gotoLoginAfterLogout"));
        return true;
    }
    
    // StaticTextLogout
    public boolean beginStaticTextLogoutDisplay(ChildDisplayEvent event) {
        return true;
    }
    
    public String getTileIndex() {
        return "";
    }
    
    public boolean beginContentStaticTextLogoutDisplay(ChildDisplayEvent event){
        return true;
    }
    
    private String getL10NMessage(Exception e, java.util.Locale locale) {
        if (e instanceof L10NMessage) {
            return ((L10NMessage)e).getL10NMessage(locale);
        } else {
            return e.getMessage();
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // Class variables
    ////////////////////////////////////////////////////////////////////////////
    
    public static final String PAGE_NAME="Logout";
    
    static Debug logoutDebug = Debug.getInstance("amLogoutViewBean");
    
    ////////////////////////////////////////////////////////////////////////////
    // Instance variables
    ////////////////////////////////////////////////////////////////////////////
    
    HttpServletRequest request;
    HttpServletResponse response;
    HttpSession session;
    ServletContext servletContext;
    AuthContext ac = null;
    java.util.Locale locale = null;
    String orgName = "";
    String indexName = "";
    AuthContext.IndexType indexType;
    String client_type = "";
    public String ResultVal = "";
    public String gotoUrl = "";
    public String jsp_page = "";
    private static String LOGINURL = "";
    private String loginURL = "";
    public ResourceBundle rb = null;
    private static final String LOGOUTCOOKIEVAULE = "LOGOUT";
    private String logoutCookie = null;
    private boolean cookieSupported;
    public static final String URL_LOGIN = "urlLogin";
    public static final String TXT_LOGOUT = "txtLogout";
    public static final String TXT_GOTO_LOGIN_AFTER_LOGOUT =
    "txtGotoLoginAfterLogout";
    public static final String HTML_TITLE_LOGOUT = "htmlTitle_Logout";
    private String clientType=null;
    private static final String LOGOUT_JSP = "Logout.jsp";
    private static final String LOGIN_JSP = "Login.jsp";
    private static final String bundleName = "amAuthUI";
    
    static {
        LOGINURL = serviceUri + "/UI/Login";
    }
}

