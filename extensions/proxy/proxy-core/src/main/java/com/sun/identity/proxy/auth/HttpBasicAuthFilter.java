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
 * $Id: HttpBasicAuthFilter.java,v 1.4 2009-10-08 04:29:40 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.auth;

import com.sun.identity.proxy.handler.Filter;
import com.sun.identity.proxy.handler.HandlerException;
import com.sun.identity.proxy.http.Exchange;
import com.sun.identity.proxy.http.Response;
import com.sun.identity.proxy.io.CachedStream;
import com.sun.identity.proxy.io.CacheFactory;
import com.sun.identity.proxy.util.Base64;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class HttpBasicAuthFilter extends Filter
{
    /** Headers (in lower-case) that are suppressed from incoming request. */
    private static final HashSet<String> SUPPRESS_REQUEST_HEADERS =
     new HashSet<String>(Arrays.asList("authorization"));

    /** Headers (in lower-case) that are suppressed for outgoing response. */
    private static final HashSet<String> SUPPRESS_RESPONSE_HEADERS =
     new HashSet<String>(Arrays.asList("www-authenticate"));

    /** A handle that this object instance can use to lookup attributes in the session object. */
    private final String objectId = UUID.randomUUID().toString();

    /** TODO: Description. */
    private PasswordCredentialSource source;

    /** TODO: Description. */
    private CacheFactory factory;

    /**
     * TODO: Description.
     *
     * @param source TODO.
     * @param factory TODO.
     */
    public HttpBasicAuthFilter(PasswordCredentialSource source, CacheFactory factory) {
        this.source = source;
        this.factory = factory;
    }

    /**
     * TODO: Description.
     *
     * @param attribute TODO.
     * @return TODO.
     */
    private String attributeName(String attribute) {
        return this.getClass().getName() + ":" + objectId + ":" + attribute;
    }

    /**
     * TODO: Description.
     *
     * @param exchange TODO.
     * @throws HandlerException TODO.
     * @throws IOException TODO.
     */
    public void handle(Exchange exchange) throws HandlerException, IOException
    {
        CachedStream entity = null;   

        exchange.request.headers.remove(SUPPRESS_REQUEST_HEADERS);
 
        // cache incoming entity for replay
        if (exchange.request.entity != null) {
            exchange.request.entity = entity = factory.cacheStream(exchange.request.entity);
        }

        // loop to retry for retrieved credentials
        for (int n = 0; n < 2; n++) {


            if (entity != null) {
                entity.rewind(); // harmless the first time around
            }

            // because credentials are sent in every request, this class caches them in the session
            String userpass = (String)exchange.request.session.get(attributeName("userpass"));

            if (userpass != null) {
                exchange.request.headers.add("Authorization", "Basic " + userpass);
            }

            next.handle(exchange);

            // successful from this filter's standpoint
            if (exchange.response.status != 401) {
                exchange.response.headers.remove(SUPPRESS_RESPONSE_HEADERS);
                return;
            }

            // credentials might be stale, so fetch them again
            PasswordCredentials credentials = source.credentials(exchange.request);

            // no credentials are handled as invalid credentials
            if (credentials == null) {
                break;
            }

            // ensure conformance with specification
            if (credentials.username.indexOf(':') > 0) {
                throw new HandlerException("username must not contain colon");
            }

            // set in session for fetch in next iteration of this loop
            exchange.request.session.put(attributeName("userpass"),
             Base64.encode((credentials.username + ":" + credentials.password).getBytes()));
        }

        // close the incoming response because it's about to be dereferenced (important!)
        if (exchange.response.entity != null) {
            exchange.response.entity.close();
        }

        // credentials were missing or invalid; let credential source handle the error
        exchange.response = new Response();
        source.invalid(exchange);
    }
}

