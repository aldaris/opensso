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
 * $Id: SimulationEvaluator.java,v 1.1 2009-02-10 19:31:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SimulatedResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * TOFIX
 */
public class SimulationEvaluator {
    private Subject adminSubject;
    private String serviceTypeName;
    private Subject subject;
    private String resource;
    private Map<String, Set<String>> envParameters;
    private Set<Policy> policies;
    private SimulationIndexCache cache;
    private boolean recursive;

    /**
     * Constructor to create an evaluator given the service type.
     *
     * @param subject Subject who credential is used for performing the
     *        evaluation.
     * @param serviceTypeName the name of the service type for
     *        which this evaluator can be used.
     * @throws EntitlementException if any other abnormal condition occured.
     */
    public SimulationEvaluator(
        Subject adminSubject,
        String serviceTypeName) {
        this.adminSubject = adminSubject;
        this.serviceTypeName = serviceTypeName;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public void setEnvParameters(Map<String, Set<String>> envParameters) {
        this.envParameters = new HashMap<String, Set<String>>();
        if ((envParameters != null) && !envParameters.isEmpty()) {
            this.envParameters.putAll(envParameters);
        }
    }

    public void setRecursive(boolean r) {
        this.recursive = r;
    }

    //TOFIX: should be setPrivileges
    public void setPolicies(Set<Policy> policies)
        throws PolicyException {
        this.policies = new HashSet<Policy>();
        if ((policies != null) && !policies.isEmpty()) {
            this.policies.addAll(policies);
        }
        buildCache();
    }

    private void buildCache()
        throws PolicyException {
        cache = new SimulationIndexCache();
        for (Policy p : policies) {
            cache.cachePolicy(p);
        }
    }

    public List<SimulatedResult> getSimulatedResults()
        throws EntitlementException {
        List<SimulatedResult> results = new ArrayList<SimulatedResult>();
        if (!policies.isEmpty()) {
            SimulationPolicyEvaluator adaptor = new SimulationPolicyEvaluator(
                cache);
            adaptor.getEntitlements(adminSubject, subject, serviceTypeName,
                resource, envParameters, recursive);
        }
        return results;
    }
}
