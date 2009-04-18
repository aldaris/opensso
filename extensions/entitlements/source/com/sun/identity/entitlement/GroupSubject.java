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
 * $Id: GroupSubject.java,v 1.7 2009-04-18 00:05:09 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.debug.Debug;

import java.security.AccessController;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * EntitlementSubject to represent group identity for membership check
 * @author dorai
 */
public class GroupSubject implements EntitlementSubject {
    private static final long serialVersionUID = -403250971215465050L;

    private String group;
    private String pSubjectName;
    boolean openSSOSubject = false;

    /**
     * Constructs an GroupSubject
     */
    public GroupSubject() {
    }

    /**
     * Constructs GroupSubject
     * @param group the uuid of the group who is member of the EntitlementSubject
     */
    public GroupSubject(String group) {
        this.group = group;
    }

    /**
     * Constructs GroupSubject
     * @param group the uuid of the group who is member of the EntitlementSubject
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when GroupSubject was created from
     * OpenSSO policy Subject
     */
    public GroupSubject(String group, String pSubjectName) {
        this.group = group;
        this.pSubjectName = pSubjectName;
    }

    /**
     * Sets state of the object
     * @param state State of the object encoded as string
     */
    public void setState(String state) {
        try {
            JSONObject jo = new JSONObject(state);
            group = jo.optString("group");
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
        jo.put("group", group);
        jo.put("pSubjectName", pSubjectName);
        return jo;
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    @Override
    public String toString() {
        String s = null;
        try {
            s = toJSONObject().toString(2);
        } catch (JSONException joe) {
            Debug debug = Debug.getInstance("Entitlement");
            debug.error("GroupESubject.toString(), JSONException:" +
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
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        boolean satified = false;

        try {
            AMIdentity idGroup = IdUtils.getIdentity(adminToken, group);
            Set<IdType> supportedType = IdType.GROUP.canHaveMembers();
            for (IdType type : supportedType) {
                if (isMember(subject, type, idGroup)) {
                    satified = true;
                    break;
                }
            }
        } catch (IdRepoException e) {
            PolicyEvaluatorFactory.debug.error("GroupSubject.evaluate", e);
        } catch (SSOException e) {
            PolicyEvaluatorFactory.debug.error("GroupSubject.evaluate", e);
        }

        return new SubjectDecision(satified, Collections.EMPTY_MAP);
    }
    
    private static boolean isMember(
        Subject subject,
        IdType type, 
        AMIdentity idGroup
    ) throws IdRepoException, SSOException {
        Set<AMIdentity> members = idGroup.getMembers(type);
        for (AMIdentity amid : members) {
            if (hasPrincipal(subject, amid.getUniversalId())) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPrincipal(Subject subject, String uuid) {
        Set<Principal> userPrincipals = subject.getPrincipals();
        for (Principal p : userPrincipals) {
            if (p.getName().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the member group of the object
     * @param group the uuid of the member group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Returns the member group of the object
     * @return  the uuid of the member group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets OpenSSO policy subject name of the object
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when GroupSubject was created from
     * OpenSSO policy Subject
     */
    public void setPSubjectName(String pSubjectName) {
        this.pSubjectName = pSubjectName;
    }

    /**
     * Returns OpenSSO policy subject name of the object
     * @return subject name as used in OpenSSO policy,
     * this is releavant only when GroupSubject was created from
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
        GroupSubject object = (GroupSubject) obj;
        if (group == null) {
            if (object.getGroup() != null) {
                return false;
            }
        } else {
            if (!group.equals(object.getGroup())) {
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
        if (group != null) {
            code += group.hashCode();
        }
        if (pSubjectName != null) {
            code += pSubjectName.hashCode();
        }
        return code;
    }

    public Map<String, Set<String>> getSearchIndexAttributes() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>(4);
        {
            Set<String> set = new HashSet<String>();
            set.add(group);
            map.put(SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
                IdType.GROUP.getName(), set);
        }
        {
            Set<String> set = new HashSet<String>();
            set.add(SubjectAttributesCollector.ATTR_NAME_ALL_ENTITIES);
            map.put(SubjectAttributesCollector.NAMESPACE_IDENTITY, set);
        }
        
        return map;
    }

    public Set<String> getRequiredAttributeNames() {
        return(Collections.EMPTY_SET);
    }
}
