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
 * $Id: Entitlement.java,v 1.15 2009-02-27 21:05:19 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.shared.debug.Debug;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class encapsulates entitlement of a subject.
 * <p>
 * Example of how to use this class
 * <pre>
 *     Set set = new HashSet();
 *     set.add("GET");
 *     Evaluator evaluator = new Evaluator(adminToken);
 *     boolean isAllowed = evaluator.hasEntitlement(subject, 
 *         new Entitlement("http://www.sun.com/example", set), 
 *         Collections.EMPTY_MAP);
 * </pre>
 * Or do a sub tree search like this.
 * <pre>
 *     Evaluator evaluator = new Evaluator(adminToken);
 *     List&lt;Entitlement> entitlements = evaluator.getEntitlements(
 *         subject, "http://www.sun.com", Collections.EMPTY_MAP, true);
 *     for (Entitlement e : entitlements) {
 *         String resource = e.getResourceNames();
 *         boolean isAllowed =((Boolean)e.getActionValue("GET")).booleanValue();
 *         ...
 *     }
 * </pre>
 */
public class Entitlement {

    private String name;
    private String serviceName;
    private String resourceName;
    private Set<String> excludedResourceNames;
    private Map<String, Object> actionValues;
    private Map<String, String> advices;
    private Map<String, Set<String>> attributes;

    /**
     * Creates an entitlement object with default service name.
     */
    public Entitlement() {
    }

    /**
     * Creates an entitlement object.
     *
     * @param resourceName Resource name.
     * @param actionNames Set of action names.
     */
    public Entitlement(String resourceName, Set<String> actionNames) {
        setResourceName(resourceName);
        setActionNames(actionNames);
    }

    /**
     * Creates an entitlement object.
     *
     * @param serviceName Service name.
     * @param resourceName Resource name.
     * @param actionNames Set of action names.
     */
    public Entitlement(
            String serviceName,
            String resourceName,
            Set<String> actionNames) {
        this.serviceName = serviceName;
        setResourceName(resourceName);
        setActionNames(actionNames);
    }

    /**
     * Creates an entitlement object.
     *
     * @param resourceName Resource name.
     * @param actionValues Map of action name to set of values.
     */
    public Entitlement(
            String resourceName,
            Map<String, Object> actionValues) {
        setResourceName(resourceName);
        setActionValues(actionValues);
    }

    /**
     * Creates an entitlement object.
     *
     * @param serviceName Service name.
     * @param resourceName Resource name.
     * @param actionValues Map of action name to set of values.
     */
    public Entitlement(
            String serviceName,
            String resourceName,
            Map<String, Object> actionValues) {
        this.serviceName = serviceName;
        setResourceName(resourceName);
        setActionValues(actionValues);
    }

    /**
     * Sets the name of the entitlement
     * @param name the name of the entitlement
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the entitlement
     * @return the name of the entitlement
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets resource name.
     *
     * @param resourceName Resource Name.
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Returns resource name.
     *
     * @return resource name.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Sets excluded resource names.
     *
     * @param excludedResourceNames excluded resource names.
     */
    public void setExcludedResourceNames(
            Set<String> excludedResourceNames) {
        this.excludedResourceNames = excludedResourceNames;
    }

    /**
     * Returns excluded resource names.
     *
     * @return excluded resource names.
     */
    public Set<String> getExcludedResourceNames() {
        return excludedResourceNames;
    }

    /**
     * Returns service name.
     *
     * @return service name.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets action name
     *
     * @param actionName Action name.
     */
    public void setActionName(String actionName) {
        actionValues = new HashMap<String, Object>();
        actionValues.put(actionName, Boolean.TRUE);
    }

    /**
     * Sets action names
     *
     * @param actionNames Set of action names.
     */
    public void setActionNames(Set<String> actionNames) {
        actionValues = new HashMap<String, Object>();
        for (String i : actionNames) {
            actionValues.put(i, Boolean.TRUE);
        }
    }

    /**
     * Sets action values map.
     *
     * @param actionValues Action values.
     */
    public void setActionValues(Map<String, Object> actionValues) {
        this.actionValues = new HashMap<String, Object>();
        for (String key : actionValues.keySet()) {
            Object val = actionValues.get(key);

            if (val instanceof Set) {
                Set copyOf = new HashSet<Object>((Set<Object>) val);
                this.actionValues.put(key, copyOf);
            } else {
                this.actionValues.put(key, val);
            }
        }

        this.actionValues = actionValues;
    }

    /**
     * Returns action value.
     *
     * @param name Name of the action.
     * @return action values.
     */
    public Object getActionValue(String name) {
        return actionValues.get(name);
    }

