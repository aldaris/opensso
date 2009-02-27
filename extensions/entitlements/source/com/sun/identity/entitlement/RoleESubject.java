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
 * $Id: RoleESubject.java,v 1.2 2009-02-27 06:19:14 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;

import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * ESubject to represent role identity for membership check
 * @author dorai
 */
public class RoleESubject implements ESubject {

    private String role;
    private String pSubjectName;
    boolean openSSOSubject = false;

    /**
     * Constructs an RoleESubject
     */
    public RoleESubject() {
    }

    /**
     * Constructs RoleESubject
     * @param role the uuid of the role who is member of the ESubject
     */
    public RoleESubject(String role) {
        this.role = role;
    }

    /**
     * Constructs RoleESubject
     * @param role the uuid of the role who is member of the ESubject
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when RoleESubject was created from
     * OpenSSO policy Subject
     */
    public RoleESubject(String role, String pSubjectName) {
        this.role = role;
        this.pSubjectName = pSubjectName;
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            role = jo.optString("role");
            pSubjectName = jo.optString("pSubjectName");
            openSSOSubject = jo.optBoolean("openSSOSubject");
        } catch (JSONException joe) {
        }
    }

    /**
     * Returns state of the object
     * @return state of the object encoded as string
     */
    public String getState() {
        return toString();
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping  of the object
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("role", role);
        jo.put("pSubjectName", pSubjectName);
        return jo;
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    public String toString() {
        String s = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("role", role);
            jo.put("pSubjectName", pSubjectName);
            s = jo.toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("RoleESubject.toString(), JSONException:" +
                    joe.getMessage());
        }
        return s;
    }

    /**
     * Returns <code>SubjectDecision</code> of
     * <code>ESubject</code> evaluation
     * @param subject ESubject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>SubjectDecision</code> of
     * <code>ESubject</code> evaluation
     * @throws com.sun.identity.entitlement,  EntitlementException in case
     * of any error
     */
    public SubjectDecision evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return null;
    }

    /**
     * Sets the member role of the object
     * @param role the uuid of the member role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns the member role of the object
     * @return  the uuid of the member role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets OpenSSO policy subject name of the object
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when RoleESubject was created from
     * OpenSSO policy Subject
     */
    public void setPSubjectName(String pSubjectName) {
        this.pSubjectName = pSubjectName;
    }

  /**
     * Returns OpenSSO policy subject name of the object
     * @return subject name as used in OpenSSO policy,
     * this is releavant only when RoleESubject was created from
     * OpenSSO policy Subject
     */
    public String getPSubjectName() {
        return pSubjectName;
    }
}
