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
 * $Id: ResourceBase.java,v 1.2 2009-10-21 01:11:05 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.util.AuthSPrincipal;
import com.sun.identity.security.ISubjectable;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.json.JSONException;

public abstract class ResourceBase {
    protected Subject getCaller(HttpServletRequest req)
        throws EntitlementException {
        Principal p = req.getUserPrincipal();
        if (p != null) {
            if (p instanceof ISubjectable) {
                try {
                    return ((ISubjectable)p).createSubject();
                } catch (Exception e) {
                    throw new EntitlementException(433, e);
                }
            }
            return toSubject(p.getName());
        }
        throw new EntitlementException(423);
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
        EntitlementException e) {
        throw new WebApplicationException(
              Response.status(e.getErrorCode())
              .entity(e.getLocalizedMessage(getUserLocale(headers)))
              .type("text/plain; charset=UTF-8").build());
    }

    protected WebApplicationException getWebApplicationException(
        JSONException e) {
        throw new WebApplicationException(
              Response.status(425)
              .entity(e.getLocalizedMessage())
              .type("text/plain; charset=UTF-8").build());
    }
    
    protected Locale getUserLocale(HttpHeaders headers) {
        List<Locale> locales = headers.getAcceptableLanguages();
        return ((locales == null) || locales.isEmpty()) ? Locale.getDefault() :
            locales.get(0);
    }
}

