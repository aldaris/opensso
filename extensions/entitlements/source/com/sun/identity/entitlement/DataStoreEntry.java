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
 * $Id: DataStoreEntry.java,v 1.1 2009-01-29 02:04:01 veiming Exp $
 */

package com.sun.identity.entitlement;

import java.util.Set;

/**
 * This class encapsulates the index entry in data store
 */
public class DataStoreEntry {
    private Set<String> hostIndexes;
    private Set<String> pathIndexes;
    private String pathParent;
    private Object policy;
    
    public DataStoreEntry(
        Set<String> hostIndexes,
        Set<String> pathIndexes,
        String pathParent,
        Object policy
    ) {
        this.hostIndexes = hostIndexes;
        this.pathIndexes = pathIndexes;
        this.pathParent = pathParent;
        this.policy = policy;
    }

    public Set<String> getHostIndexes() {
        return hostIndexes;
    }

    public Set<String> getPathIndexes() {
        return pathIndexes;
    }

    public String getPathParent() {
        return pathParent;
    }

    public Object getPolicy() {
        return policy;
    }
    
    public void setPolicy(Object p) {
        policy = p;
    }
}
