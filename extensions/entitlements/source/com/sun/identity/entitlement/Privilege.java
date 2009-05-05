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
 * $Id: Privilege.java,v 1.14 2009-05-05 21:32:13 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.ResourceName;
import com.sun.identity.policy.ResourceMatch;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
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
public abstract class Privilege implements Serializable {
    private static final long serialVersionUID = -403250971215465050L;

    /**
     * Created by index key
     */
    public static final String CREATED_BY_ATTRIBUTE = "createdby";

    /**
     * Last modified by index key
     */
    public static final String LAST_MODIFIED_BY_ATTRIBUTE = "lastmodifiedby";

    /**
     * Creation date index key
     */
    public static final String CREATION_DATE_ATTRIBUTE = "creationdate";

    /**
     * Last modified date index key
     */
    public static final String LAST_MODIFIED_DATE_ATTRIBUTE =
        "lastmodifieddate";

    private String name;
    private Entitlement entitlement;
    private EntitlementSubject eSubject;
    private EntitlementCondition eCondition;
    private Set<ResourceAttributes> eResourceAttributes;

    private String createdBy;
    private String lastModifiedBy;
    private long creationDate;
    private long lastModifiedDate;

    /**
     * Constructs entitlement privilege
     * @param name name of the privilege
     * @param eSubject EntitlementSubject used for membership check
     * @param eCondition EntitlementCondition used for constraint check
     * @param eResourceAttributes Resource1Attributes used to get additional
     * result attributes
     * @throws EntitlementException if resource names are invalid.
     */
    protected Privilege(
        String name,
        Entitlement entitlement,
        EntitlementSubject eSubject,
        EntitlementCondition eCondition,
        Set<ResourceAttributes> eResourceAttributes
    ) throws EntitlementException {
        entitlement.validateResourceNames();
        this.name = name;
        this.entitlement = entitlement;
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
     * Returns entitlement defined in the privilege
     * @return entitlement defined in the privilege
     */
    public Entitlement getEntitlement() {
        return entitlement;
    }

    /**
     * Returns privilege Type.
     * @see PrivilegeType
     *
     * @return privilege Type.
     */
    public PrivilegeType getType() {
        return PrivilegeType.UNKNOWN;
    }

    /**
     * Returns string representation of OpenSSO native policy.
     * 
     * @return string representation of OpenSSO native policy.
     */
    public abstract String getNativePolicy();
    
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
    public abstract boolean hasEntitlement(Subject subject, Entitlement e)
        throws EntitlementException;

    /**
     * Returns a list of entitlement for a given subject, resource name
     * and environment.
     * 
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param actionNames Set of action names.
     * @param environment Environment parameters.
     * @param recursive <code>true</code> to perform evaluation on sub resources
     *        from the given resource name.
     * @return a list of entitlement for a given subject, resource name
     *         and environment.
     * @throws EntitlementException if the result cannot be determined.
     */
    public abstract List<Entitlement> evaluate(
        Subject subject,
        String resourceName,
        Set<String> actionNames,
        Map<String, Set<String>> environment,
        boolean recursive) throws EntitlementException;

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
            PrivilegeManager.debug.error("Entitlement.toString()", joe);
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

        if (entitlement != null) {
            jo.append("entitlement", entitlement.toJSONObject());
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
            return false;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        Privilege object = (Privilege) obj;

        if (name == null) {
            if (object.getName() != null) {
                return false;
            }
        } else { // name not null

            if ((object.getName()) == null) {
                return false;
            } else if (!name.equals(object.getName())) {
                return false;
            }
        }
        if (entitlement == null) {
            if (object.getEntitlement() != null) {
                return false;
            }
        } else { // name not null

            if ((object.getEntitlement()) == null) {
                return false;
            } else if (!entitlement.equals(object.getEntitlement())) {
                return false;
            }
        }

        if (eSubject == null) {
            if (object.getSubject() != null) {
                return false;
            }
        } else { // name not null
            if ((object.getSubject()) == null) {
                return false;
            } else if (!eSubject.equals(object.getSubject())) {
                return false;
            }
        }

        if (eCondition == null) {
            if (object.getCondition() != null) {
                return false;
            }
        } else { // name not null

            if ((object.getCondition()) == null) {
                return false;
            } else if (!eCondition.equals(object.getCondition())) {
                return false;
            }
        }

        if (eResourceAttributes == null) {
            if (object.getResourceAttributes() != null) {
                return false;
            }
        } else { // name not null

            if ((object.getResourceAttributes()) == null) {
                return false;
            } else if (!eResourceAttributes.equals(
                object.getResourceAttributes())) {
                return false;
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
        if (entitlement != null) {
            code += entitlement.hashCode();
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

    protected boolean doesSubjectMatch(
        Map<String, Set<String>> resultAdvices,
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        SubjectAttributesManager mgr =
            SubjectAttributesManager.getInstance(subject);
        SubjectDecision sDecision = getSubject().evaluate(
            mgr, subject, resourceName, environment);
        if (!sDecision.isSatisfied()) {
            Map<String, Set<String>> advices = sDecision.getAdvices();
            if (advices != null) {
                resultAdvices.putAll(advices);
            }
            return false;
        }
        return true;
    }

    protected boolean doesConditionMatch(
        Map<String, Set<String>> resultAdvices,
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        if (eCondition == null) {
            return true;
        }

        ConditionDecision decision = eCondition.evaluate(
            subject, resourceName, environment);
        if (!decision.isSatisfied()) {
            Map<String, Set<String>> advices = decision.getAdvices();
            if (advices != null) {
                resultAdvices.putAll(advices);
            }
            return false;
        }
        return true;
    }
    
    /**
     * Returns creation date.
     *
     * @return creation date.
     */
    public long getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date.
     *
     * @param creationDate creation date.
     */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns last modified date.
     *
     * @return last modified date.
     */
    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Sets the last modified date.
     *
     * @param lastModifiedDate last modified date.
     */
    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Returns the user ID who last modified the policy.
     *
     * @return user ID who last modified the policy.
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets the user ID who last modified the policy.
     *
     * @param createdBy user ID who last modified the policy.
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * Returns the user ID who created the policy.
     *
     * @return user ID who created the policy.
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user ID who created the policy.
     *
     * @param createdBy user ID who created the policy.
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
