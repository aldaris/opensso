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
 * $Id: ClientHandler.java,v 1.1 2009-10-06 01:05:17 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.client;

import com.sun.identity.proxy.handler.Handler;
import com.sun.identity.proxy.handler.HandlerException;
import com.sun.identity.proxy.http.Exchange;
import com.sun.identity.proxy.http.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.client.protocol.RequestTargetAuthentication;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;


/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class ClientHandler implements Handler
{
    /** Default maximum number of collections through HTTP client. */
    private static final int DEFAULT_CONNECTIONS = 64;

    /** Headers (in lower-case) that are suppressed for outgoing request. */
    private static final HashSet<String> SUPPRESS_REQUEST_HEADERS = new HashSet<String>(
     Arrays.asList("connection", "content-encoding", "content-length", "content-type", "keep-alive",
      "proxy-authenticate", "proxy-authorization", "te", "trailers", "transfer-encoding", "upgrade"));

    /** Headers (in lower-case) that are suppressed from incoming response. */
    private static final HashSet<String> SUPPRESS_RESPONSE_HEADERS = new HashSet<String>(Arrays.asList(
     "connection", "keep-alive", "proxy-authenticate", "proxy-authorization", "te", "trailers",
     "transfer-encoding", "upgrade"));

    /** TODO: Description. */
    private URI target;

    /** TODO: Description. */
    private DefaultHttpClient httpClient;

    /**
     * TODO: Description.
     *
     * @param target TODO.
     */
    public ClientHandler(URI target) {
        this(target, DEFAULT_CONNECTIONS);
    }

    /**
     * TODO: Description.
     *
     * @param target TODO.
     * @param connections TODO.
     */
    public ClientHandler(URI target, int connections)
    {
        this.target = target;

    	BasicHttpParams parameters = new BasicHttpParams();
		ConnManagerParams.setMaxTotalConnections(parameters, connections);
		HttpProtocolParams.setVersion(parameters, HttpVersion.HTTP_1_1);
		HttpClientParams.setRedirecting(parameters, false);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));

        ClientConnectionManager connectionManager = new ThreadSafeClientConnManager(parameters, registry);

        httpClient = new DefaultHttpClient(connectionManager, parameters);
        httpClient.removeRequestInterceptorByClass(RequestAddCookies.class);
        httpClient.removeRequestInterceptorByClass(RequestProxyAuthentication.class);
        httpClient.removeRequestInterceptorByClass(RequestTargetAuthentication.class);
        httpClient.removeRequestInterceptorByClass(RequestTargetHost.class);
        httpClient.removeRequestInterceptorByClass(RequestUserAgent.class);
        httpClient.removeResponseInterceptorByClass(ResponseProcessCookies.class);
    }

    /**
     * TODO: Description.
     *
     * @param exchange TODO.
     * @throws IOException TODO.
     * @throws HandlerException TODO.
     */
    public void handle(Exchange exchange) throws IOException, HandlerException
    {
        HttpRequestBase clientRequest = (exchange.request.entity != null ?
         new EntityRequest(exchange.request) : new NonEntityRequest(exchange.request));

        clientRequest.setURI(target.resolve(exchange.request.uri));

        // request headers
        for (String name : exchange.request.headers.keySet()) {
            if (!SUPPRESS_REQUEST_HEADERS.contains(name.toLowerCase())) {
                for (String value : exchange.request.headers.get(name)) {
                    clientRequest.addHeader(name, value);
                }
            }
        }

        HttpResponse clientResponse = httpClient.execute(clientRequest);

        exchange.response = new Response();

        // response entity
        HttpEntity clientResponseEntity = clientResponse.getEntity();
        if (clientResponseEntity != null) {
            exchange.response.entity = clientResponseEntity.getContent();
        }

        // response status line
        StatusLine statusLine = clientResponse.getStatusLine();
        exchange.response.version = statusLine.getProtocolVersion().toString();
        exchange.response.status = statusLine.getStatusCode();
        exchange.response.reason = statusLine.getReasonPhrase();

        // response headers
        for (HeaderIterator i = clientResponse.headerIterator(); i.hasNext();) {
            Header header = i.nextHeader();
            String name = header.getName();
            if (!SUPPRESS_RESPONSE_HEADERS.contains(name.toLowerCase())) {
                exchange.response.headers.add(name, header.getValue());
            }
        }
// TODO: decide if need to try-finally to call httpRequest.abort?
    }
}

