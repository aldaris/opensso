/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at:
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 *
 * See the License for the specific language governing permission and
 * limitations under the License.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at opensso/legal/CDDLv1.0.txt. If applicable,
 * add the following below the CDDL Header, with the fields enclosed by
 * brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * $Id: OAuthServletFilter.java,v 1.1 2009-05-26 22:17:45 pbryan Exp $
 */

package com.sun.identity.oauth.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import com.sun.jersey.oauth.signature.OAuthSignature;
import com.sun.jersey.oauth.signature.OAuthSignatureException;

public class OAuthServletFilter implements Filter
{
    /** The OAuth protection realm to advertise in www-authenticate header. */
    private String realm;

    /** Manages and validates incoming nonces. */
    private NonceManager nonces;

    /** OAuth signature methods that are supported. */
    private HashSet<String> methods = new HashSet<String>();

    /** OAuth protocol versions that are supported. */
    private HashSet<String> versions = new HashSet<String>();

    /** Attributes used to index nonces in nonce manager. */
    private HashSet<String> nonceKeys = new HashSet<String>();

    /** Maximum age (in milliseconds) of timestamp to accept in incoming messages. */
    private int maxAge = -1;

    /** Average requests to process between nonce garbage collection passes. */
    private int gcPeriod = -1;

    /** Value to return in www-authenticate header when 401 response returned. */
    private String wwwAuthenticateHeader = null;

    /**
     * Called by the web container to indicate to the filter that it is being
     * placed into service.
     *
     * @param config passes information to filter during initialization.
     * @throws ServletException if an error occurs.
     */
    public void init(FilterConfig config) throws ServletException {
    
        // directly supported OAuth protocol versions
        versions.add(null);
        versions.add("1.0");

realm = "REALM"; // FIXME: configurable
methods.add("HMAC-SHA1"); // FIXME: configurable

        // set some reasonable defaults if not supplied
        if (maxAge == -1) { maxAge = 5 * 60 * 1000; } // 5 minutes
        if (gcPeriod == -1) { gcPeriod = 100; } // every 100 requests (average)
        if (nonceKeys.isEmpty()) { nonceKeys.add("consumerKey"); } // consumer index for nonces

        // mandatory configuration properties
        if (realm == null) { throw new ServletException("realm required"); }
        if (methods.isEmpty()) { throw new ServletException("methods required"); }

        nonces = new NonceManager(maxAge, gcPeriod);

        // static www-authenticate header for the life of the object
        wwwAuthenticateHeader = "OAuth realm = \"" + realm + "\"";
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
    public void doFilter(ServletRequest request,
    ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest hsRequest = (HttpServletRequest)request;
        HttpServletResponse hsResponse = (HttpServletResponse)response;    

        try {
            doFilter(request, response, chain);
        }
    
        catch (BadRequestException bre) {
            hsResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        catch (UnauthorizedException ue) {
            hsResponse.setHeader("WWW-Authenticate", wwwAuthenticateHeader);
            hsResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        }
    }

    private String required(String value) throws BadRequestException {
        if (value == null) { throw new BadRequestException(); }
        return value;
    }

    private String supported(String value, HashSet<String> set) throws BadRequestException {
        if (!set.contains(value)) { throw new BadRequestException(); }
        return value;
    }

    private void doFilter(HttpServletRequest request,
    HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        OAuthServletRequest osr = new OAuthServletRequest(request);

        OAuthParameters params = new OAuthParameters().readRequest(osr);

        // get required OAuth parameters
        String consumerKey = required(params.getConsumerKey());
        String token = required(params.getToken());
        String timestamp = required(params.getTimestamp());
        String nonce = required(params.getNonce());

        // enforce other supported and required OAuth parameters
        supported(params.getSignatureMethod(), methods);
        required(params.getSignature());
        supported(params.getVersion(), versions);

// TODO: retrieve consumer key secret
        String consumerSecret = null;

// TODO: retrieve access token secret
        String tokenSecret = null;

// TODO: retrieve OpenSSO subject behind access token
        String subject = null;

        if (consumerSecret == null || tokenSecret == null) {
            throw new UnauthorizedException();
        }

        OAuthSecrets secrets = new OAuthSecrets().consumerSecret(consumerSecret).tokenSecret(tokenSecret);

        if (!verifySignature(osr, params, secrets)) {
            throw new UnauthorizedException();
        }

        // assemble key that will be used to index nonce
        StringBuffer key = new StringBuffer();
        if (nonceKeys.contains("consumerKey")) { key.append(consumerKey); } // TODO: make more efficient? boolean?
        key.append('&');
        if (nonceKeys.contains("token")) { key.append(token); }

        if (!nonces.verify(key.toString(), timestamp, nonce)) {
            throw new UnauthorizedException();
        }

        // chain request wrapped with overridden getUserPrincipal to next filter
        chain.doFilter(new PrincipalRequestWrapper(request, new NamedPrincipal(subject)), response);
    }

    /**
     * Called by the web container to indicate to the filter that it is being
     * taken out of service.
     */
    public void destroy() {
    }
    
    private static boolean verifySignature(OAuthServletRequest osr,
    OAuthParameters params, OAuthSecrets secrets) throws ServletException {
        try { return OAuthSignature.verify(osr, params, secrets); }
        catch (OAuthSignatureException ose) { throw new ServletException(ose); }
    }
}
