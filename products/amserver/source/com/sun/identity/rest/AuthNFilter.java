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
 * $Id: AuthNFilter.java,v 1.1 2009-10-21 01:10:31 veiming Exp $
 *
 */

package com.sun.identity.rest;

import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.rest.spi.IAuthentication;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.StringTokenizer;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class AuthNFilter implements Filter {
    // TOFIX: make DEF_AUTH configurable
    public static final String DEF_AUTH = "SSOToken";
    
    private Map<String, IAuthentication> services = new
        HashMap<String, IAuthentication>();

    public void destroy() {
        for (IAuthentication auth : services.values()) {
            try {
                auth.destroy();
            } catch (Exception e) {
                // catch all exception, so that all auth filters have
                // the chance to shutdown.
                PrivilegeManager.debug.error("AuthNFilter.destroy", e);
            }
        }
        services.clear();
    }

    public void init(FilterConfig config) throws ServletException {
        ServiceLoader<IAuthentication> filters = ServiceLoader.load(
            IAuthentication.class);
        for (IAuthentication p : filters) {
            try {
                p.init(config);
                String[] acceptMtd = p.accept();
                for (int i = 0; i < acceptMtd.length; i++) {
                    services.put(acceptMtd[i], p);
                }
            } catch (Exception e) {
                // catch all exception, so that all auth filters have
                // the chance to registered
                PrivilegeManager.debug.error("AuthNFilter.init", e);
            }
        }
    }

    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain)
        throws IOException, ServletException
    {
        IAuthentication auth = getAuthenticationFilter(
            (HttpServletRequest) request);
        if (auth == null) {
            ((HttpServletResponse) response).setStatus(434);
        } else {
            auth.doFilter(request, response, chain);
        }
    }

    private IAuthentication getAuthenticationFilter(HttpServletRequest req) {
        String acceptAuth = req.getHeader("X-Accept-Authentication");
        if (acceptAuth == null) {
            return services.get(DEF_AUTH);
        }

        StringTokenizer st = new StringTokenizer(acceptAuth, ",");
        while (st.hasMoreTokens()) {
            String mtd = st.nextToken();
            IAuthentication auth = services.get(mtd);
            if (auth != null) {
                return auth;
            }
        }
        
        return null;
    }
}
