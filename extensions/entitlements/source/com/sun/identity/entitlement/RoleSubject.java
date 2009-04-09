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
 * $Id: RoleSubject.java,v 1.5 2009-04-09 13:15:02 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.idm.IdType;
import com.sun.identity.shared.debug.Debug;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementSubject to represent role identity for membership check
 * @author dorai
 */
public class RoleSubject implements EntitlementSubject {
    private static final long serialVersionUID = -403250971215465050L;

    private String role;
    private String pSubjectName;
    boolean openSSOSubject = false;

    /**
     * Constructs an RoleSubject
     */
    public RoleSubject() {
    }

    /**
     * Constructs RoleSubject
     * @param role the uuid of the role who is member of the EntitlementSubject
     */
    public RoleSubject(String role) {
        this.role = role;
    }

    /**
     * Constructs RoleSubject
     * @param role the uuid of the role who is member of the EntitlementSubject
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when RoleSubject was created from
     * OpenSSO policy Subject
     */
    public RoleSubject(String role, String pSubjectName) {
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
            s = toJSONObject().toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("RoleESubject.toString(), JSONException:" +
                    joe.getMessage());
        }
        return s;
    }

    /**
     * Returns <code>SubjectDecision</code> of
     * <code>EntitlementSubject</code> evaluation
     * @param subject EntitlementSubject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>SubjectDecision</code> of
     * <code>EntitlementSubject</code> evaluation
     * @throws com.sun.identity.entitlement,  EntitlementException in case
     * of any error
     */
    public SubjectDecision evaluate(
        SubjectAttributesManager mgr,
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment)
        throws EntitlementException {
        boolean satified = false;
        Set publicCreds = subject.getPublicCredentials();
        if ((publicCreds != null) && !publicCreds.isEmpty()) {
            Map<String, Set<String>> attributes = (Map<String, Set<String>>)
                publicCreds.iterator().next();
            Set<String> values = attributes.get(
                SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
                    IdType.ROLE.getName());
            satified = (values != null) ? values.contains(role) : false;
        }
        
        return new SubjectDecision(satified, Collections.EMPTY_MAP);
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
     * this is releavant only when RoleSubject was created from
     * OpenSSO policy Subject
     */
    public void setPSubjectName(String pSubjectName) {
        this.pSubjectName = pSubjectName;
    }

    /**
     * Returns OpenSSO policy subject name of the object
     * @return subject name as used in OpenSSO policy,
     * this is releavant only when RoleSubject was created from
     * OpenSSO policy Subject
     */
    public String getPSubjectName() {
        return pSubjectName;
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
        RoleSubject object = (RoleSubject) obj;
        if (role == null) {
            if (object.getRole() != null) {
                return false;
            }
        } else {
            if (!role.equals(object.getRole())) {
                return false;
            }
        }
        if (pSubjectName == null) {
            if (object.getPSubjectName() != null) {
                return false;
            }
        } else {
            if (!pSubjectName.equals(object.getPSubjectName())) {
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
        if (role != null) {
            code += role.hashCode();
        }
        if (pSubjectName != null) {
            code += pSubjectName.hashCode();
        }
        return code;
    }

    public Map<String, String> getSearchIndexAttributes() {
        Map<String, String> map = new HashMap<String, String>(2);
        map.put(SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
            IdType.ROLE.getName(), role);
        return map;
    }

    public Set<String> getRequiredAttributeNames() {
        Set<String> set = new HashSet<String>(2);
        set.add(SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
            IdType.ROLE.getName());
        return set;
    }
}
