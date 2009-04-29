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
 * $Id: ApplicationTypeManager.java,v 1.6 2009-04-29 18:14:14 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import com.sun.identity.entitlement.interfaces.ISaveIndex;
import com.sun.identity.entitlement.interfaces.ISearchIndex;
import com.sun.identity.entitlement.interfaces.ResourceName;
import java.util.HashSet;
import java.util.Set;

/**
 * Application Type manager.
 */
public class ApplicationTypeManager {
    public static final String URL_APPLICATION_TYPE_NAME =
        "iPlanetAMWebAgentService";
    public static final String DELEGATION_APPLICATION_TYPE_NAME =
        "sunAMDelegationService";
    
    /**
     * Returns application type names.
     *
     * @return application type names.
     */
    public static Set<String> getApplicationTypeNames() {
        Set<String> names = new HashSet<String>();
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        Set<ApplicationType> applications = policyConfig.getApplicationTypes();
        for (ApplicationType a : applications) {
            names.add(a.getName());
        }
        return names;
    }

    /**
     * Returns application type.
     *
     * @param name Name of application type.
     * @return application type.
     */
    public static ApplicationType getAppplicationType(String name) {
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        Set<ApplicationType> applications = policyConfig.getApplicationTypes();
        for (ApplicationType a : applications) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Removes application type.
     *
     * @param name Name of application type.
     */
    public static void removeApplicationType(String name) {
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        policyConfig.removeApplicationType(name);
    }

    /**
     * Stores application type.
     *
     * @param appType Application type.
     */
    public static void saveApplicationType(ApplicationType appType)
        throws EntitlementException {
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        policyConfig.storeApplicationType(appType);
    }


    public static ISearchIndex getSearchIndex(String className) {
        if (className == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(className);
            Object o = clazz.newInstance();
            if (o instanceof ISearchIndex) {
                return (ISearchIndex) o;
            }
        } catch (InstantiationException ex) {
            //TOFIX debug error
        } catch (IllegalAccessException ex) {
            //TOFIX debug error
        } catch (ClassNotFoundException ex) {
            //TOFIX debug error
        }
        return null;
    }

    public static ISaveIndex getSaveIndex(String className) {
        if (className == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(className);
            Object o = clazz.newInstance();
            if (o instanceof ISaveIndex) {
                return (ISaveIndex) o;
            }
        } catch (InstantiationException ex) {
            //TOFIX debug error
        } catch (IllegalAccessException ex) {
            //TOFIX debug error
        } catch (ClassNotFoundException ex) {
            //TOFIX debug error
        }
        return null;
    }

    public static ResourceName getResourceComparator(String className) {
        if (className == null) {
            return null;
        }
        try {
            Class clazz = Class.forName(className);
            Object o = clazz.newInstance();
            if (o instanceof ResourceName) {
                return (ResourceName) o;
            }
        } catch (InstantiationException ex) {
            //TOFIX debug error
        } catch (IllegalAccessException ex) {
            //TOFIX debug error
        } catch (ClassNotFoundException ex) {
            //TOFIX debug error
        }
        return null;
    }

}
