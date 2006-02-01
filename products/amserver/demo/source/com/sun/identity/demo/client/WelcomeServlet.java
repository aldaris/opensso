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
 * $Id: WelcomeServlet.java,v 1.2 2006-02-01 08:55:19 mrudul_uchil Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.common.Constants;

public class WelcomeServlet extends HttpServlet {
    
    public static final String NEWLINE = 
        System.getProperty("line.separator", "\n");
    static String server_url = SystemProperties.get(Constants.
        AM_SERVER_PROTOCOL)+ "://" + SystemProperties.get(
        Constants.AM_SERVER_HOST)+ ":" + SystemProperties.
        get(Constants.AM_SERVER_PORT) + SystemProperties.
        get(Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR)
        + "/UI/Login";
    
    protected void doPost(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
        try {
            SSOTokenManager mgr = SSOTokenManager.getInstance();
            SSOToken token = mgr.createSSOToken(request);
            if (mgr.isValidToken(token)) {
                String principal = "" + token.getProperty("Principal");
                String authLevel = token.getProperty("AuthLevel");
                String authType = token.getProperty("AuthType");
                String organization = token.getProperty("Organization");
                String authInstant = token.getProperty("authInstant");
                String indexType = token.getProperty("IndexType");
                String locale = token.getProperty("Locale");
                String clientType = token.getProperty("clientType");
                String charSet = token.getProperty("CharSet");
                String loginURL = token.getProperty("loginURL");
                String successURL = token.getProperty("successURL");
                String userId = token.getProperty("UserId");
                
                StringBuffer buff = new StringBuffer("<pre>");
                buff.append(NEWLINE);
                buff.append("You are already logged in as : ");
                buff.append(principal).append(NEWLINE);
                buff.append(NEWLINE);
                buff.append("Your custom properties are: ").append(NEWLINE);
                buff.append("    UserId = ").append(userId).
                append(NEWLINE);
                buff.append("    Organization = ").append(organization).
                append(NEWLINE);
                buff.append("    authInstant = ").append(authInstant).
                append(NEWLINE);
                buff.append("    IndexType = ").append(indexType).
                append(NEWLINE);
                buff.append("    Locale = ").append(locale).
                append(NEWLINE);
                buff.append("    AuthLevel = ").append(authLevel).
                append(NEWLINE);
                buff.append("    AuthType = ").append(authType).
                append(NEWLINE);
                buff.append("    ClienType = ").append(clientType).
                append(NEWLINE);
                buff.append("    CharSet = ").append(charSet).
                append(NEWLINE);
                buff.append("    LoginURL = ").append(loginURL).
                append(NEWLINE);
                buff.append("    SuccessURL = ").append(successURL).
                append(NEWLINE);
                buff.append("</pre>");
                
                sendResponse(response, buff.toString());
                return;
            }
        } catch (Exception ex) {
            String request_url = request.getRequestURL().toString();
            String redirect_url = server_url + "?goto=" + request_url;
            response.sendRedirect(redirect_url);            
        }
    }
    
    private void sendResponse(HttpServletResponse response, String message)
    throws IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        
        writer.println("<html><head><title>Client Response</title></head>");
        writer.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        writer.println("<br><h3>Demo Client</h3>");
        writer.println("<p><p><b>" + message + "</b>");
        writer.println("</body></html>");
        writer.flush();
        writer.close();
    }
    
    protected void doGet(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
