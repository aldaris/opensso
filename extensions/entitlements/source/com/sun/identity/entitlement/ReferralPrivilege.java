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
 * $Id: ReferralPrivilege.java,v 1.1 2009-06-06 00:34:42 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.ResourceName;
import com.sun.identity.entitlement.util.JSONUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Referral privilege allows application to be referred to peer and sub realm.
 */
public final class ReferralPrivilege {
    private String name;
    private String description;
    private Map<String, Set<String>> mapApplNameToResources;
    private Set<String> realms;
    private long creationDate;
    private long lastModifiedDate;
    private String lastModifiedBy;
    private String createdBy;

    private ReferralPrivilege() {
    }

    /**
     * Constructor
     *
     * @param name Name
     * @param map Map of application name to resources.
     * @param realms Realm names
     * @throws EntitlementException if map or realms are empty.
     */
    public ReferralPrivilege(
        String name,
        Map<String, Set<String>> map,
        Set<String> realms
    ) throws EntitlementException {
        if ((name == null) || (name.trim().length() == 0)) {
            throw new EntitlementException(250);
        }

        this.name = name;
        setMapApplNameToResources(map);
        setRealms(realms);
    }

    public static ReferralPrivilege getInstance(JSONObject jo) {
        try {
            ReferralPrivilege r = new ReferralPrivilege();
            r.name = jo.optString("name");
            r.description = jo.optString("description");
            r.createdBy = jo.getString("createdBy");
            r.lastModifiedBy = jo.getString("lastModifiedBy");
            r.creationDate = JSONUtils.getLong(jo, "creationDate");
            r.lastModifiedDate = JSONUtils.getLong(jo, "lastModifiedDate");
            r.mapApplNameToResources = JSONUtils.getMapStringSetString(jo,
                "mapApplNameToResources");
            r.realms = JSONUtils.getSet(jo, "realms");
            return r;
        } catch (JSONException ex) {
            PrivilegeManager.debug.error("ReferralPrivilege.getInstance", ex);
        }
        return null;
    }

    /**
     * Sets the application name to resource name.
     *
     * @param mapApplNameToResources map of application name to tesource names.
     * @throws EntitlementException if map is empty.
     */
    public void setMapApplNameToResources(Map<String, Set<String>> map)
        throws EntitlementException {
        if ((map == null) || map.isEmpty()) {
            throw new EntitlementException(251);
        }

        for (String k : map.keySet()) {
            Set<String> v = map.get(k);
            if ((v == null) || v.isEmpty()) {
                throw new EntitlementException(251);
            }
        }

        this.mapApplNameToResources = new HashMap<String, Set<String>>();
        this.mapApplNameToResources.putAll(map);
    }

    /**
     * Sets realms.
     *
     * @param realms Realms.
     * @throws EntitlementException if realms is empty.
     */
    public void setRealms(Set<String> realms)
        throws EntitlementException {
        if ((realms == null) || realms.isEmpty()) {
            throw new EntitlementException(252);
        }
        this.realms = new HashSet<String>();
        this.realms.addAll(realms);
    }

    /**
     * Returns mapping of application name to resources.
     *
     * @return mapping of application name to resources.
     */
    public Map<String, Set<String>> getMapApplNameToResources() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        for (String k : mapApplNameToResources.keySet()) {
            Set<String> set = new HashSet<String>();
            set.addAll(mapApplNameToResources.get(k));
            map.put(k, set);
        }

        return map;
    }

    /**
     * Returns name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets description.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns description.
     * 
     * @return description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns realms.
     *
     * @return realms
     */
    public Set<String> getRealms() {
        Set<String> set = new HashSet<String>();
        set.addAll(realms);
        return set;
    }

    /**
     * Returns resource save indexes.
     *
     * @param adminSubject Admin Subject.
     * @param realm Realm Name
     * @return resource save indexes.
     */
    public ResourceSaveIndexes getResourceSaveIndexes(
        Subject adminSubject,
        String realm
    ) {
        ResourceSaveIndexes result = null;

        for (String app : mapApplNameToResources.keySet()) {
            Application appl = ApplicationManager.getApplication(
                adminSubject, realm, app);
            for (String r : mapApplNameToResources.get(app)) {
                ResourceSaveIndexes rsi = appl.getResourceSaveIndex(r);
                if (result == null) {
                    result = rsi;
                } else {
                    result.addAll(rsi);
                }
            }
        }
        return result;
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

    public String toXML() {
        return toJSON();
    }

    public String toJSON() {
        JSONObject jo = new JSONObject();

        try {
            jo.put("name", name);
            jo.put("description", description);
            jo.put("createdBy", createdBy);
            jo.put("lastModifiedBy", lastModifiedBy);
            jo.put("creationDate", creationDate);
            jo.put("lastModifiedDate", lastModifiedDate);

            jo.put("mapApplNameToResources", mapApplNameToResources);
            jo.put("realms", realms);
            return jo.toString(2);
        } catch (JSONException ex) {
            PrivilegeManager.debug.error("ReferralPrivilege.toJSON", ex);
        }
        return "";
    }

    /**
     * Canonicalizes resource name before persistence.
     *
     * @param adminSubject Admin Subject.
     * @param realm Realm Name
     */
    public void canonicalizeResources(Subject adminSubject, String realm)
        throws EntitlementException {
        for (String appName : mapApplNameToResources.keySet()) {
            ResourceName resComp = getResourceComparator(adminSubject, realm,
                appName);
            Set<String> resources = mapApplNameToResources.get(appName);
            Set<String> temp = new HashSet<String>();
            for (String r : resources) {
                temp.add(resComp.canonicalize(r));
            }
            mapApplNameToResources.put(appName, temp);
        }
    }

    private ResourceName getResourceComparator(
        Subject adminSubject,
        String realm,
        String applName) {
        Application appl = ApplicationManager.getApplication(
            adminSubject, realm, applName);
        return appl.getResourceComparator();
    }

}
