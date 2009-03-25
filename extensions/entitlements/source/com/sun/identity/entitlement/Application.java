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
 * $Id: Application.java,v 1.1 2009-03-25 06:42:51 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Set;

/**
 *
 * @author dennis
 */
public class Application {
    private String name;
    private ApplicationType applicationType;
    private Set<String> actions;
    private Set<String> conditions;
    private EntitlementCombiner entitlementCombiner;

    public Application(String name, ApplicationType applicationType) {
        this.name = name;
        this.applicationType = applicationType;
    }

    public Set<String> getActions() {
        return actions;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public Set<String> getConditions() {
        return conditions;
    }

    public EntitlementCombiner getEntitlementCombiner() {
        return entitlementCombiner;
    }

    public String getName() {
        return name;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

    public void setConditions(Set<String> conditions) {
        this.conditions = conditions;
    }




}
