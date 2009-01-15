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
 * $Id: ConditionExpression.java,v 1.2 2009-01-15 01:30:01 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Class to combine entitlement conditions
 * This class uses logical OR to combine the entitlement conditions during
 * evaluation. Subclases of this class could change this behaviour.
 */
public class ConditionExpression implements ConditionFilter {

    private List<ConditionFilter> filters;

    /**
     * Constructs condition expression
     */
    public ConditionExpression() {
    }

    /**
     * Constructs condition expression
     * @param cf condition filters that would be composed into the condition 
     * expression
     */
    public ConditionExpression(List<ConditionFilter> cf) {
    }

    /**
     * Returns conditions filters composed into this condition expression
     * @return conditions filters composed into this condition expression
     */
    public List<ConditionFilter> getConditionFilters() {
        return null;
    }

    /**
     * Sets conditions filters  that would be composed into this 
     * condition expression
     * @param cf condition filters that would be composed into this
     * condition expression
     */
    public void setConditionFilters(List<ConditionFilter> cf) {
    }

    /**
     * Adds conditions filter  to this  condition expression
     * @param cf condition filter that would be added to this 
     * condition expression
     */
    public void addConditionFilter(ConditionFilter cf) {
    }

    /**
     * Checks whether the request satisfies the <code>ConditionFilter</code>
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>true</code> if the request satisfies the 
     * <code>ConditionFilter</code>, otherwise <code>false</code>
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public ConditionDecision evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return null;
    }
}
