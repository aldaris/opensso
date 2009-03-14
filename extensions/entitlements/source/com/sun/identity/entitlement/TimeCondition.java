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
 * $Id: TimeCondition.java,v 1.1 2009-03-14 03:03:17 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;

import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementCondition to represent time based constraint
 * @author dorai
 */
public class TimeCondition implements EntitlementCondition {

    private String startTime;
    private String endTime;
    private String startDay;
    private String endDay;
    private String startDate;
    private String endDate;
    private String enforcementTimeZone;
    private String pConditionName;

    /**
     * Constructs an TimeCondition
     */
    public TimeCondition() {
    }

    /**
     * Constructs IPCondition object:w
     * @param domainNameMask domain name mask, for example *.example.com,
     * only wild card allowed is *
     * @param startIp starting ip of a range for example 121.122.123.124
     * @param endIp ending ip of a range, for example 221.222.223.224
     */
    public TimeCondition(String startTime, String endTime,
            String startDay, String endDay) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDay = startDay;
        this.endDay = endDay;
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
            startTime = jo.optString("startTime");
            endTime = jo.optString("endTime");
            startDay = jo.optString("startDay");
            endDay = jo.optString("endDay");
            startDate = jo.optString("startDate");
            endDate = jo.optString("endDate");
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
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startDay the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the startDay
     */
    public String getStartDay() {
        return startDay;
    }

    /**
     * @param startDay the startDay to set
     */
    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    /**
     * @return the endDay
     */
    public String getEndDay() {
        return endDay;
    }

    /**
     * @param endDay the endDay to set
     */
    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the enforcementTimeZone
     */
    public String getEnforcementTimeZone() {
        return enforcementTimeZone;
    }

    /**
     * @param enforcementTimeZone the enforcementTimeZone to set
     */
    public void setEnforcementTimeZone(String enforcementTimeZone) {
        this.enforcementTimeZone = enforcementTimeZone;
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
        jo.put("startTime", startTime);
        jo.put("endTime", endTime);
        jo.put("startDay", startDay);
        jo.put("endDay", endDay);
        jo.put("startDate", startDate);
        jo.put("endDate", endDate);
        jo.put("enforcementTimeZone", enforcementTimeZone);
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
            equalled = false;
        }
        if (!getClass().equals(obj.getClass())) {
            equalled = false;
        }
        TimeCondition object = (TimeCondition) obj;
        if (startDay == null) {
            if (object.getStartDay() != null) {
                return false;
            }
        } else {
            if (!startDay.equals(object.getStartDay())) {
                return false;
            }
        }
        if (getEndDay() == null) {
            if (object.getEndDay() != null) {
                return false;
            }
        } else {
            if (!endDay.equals(object.getEndDay())) {
                return false;
            }
        }
        if (startDate == null) {
            if (object.getStartDate() != null) {
                return false;
            }
        } else {
            if (!startDate.equals(object.getStartDate())) {
                return false;
            }
        }
        if (getEndDate() == null) {
            if (object.getEndDate() != null) {
                return false;
            }
        } else {
            if (!endDate.equals(object.getEndDate())) {
                return false;
            }
        }

        if (getEnforcementTimeZone() == null) {
            if (object.getEnforcementTimeZone() != null) {
                return false;
            }
        } else {
            if (!enforcementTimeZone.equals(object.getEnforcementTimeZone())) {
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
        //TODO: add logic for all the fields
        if (getPConditionName() != null) {
            code += getPConditionName().hashCode();
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
            debug.error("TimeCondition.toString(), JSONException:" +
                    joe.getMessage());
        }
        return s;
    }

}
