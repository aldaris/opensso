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
 * $Id: URLApplicationType.java,v 1.1 2009-03-28 06:45:28 veiming Exp $
 */

package com.sun.identity.entitlement;

import com.sun.identity.entitlement.util.ResourceNameIndexGenerator;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class URLApplicationType extends ApplicationType {
    private static Set<String> ACTIONS = new HashSet<String>();
    private static final String NAME = "iPlanetAMWebAgentService";
    private static ApplicationType instance = new URLApplicationType();

    static {
        ACTIONS.add("GET");
        ACTIONS.add("POST");
    }

    private URLApplicationType() {
        super(NAME, ACTIONS);
    }

    public static ApplicationType getInstance() {
        return instance;
    }

    @Override
    public ResourceSearchIndexes getResourceSearchIndex(String resource) {
        return ResourceNameSplitter.split(resource);
    }

    @Override
    public ResourceSaveIndexes getResourceSaveIndex(String resource) {
        return ResourceNameIndexGenerator.getResourceIndex(resource);
    }


}
