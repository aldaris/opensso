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
 * $Id: IPolicyEvaluator.java,v 1.1 2009-04-02 22:13:39 veiming Exp $
 *
 */

package com.sun.identity.entitlement.interfaces;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

public interface IPolicyEvaluator {
    /**
     * Returns <code>true</code> if the subject is granted to an
     * entitlement.
     *
     * @param adminubject Subject for performing the evaluation.
     * @param subject Subject who is under evaluation.
     * @param serviceTypeName Application type.
     * @param entitlement Entitlement object which describes the resource name 
     *        and actions.
     * @param envParameters Map of environment parameters.
     * @return <code>true</code> if the subject is granted to an
     *         entitlement.
     * @throws EntitlementException if the result cannot be determined.
     */
    boolean hasEntitlement(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        Entitlement entitlement,
        Map<String, Set<String>> envParameters
    ) throws EntitlementException;

    /**
     * Returns list of entitlements granted to a subject.
     *
     * @param adminubject Subject for performing the evaluation.
     * @param subject Subject who is under evaluation.
     * @param serviceTypeName Application type.
     * @param entitlement Entitlement object which describes the resource name 
     *        and actions.
     * @param envParameters Map of environment parameters.
     * @param recursive <code>true</code> to perform evaluation on sub resources
     *        from the given resource name.
     * @return list of entitlements granted to a subject.
     * @throws EntitlementException if the result cannot be determined.
     */
    List<Entitlement> getEntitlements(
        Subject adminSubject,
        Subject subject,
        String serviceTypeName,
        String resourceName,
        Map<String, Set<String>> envParameters,
        boolean recursive
    ) throws EntitlementException;
}
