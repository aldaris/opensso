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
 * $Id: Privilige.java,v 1.4 2009-02-11 01:09:48 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Class representing entitlement privilige
 */
public class Privilige {

    private String name;
    private Set<Entitlement> entitlements;
    private SubjectFilter subjectFilter;
    private ConditionFilter conditionFilter;
    

    /**
     * Constructs entitlement privilige
     * @param name name of the privilige
     * @param cf condition filter for the privilige
     */
    public Privilige(
            String name,
            Set<Entitlement> entitlements,
            SubjectFilter subjectFilter,
            ConditionFilter cf) {
    }

    /**
     * Returns the name of the privilige
     * @return name of the privilige.
     * @throws EntitlementException in case of any error
     */
    public String  getName() {
        return name;
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
     * Returns entitlements defined in the privilige
     * @return entitlements defined in the privilige
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
        boolean recursive
    ) throws EntitlementException {
        return new ArrayList<Entitlement>();
    }
    
}
