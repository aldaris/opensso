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
 * $Id: SimpleProxy.java,v 1.1 2009-10-08 22:33:51 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.samples.simple;

import com.sun.identity.proxy.servlet.SimpleProxyServlet;
import javax.servlet.ServletException;

/**
 * The simplest reverse proxy servlet implementation: all incoming servlet
 * requests are sent to the specified host and port, via specified protocol.
 *
 * @author Paul C. Bryan
 */
public class SimpleProxy extends SimpleProxyServlet
{
    /**
     * Initializes the servlet. Establishes the protocol, host and port of the
     * remote server to send all incoming requests to.
     *
     * @throws ServletException if an exception occurs that interrupts normal operation.
     */
    public void init() throws ServletException {
        init("http", "1.2.3.4", -1); // -1 uses the protocol's port (80 for http)
    }
}

