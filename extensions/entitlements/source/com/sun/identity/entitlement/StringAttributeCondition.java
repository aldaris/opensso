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
 * $Id: StringAttributeCondition.java,v 1.1 2009-08-06 20:46:36 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Condition for evaluating attribute value of string type.
 */
public class StringAttributeCondition implements EntitlementCondition {
    private String attributeName;
    private boolean bCaseSensitive;
    private String value;

    public void setState(String state) {
          try {
            JSONObject jo = new JSONObject(state);
            if (jo.has("attributeName")) {
                attributeName = jo.optString("attributeName");
            }
            bCaseSensitive = Boolean.parseBoolean(
                jo.optString("bCaseSensitive"));
            if (jo.has("value")) {
                value = jo.getString("value");
            }
        } catch (JSONException e) {
            PrivilegeManager.debug.error("StringAttributeCondition.setState",
                e);
        }
    }

    public String getState() {
        try {
            JSONObject jo = new JSONObject();
            if (attributeName != null) {
                jo.put("attributeName", attributeName);
            }
            jo.put("bCaseSensitive", Boolean.toString(bCaseSensitive));

            if (value != null) {
                jo.put("value", value);
            }
            return jo.toString();
        } catch (JSONException e) {
            PrivilegeManager.debug.error("StringAttributeCondition.getState",
                e);
            return "";
        }
    }

    public ConditionDecision evaluate(String realm, Subject subject,
        String resourceName,
        Map<String, Set<String>> environment) throws EntitlementException {
        boolean allowed = false;
        if ((attributeName != null) && (attributeName.length() > 0) &&
            (value != null)) {
            Set<String> values = environment.get(attributeName);
            for (String v : values) {
                allowed = (bCaseSensitive) ? v.equals(value) :
                    v.equalsIgnoreCase(value);
                if (allowed) {
                    break;
                }
            }
        } else {
            PrivilegeManager.debug.error(
                "StringAttributeCondition cannot be evaluated because either attribute name or value is null"
                , null);
        }
        return new ConditionDecision(allowed, Collections.EMPTY_MAP);
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setCaseSensitive(boolean flag) {
        bCaseSensitive = flag;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isCaseSensitive() {
        return bCaseSensitive;
    }

    public String getValue() {
        return value;
    }
}
