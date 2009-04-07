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
 * $Id: ApplicationInfo.java,v 1.2 2009-04-07 10:25:07 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Set;

/**
 *
 * @author dennis
 */
public class ApplicationInfo {
    private String name;
    private Set<String> actions;
    private Set<String> resources;
    private Set<String> conditionClassNames;
    private String entitlementCombiner;
    private String applicationType;
    private String searchIndexImpl;
    private String saveIndexImpl;
    private String resourceComp;

    public ApplicationInfo(
        String name,
        Set<String> actions,
        Set<String> resources,
        String entitlementCombiner,
        Set<String> conditionClassNames,
        String applicationType,
        String saveIndexImpl,
        String searchIndexImpl,
        String resourceComp) {
        this.name = name;
        this.actions = actions;
        this.resources = resources;
        this.entitlementCombiner = entitlementCombiner;
        this.conditionClassNames = conditionClassNames;
        this.applicationType = applicationType;
        this.saveIndexImpl = saveIndexImpl;
        this.searchIndexImpl = searchIndexImpl;
        this.resourceComp = resourceComp;
    }

    public Set<String> getActions() {
        return actions;
    }

    public Set<String> getResources() {
        return resources;
    }

    public String getName() {
        return name;
    }

    public String getSaveIndexImpl() {
        return saveIndexImpl;
    }

    public String getSearchIndexImpl() {
        return searchIndexImpl;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public Set<String> getConditionClassNames() {
        return conditionClassNames;
    }

    public void setConditionClassNames(Set<String> conditionClassNames) {
        this.conditionClassNames = conditionClassNames;
    }

    public String getEntitlementCombiner() {
        return entitlementCombiner;
    }

    public String getResourceComparator() {
        return resourceComp;
    }
}
