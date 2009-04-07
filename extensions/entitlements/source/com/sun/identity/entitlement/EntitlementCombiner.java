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
 * $Id: EntitlementCombiner.java,v 1.3 2009-04-07 10:25:08 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.ResourceName;
import com.sun.identity.policy.ResourceMatch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public abstract class EntitlementCombiner {
    private String resourceName;
    private Set<String> actions;
    private boolean isDone;
    private boolean isRecursive;
    private List<Entitlement> results = new ArrayList<Entitlement>();
    private Entitlement rootE;
    private ResourceName resourceComparator;

    public void init(
        String applicationName,
        String resourceName,
        Set<String> actions,
        boolean isRecursive
    ) {
        this.resourceName = resourceName;
        this.isRecursive = isRecursive;
        this.actions = new HashSet<String>();

        Application application = ApplicationManager.getApplication(
            applicationName);
        rootE = new Entitlement(applicationName, resourceName,
            Collections.EMPTY_MAP);
        resourceComparator = application.getResourceComparator();

        if (!isRecursive) { // single level
            if ((actions != null) && !actions.isEmpty()) {
                this.actions.addAll(actions);
            } else {
                this.actions.addAll(application.getActions());
            }
        } else {
            this.actions.addAll(application.getActions());
        }
        results.add(rootE);

    }

    public void add(List<Entitlement> entitlements) {
        if (!isRecursive) {
            for (Entitlement e : entitlements) {
                mergeActionValues(rootE, e);
                mergeAdvices(rootE, e);
                mergeAttributes(rootE, e);
            }
        } else {
            for (Entitlement e : entitlements) {
                boolean toAdd = true;
                for (Entitlement existing : results) {
                    ResourceMatch match = resourceComparator.compare(
                        e.getResourceName(), existing.getResourceName(), true);
                    if (match.equals(ResourceMatch.EXACT_MATCH)) {
                        mergeActionValues(existing, e);
                        mergeAdvices(existing, e);
                        mergeAttributes(existing, e);
                        toAdd = false;
                    } else if (match.equals(ResourceMatch.SUB_RESOURCE_MATCH) ||
                        match.equals(ResourceMatch.WILDCARD_MATCH)) {
                        mergeActionValues(existing, e);
                        mergeAdvices(existing, e);
                        mergeAttributes(existing, e);
                    }
                }

                if (toAdd) {
                    Entitlement tmp = new Entitlement(e.getApplicationName(),
                        e.getResourceName(), e.getActionValues());
                    results.add(tmp);
                }
            }
        }
    }

    protected void mergeActionValues(Entitlement e1, Entitlement e2) {
        Map<String, Boolean> result = new HashMap<String, Boolean>();
        Map<String, Boolean> a1 = e1.getActionValues();
        Map<String, Boolean> a2 = e2.getActionValues();

        Set<String> actionNames = new HashSet<String>();
        actionNames.addAll(a1.keySet());
        actionNames.addAll(a2.keySet());

        for (String n : actionNames) {
            Boolean b1 = a1.get(n);
            Boolean b2 = a2.get(n);

            if (b1 == null) {
                result.put(n, b2);
            } else if (b2 == null) {
                result.put(n, b1);
            } else {
                Boolean b = Boolean.valueOf(combine(b1, b2));
                result.put(n, b);
            }
        }
        e1.setActionValues(result);
        isDone = isCompleted();
    }

    protected void mergeAdvices(Entitlement e1, Entitlement e2) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        Map<String, Set<String>> a1 = e1.getAdvices();
        Map<String, Set<String>> a2 = e2.getAdvices();

        Set<String> names = new HashSet<String>();
        for (String n : names) {
            Set<String> advice1 = a1.get(n);
            Set<String> advice2 = a2.get(n);

            Set<String> r = result.get(n);
            if (r == null) {
                r = new HashSet<String>();
                result.put(n, r);
            }

            if ((advice1 != null) && !advice1.isEmpty()) {
                r.addAll(advice1);
            }
            if ((advice2 != null) && !advice2.isEmpty()) {
                r.addAll(advice2);
            }
        }
        e1.setAdvices(result);
    }

    protected void mergeAttributes(Entitlement e1, Entitlement e2) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        Map<String, Set<String>> a1 = e1.getAttributes();
        Map<String, Set<String>> a2 = e2.getAttributes();

        Set<String> names = new HashSet<String>();
        for (String n : names) {
            Set<String> attr1 = a1.get(n);
            Set<String> attr2 = a2.get(n);

            Set<String> r = result.get(n);
            if (r == null) {
                r = new HashSet<String>();
                result.put(n, r);
            }

            if ((attr1 == null) && !attr1.isEmpty()) {
                r.addAll(attr1);
            }
            if ((attr2 == null) && !attr2.isEmpty()) {
                r.addAll(attr2);
            }
        }
        e1.setAttributes(result);
    }

    protected Set<String> getActions() {
        return actions;
    }

    protected boolean isRecursive() {
        return isRecursive;
    }

    protected Entitlement getRootE() {
        return rootE;
    }

    protected ResourceName getResourceComparator() {
        return resourceComparator;
    }

    public boolean isDone() {
        return isDone;
    }

    public List<Entitlement> getResults() {
        return results;
    }

    protected abstract boolean combine(Boolean b1, Boolean b2);
    protected abstract boolean isCompleted();

}
