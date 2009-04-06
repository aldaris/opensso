/*
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
 * $Id: SubjectAttributesManager.java,v 1.1 2009-04-06 23:46:08 arviranga Exp $
 */

package com.sun.identity.entitlement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Manages multiple instances of <class>SubjectAttributesCollector</class>,
 * and to be called by <class>Evaluator</class> and <class>
 * EntitlementSubject</class> implementations to obtain users' attributes and
 * memberships.
 */
public class SubjectAttributesManager {

    String realmName;

    private SubjectAttributesManager() {
        realmName = "/";
    }

    public static SubjectAttributesManager getInstance() {
        return (new SubjectAttributesManager());
    }

    public static SubjectAttributesManager getInstance(String realmName) {
        SubjectAttributesManager sam = new SubjectAttributesManager();
        sam.realmName = realmName;
        return (null);
    }

    public static Set<String> getSubjectSearchIndexes(Privilege privilege)
        throws EntitlementException {
        Set searchIndexes = new HashSet();
        EntitlementSubject es = privilege.getSubject();
        Map<String, String> sis = es.getSearchIndexAttributes();
        for (String attrName : sis.keySet()) {
            String attrValue = sis.get(attrName);
            searchIndexes.add(attrName + "=" + attrValue);
        }
        return (searchIndexes);
    }

    public static Set<String> getRequiredAttributeNames(Privilege privilege) {
        return (privilege.getSubject().getRequiredAttributeNames());
    }

    public static Set<String> getSubjectSearchFilter(Subject subject) {
        return (null);
    }

    /**
     * Returns the attribute values of the given user represented by
     * <class>Subject</class> object.
     * @param subject identity of the user
     * @param attrNames requested attribute names
     * @return a map of attribute names and their values
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public Map<String, Set> getAttributes(Subject subject,
        Set<String> attrNames) throws EntitlementException {
        return (null);
    }

    /**
     * Checks the presence of attribute value for the given user
     * represented by <class>Subject</class> object.
     * @param subject identity of the user
     * @param attrName attribute name to check
     * @param attrValue attribute value to check
     * @return
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public boolean hasAttribute(Subject subject, String attrName,
        String attrValue) throws EntitlementException {
        return (false);
    }
}
