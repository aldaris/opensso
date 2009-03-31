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
 * $Id: ApplicationTypeManager.java,v 1.1 2009-03-31 01:16:11 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.EntitlementService.ApplicationTypeInfo;
import com.sun.identity.entitlement.interfaces.ISaveIndex;
import com.sun.identity.entitlement.interfaces.ISearchIndex;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class ApplicationTypeManager {
    public static final String URL_APPLICATION_TYPE_NAME =
        "iPlanetAMWebAgentService";
    public static final String DELEGATION_APPLICATION_TYPE_NAME =
        "sunAMDelegationService";

    private static Map<String, ApplicationType> applicationTypes =
        new HashMap<String, ApplicationType>();

    static {
        Set<ApplicationTypeInfo> info =
            EntitlementService.getInstance().getApplicationTypes();
        for (ApplicationTypeInfo i : info) {
            addApplicationType(i);
        }
    }

    public static void addApplicationType(ApplicationTypeInfo info) {
        String name = info.getName();
        Set<String> actions = info.getActions();
        String searchIndexClassName = info.getSearchIndexImpl();
        String saveIndexClassName = info.getSaveIndexImpl();

        ISearchIndex searchIndex = ApplicationTypeManager.getSearchIndex(
            searchIndexClassName);
        ISaveIndex saveIndex = ApplicationTypeManager.getSaveIndex(
            saveIndexClassName);
        applicationTypes.put(name, new ApplicationType(name, actions,
            searchIndex, saveIndex));
    }

    public static ApplicationType get(String name) {
        return applicationTypes.get(name);
    }

    static ISearchIndex getSearchIndex(String className) {
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

    static ISaveIndex getSaveIndex(String className) {
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
}
