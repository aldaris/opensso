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
 * $Id: MissedSubResources.java,v 1.1 2009-02-04 07:41:20 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.DataStoreEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class MissedSubResources extends SubResources {
    private SSOToken adminSSOToken;
    private Map<String, Set<String>> misses;
    private Set<Policy> hitPolicies;

    public MissedSubResources(
        Object parent,
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

    public void setSearchParameter(
        SSOToken adminSSOToken,
        Map<String, Set<String>> misses
    ) {
        this.adminSSOToken = adminSSOToken;
        this.misses = misses;
    }

    public void run() {
        try {
            Set<DataStoreEntry> searchResults = 
                PolicyEvaluatorAdaptor.recursiveSearch(adminSSOToken, misses);
            entries = new HashMap<Policy, Map<String, Set<String>>>();
            for (DataStoreEntry d : searchResults) {
                Policy policy = (Policy)d.getPolicy();
                if (!hitPolicies.contains(policy)) {
                    Set<String> hostIdx = d.getHostIndexes();
                    Set<String> pathIdx = d.getPathIndexes();
                    String pathParent = d.getPathParent();
                    Set<String> setPathParent = new HashSet<String>();
                    if (pathParent == null) {
                        setPathParent.add(pathParent);
                    }

                    Map<String, Set<String>> map = entries.get(policy);
                    if (map == null) {
                        map = new HashMap<String, Set<String>>();
                        entries.put(policy, map);
                        map.put(PolicyEvaluatorAdaptor.LBL_HOST_IDX, hostIdx);
                        map.put(PolicyEvaluatorAdaptor.LBL_PATH_IDX, pathIdx);
                        map.put(PolicyEvaluatorAdaptor.LBL_PATH_PARENT_IDX,
                            setPathParent);
                    } else {
                        map.get(PolicyEvaluatorAdaptor.LBL_HOST_IDX).addAll(
                            hostIdx);
                        map.get(PolicyEvaluatorAdaptor.LBL_PATH_IDX).addAll(
                            pathIdx);
                        map.get(PolicyEvaluatorAdaptor.LBL_PATH_PARENT_IDX).
                            addAll(setPathParent);
                    }
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

