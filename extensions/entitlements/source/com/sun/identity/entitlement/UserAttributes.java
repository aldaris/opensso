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
 * $Id: UserAttributes.java,v 1.3 2009-03-27 16:29:10 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Interface specification for entitlement <code>ResourceAttributes</code>
 */
public class UserAttributes implements ResourceAttributes {
    private static final long serialVersionUID = -403250971215465050L;

    private Map<String, Set<String>> properties;
    private String pResponseProviderName;

    /**
     * Constructs an instance of UserAttributes
     */
    public UserAttributes() {

    }

    /**
     * Constructs an instance of UserAttributes
     * @param properties configuration properties for  this object
     */
    public UserAttributes(Map<String, Set<String>> properties) {
        this.properties = properties;
    }

    /**
     * Sets configuration properties for this <code>UserAttributes</code>
     * @param properties configuration properties for  this
     * <code>UserAttributes</code>
     * @throws com.sun.identity.entitlement.EntitlementException if any
     * abnormal condition occured
     */
    public void setProperties(Map<String, Set<String>> properties)
            throws EntitlementException {
        this.properties = properties;
    }

    /**
     * <code>ResourceAttributes</code>
     * @return configuration properties for this
     * <code>UserAttributes</code>
     */
    public Map<String, Set<String>> getProperties() {
        return properties;
    }

    /**
     * Returns resoruce attributes aplicable to the request
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return applicable resource attributes
     * @throws com.sun.identity.entitlement.EntitlementException
     * if can not get condition decision
     */
    public Map<String, Set<String>> evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return null;
    }

    /**
     * Returns JSONObject mapping of the object
     * @return JSONObject mapping  of the object
     */
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("properties", properties);
        jo.put("pResponseProviderName", pResponseProviderName);
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
            debug.error("UserESubject.toString(), JSONException:" +
                    joe.getMessage());
        }
        return s;
    }
        /**
     * Sets OpenSSO policy response provider name of the object
     * @param pResponseProviderName response provider name as used in OpenSSO policy,
     * this is releavant only when UserAttributes was created from
     * OpenSSO policy Subject
     */
    public void setPResponseProviderName(String pResponseProviderName) {
        this.pResponseProviderName = pResponseProviderName;
    }

    /**
     * Returns OpenSSO policy response provider name of the object
     * @return response provider name as used in OpenSSO policy,
     * this is releavant only when UserAttributes was created from
     * OpenSSO policy Subject
     */
    public String getPResponseProviderName() {
        return pResponseProviderName;
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
        UserAttributes object = (UserAttributes) obj;
        if (properties == null) {
            if (object.getProperties() != null) {
                return false;
            }
        } else {
            if (!properties.equals(object.getProperties())) {
                return false;
            }
        }
        if (pResponseProviderName == null) {
            if (object.getPResponseProviderName() != null) {
                return false;
            }
        } else {
            if (!pResponseProviderName.equals(
                    object.getPResponseProviderName())) {
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
        if (properties != null) {
            code += properties.hashCode();
        }
        if (pResponseProviderName != null) {
             code += pResponseProviderName.hashCode();
        }
        return code;
    }

}

