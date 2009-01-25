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
 * $Id: ResourceIndex.java,v 1.1 2009-01-25 09:39:25 veiming Exp $
 *
 */
package com.sun.identity.entitlement.util;

import java.util.HashSet;
import java.util.Set;

/**
 * This class encapsulates the different indexes for a policy.
 */
public class ResourceIndex {
    private String hostIndex;
    private String pathIndex;
    private Set<String> pathParentIndex;

    /**
     * Constructs a resource index object.
     * 
     * @param hostIdx Host Index.
     * @param pathIdx Path Index.
     * @param pathParentIdx a set of path parent index.
     */
    public ResourceIndex(
        String hostIdx, 
        String pathIdx, 
        Set<String>pathParentIdx
    ) {
        hostIndex = hostIdx;
        pathIndex = pathIdx;
        pathParentIndex = new HashSet<String>();
        pathParentIndex.addAll(pathParentIdx);
    }

    /**
     * Returns host index.
     * 
     * @return host index.
     */
    public String getHostIndex() {
        return hostIndex;
    }

    /**
     * Returns path index.
     * 
     * @return path index.
     */
    public String getPathIndex() {
        return pathIndex;
    }

    /**
     * Returns a set of path parent indexes.
     * 
     * @return a set of path parent indexes.
     */
    public Set<String> getPathParentIndex() {
        return pathParentIndex;
    }

    
    
}
