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
 * $Id: UserESubject.java,v 1.1 2009-02-26 00:46:38 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

public class UserESubject implements ESubject {

    private String user;
    private String pSubjectName;
    boolean openSSOSubject = false;

    public UserESubject() {
    }

    public UserESubject(String user, String pSubjectName) {
        this.user = user;
        this.pSubjectName = pSubjectName;
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            user = jo.optString("user");
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
        String s = null;
        try {
            JSONObject jo = new JSONObject();
            jo.put("user", user);
            jo.put("pSubjectName", pSubjectName);
            s = jo.toString(2);
        } catch (JSONException joe) {
        }
        return s;
    }

    public JSONObject toJSONObject() {
        JSONObject jo = null;
        try {
            jo = new JSONObject();
            jo.put("user", user);
            jo.put("pSubjectName", pSubjectName);
        } catch (JSONException joe) {
        }
        return jo;
    }

    public String toString() {
        JSONObject jo = toJSONObject();
        String s = null;
        try {
            s = (jo == null) ? super.toString() : jo.toString(2);
        } catch (JSONException joe) {
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

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setPSubjectName(String pSubjectName) {
        this.pSubjectName = pSubjectName;
    }

    public String getPSubjectName() {
        return pSubjectName;
    }

    public void flagAsOpenSSOSubject() {
        this.openSSOSubject = true;
    }

    public boolean isOpenSSOSubject() {
        return openSSOSubject;
    }
}