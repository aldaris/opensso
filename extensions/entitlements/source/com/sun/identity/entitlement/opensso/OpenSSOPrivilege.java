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
 * $Id: OpenSSOPrivilege.java,v 1.7 2009-05-05 08:19:36 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeType;
import com.sun.identity.entitlement.ResourceAttributes;
import com.sun.identity.entitlement.util.NetworkMonitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 *
 * @author dennis
 */
public class OpenSSOPrivilege extends Privilege {
    private static final NetworkMonitor EVAL_SINGLE_LEVEL_MONITOR =
        NetworkMonitor.getInstance("privilegeSingleLevelEvaluation");
    private static final NetworkMonitor EVAL_SUB_TREE_MONITOR =
        NetworkMonitor.getInstance("privilegeSubTreeEvaluation");

    /**
     * Constructs entitlement privilege
     * @param name name of the privilege
     * @param eSubject EntitlementSubject used for membership check
     * @param eCondition EntitlementCondition used for constraint check
     * @param eResourceAttributes Resource1Attributes used to get additional
     * result attributes
     * @throws EntitlementException if resource names are invalid.
     */
    public OpenSSOPrivilege(
        String name,
        Entitlement entitlement,
        EntitlementSubject eSubject,
        EntitlementCondition eCondition,
        Set<ResourceAttributes> eResourceAttributes
    ) throws EntitlementException {
        super(name, entitlement, eSubject, eCondition, eResourceAttributes);
    }

    @Override
    public PrivilegeType getType() {
        return PrivilegeType.OPENSSO;
    }

    @Override
    public String getNativePolicy() {
        return null; //TOFIX;
    }

    @Override
    public boolean hasEntitlement(Subject subject, Entitlement e)
        throws EntitlementException {
        return false;
    }

    @Override
    public List<Entitlement> evaluate(
        Subject subject,
        String resourceName,
        Map<String, Set<String>> environment,
        boolean recursive
    ) throws EntitlementException {
        long start = (recursive) ? EVAL_SUB_TREE_MONITOR.start() :
            EVAL_SINGLE_LEVEL_MONITOR.start();

        List<Entitlement> results = new ArrayList<Entitlement>();

        Map<String, Set<String>> advices = new HashMap<String, Set<String>>();

        if (doesSubjectMatch(advices, subject, resourceName, environment) &&
            doesConditionMatch(advices, subject, resourceName, environment)
        ) {
            Set<String> resources = getMatchingResources(resourceName,
                recursive);
            Entitlement origE = getEntitlement();
            for (String r : resources) {
                Entitlement e = new Entitlement(origE.getApplicationName(),
                    r, origE.getActionValues());
                results.add(e);
            }
        } else {
            Entitlement origE = getEntitlement();
            Entitlement e = new Entitlement(origE.getApplicationName(),
                origE.getResourceName(), Collections.EMPTY_SET);
            e.setAdvices(advices);
            results.add(e);
        }

        if (recursive) {
            EVAL_SUB_TREE_MONITOR.end(start);
        } else {
            EVAL_SINGLE_LEVEL_MONITOR.end(start);
        }

        return results;
    }
}
