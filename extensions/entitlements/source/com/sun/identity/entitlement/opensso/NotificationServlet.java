/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: NotificationServlet.java,v 1.3 2009-06-09 19:10:24 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.ApplicationManager;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.PrivilegeIndexStore;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.security.AdminTokenAction;
import java.io.IOException;
import java.io.Writer;
import java.security.AccessController;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles notification request for privilege changes.
 */
public class NotificationServlet extends HttpServlet {
    public static final String CONTEXT_PATH = "/notification";
    public static final String PRIVILEGE_DELETED = "privilegedeleted";
    public static final String REFERRAL_DELETED = "referraldeleted";
    public static final String APPLICATIONS_CHANGED = "applicationsChanged";
    public static final String ATTR_REALM_NAME = "realm";
    public static final String ATTR_NAME = "name";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        handleRequest(req, res);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException {
        handleRequest(req, res);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse res){
        Writer writer = null;
        try {
            String uri = req.getRequestURI();
            String path = req.getContextPath();

            String action = uri.substring(path.length());
            action = action.substring(CONTEXT_PATH.length() +1);

            if (action.equals(PRIVILEGE_DELETED)) {
                handlePrivilegeDeleted(req);
            } else if (action.equals(REFERRAL_DELETED)) {
                handleReferralPrivilegeDeleted(req);
            } else if (action.equals(APPLICATIONS_CHANGED)) {
                handleApplicationsChanged(req);
            }

            writer = res.getWriter();
            res.setHeader("content-type", "text/html");
            writer.write("200");
            writer.flush();
        } catch (IOException ex) {
            PrivilegeManager.debug.error("NotificationServlet.handleRequest",
                ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                //ignore
            }
        }
    }

    private void handlePrivilegeDeleted(HttpServletRequest req) {
        String privilegeName = req.getParameter(ATTR_NAME);
        String realm = req.getParameter(ATTR_REALM_NAME);

        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PrivilegeIndexStore pis = PrivilegeIndexStore.getInstance(
            SubjectUtils.createSubject(adminToken), realm);
        try {
            pis.delete(privilegeName, false);
        } catch (EntitlementException e) {
            //ignore
        }
    }

    private void handleReferralPrivilegeDeleted(HttpServletRequest req) {
        String referralName = req.getParameter(ATTR_NAME);
        String realm = req.getParameter(ATTR_REALM_NAME);

        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        PrivilegeIndexStore pis = PrivilegeIndexStore.getInstance(
            SubjectUtils.createSubject(adminToken), realm);
        try {
            pis.deleteReferral(referralName, false);
        } catch (EntitlementException e) {
            //ignore
        }
    }

    private void handleApplicationsChanged(HttpServletRequest req) {
        String realm = req.getParameter(ATTR_REALM_NAME);
        ApplicationManager.clearCache(realm);
    }

}
