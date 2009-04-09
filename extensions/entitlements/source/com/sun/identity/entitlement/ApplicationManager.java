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
 * $Id: ApplicationManager.java,v 1.9 2009-04-09 13:15:01 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import com.sun.identity.entitlement.interfaces.ISaveIndex;
import com.sun.identity.entitlement.interfaces.ISearchIndex;
import com.sun.identity.entitlement.interfaces.ResourceName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Application Manager handles addition, deletion and listing of applications
 * for each realm.
 */
public final class ApplicationManager {
    private static Map<String, Map<String, Application>> applications =
        new HashMap<String, Map<String, Application>>();

    static {
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        Set<ApplicationInfo> info = policyConfig.getApplications("/");
        for (ApplicationInfo i : info) {
            addApplication("/", i); //TOFIX
        }
    }

    private ApplicationManager() {
    }

    /**
     * Returns the application names in a realm.
     *
     * @param realm Realm name.
     * @return application names in a realm.
     */
    public static Set<String> getApplicationNames(String realm) {
        Map<String, Application> map = applications.get(realm);
        return (map == null) ? Collections.EMPTY_SET : map.keySet();
    }

    /**
     * Returns application.
     *
     * @param realm Realm name.
     * @param name Name of Application.
     * @return application.
     */
    public static Application getApplication(String realm, String name) {
        if ((name == null) || (name.length() == 0)) {
            name = ApplicationTypeManager.URL_APPLICATION_TYPE_NAME;
        }
        Map<String, Application> map = applications.get(realm);
        return (map == null) ? null : map.get(name);
    }

    private static void addApplication(String realm, ApplicationInfo info) {
        String name = info.getName();
        String appTypeName = info.getApplicationType();
        Map<String, Boolean> actions = info.getActions();
        Set<String> resources = info.getResources();
        Set<String> conditions = info.getConditionClassNames();
        String searchIndexClassName = info.getSearchIndexImpl();
        String saveIndexClassName = info.getSaveIndexImpl();
        String resourceComp = info.getResourceComparator();

        ApplicationType appType = ApplicationTypeManager.get(appTypeName);
        Application app = new Application(name, appType);
        Class combiner = getEntitlementCombiner(info.getEntitlementCombiner());
        app.setEntitlementCombiner(combiner);

        if (actions != null) {
            app.setActions(actions);
        }
        if (resources != null) {
            app.setResources(resources);
        }
        if (conditions != null) {
            app.setResources(conditions);
        }

        ISearchIndex searchIndex = ApplicationTypeManager.getSearchIndex(
            searchIndexClassName);
        ISaveIndex saveIndex = ApplicationTypeManager.getSaveIndex(
            saveIndexClassName);
        ResourceName resComp = ApplicationTypeManager.getResourceComparator(
            resourceComp);
        if (searchIndex != null) {
            app.setSearchIndex(searchIndex);
        }
        if (saveIndex != null) {
            app.setSaveIndex(saveIndex);
        }
        if (resComp != null) {
            app.setResourceComparator(resComp);
        }
        addApplication(realm, app);
    }

     private static Class getEntitlementCombiner(String className) {
        if (className == null) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            //TOFIX debug error
        }
        return com.sun.identity.entitlement.DenyOverride.class;
    }

    /**
     * Adds application.
     *
     * @param realm Realm name.
     * @param application Application object.
     */
    public synchronized static void addApplication(
        String realm,
        Application application
    ) {
        Map<String, Application> map = applications.get(realm);
        if (map == null) {
            map = new HashMap<String, Application>();
            applications.put(realm, map);
        }
        map.put(application.getName(), application);
    }

    /**
     * Removes application.
     *
     * @param realm Realm Name.
     * @param name Application Name.
     */
    public synchronized  static void deleteApplication(
        String realm,
        String name
    ) {
        Map<String, Application> map = applications.get(realm);
        if (map != null) {
            map.remove(name);
        }
    }
}
