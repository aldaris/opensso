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
 * $Id: WelcomeServlet.java,v 1.1 2005-11-01 00:28:33 arvindp Exp $
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

import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;

public class WelcomeServlet extends HttpServlet {

    public static final String NEWLINE = 
        System.getProperty("line.separator", "\n");

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
            sendResponse(response, "You do not have a valid session!");
        }
    }

    private void sendResponse(HttpServletResponse response, String message) 
        throws IOException 
    {
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
            HttpServletResponse response) throws ServletException, IOException 
    {
        doPost(request, response);
    }    
}
