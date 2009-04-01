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
 * $Id: DenyOverride.java,v 1.2 2009-04-01 00:21:29 dillidorai Exp $
 */

package com.sun.identity.entitlement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class DenyOverride implements EntitlementCombiner {

    public Entitlement combines(
        Entitlement e1,
        Entitlement e2
    ) {
        Entitlement result = null;
        if (e1.getApplicationName().equals(e2.getApplicationName())) {
            result = new Entitlement(e1.getApplicationName(),
                e1.getResourceNames(), mergeActionValues(e1, e2)); //TODO: recheck
            result.setAdvices(mergeAdvices(e1, e2));
            result.setAttributes(mergeAttributes(e1, e2));
        }
        return result;

    }

    private Map<String, Boolean> mergeActionValues(
        Entitlement e1,
        Entitlement e2
    ) {
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
                Boolean b = Boolean.valueOf(
                    b1.booleanValue() & b2.booleanValue());
                result.put(n, b);
            }
        }
        return result;
    }

    private Map<String, String> mergeAdvices(Entitlement e1, Entitlement e2) {
        Map<String, String> result = new HashMap<String, String>();
        Map<String, String> a1 = e1.getAdvices();
        Map<String, String> a2 = e2.getAdvices();

        Set<String> names = new HashSet<String>();
        for (String n : names) {
            String advice1 = a1.get(n);
            String advice2 = a2.get(n);

            if (advice1 == null) {
                result.put(n, advice2);
            } else {
                result.put(n, advice1);
            }
        }
        return result;
    }

    private Map<String, Set<String>> mergeAttributes(
        Entitlement e1,
        Entitlement e2
    ) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        Map<String, Set<String>> a1 = e1.getAttributes();
        Map<String, Set<String>> a2 = e2.getAttributes();

        Set<String> names = new HashSet<String>();
        for (String n : names) {
            Set<String> attr1 = a1.get(n);
            Set<String> attr2 = a2.get(n);

            if (attr1 == null) {
                result.put(n, attr2);
            } else if (attr2 == null) {
                result.put(n, attr1);
            } else {
                Set<String> combined = new HashSet<String>();
                combined.addAll(attr1);
                combined.addAll(attr2);
                result.put(n, combined);
            }
        }
        return result;
    }
}