    /**
     * Returns action values.
     *
     * @return action values.
     */
    public Map<String, Object> getActionValues() {
        return actionValues;
    }

    /**
     * Returns action values.
     *
     * @param name Name of the action.
     * @return action values.
     */
    public Set<Object> getActionValues(String name) {
        Object o = actionValues.get(name);
        if (o instanceof Set) {
            return (Set<Object>) o;
        }

        Set<Object> set = new HashSet<Object>();
        set.add(o);
        return set;
    }

    /**
     * Sets advices.
     *
     * @param advices Advices.
     */
    public void setAdvices(Map<String, String> advices) {
        this.advices = advices;
    }

    /**
     * Returns advices.
     *
     * @return Advices.
     */
    public Map<String, String> getAdvices() {
        return advices;
    }

    /**
     * Sets attributes.
     *
     * @param attributes Attributes.
     */
    public void setAttributes(Map<String, Set<String>> attributes) {
        this.attributes = attributes;
    }

    /**
     * Returns attributes.
     *
     * @return Attributes.
     */
    public Map<String, Set<String>> getAttributes() {
        return attributes;
    }

    /**
     * Returns <code>true</code> if the request satisfies the request
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>true</code> if the request satisfies the 
     * <code>SubjectFilter</code>, otherwise <code>false</code>
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public boolean evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return false;
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
        jo.put("serviceName", serviceName);
        jo.put("resourceName", resourceName);
        jo.put("excludedResourceNames", excludedResourceNames);
        jo.put("actionsValues", actionValues);
        jo.put("advices", advices);
        jo.put("attributes", attributes);
        return jo;
    }

     /**
     * Returns <code>true</code> if the passed in object is equal to this object
     * @param obj object to check for equality
     * @return  <code>true</code> if the passed in object is equal to this object
     */
    public boolean equals(Object obj) {
        boolean equalled = true;
        /*
            private String name;
            private String serviceName;
            private String resourceName;
            private Set<String> excludedResourceNames;
            private Map<String, Object> actionValues;
            private Map<String, String> advices;
            private Map<String, Set<String>> attributes;
        */
        if (obj == null) {
            equalled = false;
        }
        if (!getClass().equals(obj.getClass())) {
            equalled = false;
        }
        Entitlement object = (Entitlement) obj;

        if (serviceName == null) {
            if (object.getServiceName() != null) {
                equalled = false;
            }
        } else { // serviceName not null
            if ((object.getServiceName()) != null) {
                equalled = false;
            } else if (!serviceName.equals(object.getServiceName())) {
                equalled = false;
            }
        }

        if (resourceName == null) {
            if (object.getResourceName() != null) {
                equalled = false;
            }
        } else { // resourceName not null
            if ((object.getResourceName()) != null) {
                equalled = false;
            } else if (!resourceName.equals(object.getResourceName())) {
                equalled = false;
            }
        }

        if (excludedResourceNames == null) {
            if (object.getExcludedResourceNames() != null) {
                equalled = false;
            }
        } else { // excludedResourceNames not null
            if ((object.getExcludedResourceNames()) != null) {
                equalled = false;
            } else if (!excludedResourceNames.equals(
                    object.getExcludedResourceNames())) {
                equalled = false;
            }
        }

        if (actionValues == null) {
            if (object.getActionValues() != null) {
                equalled = false;
            }
        } else { // actionValues not null
            if ((object.getActionValues()) != null) {
                equalled = false;
            } else if (!actionValues.equals(
                    object.getActionValues())) {
                equalled = false;
            }
        }

        if (advices == null) {
            if (object.getAdvices() != null) {
                equalled = false;
            }
        } else { // advices not null
            if ((object.getAdvices()) != null) {
                equalled = false;
            } else if (!advices.equals(
                    object.getAdvices())) {
                equalled = false;
            }
        }

        if (attributes == null) {
            if (object.getAttributes() != null) {
                equalled = false;
            }
        } else { // attributes not null
            if ((object.getAttributes()) != null) {
                equalled = false;
            } else if (!attributes.equals(
                    object.getAttributes())) {
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
        if (name != null) {
            code += name.hashCode();
        }
        if (serviceName != null) {
            code += serviceName.hashCode();
        }
        if (resourceName != null) {
            code += resourceName.hashCode();
        }
        if (excludedResourceNames != null) {
            code += excludedResourceNames.hashCode();
        }
        if (actionValues != null) {
            code += actionValues.hashCode();
        }
        if (advices != null) {
            code += advices.hashCode();
        }
        if (attributes != null) {
            code += attributes.hashCode();
        }
        return code;
    }
}
