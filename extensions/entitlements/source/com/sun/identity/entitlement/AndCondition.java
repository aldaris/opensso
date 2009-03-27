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
 * $Id: AndCondition.java,v 1.3 2009-03-27 16:29:09 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementCondition wrapper on a set of EntitlementCondition(s) to provide boolean OR logic
 * Membership is of AndCondition is satisfied if the user is a member of any
 * of the wrapped EntitlementCondition
 * @author dorai
 */
public class AndCondition implements EntitlementCondition {
    private static final long serialVersionUID = -403250971215465050L;

    private Set<EntitlementCondition> eConditions;
    private String pConditionName;

    /**
     * Constructs AndCondition
     */
    public AndCondition() {
    }

    /**
     * Constructs AndCondition
     * @param eConditions wrapped EntitlementCondition(s)
     */
    public AndCondition(Set<EntitlementCondition> eConditions) {
        this.eConditions = eConditions;
    }

    /**
     * Constructs AndCondition
     * @param eConditions wrapped EntitlementCondition(s)
     * @param pConditionName subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public AndCondition(Set<EntitlementCondition> eConditions, String pConditionName) {
        this.eConditions = eConditions;
        this.pConditionName = pConditionName;
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        //TODO
    }

    /**
     * Returns state of the object
     * @return state of the object encoded as string
     */
    public String getState() {
        return toString();
    }

    /**
     * Returns <code>ConditionDecision</code> of
     * <code>EntitlementCondition</code> evaluation
     * @param subject EntitlementCondition who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>ConditionDecision</code> of
     * <code>EntitlementCondition</code> evaluation
     * @throws com.sun.identity.entitlement,  EntitlementException in case
     * of any error
     */
    public ConditionDecision evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return null;
    }

    /**
     * Sets the nested EntitlementCondition(s)
     * @param eConditions the nested EntitlementCondition(s)
     */
    public void setEConditions(Set<EntitlementCondition> eConditions) {
        this.eConditions = eConditions;
    }

    /**
     * Returns the nested EntitlementCondition(s)
     * @return  the nested EntitlementCondition(s)
     */
    public Set<EntitlementCondition> getEConditions() {
        return eConditions;
    }

    /**
     * Sets OpenSSO policy Condition name
     * @param pConditionName subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public void setPConditionName(String pConditionName) {
        this.pConditionName = pConditionName;
    }

    /**
     * Returns OpenSSO policy Condition name
     * @return  subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public String getPConditionName() {
        return pConditionName;
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping of the object
     * @throws org.json.JSONException if can not map to JSONObject
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("pConditionName", pConditionName);
        for (EntitlementCondition eCondition : eConditions) {
            JSONObject subjo = new JSONObject();
            subjo.put("className", eCondition.getClass().getName());
            subjo.put("state", eCondition.getState());
            jo.append("memberECondition", subjo);
        }
        return jo;
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    public String toString() {
        String s = null;
        try {
            JSONObject jo = toJSONObject();
            s = (jo == null) ? super.toString() : jo.toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("OrECondition.toString(), JSONException: " +
                    joe.getMessage());
        }
        return s;
    }

    /**
     * Returns <code>true</code> if the passed in object is equal to this object
     * @param obj object to check for equality
     * @return  <code>true</code> if the passed in object is equal to this object
     */
    public boolean equals(Object obj) {
        boolean equalled = true;
        if (obj == null) {
            return false;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        AndCondition object = (AndCondition) obj;
        if (eConditions == null) {
            if (object.getEConditions() != null) {
                return false;
            }
        } else { // eConditions not null
            if ((object.getEConditions()) == null) {
                return false;
            } else if (!eConditions.containsAll(object.getEConditions())) {
                equalled = false;
            } else if (!object.getEConditions().containsAll(eConditions)) {
                return false;
            }
        }
        if (pConditionName == null) {
            if (object.getPConditionName() != null) {
                return false;
            }
        } else {
            if (!pConditionName.equals(object.getPConditionName())) {
                return false;
            }
        }
        return equalled;
    }

    /**
     * Returns hash code of the object
     * @return hash code of the object
     */
    public int hashCode() {
        int code = 0;
        if (eConditions != null) {
            for (EntitlementCondition eCondition : eConditions) {
                code += eCondition.hashCode();
            }
        }
        if (pConditionName != null) {
            code += pConditionName.hashCode();
        }
        return code;
    }
}
