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
 * $Id: Evaluator.java,v 1.11 2009-04-07 10:25:08 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyEvaluator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * The class evaluates entitlement request and provides decisions.
 * @supported.api
 */
public class Evaluator {
    private Subject adminSubject;
    private String applicationName; //TOFIX: need a default for this.
    
    /**
     * Constructor to create an evaluator of default service type.
     *
     * @throws EntitlementException if any other abnormal condition occ.
     */
    private Evaluator()
        throws EntitlementException {
    }

    /**
     * Constructor to create an evaluator given the service type.
     *
     * @param subject Subject who credential is used for performing the 
     *        evaluation.
     * @param applicationName the name of the aplication for
     *        which this evaluator can be used.
     * @throws EntitlementException if any other abnormal condition occured.
     */
    public Evaluator(Subject subject, String applicationName)
        throws EntitlementException {
        adminSubject = subject;
        this.applicationName = applicationName;
    }

    /**
     * Constructor to create an evaluator the default service type.
     *
     * @param subject Subject who credential is used for performing the 
     *        evaluation.
     * @throws EntitlementException if any other abnormal condition occured.
     */
    public Evaluator(Subject subject)
        throws EntitlementException {
        adminSubject = subject;
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
    public boolean hasEntitlement(Subject subject, Entitlement e) 
        throws EntitlementException {
        //TOFIX: need a swtich choosing opensso or XACML 
        return evaluate(subject, e, Collections.EMPTY_MAP);
    }
    
    /**
     * Returns <code>true</code> if the subject is granted to an
     * entitlement.
     *
     * @param subject Subject who is under evaluation.
     * @param e Entitlement object which describes the resource name and 
     *          actions.
     * @param envParameters Map of environment parameters.
     * @return <code>true</code> if the subject is granted to an
     *         entitlement.
     * @throws EntitlementException if the result cannot be determined.
     */
    public boolean evaluate(
        Subject subject, 
        Entitlement e,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        IPolicyEvaluator evaluator = 
            PolicyEvaluatorFactory.getInstance().getEvaluator();
        return evaluator.hasEntitlement(
            adminSubject, subject, applicationName, e, envParameters);
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
    public List<Entitlement> evaluate(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment,
        boolean recursive
    ) throws EntitlementException {
        IPolicyEvaluator evaluator = 
            PolicyEvaluatorFactory.getInstance().getEvaluator();
        return evaluator.evaluate(adminSubject, subject, applicationName,
            resourceName, environment, recursive);
    }

    public String getApplicationName() {
        return applicationName;
    }

    
}

