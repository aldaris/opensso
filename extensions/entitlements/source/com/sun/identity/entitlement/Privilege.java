/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: Privilege.java,v 1.3 2009-03-27 16:29:10 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing entitlement privilege
 */
public class Privilege implements Serializable {
    private static final long serialVersionUID = -403250971215465050L;

    private String name;
    private Set<Entitlement> entitlements;
    private EntitlementSubject eSubject;
    private EntitlementCondition eCondition;
    private Set<ResourceAttributes> eResourceAttributes;

    /**
     * Constructs entitlement privilege
     * @param name name of the privilege
     * @param eSubject EntitlementSubject used for membership check
     * @param eCondition EntitlementCondition used for constraint check
     * @param eResourceAttributes Resource1Attributes used to get additional
     * result attributes
     */
    public Privilege(
            String name,
            Set<Entitlement> entitlements,
            EntitlementSubject eSubject,
            EntitlementCondition eCondition,
            Set<ResourceAttributes> eResourceAttributes) {
        this.name = name;
        this.entitlements = entitlements;
        this.eSubject = eSubject;
        this.eCondition = eCondition;
        this.eResourceAttributes = eResourceAttributes;
    }

    /**
     * Returns the name of the privilege
     * @return name of the privilege.
     * @throws EntitlementException in case of any error
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the eSubject the privilege
     * @return eSubject of the privilege.
     * @throws EntitlementException in case of any error
     */
    public EntitlementSubject getSubject() {
        return eSubject;
    }

    /**
     * Returns the eCondition the privilege
     * @return eCondition of the privilege.
     * @throws EntitlementException in case of any error
     */
    public EntitlementCondition getCondition() {
        return eCondition;
    }

    /**
     * Returns the eResurceAttributes of  the privilege
     * @return eResourceAttributes of the privilege.
     * @throws EntitlementException in case of any error
     */
    public Set<ResourceAttributes> getResourceAttributes() {
        return eResourceAttributes;
    }

    /**
     * Returns <code>true</code> if the subject is granted to an
     * entitlement.
     *
     * @param subject Subject who is under evaluation.
     * @param e Entitlement object which describes the resource name and 
     *          actions.
     * @return <code>true</code> if the subject is granted to an
     *         entitlement.
     * @throws EntitlementException if the result cannot be determined.
     */
    boolean hasEntitlement(Subject subject, Entitlement e)
            throws EntitlementException {
        return false;
    }

    /**
     * Returns entitlements defined in the privilege
     * @return entitlements defined in the privilege
     */
    public Set<Entitlement> getEntitlements() {
        return entitlements;
    }

    /**
     * Returns a list of entitlements for a given subject, resource name
     * and environment.
     * 
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @param recursive <code>true</code> to perform evaluation on sub resources
     *        from the given resource name.
     * @return a list of entitlements for a given subject, resource name
     *         and environment.
     * @throws EntitlementException if the result cannot be determined.
     */
    public List<Entitlement> getEntitlements(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment,
            boolean recursive) throws EntitlementException {
        return new ArrayList<Entitlement>();
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    @Override
    public String toString() {
        String s = null;
        try {
            JSONObject jo = toJSONObject();
            s = (jo == null) ? super.toString() : jo.toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("Entitlement.toString(), JSONException: " + joe.getMessage());
        }
        return s;
    }

    /**
     * Returns JSONObject mapping of  the object
     * @return JSONObject mapping of  the object
     * @throws JSONException if can not map to JSONObject
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("name", name);

        if (entitlements != null) {
            for (Entitlement e : entitlements) {
                jo.append("entitlements", e.toJSONObject());
            }
        }

        if (eSubject != null) {
            JSONObject subjo = new JSONObject();
            subjo.put("className", eSubject.getClass().getName());
            subjo.put("state", eSubject.getState());
            jo.put("eSubject", subjo);
        }

        if (eCondition != null) {
            JSONObject subjo = new JSONObject();
            subjo.put("className", eCondition.getClass().getName());
            subjo.put("state", eCondition.getState());
            jo.put("eCondition", subjo);
        }

        //TODO: test and fix
        if (eResourceAttributes != null) {
            JSONArray subjo = new JSONArray();
            if (eResourceAttributes != null) {
                JSONArray arr = new JSONArray();
                for (ResourceAttributes ra : eResourceAttributes) {
                    JSONObject subjo1 = new JSONObject();
                    subjo1.put("className", ra.getClass().getName());
                    subjo1.put("state", ra.toString());
                    subjo.put(subjo1);
                }
            }
            jo.put("eResourceAttributes", subjo);
        }
        return jo;
    }

    /**
     * Returns <code>true</code> if the passed in object is equal to this object
     * @param obj object to check for equality
     * @return  <code>true</code> if the passed in object is equal to this object
     */
    @Override
    public boolean equals(Object obj) {
        boolean equalled = true;
        if (obj == null) {
            equalled = false;
        }
        if (!getClass().equals(obj.getClass())) {
            equalled = false;
        }
        Privilege object = (Privilege) obj;

        if (name == null) {
            if (object.getName() != null) {
                equalled = false;
            }
        } else { // name not null

            if ((object.getName()) == null) {
                equalled = false;
            } else if (!name.equals(object.getName())) {
                equalled = false;
            }
        }
        if (entitlements == null) {
            if (object.getEntitlements() != null) {
                equalled = false;
            }
        } else { // name not null

            if ((object.getEntitlements()) == null) {
                equalled = false;
            } else if (!entitlements.equals(object.getEntitlements())) {
                equalled = false;
            }
        }
        if (eSubject == null) {
            if (object.getSubject() != null) {
                equalled = false;
            }
        } else { // name not null

            if ((object.getSubject()) == null) {
                equalled = false;
            } else if (!eSubject.equals(object.getSubject())) {
                equalled = false;
            }
        }
        if (eCondition == null) {
            if (object.getCondition() != null) {
                equalled = false;
            }
        } else { // name not null

            if ((object.getCondition()) == null) {
                equalled = false;
            } else if (!eCondition.equals(object.getCondition())) {
                equalled = false;
            }
        }
        if (eResourceAttributes == null) {
            if (object.getResourceAttributes() != null) {
                equalled = false;
            }
        } else { // name not null

            if ((object.getResourceAttributes()) == null) {
                equalled = false;
            } else if (!eResourceAttributes.equals(
                object.getResourceAttributes())) {
                equalled = false;
            }
        }
        return equalled;
    }

    /**
     * Returns hash code of the object
     * @return hash code of the object
     */
    @Override
    public int hashCode() {
        int code = 0;
        if (name != null) {
            code += name.hashCode();
        }
        if (entitlements != null) {
            code += entitlements.hashCode();
        }
        if (eSubject != null) {
            code += eSubject.hashCode();
        }
        if (eCondition != null) {
            code += eCondition.hashCode();
        }
        if (eResourceAttributes != null) {
            code += eResourceAttributes.hashCode();
        }
        return code;
    }
}
