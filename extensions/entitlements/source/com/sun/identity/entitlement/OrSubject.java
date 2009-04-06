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
 * $Id: OrSubject.java,v 1.4 2009-04-06 23:46:08 arviranga Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementSubject wrapper on a set of EntitlementSubject(s) to provide boolean OR logic
 * Membership is of OrSubject is satisfied if the user is a member of any
 * of the wrapped EntitlementSubject
 * @author dorai
 */
public class OrSubject implements EntitlementSubject {
    private static final long serialVersionUID = -403250971215465050L;

    private Set<EntitlementSubject> eSubjects;
    private String pSubjectName;

    /**
     * Constructs OrSubject
     */
    public OrSubject() {
    }

    /**
     * Constructs OrSubject
     * @param eSubjects wrapped EntitlementSubject(s)
     */
    public OrSubject(Set<EntitlementSubject> eSubjects) {
        this.eSubjects = eSubjects;
    }

    /**
     * Constructs OrSubject
     * @param eSubjects wrapped EntitlementSubject(s)
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when UserESubject was created from
     * OpenSSO policy Subject
     */
    public OrSubject(Set<EntitlementSubject> eSubjects, String pSubjectName) {
        this.eSubjects = eSubjects;
        this.pSubjectName = pSubjectName;
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
        return null;
    }

    /**
     * Sets the nested EntitlementSubject(s)
     * @param eSubjects the nested EntitlementSubject(s)
     */
    public void setESubjects(Set<EntitlementSubject> eSubjects) {
        this.eSubjects = eSubjects;
    }

    /**
     * Returns the nested EntitlementSubject(s)
     * @return  the nested EntitlementSubject(s)
     */
    public Set<EntitlementSubject> getESubjects() {
        return eSubjects;
    }

    /**
     * Sets OpenSSO policy Subject name
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when UserESubject was created from
     * OpenSSO policy Subject
     */
    public void setPSubjectName(String pSubjectName) {
        this.pSubjectName = pSubjectName;
    }

    /**
     * Returns OpenSSO policy Subject name
     * @return  subject name as used in OpenSSO policy,
     * this is releavant only when UserESubject was created from
     * OpenSSO policy Subject
     */
    public String getPSubjectName() {
        return pSubjectName;
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping of the object
     * @throws org.json.JSONException if can not map to JSONObject
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("pSubjectName", pSubjectName);
        for (EntitlementSubject eSubject : eSubjects) {
            JSONObject subjo = new JSONObject();
            subjo.put("className", eSubject.getClass().getName());
            subjo.put("state", eSubject.getState());
            jo.append("memberESubject", subjo);
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
            debug.error("OrESubject.toString(), JSONException: " +
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
        OrSubject object = (OrSubject) obj;
        if (eSubjects == null) {
            if (object.getESubjects() != null) {
                return false;
            }
        } else { // eSubjects not null
            if ((object.getESubjects()) == null) {
                return false;
            } else if (!eSubjects.containsAll(object.getESubjects())) {
                return false;
            } else if (!object.getESubjects().containsAll(eSubjects)) {
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
        if (eSubjects != null) {
            for (EntitlementSubject eSubject : eSubjects) {
                code += eSubject.hashCode();
            }
        }
        if (pSubjectName != null) {
            code += pSubjectName.hashCode();
        }
        return code;
    }

    public Map<String, String> getSearchIndexAttributes() {
        return (Collections.EMPTY_MAP);
    }

    public Set<String> getRequiredAttributeNames() {
        return(Collections.EMPTY_SET);
    }
}
