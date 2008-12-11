/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: Evaluator.java,v 1.1 2008-12-11 17:13:41 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * The class evaluates entitlement request and provides decisions.
 * @supported.api
 */
public class Evaluator {

    /**
     * Constructor to create an evaluator of default service type.
     *
     * @throws EntitlementException if any other abnormal condition occ.
     */
    public Evaluator()
        throws EntitlementException {
    }

    /**
     * Constructor to create an evaluator given the service type.
     *
     * @param serviceTypeName the name of the service type for 
     *        which this evaluator can be used.
     * @throws EntitlementException if any other abnormal condition occured.
     */
    public Evaluator(String serviceTypeName)
        throws EntitlementException {
    }

    /**
     * Returns <code>true</code> if a subject is allowed to perform an
     * action on a resource. 
     *
     * @param subject Subject who is under evaluation.
     * @param resource Resource the subject is trying to access.
     * @param actionName Action to be perform by the subject on the resource
     *
     * @return <code>true</code> if a subject is allowed to perform an
     *         action on a resource. 
     * @throws EntitlementException if the result cannot be determined.
     * 
     */
    public boolean isAllowed(
        Subject subject,
        String resource,
        String actionName
    ) throws EntitlementException {
        return isAllowed(subject, resource, actionName, Collections.EMPTY_MAP);
    }

    /**
     * Returns <code>true</code> if a subject is allowed to perform an
     * action on a resource. 
     *
     * @param subject Subject who is under evaluation.
     * @param resource Resource the subject is trying to access.
     * @param actionName Action to be perform by the subject on the resource
     * @param environment The environment of which evaluation is perform under.
     *
     * @return <code>true</code> if a subject is allowed to perform an
     *         action on a resource. 
     * @throws EntitlementException if the result cannot be determined.
     */
    public boolean isAllowed(
        Subject subject,
        String resource,
        String actionName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        return false;
    }

    /**
     * Returns <code>true</code> if a subject is allowed to perform an
     * action on a resource. 
     *
     * @param subject Subject who is under evaluation.
     * @param resources Set of resources the subject is trying to access.
     * @param actionNames Set of action to be perform by the subject on the
     *                   resource
     * @param environment The environment of which evaluation is perform under.
     *
     * @return <code>true</code> if a subject is allowed to perform an
     *         action on a resource. 
     * @throws EntitlementException if the result cannot be determined.
     */
    public boolean isAllowed(
        Subject subject,
        Set<String> resource,
        Set<String> actionName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        return false;
    }

    /**
     * Returns entitlements.
     *
     * @param subject Subject who is under evaluation.
     * @param resource Resource the subject is trying to access.
     * @param actionName Action to be perform by the subject on the resource
     * @param environment The environment of which evaluation is perform under.
     * @return entitlements.
     * @throws EntitlementException if the result cannot be determined.
     */
    public Set<Entitlement> getEntitlements(
        Subject subject,
        String resource,
        String actionName,
        Map<String, Set<String>> environment
    ) throws EntitlementException {
        return Collections.EMPTY_SET;
    }

    /**
     * Returns entitlements.
     *
     * @param subject Subject who is under evaluation.
     * @param resource Resource the subject is trying to access.
     * @param actionName Action to be perform by the subject on the resource
     * @param environment The environment of which evaluation is perform under.
     * @param recursive <code>true</code> to return entitlements for
     *        descendent resources.
     * @return entitlements.
     * @throws EntitlementException if the result cannot be determined.
     */
    public Set<Entitlement> getEntitlements(
        Subject subject,
        Set<String> resource,
        Set<String> actionName,
        Map<String, Set<String>> environment,
        boolean recursive
    ) throws EntitlementException {
        return Collections.EMPTY_SET;
    }
}

