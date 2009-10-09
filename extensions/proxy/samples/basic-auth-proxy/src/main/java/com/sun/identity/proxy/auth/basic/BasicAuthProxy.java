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
 * $Id: BasicAuthProxy.java,v 1.2 2009-10-09 07:38:39 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.samples.auth.basic;

import com.sun.identity.proxy.auth.HttpBasicAuthFilter;
import com.sun.identity.proxy.auth.StaticCredentialSource;
import com.sun.identity.proxy.io.DefaultCacheFactory;
import com.sun.identity.proxy.servlet.SimpleProxyServlet;
import javax.servlet.ServletException;

/**
 * The simplest reverse proxy servlet implementation with authentication.
 * All incoming servlet requests are sent to the specified host and port,
 * via the specified protocol. Authentication is performed via HTTP basic
 * authentication, with static credentials.
 *
 * @author Paul C. Bryan
 */
public class BasicAuthProxy extends SimpleProxyServlet
{
    /**
     * Initializes the servlet. Establishes the remote protocol, host and port
     * of the remote server to send all incoming requests to and adds an HTTP
     * basic authentication filter to the filter chain with hard-coded
     * credentials.
     *
     * @throws ServletException if an exception occurs that interrupts normal operation.
     */
    public void init() throws ServletException
    {
        // establish the protocol, host and port of the remote server
        init("http", "1.2.3.4", -1);  // -1 uses the protocol's port (80 for http)

        // add filter to transparently perform HTTP basic authentication
        chain.addFilter(new HttpBasicAuthFilter(
         new StaticCredentialSource("MyUsername", "MyPassword"),
         new DefaultCacheFactory()));
    }
}

