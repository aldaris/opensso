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
 * $Id: AttributeLookupCondition.java,v 1.1 2009-04-23 23:29:20 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TOFIX
 */
public class AttributeLookupCondition implements EntitlementCondition {
    public static final String MACRO_USER = "$USER";
    public static final String MACRO_RESOURCE = "$RES";

    private String key;
    private String value;
    private String pConditionName = "";

    public AttributeLookupCondition() {
    }

    public AttributeLookupCondition(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            key = jo.optString("key");
            value = jo.optString("value");
            pConditionName = jo.optString("pConditionName");
        } catch (JSONException joe) {
            //TOFIX
        }
    }

    public String getState() {
        return toString();
    }

    public ConditionDecision evaluate(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment)
        throws EntitlementException {
        String evalKey = null;

        // e.g. $USER.postaladdress;
        int idxUserMacro = key.indexOf(MACRO_USER);
        if (idxUserMacro != -1) {
            String attrName = key.substring(MACRO_USER.length() +1);
            evalKey = getAttributeFromSubject(subject, attrName);
        } else {
            evalKey = key;
        }


        // e.g. $RES.postaladdress;
        String searchKey = value.replace(MACRO_RESOURCE, resourceName);
        Set<String> evalValues = environment.get(searchKey);
        String evalVal = ((evalValues == null) || evalValues.isEmpty()) ?
            null : (String)evalValues.iterator().next();

        if ((evalKey == null) && (evalVal != null)) {
            return getFailedDecision(key, evalVal);
        } else if ((evalVal == null) && (evalKey != null)) {
            return getFailedDecision(value, evalKey);
        } else if ((evalVal == null) && (evalKey == null)) {
            return getFailedDecision(key, value);
        }

        return new ConditionDecision(
            evalValues.contains(evalKey), Collections.EMPTY_MAP);
    }

    private ConditionDecision getFailedDecision(String prefix, String suffix) {
        Map<String, Set<String>> advices = new HashMap<String, Set<String>>();
        Set<String> set = new HashSet<String>();
        set.add(prefix + "=" + suffix);
        advices.put(getClass().getName(), set);
        return new ConditionDecision(false, advices);
    }
    
    private String getAttributeFromSubject(Subject subject, String attrName) {
        Set publicCreds = subject.getPublicCredentials();
        String attrValue = null;
        
        for (Iterator i = publicCreds.iterator(); 
            i.hasNext() && (attrValue == null); ) {
            Object o = i.next();
            if (o instanceof String) {
                String v = (String)o;
                if (v.startsWith(attrName + "=")) {
                    attrValue = v.substring(attrName.length() + 1);
                }
            }
        }
        
        return attrValue;
    }

    public String getKey() {
        return key;
    }

    public String getPConditionName() {
        return pConditionName;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setPConditionName(String pConditionName) {
        this.pConditionName = pConditionName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping  of the object
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("key", key);
        jo.put("value", value);
        jo.put("pConditionName", pConditionName);
        return jo;
    }

    /**
     * Returns <code>true</code> if the passed in object is equal to this object
     * @param obj object to check for equality
     * @return  <code>true</code> if the passed in object is equal to this object
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !getClass().equals(obj.getClass())) {
            return false;
        }
        AttributeLookupCondition object = (AttributeLookupCondition) obj;
        if (key == null) {
            if (object.key != null) {
                return false;
            }
        } else {
            if (!key.equals(object.key)) {
                return false;
            }
        }
        if (value == null) {
            if (object.value != null) {
                return false;
            }
        } else {
            if (!value.equals(object.value)) {
                return false;
            }
        }
        if (pConditionName == null) {
            if (object.pConditionName != null) {
                return false;
            }
        } else {
            if (!pConditionName.equals(object.pConditionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns hash code of the object
     * @return hash code of the object
     */
    @Override
    public int hashCode() {
        int code = 0;
        if (key != null) {
            code += key.hashCode();
        }
        if (value != null) {
            code += value.hashCode();
        }
        if (pConditionName != null) {
            code += pConditionName.hashCode();
        }
        return code;
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    @Override
    public String toString() {
        String s = null;
        try {
            s = toJSONObject().toString(2);
        } catch (JSONException joe) {
            //TOFIX
        }
        return s;
    }

}
