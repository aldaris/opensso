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
 * $Id: SubjectExpression.java,v 1.1 2009-01-12 22:08:38 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Class to combine entitlement subjects
 * This class uses logical OR to combine the entitlement subjects during
 * evaluation. Subclases of this class could change this behaviour.
 */
public class SubjectExpression implements SubjectFilter {

    private List<SubjectFilter> filters;

    /**
     * Constructs subject expression
     */
    public SubjectExpression() {
    }

    /**
     * Constructs subject expression
     * @param sf subject filters that would be composed into the subject 
     * expression
     */
    public SubjectExpression(List<SubjectFilter> sf) {
    }

    /**
     * Returns subject filters composed into this subject expression
     * @return subject filters composed into this subject expression
     */
    public List<SubjectFilter> getSubjectFilters() {
        return null;
    }

    /**
     * Sets subject filters  that would be composed into this 
     * subject expression
     * @param cf subject filters that would be composed into this
     * subject expression
     */
    public void setSubjectFilters(List<SubjectFilter> sf) {
    }

    /**
     * Adds subject filter  to this  subject expression
     * @param sf subject filter that would be added to this 
     * subject expression
     */
    public void addSubjectFilter(SubjectFilter sf) {
    }

    /**
     * Checks whether the request satisfies the <code>SubjectFilter</code>
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return <code>true</code> if the request satisfies the 
     * <code>ConditionFilter</code>, otherwise <code>false</code>
     * @throws com.sun.identity.entitlement.EntitlementException
     */
    public boolean evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException {
        return false;
    }
}
