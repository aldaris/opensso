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
 * $Id: ResourceAttributes.java,v 1.2 2009-03-20 01:10:15 dillidorai Exp $
 */
package com.sun.identity.entitlement;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Interface specification for entitlement <code>ResourceAttributes</code>
 */
public interface ResourceAttributes {

    /**
     * Sets configuration properties for this <code>ResourceAttributes</code>
     * @param properties configuration properties for  this
     * <code>ResourceAttributes</code>
     * @throws com.sun.identity.entitlement.EntitlementException if any
     * abnormal condition occured
     */
    public void setProperties(Map<String, Set<String>> properties)
            throws EntitlementException;

    /**
     * <code>ResourceAttributes</code>
     * @return configuration properties for this
     * <code>ResourceAttributes</code>
     */
    public Map<String, Set<String>> getProperties();

    /**
     * Returns resoruce attributes aplicable to the request
     * @param subject Subject who is under evaluation.
     * @param resourceName Resource name.
     * @param environment Environment parameters.
     * @return applicable resource attributes
     * @throws com.sun.identity.entitlement.EntitlementException
     * if can not get condition decision
     */
    public Map<String, Set<String>> evaluate(
            Subject subject,
            String resourceName,
            Map<String, Set<String>> environment)
            throws EntitlementException;
}
