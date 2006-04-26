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
 * $Id: CreatePolicyServlet.java,v 1.1 2006-04-26 18:41:56 bhavnab Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.demo.server;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
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
import com.sun.identity.idm.IdUtils;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdSearchControl;

import com.sun.identity.policy.Policy;
import com.sun.identity.policy.PolicyManager;
import com.sun.identity.policy.Rule;
import com.sun.identity.policy.SubjectTypeManager;
import com.sun.identity.policy.interfaces.Subject;
import com.sun.identity.security.AdminTokenAction;

public class CreatePolicyServlet extends HttpServlet {
    
    // the debug file
    private static Debug debug = Debug.getInstance("amCreatePolicyServlet");
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
     * Reads in the servlet request parameters 
     * <li><code>policyName</code> - name of the policy</li>
     * <li><code>resource</code> - resource name to be used while creating the 
     *     policy rule</li>
     * <li><code>user</code> - user name for who the policy is being 
     *     defined</li>
     * Policy is created for default realm as defined in
     * "com.iplanet.am.defaultOrg" in server_env.properties.
     * Policy is created for "iPlanetAMWebAgentService" service.
     * Only one rule is created for the policy, the <code>resource</code> name
     * is used in the creation of this rule.
     * Only one subject is created in the policy of type <code>
     * AMIdentitySubject</code> which is the universal Identifier  of 
     * <code>user</code>
     * Only "GET" action is defined with value being "ALLOW".
     * 
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
        String policyName = request.getParameter("policyName");
        String resource = request.getParameter("resource");
        String user = request.getParameter("user");
        if (debug.messageEnabled()) {
            debug.message(" Creating Policy = " + policyName);
            debug.message(" resource, user :" +resource+","+ user);
        }
        SSOToken ssoToken = (SSOToken) AccessController.doPrivileged(
        AdminTokenAction.getInstance());
        try {
            organization = SystemProperties.get("com.iplanet.am.defaultOrg");
            PolicyManager pm = new PolicyManager(ssoToken, organization);
            Policy policy = new Policy(policyName, policyName);
            Map actions = new HashMap(1);
            Set values = new HashSet(1);
            values.add("allow");
            actions.put("GET",values); 
            Rule rule = new Rule("rule1", "iPlanetAMWebAgentService", 
                resource, actions);
            policy.addRule(rule);
            SubjectTypeManager stm = pm.getSubjectTypeManager();
            Subject subject = stm.getSubject("AMIdentitySubject");
            Set subjectValues = new HashSet(1);
            String userPattern = "*"+user+"*";
            AMIdentityRepository amidRep = 
                new AMIdentityRepository(ssoToken, organization);
            debug.message("userPattern:"+userPattern);
            Set searchresults = amidRep.searchIdentities(IdType.USER, 
                userPattern, new IdSearchControl()).getSearchResults();
          
            if (debug.messageEnabled()) {
                debug.message("set size:"+searchresults.size()); 
                debug.message("searchresults:"+searchresults.toString());
            }
            AMIdentity amid = null;
            Iterator it = searchresults.iterator(); 
            if (it.hasNext()) {
                amid = (AMIdentity)it.next();
            }
            if (amid != null) {
                debug.message("did get AMIdentity");
                subjectValues.add(IdUtils.getUniversalId(amid));
                subject.setValues(subjectValues);
            }
            policy.addSubject("AMIdentitySubject",subject);
            pm.addPolicy(policy);

            if (debug.messageEnabled()) {
                debug.message("Policy "+ policyName + " Created");
            }
            sendResponse(response, "Policy " + policyName 
                + " created successfully.");
            return;
        } catch (Exception ire) {
            debug.error("Error: Cannot create policy : " ,ire);
            sendResponse(response, "Error: Can not create policy.");
            return;
        }
    }
    
    private void sendResponse(HttpServletResponse response, String message)
        throws IOException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        
        writer.println("<html><head><title>Policy Create Response"
            +"</title></head>");
        writer.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        writer.println("<p><p><b>" + message + "</b>");
        writer.println("</body></html>");
        writer.flush();
        writer.close();
    }
}
