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
 * $Id: PolicyCondition.java,v 1.1 2009-04-28 00:34:34 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.policy.PolicyException;
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
public class PolicyCondition implements EntitlementCondition {
    private String className;
    private String name;
    private Map<String, Set<String>> properties;

    public PolicyCondition(
        String name,
        String className,
        Map<String, Set<String>> properties) {
        this.className = className;
        this.properties = properties;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public Map<String, Set<String>> getProperties() {
        return properties;
    }

    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            this.name = jo.optString("name");
            this.className = jo.optString("className");
            this.properties = getProperties((JSONObject)jo.opt("properties"));
        } catch (JSONException ex) {
            //TOFIX
        }
    }

    private Map<String, Set<String>> getProperties(JSONObject jo) 
        throws JSONException {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        for (Iterator i = jo.keys(); i.hasNext(); ) {
            String key = (String)i.next();
            JSONArray arr = (JSONArray)jo.opt(key);
            Set set = new HashSet<String>();
            result.put(key, set);

            for (int j = 0; j < arr.length(); j++) {
                set.add(arr.getString(j));
            }
        }
        return result;
    }

    public String getState() {
        JSONObject jo = new JSONObject();

        try {
            jo.put("className", className);
            jo.put("name", name);
            jo.put("properties", properties);
            return jo.toString(2);
        } catch (JSONException ex) {
            //TOFIX
        }
        return "";
    }

    public ConditionDecision evaluate(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        try {
            com.sun.identity.policy.interfaces.Condition cond =
                (com.sun.identity.policy.interfaces.Condition)
                Class.forName(className).newInstance();
            cond.setProperties(properties);
            SSOToken token = getSSOToken(subject);
            com.sun.identity.policy.ConditionDecision dec =
                cond.getConditionDecision(token, properties);
            return new ConditionDecision(dec.isAllowed(), dec.getAdvices());
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
