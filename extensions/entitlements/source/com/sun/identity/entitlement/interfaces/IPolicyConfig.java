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
 * $Id: IPolicyConfig.java,v 1.9 2009-05-07 22:13:32 veiming Exp $
 */

package com.sun.identity.entitlement.interfaces;

import com.sun.identity.entitlement.Application;
import com.sun.identity.entitlement.ApplicationType;
import com.sun.identity.entitlement.EntitlementException;
import java.util.Set;

/**
 * This interfaces defines the methods required from a policy configuration.
 */
public interface IPolicyConfig {
    String POLICY_THREAD_SIZE = "threadSize";
    String POLICY_CACHE_SIZE = "policyCacheSize";
    String INDEX_CACHE_SIZE = "indexCacheSize";
    String RESOURCE_COMPARATOR = "resourceComparator";

    /**
     * Returns a set of registered applications.
     *
     * @param realm Realm name.
     * @return a set of registered applications.
     */
    Set<Application> getApplications(String realm);

    /**
     * Removes application.
     *
     * @param realm Realm name.
     * @param name name of application to be removed.
     * @throws EntitlementException if application cannot be removed.
     */
    void removeApplication(String realm, String name)
        throws EntitlementException;

    /**
     * Stores the application to data store.
     *
     * @param realm Realm name
     * @param application Application object.
     * @throws EntitlementException if application cannot be stored.
     */
    void storeApplication(String realm, Application application)
        throws EntitlementException;

    /**
     * Returns a set of registered application type.
     *
     * @return A set of registered application type.
     */
    Set<ApplicationType> getApplicationTypes();

    /**
     * Removes application type.
     *
     * @param name name of application type to be removed.
     * @throws EntitlementException  if application type cannot be removed.
     */
    void removeApplicationType(String name)
        throws EntitlementException;

    /**
     * Stores the application type to data store.
     *
     * @param applicationType Application type  object.
     * @throws EntitlementException if application type cannot be stored.
     */
    void storeApplicationType(ApplicationType applicationType)
        throws EntitlementException;

    /**
     * Returns attribute value of a given attribute name,
     *
     * @param attributeName attribute name.
     * @return attribute value of a given attribute name,
     */
    String getAttributeValue(String attributeName);

    /**
     * Returns set of attribute values of a given attribute name,
     *
     * @param attributeName attribute name.
     * @return set of attribute values of a given attribute name,
     */
    Set<String> getAttributeValues(String attributeName);

    /**
     * Returns subject attribute names.
     *
     * @param realm Realm name.
     * @param application Application name.
     * @return subject attribute names.
     * @throws EntitlementException if subject attribute names cannot be
     * returned.
     */
    Set<String> getSubjectAttributeNames(String realm, String application)
        throws EntitlementException;

    /**
     * Adds subject attribute names.
     *
     * @param realm Realm name.
     * @param application Application name.
     * @param names Set of subject attribute names.
       @throws EntitlementException if subject attribute names cannot be
     *         added.
     */
    void addSubjectAttributeNames(String realm, String application,
        Set<String> names) throws EntitlementException;;

    /**
     * Adds a new action.
     *
     * @param realm Realm name.
     * @param appName Application name,
     * @param name Action name.
     * @param defVal Default value.
     * @throws EntitlementException if action cannot be added.
     */
    void addApplicationAction(
        String realm,
        String appName,
        String name,
        Boolean defVal
    ) throws EntitlementException;

    /**
     * Returns <code>true</code> if OpenSSO policy data is migrated to a
     * form that entitlements service can operates on them.
     *
     * @return <code>true</code> if OpenSSO policy data is migrated to a
     * form that entitlements service can operates on them.
     */
    boolean hasEntitlementDITs();

    /**
     * Returns <code>true</code> if the system is migrated to support
     * entitlement services.
     *
     * @return <code>true</code> if the system is migrated to support
     * entitlement services.
     */
    boolean migratedToEntitlementService();
}
