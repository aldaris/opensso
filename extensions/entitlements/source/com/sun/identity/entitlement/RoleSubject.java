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
 * $Id: RoleSubject.java,v 1.8 2009-04-21 13:08:02 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.idm.IdType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * EntitlementSubject to represent role identity for membership check
 * @author dorai
 */
public class RoleSubject extends EntitlementSubjectImpl {
    private static final long serialVersionUID = -403250971215465050L;

    /**
     * Constructs an RoleSubject
     */
    public RoleSubject() {
        super();
    }

    /**
     * Constructs RoleSubject
     * @param role the uuid of the role who is member of the EntitlementSubject
     */
    public RoleSubject(String role) {
        super(role);
    }

    /**
     * Constructs RoleSubject
     * @param role the uuid of the role who is member of the EntitlementSubject
     * @param pSubjectName subject name as used in OpenSSO policy,
     * this is releavant only when RoleSubject was created from
     * OpenSSO policy Subject
     */
    public RoleSubject(String role, String pSubjectName) {
        super(role, pSubjectName);
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
            satified = (values != null) ? values.contains(getID()) : false;
        }
        
        return new SubjectDecision(satified, Collections.EMPTY_MAP);
    }


    public Map<String, Set<String>> getSearchIndexAttributes() {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>(2);
        Set<String> set = new HashSet<String>();
        set.add(getID());
        map.put(SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
            IdType.ROLE.getName(), set);
        return map;
    }

    public Set<String> getRequiredAttributeNames() {
        Set<String> set = new HashSet<String>(2);
        set.add(SubjectAttributesCollector.NAMESPACE_MEMBERSHIP +
            IdType.ROLE.getName());
        return set;
    }
}
