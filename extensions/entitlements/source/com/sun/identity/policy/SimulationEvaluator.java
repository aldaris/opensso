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
 * $Id: SimulationEvaluator.java,v 1.3 2009-02-12 05:33:10 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.SimulatedResult;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
    List<SimulatedResult> simulatedResults;
    List<Entitlement> entitlements;

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

    public void setPolicies(Set<String> policyNames)
        throws PolicyException, SSOException {
        this.policies = new HashSet<Policy>();

        if ((policyNames != null) && !policyNames.isEmpty()) {
            SSOToken adminSSOToken = getSSOToken(adminSubject);
            PolicyManager pm = new PolicyManager(adminSSOToken, "/");

            for (String name : policyNames) {
                this.policies.add(pm.getPolicy(name));
            }
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

    public void evaluate(boolean recursive)
        throws EntitlementException {
        simulatedResults = null;
        entitlements = null;

        if ((policies != null) && !policies.isEmpty()) {
            SimulationPolicyEvaluator adaptor = new SimulationPolicyEvaluator(
                cache);
            simulatedResults = adaptor.getSimulatedResults(adminSubject, 
                subject, serviceTypeName, resource, envParameters, recursive);
            computeNotApplicablePolicies();
            mergeResults();
        }
    }

    private void computeNotApplicablePolicies() {
        Set<String> applicables = new HashSet<String>();
        for (SimulatedResult r : simulatedResults) {
            applicables.add(r.getPrivilegeName());
        }
        Set<SimulatedResult> notApplicables = new HashSet<SimulatedResult>();
        for (Policy p : policies) {
            String policyName = p.getName();
            if (!applicables.contains(policyName)) {
                Entitlement ent = new Entitlement(
                    serviceTypeName, resource, Collections.EMPTY_MAP);
                notApplicables.add(new SimulatedResult(
                    ent, false, "", policyName));
            }
        }
        simulatedResults.addAll(notApplicables);
    }

    private void mergeResults()
        throws EntitlementException {
        try {
            if ((simulatedResults != null) && !simulatedResults.isEmpty()) {
                ServiceType serviceType =
                    ServiceTypeManager.getServiceTypeManager().getServiceType(
                        serviceTypeName);
                Map<String, PolicyDecision> tracker = new
                    HashMap<String, PolicyDecision>();

                for (SimulatedResult r : simulatedResults) {
                    Entitlement ent = r.getEntitlement();
                    String res = ent.getResourceName();
                    PolicyDecision pd = EntitlementToPolicyDecision(
                        ent, serviceType);
                    PolicyDecision prevPd = tracker.get(res);
                    if (prevPd == null) {
                        tracker.put(res, pd);
                    } else {
                        tracker.put(res, PolicyEvaluator.mergePolicyDecisions(
                            serviceType, prevPd, pd));
                    }
                }

                entitlements = new ArrayList<Entitlement>();

                for (String res : tracker.keySet()) {
                    PolicyDecision pd = tracker.get(res);
                    entitlements.add(PolicyEvaluatorAdaptor.getEntitlement(
                        serviceType, res, pd));
                }
            }
        } catch (PolicyException e) {
            throw new EntitlementException(e.getMessage(), -1);
        } catch (SSOException e) {
            throw new EntitlementException(e.getMessage(), -1);
        }
    }

    private PolicyDecision EntitlementToPolicyDecision(
        Entitlement entitlement,
        ServiceType serviceType
    ) throws PolicyException {
        PolicyDecision pd = new PolicyDecision();
        Map actionValues = entitlement.getActionValues();

        for (Iterator i = actionValues.keySet().iterator(); i.hasNext(); ) {
            String actionName = (String)i.next();
            Set values = (Set)actionValues.get(actionName);
            ActionDecision ad = new ActionDecision(actionName, values);
            ad.setAdvices(entitlement.getAdvices());
            pd.addActionDecision(ad, serviceType);
        }

        pd.setResponseAttributes(entitlement.getAttributes());
        return pd;
    }

    public List<Entitlement> getEntitlements() {
        return entitlements;
    }

    public List<SimulatedResult> getSimulatedResults() {
        return simulatedResults;
    }

    private SSOToken getSSOToken(Subject subject)
        throws SSOException {
        Set<Principal> principals = subject.getPrincipals();
        if (!principals.isEmpty()) {
            try {
                Principal p = principals.iterator().next();
                String tokenId = p.getName();
                SSOTokenManager mgr = SSOTokenManager.getInstance();
                return mgr.createSSOToken(tokenId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
