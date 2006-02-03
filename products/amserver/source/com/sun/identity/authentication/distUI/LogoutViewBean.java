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
 * $Id: LogoutViewBean.java,v 1.3 2006-02-03 07:54:49 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.authentication.distUI;

import java.io.IOException;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.iplanet.am.util.Debug;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.StaticTextField;
import com.sun.identity.authentication.AuthContext;
import com.sun.identity.authentication.service.AuthUtils;
import com.sun.identity.common.ISLocaleContext;
import com.sun.identity.common.L10NMessage;

/**
 * This class is a default implementation of <code>LogoutViewBean</code> auth 
 * Logout UI.
 */
public class LogoutViewBean 
extends com.sun.identity.authentication.UI.AuthViewBeanBase {
    
    /**
     * Creates <code>LoginViewBean</code> object.
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
    
    /**
     * Forwards the request to this view bean, displaying the page. This
     * method is the equivalent of <code>RequestDispatcher.forward()</code>,
     * meaning that the same semantics apply to the use of this method.
     * This method makes implicit use of the display URL returned
     * by the <code>getDisplayURL()</code> method.
     * @param requestContext servlet context for auth request
     */
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
    
    /**
     * Returns display url for auth auth Logout UI
     * 
     * @return display url for auth auth Logout  UI
     */
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
    
    /**
     * Called as notification that the JSP has begun its display 
     * processing. In addition to performing the default behavior in the 
     * superclass's version, this method executes any auto-retrieving or auto-
     * executing models associated with this view unless auto-retrieval is
     * disabled.
     *
     * @param event Display Event.
     * @throws ModelControlException if manipulation of a model fails during
     *         display preparation or execution of auto-retrieving models.
     */
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
     * Handles href logout request
     * @param event request invocation event
     * @throws ServletException if it fails to forward logout request
     * @throws IOException  if it fails to forward logout request
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
    /**
     * Returns if it begins href logout display
     * @param event child display event
     * @return <code>true</code> by default.
     */ 
    public boolean beginHrefLogoutDisplay(ChildDisplayEvent event) {
        return true;
    }
    
    /**
     * Returns if it begins content href logout display
     * @param event child display event
     * @return <code>true</code> by default.
     */ 
    public boolean beginContentHrefLogoutDisplay(ChildDisplayEvent event) {
        setDisplayFieldValue(
        TXT_GOTO_LOGIN_AFTER_LOGOUT,
        rb.getString("gotoLoginAfterLogout"));
        return true;
    }
    
    /**
     * Returns if it begins static text logout display
     * @param event child display event
     * @return <code>true</code> by default.
     */ 
    public boolean beginStaticTextLogoutDisplay(ChildDisplayEvent event) {
        return true;
    }
    
    /**
     * Returns tile Index.
     *
     * @return Tile index.
     */
    public String getTileIndex() {
        return "";
    }
    
    /**
     * Returns <code>true</code> to display static text content.
     *
     * @param event Child display event.
     * @return <code>true</code> to display static text content.
     */
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
    
    /** Default page name */
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
    /** Logout result value */
    public String ResultVal = "";
    /** Goto url */
    public String gotoUrl = "";
    /** JSP page */
    public String jsp_page = "";
    private static String LOGINURL = "";
    private String loginURL = "";
    /** Resource bundle for <code>Locale</code> */
    public ResourceBundle rb = null;
    private static final String LOGOUTCOOKIEVAULE = "LOGOUT";
    private String logoutCookie = null;
    private boolean cookieSupported;
    /** Default parameter name for login url */
    public static final String URL_LOGIN = "urlLogin";
    /** Default parameter name for logout text */
    public static final String TXT_LOGOUT = "txtLogout";
    /** Default parameter name for goto login text after logout */
    public static final String TXT_GOTO_LOGIN_AFTER_LOGOUT =
    "txtGotoLoginAfterLogout";
    /** Default parameter name for logout html title */
    public static final String HTML_TITLE_LOGOUT = "htmlTitle_Logout";
    private String clientType=null;
    private static final String LOGOUT_JSP = "Logout.jsp";
    private static final String LOGIN_JSP = "Login.jsp";
    private static final String bundleName = "amAuthUI";
    
    static {
        LOGINURL = serviceUri + "/UI/Login";
    }
}

