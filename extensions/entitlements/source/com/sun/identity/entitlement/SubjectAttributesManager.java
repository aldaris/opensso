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
 * $Id: SubjectAttributesManager.java,v 1.3 2009-04-10 22:40:01 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import java.util.Collections;
import java.util.HashMap;
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
    private String realmName;
    private SubjectAttributesCollector attrCollector;
    private static final String DEFAULT_IMPL =
        "com.sun.identity.entitlement.opensso.OpenSSOSubjectAttributesCollector";
    private static Class DEFAULT_IMPL_CLASS;
    private static Map<String, SubjectAttributesManager> instances =
        new HashMap<String, SubjectAttributesManager>();

    static {
        try {
            DEFAULT_IMPL_CLASS = Class.forName(DEFAULT_IMPL);
        } catch (ClassNotFoundException ex) {
            //TOFIX
        }
    }

    private SubjectAttributesManager(String realmName) {
        this.realmName = realmName;
        if (DEFAULT_IMPL_CLASS != null) {
            try {
                this.attrCollector = (SubjectAttributesCollector)
                    DEFAULT_IMPL_CLASS.newInstance();
            } catch (InstantiationException ex) {
                //TOFIX
            } catch (IllegalAccessException ex) {
                //TOFIX
            }
        }
    }

    public static SubjectAttributesManager getInstance() {
        return getInstance("/");
    }

    public static SubjectAttributesManager getInstance(Subject subject) {
        //TOFIX get realm from subject;
        return getInstance("/");
    }

    public synchronized static SubjectAttributesManager getInstance(
        String realmName) {
        SubjectAttributesManager sam = instances.get(realmName);
        if (sam == null) {
            sam = new SubjectAttributesManager(realmName);
            instances.put(realmName, sam);
        }
        return sam;
    }

    public static Set<String> getSubjectSearchIndexes(Privilege privilege)
        throws EntitlementException {
        Set searchIndexes = new HashSet();
        EntitlementSubject es = privilege.getSubject();
        if (es != null) {
            Map<String, Set<String>> sis = es.getSearchIndexAttributes();
            for (String attrName : sis.keySet()) {
                Set<String> attrValues = sis.get(attrName);
                for (String v : attrValues) {
                    searchIndexes.add(attrName + "=" + v);
                }
            }
        }
        return (searchIndexes);
    }

    public static Set<String> getRequiredAttributeNames(Privilege privilege) {
        EntitlementSubject e = privilege.getSubject();
        return (e != null) ? e.getRequiredAttributeNames() :
            Collections.EMPTY_SET;
    }

    public static Set<String> getSubjectSearchFilter(Subject subject)
        throws EntitlementException {
        Set<String> results = new HashSet<String>();
        results.add(SubjectAttributesCollector.NAMESPACE_IDENTITY + "=" +
            SubjectAttributesCollector.ATTR_NAME_ALL_ENTITIES);
        String realm = "/"; //TOFIX
        if (subject != null) {
            IPolicyConfig pc = PolicyConfigFactory.getPolicyConfig();
            Set<String> names = pc.getSubjectAttributeNames(realm);
            SubjectAttributesManager sam = SubjectAttributesManager.getInstance(
                realm);
            Map<String, Set<String>> values = sam.getAttributes(subject, names);

            if (values != null) {
                for (String k : values.keySet()) {
                    for (String v : values.get(k)) {
                        results.add(k + "=" + v);
                    }
                }
            }
        }
        return results;
    }

    /**
     * Returns the attribute values of the given user represented by
     * <class>Subject</class> object.
     * @param subject identity of the user
     * @param attrNames requested attribute names
     * @return a map of attribute names and their values
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public Map<String, Set<String>> getAttributes(
        Subject subject,
        Set<String> attrNames
    ) throws EntitlementException {
        return attrCollector.getAttributes(subject, attrNames);
    }

    /**
     * Returns <code>true</code> if attribute value for the given user
     * represented by <class>Subject</class> object is present.
     *
     * @param subject identity of the user
     * @param attrName attribute name to check
     * @param attrValue attribute value to check
     * @return <code>true</code> if attribute value for the given user
     * represented by <class>Subject</class> object is present.
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public boolean hasAttribute(
        Subject subject,
        String attrName,
        String attrValue
    ) throws EntitlementException {
        return attrCollector.hasAttribute(subject, attrName, attrValue);
    }
}
