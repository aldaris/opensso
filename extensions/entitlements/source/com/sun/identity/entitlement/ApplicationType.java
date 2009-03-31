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
 * $Id: ApplicationType.java,v 1.3 2009-03-31 01:16:10 veiming Exp $
 */
package com.sun.identity.entitlement;

import com.sun.identity.entitlement.interfaces.ISaveIndex;
import com.sun.identity.entitlement.interfaces.ISearchIndex;
import com.sun.identity.entitlement.util.ResourceNameIndexGenerator;
import com.sun.identity.entitlement.util.ResourceNameSplitter;
import java.util.Set;

/**
 *
 * @author dennis
 */
public class ApplicationType {
    private String name;
    private Set<String> actions;
    private ISearchIndex searchIndex;
    private ISaveIndex saveIndex;

    public ApplicationType(
        String name,
        Set<String> actions,
        ISearchIndex searchIndex,
        ISaveIndex saveIndex
    ) {
        this.name = name;
        this.actions = actions;

        if (searchIndex == null) {
            this.searchIndex = new ResourceNameSplitter();
        } else {
            this.searchIndex = searchIndex;
        }
        if (saveIndex == null) {
            this.saveIndex = new ResourceNameIndexGenerator();
        } else {
            this.saveIndex = saveIndex;
        }
    }

    public String getName() {
        return name;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setSaveIndex(ISaveIndex saveIndex) {
        this.saveIndex = saveIndex;
    }

    public void setSearchIndex(ISearchIndex searchIndex) {
        this.searchIndex = searchIndex;
    }

    public ResourceSearchIndexes getResourceSearchIndex(String resource) {
        return searchIndex.getIndexes(resource);
    }

    public ResourceSaveIndexes getResourceSaveIndex(String resource) {
        return saveIndex.getIndexes(resource);
    }

}
