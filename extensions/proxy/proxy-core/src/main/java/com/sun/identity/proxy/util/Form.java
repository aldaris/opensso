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
 * $Id: Form.java,v 1.1 2009-10-06 20:28:47 pbryan Exp $
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.proxy.util;

import com.sun.identity.proxy.http.Request;
import com.sun.identity.proxy.io.Streamer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class Form extends ListMap
{
    /**
     * TODO: Description.
     */
    public void parseQueryParams(Request request) {
        if (request != null && request.uri != null & request.uri.indexOf('?') > 0) {
            parse(request.uri.split("?", 2)[1]);
        }
    }

    /**
     * TODO: Description.
     *
     * @param request TODO.
     * @throws IOException if an I/O error occurs.
     */
    public void parseFormEntity(Request request) throws IOException {
        if (request != null & request.entity != null && request.headers != null
        && request.headers.first("Content-Type").equals("application/x-www-form-urlencoded")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Streamer.stream(request.entity, baos);
            parse(baos.toString());
        }
    }

    /**
     * TODO: Description.
     *
     * @param s TODO.
     */
    public void parse(String s) {
        for (String param : s.split("&")) {
            String[] nv = param.split("=", 2);
            if (nv.length == 2) {
                add(URLDecoder.decode(nv[0]), URLDecoder.decode(nv[1]));
            }
        }
    }

    /**
     * Populates a request URI with query parameters necessary for the form to
     * be submitted as a GET request. This adds query parameters to a URI that
     * may already have parameters.
     *
     * @param request the request to add query parameters to.
     */
    public void toQueryParams(Request request) {
        String query = toString();
        if (query.length() > 0) {
            String uri = (request.uri != null ? request.uri : "");
            int index = uri.indexOf('?');
            uri = uri + (uri.indexOf('?') > 0 ? '&' : '?') + query;
        }
    }

    /**
     * Populates a request with the necessary headers and entity for the form
     * to be submitted as a POST with application/x-www-form-urlencoded content
     * type. This overwrites any entity that may already be in the request.
     *
     * @param request the request to add the form entity to.
     */
    public void toFormEntity(Request request) {
        String form = toString();
        request.headers.put("Content-Type", "application/x-www-form-urlencoded");
        request.headers.put("Content-Length", Integer.toString(form.length()));
        request.entity = new ByteArrayInputStream(form.getBytes());
    }

    /**
     * TODO: Description.
     *
     * @return TODO.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (String name : keySet()) {
            List<String> values = get(name);
            if (values != null) {
                for (Iterator i = values.iterator(); i.hasNext();) {
                    String value = (String)i.next();
                    buf.append(URLEncoder.encode(name)).append('=').append(URLEncoder.encode(value));
                    if (i.hasNext()) {
                        buf.append('&');
                    }
                }
            }
        }
        return buf.toString();
    }
}

