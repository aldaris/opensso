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
 * $Id: ApplicationManager.java,v 1.14 2009-06-06 00:34:42 veiming Exp $
 */
package com.sun.identity.entitlement;

import java.util.HashSet;
import java.util.Set;
import javax.security.auth.Subject;

/**
 * Application Manager handles addition, deletion and listing of applications
 * for each realm.
 */
public final class ApplicationManager {

    private ApplicationManager() {
    }

    /**
     * Returns the application names in a realm.
     *
     * @param adminSubject Admin Subject who has the rights to access
     *        configuration datastore.
     * @param realm Realm name.
     * @return application names in a realm.
     */
    public static Set<String> getApplicationNames(
        Subject adminSubject,
        String realm
    ) {
        Set<String> results = new HashSet<String>();
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, realm);
        Set<Application> applications = ec.getApplications();
        for (Application appl : applications) {
            results.add(appl.getName());
        }
        return results;
    }

    /**
     * Returns application.
     *
     * @param adminSubject Admin Subject who has the rights to access
     *        configuration datastore.
     * @param realm Realm name.
     * @param name Name of Application.
     * @return application.
     */
    public static Application getApplication(
        Subject adminSubject,
        String realm,
        String name
    ) {
        if ((name == null) || (name.length() == 0)) {
            name = ApplicationTypeManager.URL_APPLICATION_TYPE_NAME;
        }
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, realm);
        Set<Application> applications = ec.getApplications();
        for (Application appl : applications) {
            if (appl.getName().equals(name)) {
                return appl;
            }
        }
        return null;
    }

    /**
     * Removes application.
     *
     * @param adminSubject Admin Subject who has the rights to access
     *        configuration datastore.
     * @param realm Realm Name.
     * @param name Application Name.
     * @throws EntitlementException
     */
    public static void deleteApplication(
        Subject adminSubject,
        String realm,
        String name
    ) throws EntitlementException {
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, realm);
        ec.removeApplication(name);
    }

    /**
     * Saves application data.
     *
     * @param adminSubject Admin Subject who has the rights to access
     *        configuration datastore.
     * @param realm Realm Name.
     * @param application Application object.
     */
    public static void saveApplication(
        Subject adminSubject,
        String realm,
        Application application
    ) throws EntitlementException {
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, realm);
        ec.storeApplication(application);
    }

    public static void referApplication(
        Subject adminSubject,
        String parentRealm,
        String referRealm,
        String applicationName,
        Set<String> resources
    ) throws EntitlementException {
        Application appl = getApplication(adminSubject, parentRealm,
            applicationName);
        if (appl == null) {
            Object[] params = {parentRealm, referRealm, applicationName};
            throw new EntitlementException(280, params);
        }

        appl.refers(referRealm, resources);
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, referRealm);
        ec.storeApplication(appl);
    }

    public static void dereferApplication(
        Subject adminSubject,
        String referRealm,
        String applicationName,
        Set<String> resources
    ) throws EntitlementException {
        EntitlementConfiguration ec = EntitlementConfiguration.getInstance(
            adminSubject, referRealm);
        ec.removeApplication(applicationName, resources);
    }
}
