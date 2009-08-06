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
 * $Id: NumericAttributeCondition.java,v 1.1 2009-08-06 20:46:36 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Condition for evaluating attribute value of numeric type.
 */
public class NumericAttributeCondition implements EntitlementCondition {
    public static enum Operator {LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL, 
        GREATER_THAN_OR_EQUAL, GREATER_THAN};
    private String attributeName;
    private Operator operator = Operator.EQUAL;
    private float value;

    public void setState(String state) {
          try {
            JSONObject jo = new JSONObject(state);
            if (jo.has("attributeName")) {
                attributeName = jo.optString("attributeName");
            }
            if (jo.has("operator")) {
                String strOp = jo.getString("operator");
                for (Operator o : Operator.values()) {
                    if (o.toString().equals(strOp)) {
                        operator = o;
                        break;
                    }
                }
                if (operator == null) {
                    operator = Operator.EQUAL;
                }
            }

            String strValue = jo.getString("value");
            try {
                value = Float.parseFloat(strValue);
            } catch (NumberFormatException e) {
                PrivilegeManager.debug.error(
                    "NumericAttributeCondition.setState",
                    e);
            }
        } catch (JSONException e) {
            PrivilegeManager.debug.error("NumericAttributeCondition.setState",
                e);
        }
    }

    public String getState() {
        try {
            JSONObject jo = new JSONObject();
            if (attributeName != null) {
                jo.put("attributeName", attributeName);
            }
            if (operator != null) {
                jo.put("operator", operator);
            }
            jo.put("value", Float.toString(value));
            return jo.toString();
        } catch (JSONException e) {
            PrivilegeManager.debug.error("NumericAttributeCondition.getState",
                e);
            return "";
        }
    }

    public ConditionDecision evaluate(String realm, Subject subject,
        String resourceName,
        Map<String, Set<String>> environment) throws EntitlementException {
        boolean allowed = false;
        if ((attributeName != null) && (attributeName.length() > 0)) {
            Set<String> values = environment.get(attributeName);
            for (String v : values) {
                allowed = match(v);
                if (allowed) {
                    break;
                }
            }
        } else {
            PrivilegeManager.debug.error(
                "NumericAttributeCondition cannot be evaluated because attribute name is null",
                null);
        }
        return new ConditionDecision(allowed, Collections.EMPTY_MAP);
    }

    private boolean match(String str) {
        boolean match = false;
        try {
            Float v = Float.parseFloat(str);
            switch (operator) {
                case LESS_THAN:
                    match = (v < value);
                    break;
                case LESS_THAN_OR_EQUAL:
                    match = (v <= value);
                    break;
                case EQUAL:
                    match = (v == value);
                    break;
                case GREATER_THAN_OR_EQUAL:
                    match = (v >= value);
                    break;
                case GREATER_THAN:
                    match = (v > value);
                    break;
            }
        } catch (NumberFormatException e) {
            PrivilegeManager.debug.warning(
                "NumericAttributeCondition.match",
                e);
        }
        return match;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public Operator getOperator() {
        return operator;
    }

    public float getValue() {
        return value;
    }
}
