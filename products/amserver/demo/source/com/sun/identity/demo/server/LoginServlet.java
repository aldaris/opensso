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
 * $Id: LoginServlet.java,v 1.1 2005-11-01 00:28:35 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.dpro.session.Session;
import com.iplanet.dpro.session.service.InternalSession;
import com.iplanet.dpro.session.service.SessionService;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

public class LoginServlet extends HttpServlet {
    
    public static final int MAX_SESSION_TIME = 10;
    public static final int MAX_IDLE_TIME = 5; 
    public static final String NEWLINE = 
        System.getProperty("line.separator", "\n");

    
    private String cookieDomain = null;
    private SessionService sessionService = null;
    private String organization = null;
    private String cookieName = null;
    
    public void init() throws ServletException {
        String serverHost = SystemProperties.get("com.iplanet.am.server.host");
        if (serverHost == null || serverHost.trim().length() == 0) {
            throw new ServletException("Failed to read SystemProeprty " +
                    "com.iplanet.am.server.host");
        }
        
        int firstPeriodIndex = serverHost.indexOf('.');
        if (firstPeriodIndex == -1) {
            throw new ServletException("Host name is not fully qualified : " 
                    + serverHost);
        }
        
        int secondPeriodIndex = serverHost.indexOf('.', firstPeriodIndex);
        if (secondPeriodIndex == -1) {
            throw new ServletException("Malformed host name: " + serverHost);
        }
        
        cookieDomain = serverHost.substring(firstPeriodIndex);
        
        sessionService = SessionService.getSessionService();
        
        organization = SystemProperties.get("com.iplanet.am.defaultOrg");
        
        cookieName = SystemProperties.get("com.iplanet.am.cookie.name");
    }

    protected void doPost(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException 
    {
        try {
            SSOTokenManager mgr = SSOTokenManager.getInstance();
            SSOToken token = mgr.createSSOToken(request);
            if (mgr.isValidToken(token)) {
                String principal = "" + token.getPrincipal();
                String p1 = token.getProperty("p1");
                String p2 = token.getProperty("p2");
                String p3 = token.getProperty("p3");
                
                StringBuffer buff = new StringBuffer("<pre>");
                buff.append(NEWLINE);
                buff.append("You are already logged in as: ");
                buff.append(principal).append(NEWLINE);
                buff.append(NEWLINE);
                buff.append("Your custom properties are: ").append(NEWLINE);
                buff.append("    p1 = ").append(p1).append(NEWLINE);
                buff.append("    p2 = ").append(p2).append(NEWLINE);
                buff.append("    p3 = ").append(p3).append(NEWLINE);
                buff.append("</pre>");
                
                sendResponse(response, buff.toString());
                return;
            }
        } catch (Exception ex) {
            // ignore this - the session may have expired
        }
        
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (userName == null || userName.trim().length() == 0) {
            sendResponse(response, "Error: username not specified");
            return;
        }
        
        if (password == null || password.trim().length() == 0) {
            sendResponse(response, "Error: password not specified");
            return;
        }
        
        if (checkUser(userName, password)) {
            //handle success
            String ssoTokenString = 
                initiateSession(request, response, userName);
            sendResponse(response, "Success: You session cookie is:"
                    + ssoTokenString);
            return;
        } else {
            sendResponse(response, "Error: invalid username/password");
            return;
        }
    }
    
    private boolean checkUser(String userName, String password) {
        boolean result = false;
        if (userName.equals(password)) {
            if (userName.equals("andy") 
                    || userName.equals("bob") 
                    || userName.equals("chris")) 
            {
                result = true;
            }        
        }
        
        return result;
    }
    
    private String initiateSession(HttpServletRequest request, 
            HttpServletResponse response, String userName) 
    {
        InternalSession session = 
            sessionService.newInternalSession(organization, null);
        
        session.setClientID(userName);
        String userDN = "uid=" + userName + ",ou=People," + organization;
        session.activate(userDN);  
        session.putProperty("Principal", userDN);
        session.putProperty("UserId", userName);
        session.putProperty("Host", request.getRemoteHost());
        session.putProperty("RemoteAddress", request.getRemoteAddr());
        session.putProperty("CookieDomain", cookieDomain);
        session.setMaxSessionTime(MAX_SESSION_TIME);
        session.setMaxIdleTime(MAX_IDLE_TIME);
        session.setType(Session.USER_SESSION);
        
        String p1 = request.getParameter("p1");
        String p2 = request.getParameter("p2");
        String p3 = request.getParameter("p3");

        addSessionProperty(session, "p1", p1);
        addSessionProperty(session, "p2", p2);
        addSessionProperty(session, "p3", p3);
        
        String tokenString = session.getID().toString();
        
        Cookie sessionCookie = new Cookie(cookieName, tokenString);
        sessionCookie.setDomain(cookieDomain);
        sessionCookie.setPath("/");
        
        response.addCookie(sessionCookie);
        
        return tokenString;
    }
    
    private void addSessionProperty(InternalSession session, 
            String name, String value) 
    {
        if (name != null && name.trim().length() > 0 &&
                value != null && value.trim().length() > 0) 
        {
            session.putProperty(name, value);
        }
    }
    
    private void sendResponse(HttpServletResponse response, String message) 
        throws IOException 
    {
       response.setContentType("text/html");
       PrintWriter writer = response.getWriter();
       
       writer.println("<html><head><title>Login Response</title></head>");
       writer.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");       
       writer.println("<p><p><b>" + message + "</b>");
       writer.println("</body></html>");
       writer.flush();
       writer.close();
    }
    
    
    protected void doGet(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException 
    {
        doPost(request, response);
    }
}
