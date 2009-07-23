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
 * $Id: ClientFilter.java,v 1.1 2009-07-23 20:04:29 mrudul_uchil Exp $
 *
 */

package com.sun.identity.wssagents.jaxws.client;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.security.auth.Subject;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.am.util.SystemProperties;
import com.sun.identity.shared.Constants;

public class ClientFilter implements Filter {

     public static ThreadLocal cred = new ThreadLocal();

     public void init(FilterConfig config) {
     }

     public void doFilter(ServletRequest request, ServletResponse response,
                   FilterChain chain) throws ServletException {
         HttpServletRequest httpRequest = 
                (HttpServletRequest)request;
         HttpServletResponse httpResponse = (HttpServletResponse)response;
         SSOToken ssoToken = null;

         String redirectLoginUrl = getLoginURL(httpRequest);

         try {
             SSOTokenManager manager = SSOTokenManager.getInstance();
             ssoToken = manager.createSSOToken(httpRequest);
             if (!manager.isValidToken(ssoToken)) {
                 httpResponse.sendRedirect(redirectLoginUrl);
                 return;
             }
             Subject subject = new Subject();
             subject.getPrivateCredentials().add(ssoToken);
             cred.set(subject);
             chain.doFilter(request, response);
         } catch (Exception e) {
             //Invalid SSOToken, hence redirect to Login URL
             try {
                 httpResponse.sendRedirect(redirectLoginUrl);
             } catch (IOException ie) {
                ie.printStackTrace();
                throw new ServletException(ie.getMessage());
             }
             return;
         }
         
     }

    /**
     * Returns Login URL for client to be redirected.
     * @param request the <code>HttpServletRequest</code>.
     *
     * @return String Login URL
     */
     public String getLoginURL(HttpServletRequest request) {
         String loginURL =
             SystemProperties.get(Constants.LOGIN_URL);
         StringBuffer requestURL = request.getRequestURL();

         // This is useful for SAML2 integrations.
         String gotoparam = SystemProperties.get(
                "com.sun.identity.loginurl.goto", "goto");
         loginURL = loginURL + "?" + gotoparam + "=" + requestURL.toString();
         String query = request.getQueryString();
         if(query != null) {
             loginURL = loginURL + "&" + query;
         }
         return loginURL;

     }

     public void destroy() {
     }

}
