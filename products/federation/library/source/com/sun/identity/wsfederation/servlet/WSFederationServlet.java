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
 * $Id: WSFederationServlet.java,v 1.1 2007-06-21 23:01:33 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.servlet;

import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.wsfederation.common.WSFederationConstants;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.sun.identity.wsfederation.common.WSFederationUtils;

/**
 *
 * @author ap102904
 * @version
 */
public class WSFederationServlet extends HttpServlet {
    private static Debug debug = WSFederationUtils.debug;
        
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected void doGet(HttpServletRequest request, 
        HttpServletResponse response)
    throws ServletException, IOException {
        String classMethod = "WSFederationServlet.doGet: ";
        
        String wa = (String)request.getParameter("wa");
        if (wa!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wa="+wa);
        }
        
        String whr = (String)request.getParameter("whr");
        if (whr!=null && debug.messageEnabled()) {
            debug.message(classMethod + "whr="+whr);
        }
        
        String wtrealm = (String)request.getParameter("wtrealm");
        if (wtrealm!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wtrealm="+wtrealm);
        }
        
        String wreply = (String)request.getParameter("wreply");
        if (wreply!=null && debug.messageEnabled()) {
            debug.message(classMethod + "whr="+wreply);
        }

        String wct = (String)request.getParameter("wct");
        if (wct!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wct="+wct);
        }

        String wctx = (String)request.getParameter("wctx");
        if (wctx!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wctx="+wctx);
        }

        WSFederationAction action = null;
        if (wa != null && wa.equals(WSFederationConstants.WSIGNIN10))
        {
            // GET with wa == wsignin1.0 => IP signin request
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "initiating IP signin request");
            }
            action = new IPSigninRequest(request, response, whr, wtrealm, wct,
                wctx, wreply);
        }
        else
        {
            // GET with no wa parameter = RP signin request
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "initiating SP signin request");                
            }

            // wtrealm (==metaalias) is mandatory
            if (wtrealm==null) {
                response.sendError(response.SC_BAD_REQUEST,
                        SAML2Utils.bundle.getString("nullSPEntityID"));
                return;
            }
            
            action = new RPSigninRequest(request, response, whr, wtrealm, wct,
                wctx, wreply);
        }
        
        action.process();
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException 
     * @throws java.io.IOException 
     */
    protected void doPost(HttpServletRequest request, 
        HttpServletResponse response)
    throws ServletException, IOException {
        String classMethod = "WSFederationServlet.doGet: ";
        
        String wa = (String)request.getParameter("wa");
        if (wa!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wa=" + wa);
        }
        
        String wresult = (String)request.getParameter("wresult");
        if (wresult!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wresult="+wresult);
        }
        
        String wctx = (String)request.getParameter("wctx");
        if (wctx!=null && debug.messageEnabled()) {
            debug.message(classMethod + "wctx="+wctx);
        }
        
        WSFederationAction action = null;
        if (wa != null && wa.equals(WSFederationConstants.WSIGNIN10) && 
            wresult != null)
        {
            if ( debug.messageEnabled() ) {
                debug.message(classMethod + "initiating SP signin response");
            }
            action = new RPSigninResponse(request,response,wresult,wctx);
        }
        else
        {
            debug.error(classMethod + "initiating SP signin response");
            response.sendError(response.SC_FORBIDDEN);
            return;
        }

        action.process();
    }
    
    /** Returns a short description of the servlet.
     * @return a short description of the servlet
     */
    public String getServletInfo() {
        return "Sun Java System Access Manager WS-Federation Servlet";
    }
    // </editor-fold>
}
