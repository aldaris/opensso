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
 * $Id: EntitlementExpression.java,v 1.1 2009-01-12 22:08:38 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Class to combine entitlement filters
 * This class uses logical OR to combine the entitlement filters during
 * evaluation. Subclases of this class could change this behaviour.
 */
public class EntitlementExpression implements EntitlementFilter {

    private List<EntitlementFilter> filters;

    /**
     * Constructs entitlement expression
     */
    public EntitlementExpression() {
    }

    /**
     * Constructs entitlement expression
     * @param ef entitlement filters that would be composed into the entitlement 
     * expression
     */
    public EntitlementExpression(List<EntitlementFilter> ef) {
    }

    /**
     * Returns entitlement filters composed into this entitlement expression
     * @return entitlement filters composed into this entitlement expression
     */
    public List<EntitlementFilter> getEntitlementFilters() {
        return null;
    }

    /**
     * Sets entitlement filters  that would be composed into this 
     * entitlement expression
     * @param cf entitlement filters that would be composed into this
     * entitlement expression
     */
    public void setEntitlementFilters(List<ConditionFilter> ef) {
    }

    /**
     * Adds entitlement filter  to this  entitlement expression
     * @param cf entitlement filter that would be added to this 
     * entitlement expression
     */
    public void addEntitlementFilter(EntitlementFilter cf) {
    }

    /**
     * Returns the entitlements satisfying the request parameters 
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return entitlements satisfying the request parameters 
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public List<Entitlement> evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return null;
    }
}
