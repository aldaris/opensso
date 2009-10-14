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
 * $Id: SimpleProxyServlet.java,v 1.2 2009-10-14 08:57:03 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.servlet;

import com.sun.identity.proxy.handler.Chain;
import com.sun.identity.proxy.client.ClientHandler;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.ServletException;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class SimpleProxyServlet extends HandlerServlet
{
    /** TODO: Description. */
    protected Chain chain = null;

    /**
     * TODO: Description.
     *
     * @param scheme TODO.
     * @param host TODO.
     * @param port specific port number, or -1 to use scheme-default port.
     * @throws ServletException TODO.
     */
    protected void init(String scheme, String host, int port) throws ServletException {
        try {
            base = new URI(scheme, null, host, port, null, null, null);
        }
        catch (URISyntaxException use) {
            throw new ServletException(use);
        }
        handler = chain = new Chain(new ClientHandler());
    }
}

