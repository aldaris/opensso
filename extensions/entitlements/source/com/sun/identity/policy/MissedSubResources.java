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
 * $Id: MissedSubResources.java,v 1.4 2009-02-10 19:31:03 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class does what <code>SubResources</code> class is doing except
 * it does a search for policies before doing policy evaluation.
 */
public class MissedSubResources extends SubResources {
    private SSOToken adminSSOToken;
    private Map<String, Set<String>> misses;
    private Set<Policy> hitPolicies;

    public MissedSubResources(
        PolicyEvaluatorAdaptor parent,
        SSOToken token,
        ServiceType serviceType,
        String rootResource,
        Set<String> actionNames,
        Map<String, Set<String>> envParameters,
        Set<Policy> hits
    ) {
        super(parent, token, serviceType, rootResource, actionNames,
            envParameters, null);
        hitPolicies = new HashSet<Policy>();
        if (hits != null) {
            hitPolicies.addAll(hits);
        }
    }

    void setSearchParameter(
        SSOToken adminSSOToken,
        Map<String, Set<String>> misses
    ) {
        this.adminSSOToken = adminSSOToken;
        this.misses = misses;
    }

    public void run() {
        try {
            Set<Policy> searchResults =
                parent.recursiveSearch(adminSSOToken, misses);
            policies = new HashSet<Policy>();
            for (Policy policy : searchResults) {
                if (!hitPolicies.contains(policy)) {
                    policies.add(policy);
                }
            }

            super.run();
        } catch (SSOException ex) {
            exception = ex;
        } catch (PolicyException ex) {
            exception = ex;
        }
    }
}

