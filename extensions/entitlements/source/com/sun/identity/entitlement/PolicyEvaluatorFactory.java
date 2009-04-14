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
 * $Id: PolicyEvaluatorFactory.java,v 1.6 2009-04-14 00:24:18 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyEvaluator;
import com.sun.identity.entitlement.util.DebugFactory;
import com.sun.identity.shared.debug.IDebug;

/**
 * This class returns the privilege evaluator object.
 */
public final class PolicyEvaluatorFactory {
    private static final PolicyEvaluatorFactory instance =
        new PolicyEvaluatorFactory();
    private static Class defaultImpl;
    public static IDebug debug = DebugFactory.getDebug(
        "entitlementEvaluation");
    
    static {
        try {
            defaultImpl = Class.forName(
                "com.sun.identity.entitlement.PrivilegeEvaluator");
        } catch (ClassNotFoundException e) {
            debug.error("PolicyEvaluationFactory.static<init>", e);
        }
    }
    
    private PolicyEvaluatorFactory() {
    }

    /**
     * Returns an instance of this factory.
     *
     * @return an instance of this factory.
     */
    public static PolicyEvaluatorFactory getInstance() {
        return instance;
    }

    /**
     * Returns a NEW instance of the privilege evaluation.
     * 
     * @return a NEW instance of the privilege evaluation.
     */
    public IPolicyEvaluator getEvaluator() {
        try {
            return (IPolicyEvaluator)defaultImpl.newInstance();
        } catch (InstantiationException e) {
            debug.error("PolicyEvaluatorFactory.getEvaluator", e);
        } catch (IllegalAccessException e) {
            debug.error("PolicyEvaluatorFactory.getEvaluator", e);
        }
        return null;
    }
}
