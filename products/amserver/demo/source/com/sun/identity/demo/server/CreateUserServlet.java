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
 * $Id: CreateUserServlet.java,v 1.1 2006-02-02 03:56:05 mrudul_uchil Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Properties;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Enumeration;
import java.util.Properties;

import java.security.AccessController;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdOperation;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.security.AdminTokenAction;

public class CreateUserServlet extends HttpServlet {
    
    // the debug file
    private static Debug debug = Debug.getInstance("amCreateUserServlet");
    
    ServletConfig config = null;
    
    /**
     * Initializes the servlet.
     * @param config servlet config
     * @throws ServletException if it fails to get servlet context.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.config = config;
    }
    
    /** Destroys the servlet.
     */
    public void destroy() {
        
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request.
     * @param response servlet response.
     * @throws ServletException
     * @throws java.io.IOException
     */
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException
     * @throws java.io.IOException
     */
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException
     * @throws java.io.IOException
     */
    protected void processRequest(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, java.io.IOException {        
        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        Map attrMap = new HashMap();
        String key1 = "iplanet-am-user-login-status";
        Set set1 = new HashSet();
        set1.add("Active");
        attrMap.put(key1, set1);
        String key2 = "uid";
        Set set2 = new HashSet();
        set2.add(userName);
        attrMap.put(key2, set2);
        String key3 = "givenname";
        Set set3 = new HashSet();
        set3.add(userName);
        attrMap.put(key3, set3);
        String key4 = "sn";
        Set set4 = new HashSet();
        set4.add(" ");
        attrMap.put(key4, set4);
        String key5 = "cn";
        Set set5 = new HashSet();
        set5.add(" ");
        attrMap.put(key5, set5);
        String key6 = "userpassword";
        Set set6 = new HashSet();
        set6.add(password);
        attrMap.put(key6, set6);
        debug.message(" Creating User = " + userName);
        SSOToken ssoToken = (SSOToken) AccessController.doPrivileged(
        AdminTokenAction.getInstance());
        try {
            AMIdentityRepository amir =
            new AMIdentityRepository(ssoToken, "/");
            
            //  see if services are supported for the given IdType
            Set set = amir.getAllowedIdOperations(IdType.USER);
            if (!set.contains(IdOperation.CREATE)) {
                debug.message("Create not allowed : " + set);
            }
            amir.createIdentity(IdType.USER, userName, attrMap);
            debug.message("User "+ userName + " Created");
            sendResponse(response, "User " + userName 
                + " created successfully.");
            return;
        } catch (Exception ire) {
            debug.message("Error: Can not create user : " + ire.toString());
            sendResponse(response, "Error: Can not create user.");
            return;
        }
    }
    
    private void sendResponse(HttpServletResponse response, String message)
        throws IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        
        writer.println("<html><head><title>Login Response</title></head>");
        writer.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        writer.println("<p><p><b>" + message + "</b>");
        writer.println("</body></html>");
        writer.flush();
        writer.close();
    }
}
