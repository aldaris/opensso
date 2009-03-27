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
 * $Id: IPCondition.java,v 1.2 2009-03-27 00:27:24 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;

import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementCondition to represent IP, DNS name based  constraint
 * @author dorai
 */
public class IPCondition implements EntitlementCondition {

    private String startIp;
    private String endIp;
    private String domainNameMask;
    private String pConditionName;

    /**
     * Constructs an IPCondition
     */
    public IPCondition() {
    }

    /**
     * Constructs IPCondition object:w
     * @param domainNameMask domain name mask, for example *.example.com,
     * only wild card allowed is *
     */
    public IPCondition(String domainNameMask) {
        this.domainNameMask = domainNameMask;
    }

    /**
     * Constructs IPCondition object:w
     * @param startIp starting ip of a range for example 121.122.123.124
     * @param endIp ending ip of a range, for example 221.222.223.224
     */
    public IPCondition(String startIp, String endIp) {
        this.startIp = startIp;
        this.endIp = endIp;
    }

    /**
     * Constructs IPCondition object:w
     * @param domainNameMask domain name mask, for example *.example.com,
     * only wild card allowed is *
     * @param startIp starting ip of a range for example 121.122.123.124
     * @param endIp ending ip of a range, for example 221.222.223.224
     */
    public IPCondition(String domainNameMask, String startIp, String endIp) {
        this.domainNameMask = domainNameMask;
        this.startIp = startIp;
        this.endIp = endIp;
    }

    /**
     * Returns state of the object
     * @return state of the object encoded as string
     */
    public String getState() {
        return toString();
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            domainNameMask = jo.optString("domainNameMask");
            startIp = jo.optString("startIp");
            endIp = jo.optString("endIp");
            pConditionName = jo.optString("pConditionName");
        } catch (JSONException joe) {
        }
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
     * @return the domainNameMask
     */
    public String getDomainNameMask() {
        return domainNameMask;
    }

    /**
     * @param domainNameMask the domainNameMask to set
     */
    public void setDomainNameMask(String domainNameMask) {
        this.domainNameMask = domainNameMask;
    }

    /**
     * @return the startIp
     */
    public String getStartIp() {
        return startIp;
    }

    /**
     * @param startIp the startIp to set
     */
    public void setStartIp(String startIp) {
        this.startIp = startIp;
    }

    /**
     * @return the endIp
     */
    public String getEndIp() {
        return endIp;
    }

    /**
     * @param endIp the endIp to set
     */
    public void setEndIp(String endIp) {
        this.endIp = endIp;
    }

    /**
     * Returns OpenSSO policy subject name of the object
     * @return subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public String getPConditionName() {
        return pConditionName;
    }

    /**
     * Sets OpenSSO policy subject name of the object
     * @param pConditionName subject name as used in OpenSSO policy,
     * this is releavant only when UserECondition was created from
     * OpenSSO policy Condition
     */
    public void setPConditionName(String pConditionName) {
        this.pConditionName = pConditionName;
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping  of the object
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("domainNameMask", domainNameMask);
        jo.put("startIp", startIp);
        jo.put("endIp", endIp);
        jo.put("pConditionName", pConditionName);
        return jo;
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
        IPCondition object = (IPCondition) obj;
        if (getDomainNameMask() == null) {
            if (object.getDomainNameMask() != null) {
                return false;
            }
        } else {
            if (!domainNameMask.equals(object.getDomainNameMask())) {
                return false;
            }
        }
        if (getStartIp() == null) {
            if (object.getStartIp() != null) {
                return false;
            }
        } else {
            if (!startIp.equals(object.getStartIp())) {
                return false;
            }
        }
        if (getEndIp() == null) {
            if (object.getEndIp() != null) {
                return false;
            }
        } else {
            if (!endIp.equals(object.getEndIp())) {
                return false;
            }
        }
        if (getPConditionName() == null) {
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
        if (domainNameMask != null) {
            code += domainNameMask.hashCode();
        }
        if (startIp != null) {
            code += startIp.hashCode();
        }
        if (endIp != null) {
            code += endIp.hashCode();
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
    public String toString() {
        String s = null;
        try {
            s = toJSONObject().toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("IPCondiiton.toString(), JSONException:" +
                    joe.getMessage());
        }
        return s;
    }
}
