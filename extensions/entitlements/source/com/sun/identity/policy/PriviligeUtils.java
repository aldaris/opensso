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
 * $Id: PriviligeUtils.java,v 1.1 2009-02-11 01:45:29 dillidorai Exp $
 */

package com.sun.identity.policy;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.Privilige;
import java.util.Set;

/**
 * Class with utility methods to map from
 * <code>com.sun.identity.entity.Privilige</code>
 * to
 * </code>com.sun.identity.policy.Policy</code>
 */
public class PriviligeUtils {

    private PriviligeUtils() {
    }

    static Policy priviligeToPolicy(Privilige privilige) 
            throws PolicyException {
        Policy policy = null;
        policy = new Policy(privilige.getName());
        Set<Entitlement> entitlements = privilige.getEntitlements();
        for(Entitlement entitlement: entitlements) {
            Rule rule = entitlmentToRule(entitlement);
            policy.addRule(rule);
        }

        return policy;
    }

    static Rule entitlmentToRule(Entitlement entitlement)
            throws PolicyException {
        return null;
    }
    
}
