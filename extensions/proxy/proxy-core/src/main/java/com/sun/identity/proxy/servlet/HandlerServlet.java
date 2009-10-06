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
 * $Id: HandlerServlet.java,v 1.1 2009-10-06 01:05:19 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.servlet;

import com.sun.identity.proxy.handler.Handler;
import com.sun.identity.proxy.handler.HandlerException;
import com.sun.identity.proxy.http.Exchange;
import com.sun.identity.proxy.http.Request;
import com.sun.identity.proxy.handler.Session;
import com.sun.identity.proxy.io.Streamer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class HandlerServlet extends HttpServlet
{
    /** Headers (in lower-case) that are suppressed from incoming request. */
    private static final HashSet<String> SUPPRESS_REQUEST_HEADERS = new HashSet<String>(Arrays.asList("expect"));

    /** Headers (in lower-case) that are suppressed for outgoing response. */
    private static final HashSet<String> SUPPRESS_RESPONSE_HEADERS = new HashSet<String>(Arrays.asList("server"));

    /** TODO: Description. */
    protected Handler handler = null;

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @param response TODO.
     * @throws IOException TODO.
     * @throws ServletException TODO.
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        Exchange exchange = new Exchange();

        // ----- translate request --------------------------------------------

        exchange.request = new Request();
        
        // request method
        exchange.request.method = request.getMethod();

        try {
            StringBuffer buf = new StringBuffer(request.getRequestURI());

            String queryString = request.getQueryString();
            if (queryString != null) {
                buf.append('?').append(queryString);
            }

            // use single-argument constructor to preserve escaped octets and other characters
            exchange.request.uri = new URI(buf.toString());        
        }

        catch (URISyntaxException use) {
            throw new ServletException(use);
        }

        // request headers
        for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
// FIXME: suppress JSESSIONID cookie from incoming requests (cookie filter to the rescue?)
            String name = e.nextElement();
            if (!SUPPRESS_REQUEST_HEADERS.contains(name.toLowerCase())) {
                exchange.request.headers.add(name, Collections.list(request.getHeaders(name)));
            }
        }

        String method = request.getMethod().toUpperCase();

        // include request entity if appears to be provided with request
        if ((request.getContentLength() > 0 || request.getHeader("Transfer-Encoding") != null)
        && !method.equals("GET") && !method.equals("HEAD") && !method.equals("TRACE")
        && !method.equals("DELETE")) {
            exchange.request.entity = request.getInputStream();
        }

        HttpSession httpSession = request.getSession();
        synchronized(httpSession) { // tantamount to lazy initialization
            Session session = (Session)httpSession.getAttribute(Session.class.getName());
            if (session == null) {
                session = new Session();
                httpSession.setAttribute(Session.class.getName(), session);
            }
            exchange.request.session = session;
        }

        exchange.request.principal = request.getUserPrincipal();

        // handy servlet-specific attributes, sure to be abused by downstream filters
        exchange.request.attributes.put("javax.servlet.http.HttpServletRequest", request);
        exchange.request.attributes.put("javax.servlet.http.HttpServletResponse", response);

        try {

        // ----- execute request ----------------------------------------------

            try {
                handler.handle(exchange);
            }
            catch (HandlerException he) {
                throw new ServletException(he);
            }

        // ----- translate response -------------------------------------------

            // response status-code (reason-phrase is deprecated in servlet api)
            response.setStatus(exchange.response.status);

            // response headers
            for (String name : exchange.response.headers.keySet()) {
// FIXME: suppress JSESSIONID cookie from outgoing responses (cookie filter to the rescue?)
                if (!SUPPRESS_RESPONSE_HEADERS.contains(name.toLowerCase())) {
                    for (String value : exchange.response.headers.get(name)) {
                        if (value != null && value.length() > 0) {
                            response.addHeader(name, value);
                        }
                    }
                }
            }

            // response entity
            if (exchange.response.entity != null) {
                OutputStream out = response.getOutputStream();
                Streamer.stream(exchange.response.entity, out);
                out.flush();
            }
        }

        // ----- make sure underlying client can make new requests ------------

        finally {

            if (exchange.response != null && exchange.response.entity != null) {
                try {
                    exchange.response.entity.close(); // important!
                }
                catch (IOException ioe) {
                }
            }
        }        
    }
}

