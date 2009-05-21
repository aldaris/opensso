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
 * $Id: AttributeCondition.java,v 1.1 2009-05-21 23:30:23 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

public class AttributeCondition implements EntitlementCondition {
    private String attribute;

    public AttributeCondition(String attribute) {
        this.attribute = attribute;
    }

    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            attribute = jo.optString("attribute");
        } catch (JSONException joe) {
            PrivilegeManager.debug.error("AttributeCondition.setState", joe);
        }
    }

    public String getState() {
        try {
            return toJSONObject().toString();
        } catch (JSONException ex) {
            return "";
        }
    }

    public ConditionDecision evaluate(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        int idx = resourceName.indexOf("=");
        if (idx != -1) {
            return new ConditionDecision(false, Collections.EMPTY_MAP);
        }
        
        String prefix = resourceName.substring(0, idx).trim();
        String value = resourceName.substring(idx+1).trim();
        idx = prefix.indexOf(Privilege.RESOURCE_MACRO_ATTRIBUTE + "(");

        if (idx != 0) {
            return new ConditionDecision(false, Collections.EMPTY_MAP);
        }

        int marker = idx + Privilege.RESOURCE_MACRO_ATTRIBUTE.length() + 1;
        int end = prefix.indexOf(")", marker);
        if (end == -1) {
            return new ConditionDecision(false, Collections.EMPTY_MAP);
        }

        String attrName = prefix.substring(marker, end);
        Set<String> searchAttrName = new HashSet<String>();
        searchAttrName.add(attrName);
        SubjectAttributesManager m =
            SubjectAttributesManager.getInstance("/"); //TOFIX REALM
        Map<String, Set<String>> mapResults = 
            m.getAttributes(subject, searchAttrName);
        Set<String> results = mapResults.get(attrName);
        if ((results == null) || !results.contains(value)) {
            return new ConditionDecision(false, Collections.EMPTY_MAP);
        }

        return new ConditionDecision(true, Collections.EMPTY_MAP);
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping  of the object
     */
    private JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("attribute", attribute);
        return jo;
    }
}
