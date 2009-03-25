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
 * $Id: ResourceSaveIndexes.java,v 1.1 2009-03-25 06:42:51 veiming Exp $
 */
package com.sun.identity.entitlement;

import java.util.Set;

/**
 *
 * @author dennis
 */
public class ResourceSaveIndexes {
    private Set<String> hostIndexes;
    private Set<String> pathIndexes;
    private Set<String> parentPath;

    public ResourceSaveIndexes(
        Set<String> hostIndexes,
        Set<String> pathIndexes,
        Set<String> parentPath
    ) {
        this.hostIndexes = hostIndexes;
        this.pathIndexes = pathIndexes;
        this.parentPath = parentPath;
    }

    public Set<String> getHostIndexes() {
        return hostIndexes;
    }

    public Set<String> getParentPath() {
        return parentPath;
    }

    public Set<String> getPathIndexes() {
        return pathIndexes;
    }

    public void addAll(ResourceSaveIndexes other) {
        this.hostIndexes.addAll(other.hostIndexes);
        this.pathIndexes.addAll(other.pathIndexes);
        this.parentPath.addAll(other.parentPath);
    }

}
