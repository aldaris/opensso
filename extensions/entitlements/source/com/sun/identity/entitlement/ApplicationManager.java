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
 * $Id: ApplicationManager.java,v 1.7 2009-04-07 10:25:07 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import com.sun.identity.entitlement.interfaces.ISaveIndex;
import com.sun.identity.entitlement.interfaces.ISearchIndex;
import com.sun.identity.entitlement.interfaces.ResourceName;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public final class ApplicationManager {
    private static Map<String, Application> applications =
        new HashMap<String, Application>();

    static {
        IPolicyConfig policyConfig = PolicyConfigFactory.getPolicyConfig();
        Set<ApplicationInfo> info = policyConfig.getApplications("/");
        for (ApplicationInfo i : info) {
            addApplication(i);
        }
    }

    private ApplicationManager() {
    }

    /**
     * TODO
     * create application
     * delete application
     * list applications
     */
    public static Application getApplication(String name) {
        if ((name == null) || (name.length() == 0)) {
            name = ApplicationTypeManager.URL_APPLICATION_TYPE_NAME;
        }
        return applications.get(name);
    }

    private static void addApplication(ApplicationInfo info) {
        String name = info.getName();
        String appTypeName = info.getApplicationType();
        Set<String> actions = info.getActions();
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
        addApplication(app);
    }

     private static Class getEntitlementCombiner(String className)
     {
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

    public static void addApplication(Application application) {
        applications.put(application.getName(), application);
    }

    public static void deleteApplication(String name) {
        applications.remove(name);
    }
}
