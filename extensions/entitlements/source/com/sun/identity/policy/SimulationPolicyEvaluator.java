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
 * $Id: SimulationPolicyEvaluator.java,v 1.1 2009-02-10 19:31:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SimulatedResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 *
 * TOFIX
 */
public class SimulationPolicyEvaluator 
    extends PolicyEvaluatorAdaptor {
    private SimulationIndexCache cache;

    public SimulationPolicyEvaluator(SimulationIndexCache cache) {
        this.cache = cache;
    }

    Set<Policy> recursiveSearch(
        SSOToken token,
        Map<String, Set<String>> misses
    ) throws SSOException, PolicyException {
        return Collections.EMPTY_SET;
    }


    IIndexCache getIndexCache() {
        return cache;
    }

    public boolean hasEntitlement(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        Entitlement entitlement,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
            return false;
    }
    
    public List<Entitlement> getEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException {
        return Collections.EMPTY_LIST;
    }

    public List<SimulatedResult> getSimulatedResults(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException {
        if (!recursive) {
            return getSimulatedResults(adminSubject, subject, serviceTypeName,
                resourceName, envParameters);
        } else {
            return getSubTreeSimulatedResults(adminSubject, subject,
                serviceTypeName, resourceName, envParameters);
        }
    }

    private List<SimulatedResult> getSimulatedResults(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        try {
            Set<PolicyDecisionTask.Task> results = performPolicyEvaluation(
                adminSubject, subject, serviceTypeName, resourceName,
                envParameters);
            return getSimulatedResults(results, serviceTypeName);
        } catch (SSOException ex) {
            throw new EntitlementException(ex.getMessage(), -1);
        } catch (PolicyException ex) {
            throw new EntitlementException(ex.getMessage(), -1);
        }
    }

    private List<SimulatedResult> getSimulatedResults(
        Set<PolicyDecisionTask.Task> results,
        String serviceTypeName
    ) {
        List<SimulatedResult> simResults = new ArrayList<SimulatedResult>();
        for (PolicyDecisionTask.Task t : results) {
            Policy p = t.policy;
            PolicyDecision pd = t.policyDecision;
            Entitlement ent = new Entitlement(serviceTypeName, t.resource,
                pd.getActionDecisions());
            ent.setAdvices(pd.getResponseDecisions());
            ent.setAttributes(pd.getResponseAttributes());

            //TOFIX: reason
            simResults.add(new SimulatedResult(ent, true, "", p.getName()));
        }
        return simResults;
    }

    private List<SimulatedResult> getSubTreeSimulatedResults(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException {
        Set<PolicyDecisionTask.Task> tasks = performSubTreeEvaluation(
            adminSubject, subject, serviceTypeName, resourceName,
            envParameters);
        return getSimulatedResults(tasks, serviceTypeName);
    }
}
