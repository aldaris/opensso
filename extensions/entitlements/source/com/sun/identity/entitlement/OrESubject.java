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
 * $Id: OrESubject.java,v 1.3 2009-02-27 16:58:44 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * ESubject wrapper on a set of ESubject(s) to provide boolean OR logic
 * Membership is of OrESubject is satisfied if the user is a member of any
 * of the wrapped ESubject
 * @author dorai
 */
public class OrESubject implements ESubject {

    private Set<ESubject> eSubjects;
    private String pSubjectName;

    /**
     * Constructs OrESubject
     */
    public OrESubject() {
    }

    /**
     * Constructs OrESubject
     * @param eSubjects wrapped ESubject(s)
     */
    public OrESubject(Set<ESubject> eSubjects) {
        this.eSubjects = eSubjects;
    }

    /**
     * Constructs OrESubject
     * @param eSubjects wrapped ESubject(s)
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when UserESubject was created from
     * OpenSSO policy Subject
     */
    public OrESubject(Set<ESubject> eSubjects, String pSubjectName) {
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
     * Sets the nested ESubject(s)
     * @param eSubjects the nested ESubject(s)
     */
    public void setESubjects(Set<ESubject> eSubjects) {
        this.eSubjects = eSubjects;
    }

    /**
     * Returns the nested ESubject(s)
     * @return  the nested ESubject(s)
     */
    public Set<ESubject> getESubjects() {
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
        for (ESubject eSubject : eSubjects) {
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
            equalled = false;
        }
        if (!getClass().equals(obj.getClass())) {
            equalled = false;
        }
        OrESubject object = (OrESubject) obj;
        if (eSubjects == null) {
            if (object.getESubjects() != null) {
                equalled = false;
            }
        } else { // eSubjects not null
            if ((object.getESubjects()) != null) {
                equalled = false;
            } else if (!eSubjects.containsAll(object.getESubjects())) {
                equalled = false;
            } else if (!object.getESubjects().containsAll(eSubjects)) {
                equalled = false;
            }
        }
        if (pSubjectName == null) {
            if (object.getPSubjectName() != null) {
                equalled = false;
            }
        } else {
            if (!pSubjectName.equals(object.getPSubjectName())) {
                equalled = false;
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
            for (ESubject eSubject : eSubjects) {
                code += eSubject.hashCode();
            }
        }
        if (pSubjectName != null) {
            code += pSubjectName.hashCode();
        }
        return code;
    }
}
