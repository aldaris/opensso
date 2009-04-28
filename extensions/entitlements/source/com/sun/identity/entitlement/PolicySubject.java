/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: PolicySubject.java,v 1.1 2009-04-28 00:34:34 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.policy.PolicyException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * //TOFIX
 * @author dennis
 */
public class PolicySubject implements EntitlementSubject {
    private String name;
    private String className;
    private Set<String> values;
    private boolean exclusive;

    public PolicySubject(
        String name,
        String className,
        Set<String> values,
        boolean exclusive) {
        this.name = name;
        this.className = className;
        this.values = values;
        this.exclusive = exclusive;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getValues() {
        return values;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            this.name = jo.optString("name");
            this.className = jo.optString("className");
            this.exclusive = jo.optBoolean("exclusive");
            this.values = getValues((JSONArray)jo.opt("values"));
        } catch (JSONException ex) {
            //TOFIX
        }
    }

    private Set<String> getValues(JSONArray jo)
        throws JSONException {
        Set<String> result = new HashSet<String>();
        for (int i = 0; i < jo.length(); i++) {
            result.add(jo.getString(i));
        }
        return result;
    }

    public String getState() {
        JSONObject jo = new JSONObject();

        try {
            jo.put("name", name);
            jo.put("className", className);
            jo.put("exclusive", exclusive);
            jo.put("values", values);
            return jo.toString(2);
        } catch (JSONException ex) {
            //TOFIX
        }
        return "";
    }

    public Map<String, Set<String>> getSearchIndexAttributes() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>(4);
        Set<String> set = new HashSet<String>();
        set.add(SubjectAttributesCollector.ATTR_NAME_ALL_ENTITIES);
        map.put(SubjectAttributesCollector.NAMESPACE_IDENTITY, set);
        return map;
    }

    public Set<String> getRequiredAttributeNames() {
        return(Collections.EMPTY_SET);
    }

    public SubjectDecision evaluate(
        SubjectAttributesManager mgr,
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        try {
            com.sun.identity.policy.interfaces.Subject sbj =
                (com.sun.identity.policy.interfaces.Subject)
                Class.forName(className).newInstance();
            sbj.setValues(values);
            SSOToken token = getSSOToken(subject);
            return new SubjectDecision(sbj.isMember(token),
                Collections.EMPTY_MAP);
        } catch (SSOException ex) {
            //TOFIX
        } catch (PolicyException ex) {
            //TOFIX
        } catch (ClassNotFoundException ex) {
            //TOFIX
        } catch (InstantiationException ex) {
            //TOFIX
        } catch (IllegalAccessException ex) {
            //TOFIX
        }
        return null;
    }

    private static SSOToken getSSOToken(Subject subject) {
        Set privateCred = subject.getPrivateCredentials();
        for (Iterator i = privateCred.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o instanceof SSOToken) {
                return (SSOToken)o;
            }
        }
        return null;
    }
}
