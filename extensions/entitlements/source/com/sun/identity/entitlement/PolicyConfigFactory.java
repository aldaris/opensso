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
 * $Id: PolicyConfigFactory.java,v 1.2 2009-04-14 00:24:18 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import com.sun.identity.entitlement.util.DebugFactory;
import com.sun.identity.shared.debug.IDebug;

/**
 * This factory is responsible for providing the policy configuration object.
 */
public final class PolicyConfigFactory {
    private static final PolicyConfigFactory instance = new 
        PolicyConfigFactory();
    private IPolicyConfig policyConfig;
    public static IDebug debug = DebugFactory.getDebug("entitlementConfig");

    private PolicyConfigFactory() {
        try {
            //TOFIX
            Class clazz = Class.forName(
                "com.sun.identity.entitlement.opensso.EntitlementService");
            policyConfig = (IPolicyConfig)clazz.newInstance();
        } catch (InstantiationException e) {
            debug.error("PolicyConfigFactory.<init>", e);
        } catch (IllegalAccessException e) {
            debug.error("PolicyConfigFactory.<init>", e);
        } catch (ClassNotFoundException e) {
            debug.error("PolicyConfigFactory.<init>", e);
        }
    }

    /**
     * Returns the policy configuration object.
     * 
     * @return the policy configuration object.
     */
    public static IPolicyConfig getPolicyConfig() {
        return instance.policyConfig;
    }
}
