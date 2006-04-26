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
 * $Id: DeletePolicyServlet.java,v 1.1 2006-04-26 18:41:57 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Set;
import java.util.Iterator;

import java.security.AccessController;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.sso.SSOToken;

import com.sun.identity.policy.PolicyManager;
import com.sun.identity.policy.PolicyUtils;
import com.sun.identity.security.AdminTokenAction;

public class DeletePolicyServlet extends HttpServlet {
    
    // the debug file
    private static Debug debug = Debug.getInstance("amDeletePolicyServlet");
    private String organization = null;
    
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
    
    /** 
     * Destroys the servlet.
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
     * Reads in  the "," separated names of policies to be deleted
     * from the servlet request parameter </code>policyNames</code> and 
     * deletes the policies with the given names.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException
     * @throws java.io.IOException
     */
    protected void processRequest(
        HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, java.io.IOException 
    {        
        String policyNames = request.getParameter("policyNames");
        Set policies = null;
        if ((policyNames != null) && !(policyNames.equals(""))) {
            if (debug.messageEnabled()) {
                debug.message(" Deleting Policy = " + policyNames);
            }
            policies = PolicyUtils.delimStringToSet(policyNames, ",");
        } 
        SSOToken ssoToken = (SSOToken) AccessController.doPrivileged(
        AdminTokenAction.getInstance());
        try {
            organization = SystemProperties.get("com.iplanet.am.defaultOrg");
            PolicyManager pm = new PolicyManager(ssoToken, organization);
            Iterator it = policies.iterator();
            while (it.hasNext()) {
                String policyName = (String)it.next();
                pm.removePolicy(policyName);
                if (debug.messageEnabled()) {
                    debug.message("removed policy:"+policyName);
                }
            }
            sendResponse(response, "Policy " + policyNames
                + " deleted successfully.");
            return;
        } catch (Exception ire) {
            debug.error("Error: Cannot delete policy : " ,ire);
            sendResponse(response, "Error: Can not delete policy.");
            return;
        }
    }
    
    private void sendResponse(HttpServletResponse response, String message)
        throws IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        
        writer.println("<html><head><title>Policy Delete Response"
            +"</title></head>");
        writer.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        writer.println("<p><p><b>" + message + "</b>");
        writer.println("</body></html>");
        writer.flush();
        writer.close();
    }
}
