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
 * $Id: EntitlementService.java,v 1.2 2009-04-07 10:25:11 veiming Exp $
 */

package com.sun.identity.entitlement.opensso;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.entitlement.ApplicationInfo;
import com.sun.identity.entitlement.ApplicationTypeInfo;
import com.sun.identity.entitlement.interfaces.IPolicyConfig;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceSchemaManager;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class EntitlementService implements IPolicyConfig {
    public static final String SERVICE_NAME = "openssoEntitlement";

    private static final String CONFIG_APPLICATIONS = "registeredApplications";
    private static final String CONFIG_APPLICATIONTYPE = "applicationType";
    private static final String CONFIG_ACTIONS = "actions";
    private static final String CONFIG_RESOURCES = "resources";
    private static final String CONFIG_CONDITIONS = "conditions";
    private static final String CONFIG_ENTITLEMENT_COMBINER =
        "entitlementCombiner";
    private static final String CONFIG_SEARCH_INDEX_IMPL = "searchIndexImpl";
    private static final String CONFIG_SAVE_INDEX_IMPL = "saveIndexImpl";
    private static final String CONFIG_RESOURCE_COMP_IMPL = "resourceComparator";
    private static final String CONFIG_APPLICATION_TYPES = "applicationTypes";

    public EntitlementService() {
    }

    public String getAttributeValue(String attrName) {
        Set<String> values = getAttributeValues(attrName);
        return ((values != null) && !values.isEmpty()) ?
            values.iterator().next() : null;
    }

    public Set<String> getAttributeValues(String attrName) {
        try {
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            ServiceSchemaManager smgr = new ServiceSchemaManager(
                SERVICE_NAME, adminToken);
            AttributeSchema as = smgr.getGlobalSchema().getAttributeSchema(
                attrName);
            return as.getDefaultValues();
        } catch (SMSException ex) {
            //TOFIX;
        } catch (SSOException ex) {
            //TOFIX;
        }
        return Collections.EMPTY_SET;
    }

    public Set<ApplicationTypeInfo> getApplicationTypes() {
        Set<ApplicationTypeInfo> results = new HashSet<ApplicationTypeInfo>();
        try {
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
            ServiceConfigManager mgr = new ServiceConfigManager(
                SERVICE_NAME, adminToken);
            ServiceConfig globalConfig = mgr.getGlobalConfig(null);
            if (globalConfig != null) {
                ServiceConfig conf = globalConfig.getSubConfig(
                    CONFIG_APPLICATION_TYPES);
                Set<String> names = conf.getSubConfigNames();

                for (String name : names) {
                    ServiceConfig appType = conf.getSubConfig(name);
                    Map<String, Set<String>> data = appType.getAttributes();
                    Set<String> actions = data.get(CONFIG_ACTIONS);
                    String saveIndexImpl = getAttribute(data,
                        CONFIG_SAVE_INDEX_IMPL);
                    String searchIndexImpl = getAttribute(data,
                        CONFIG_SEARCH_INDEX_IMPL);
                    String resourceComp = getAttribute(data,
                        CONFIG_RESOURCE_COMP_IMPL);
                    results.add(new ApplicationTypeInfo(
                        name, actions, saveIndexImpl, searchIndexImpl,
                        resourceComp));
                }
            }
        } catch (SMSException ex) {
            // TOFIX
        } catch (SSOException ex) {
            //TOFIX
        }
        return results;
    }

    private String getAttribute(
        Map<String, Set<String>> data,
        String attributeName) {
        Set<String> set = data.get(attributeName);
        return ((set != null) && !set.isEmpty()) ? set.iterator().next() : null;
    }

    public Set<ApplicationInfo> getApplications(String realm) {
        Set<ApplicationInfo> results = new HashSet<ApplicationInfo>();
        try {
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            ServiceConfigManager mgr = new ServiceConfigManager(
                SERVICE_NAME, adminToken);
            ServiceConfig orgConfig = mgr.getOrganizationConfig(realm, null);
            if (orgConfig != null) {
                ServiceConfig conf = orgConfig.getSubConfig(
                    CONFIG_APPLICATIONS);
                Set<String> names = conf.getSubConfigNames();

                for (String name : names) {
                    ServiceConfig appType = conf.getSubConfig(name);
                    Map<String, Set<String>> data = appType.getAttributes();
                    Set<String> actions = data.get(CONFIG_ACTIONS);
                    Set<String> resources = data.get(CONFIG_RESOURCES);
                    String entitlementCombiner = getAttribute(data,
                        CONFIG_ENTITLEMENT_COMBINER);
                    Set<String> conditionClassNames = data.get(
                        CONFIG_CONDITIONS);
                    String applicationType = getAttribute(data,
                        CONFIG_APPLICATIONTYPE);
                    String saveIndexImpl = getAttribute(data,
                        CONFIG_SAVE_INDEX_IMPL);
                    String searchIndexImpl = getAttribute(data,
                        CONFIG_SEARCH_INDEX_IMPL);
                    String resourceComp = getAttribute(data,
                        CONFIG_RESOURCE_COMP_IMPL);

                    results.add(new ApplicationInfo(
                        name, actions, resources, entitlementCombiner,
                        conditionClassNames, applicationType, saveIndexImpl,
                        searchIndexImpl, resourceComp));
                }
            }
        } catch (SMSException ex) {
            // TOFIX
        } catch (SSOException ex) {
            //TOFIX
        }
        return results;
    }



}
