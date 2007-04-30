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
 * $Id: PrincipalFilter.java,v 1.1 2007-04-30 01:28:27 pbryan Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2007 Paul C. Bryan
 */

package com.sun.identity.openid.filter;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.sso.SSOToken;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Servlet filter that uses OpenSSO client SDK to determine authenticated
 * principal and pass it to the servlet in the request. This filter constitutes
 * the sole binding between the OpenID extension and OpenSSO.
 *
 * @author pbryan
 */
public class PrincipalFilter implements Filter
{
    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @return TODO.
     */
    private ServletRequest wrapRequest(ServletRequest request)
    {
        // this filter only manages HTTP requests
        if (!(request instanceof HttpServletRequest)) {
            return request;
        }

        HttpServletRequest httpRequest = (HttpServletRequest)request;

        SSOTokenManager manager;
        
        try {
            manager = SSOTokenManager.getInstance();
        }

        catch (SSOException ssoe) {
            return request;
        }

        SSOToken token;

        try {
            token = manager.createSSOToken(httpRequest);
        }

        catch (SSOException ssoe) {
            return request;
        }

        // token must be valid to use its principal
        if (!manager.isValidToken(token)) {
            return request;
        }

        try {
            return new PrincipalWrapper(httpRequest, token.getPrincipal());
        }

        catch (SSOException ssoe) {
            return request;
        }
    }

    /**
     * Called by the web container to indicate to the filter that it is being
     * placed into service.
     *
     * @param config passes information to filter during initialization.
     * @throws ServletException if an error occurs.
     */
    public void init(FilterConfig config) {
    }

    /**
     * Called by the web container each time a request/response pair is passed
     * through the chain due to a client request for a resource at the end of
     * the chain.
     *
     * @param request object to provide client request information to a servlet.
     * @param response object to assist in sending a response to the client.
     * @param chain gives a view into the invocation chain of a filtered request.
     * @throws IOException if an I/O error occurs.
     * @throws ServletException if an error occurs.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
    FilterChain chain) throws IOException, ServletException {
        chain.doFilter(wrapRequest(request), response);
    }

    /**
     * Called by the web container to indicate to the filter that it is being
     * taken out of service.
     */
    public void destroy() {
    }
}
