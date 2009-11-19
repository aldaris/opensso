/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms of the Common
 * Development and Distribution License (the License). You may not use
 * this file except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each
 * file and include the License file at opensso/legal/CDDLv1.0.txt. If
 * applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: ResourceBase.java,v 1.2 2009-11-19 00:10:38 qcheng Exp $
 */

package com.sun.identity.rest;

import com.sun.identity.coretoken.CoreTokenException;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.PrivilegeManager;
import com.sun.identity.entitlement.util.AuthSPrincipal;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.json.JSONObject;
import org.json.JSONException;

public abstract class ResourceBase {

    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS_MESSAGE = "statusMessage";
    public static final String BODY = "body";

    public enum MimeType {PLAIN, JSON};

    private static final String RES_BUNDLE_NAME = "RestException";

    protected Subject getCaller(HttpServletRequest req)
        throws RestException {
        Principal p = req.getUserPrincipal();
        if (p != null) {
            if (p instanceof ISubjectable) {
                try {
                    return ((ISubjectable)p).createSubject();
                } catch (Exception e) {
                    throw new RestException(1, e);
                }
            }
            return toSubject(p.getName());
        }
        throw new RestException(2);
    }

    protected Subject getSubject(HttpServletRequest request) 
        throws RestException {
        return RestServiceManager.getInstance().getAuthZSubject(request);
    }

    protected Map<String, Set<String>> getMap(List<String> list) {
        Map<String, Set<String>> env = new HashMap<String, Set<String>>();

        if ((list != null) && !list.isEmpty()) {
            for (String l : list) {
                if (l.contains("=")) {
                    String[] cond = l.split("=", 2);
                    Set<String> set = env.get(cond[0]);

                    if (set == null) {
                        set = new HashSet<String>();
                        env.put(cond[0], set);
                    }

                    set.add(cond[1]);
                }
            }
        }
        
        return env;
    }

    protected Entitlement toEntitlement(String resource, String action) {
        Set<String> set = new HashSet<String>();
        set.add(action);
        return new Entitlement(resource, set);
    }

    protected Subject toSubject(Principal principal) {
        if (principal == null) {
            return null;
        }
        Set<Principal> set = new HashSet<Principal>();
        set.add(principal);
        return new Subject(false, set, new HashSet(), new HashSet());
    }

    protected Subject toSubject(String subject) {
        return (subject == null) ? null :
            toSubject(new AuthSPrincipal(subject));
    }

     protected WebApplicationException getWebApplicationException(
            HttpHeaders headers,
            RestException e,
            MimeType mimeType
     ) {
        if (MimeType.JSON == mimeType) {
            int statusCode = e.getErrorCode();
            String statusMessage =  e.getLocalizedMessage(
                getUserLocale(headers));
            String responseJsonString = createResponseJSONString(
                statusCode, statusMessage, null);
            ResponseBuilder responseBuilder =  Response.status(statusCode);
            responseBuilder.status(statusCode);
            responseBuilder.entity(responseJsonString);
            responseBuilder.type("applicaton/json; charset=UTF-8");
            throw new WebApplicationException(responseBuilder.build());
        } else {
            throw new WebApplicationException(
                  Response.status(e.getErrorCode())
                  .entity(e.getLocalizedMessage(getUserLocale(headers)))
                  .type("text/plain; charset=UTF-8").build());
        }
    }

    protected WebApplicationException getWebApplicationException(
            HttpHeaders headers,
            EntitlementException e, MimeType mimeType) {
        if (MimeType.JSON == mimeType) {
            int statusCode = e.getErrorCode();
            String statusMessage =  e.getLocalizedMessage(
                getUserLocale(headers));
            String responseJsonString = createResponseJSONString(
                statusCode, statusMessage,  null);
            ResponseBuilder responseBuilder =  Response.status(statusCode);
            responseBuilder.status(statusCode);
            responseBuilder.entity(responseJsonString);
            responseBuilder.type("applicaton/json; charset=UTF-8");
            throw new WebApplicationException(responseBuilder.build());
        } else {
            throw new WebApplicationException(
                  Response.status(e.getErrorCode())
                  .entity(e.getLocalizedMessage(getUserLocale(headers)))
                  .type("text/plain; charset=UTF-8").build());
        }
    }

    protected WebApplicationException getWebApplicationException(
        HttpHeaders headers, CoreTokenException e) {
        throw new WebApplicationException (
              Response.status(e.getHttpStatusCode())
              .entity(e.getLocalizedMessage(getUserLocale(headers)))
              .type("text/plain; charset=UTF-8").build());
    }

    protected WebApplicationException getWebApplicationException(
            JSONException e, MimeType mimeType) {
        return getWebApplicationException(425, e, mimeType);
    }

    protected WebApplicationException getWebApplicationException(
            int statusCode,
            Exception e, MimeType mimeType) {
        if (MimeType.JSON == mimeType) {
            String statusMessage =  e.getLocalizedMessage();
            String responseJsonString = createResponseJSONString(
                statusCode, statusMessage, null);
            ResponseBuilder responseBuilder =  Response.status(statusCode);
            responseBuilder.status(statusCode);
            responseBuilder.entity(responseJsonString);
            responseBuilder.type("applicaton/json; charset=UTF-8");
            throw new WebApplicationException(responseBuilder.build());
        } else {
            throw new WebApplicationException(
                  Response.status(statusCode)
                  .entity(e.getLocalizedMessage())
                  .type("text/plain; charset=UTF-8").build());
        }
    }
    
    protected Locale getUserLocale(HttpHeaders headers) {
        List<Locale> locales = headers.getAcceptableLanguages();
        return ((locales == null) || locales.isEmpty()) ? Locale.getDefault() :
            locales.get(0);
    }

    protected String createResponseJSONString(
        int statusCode,
        String statusMessage,
        JSONObject body
    ) {
        JSONObject jo = null;
        try {
            jo = new JSONObject();
            jo.put(STATUS_CODE, statusCode);
            if (statusMessage != null) {
                jo.put(STATUS_MESSAGE, statusMessage);
            }
            if (body != null) {
                jo.put(BODY, body);
            }
        } catch (JSONException je) {
            PrivilegeManager.debug.error(
                "ResourceBase.createeResponseJSONString(): hit JSONException",
                je);
        }
        return (jo != null) ? jo.toString(): "{}";
    }

     /**
     * Returns localized exception message.
     *
     * @param locale Locale of the message.
     * @param statusCode statusCode of the message
     * @return localized exception message.
     */
    protected String getLocalizedMessage(Locale locale, int statusCode) {
        ResourceBundle rb = ResourceBundle.getBundle(RES_BUNDLE_NAME, locale);
        String msg = rb.getString(Integer.toString(statusCode));
        return msg;
    }

}


