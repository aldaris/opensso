/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: IndexCache.java,v 1.1 2009-02-04 07:41:20 veiming Exp $
 */

package com.sun.identity.entitlement.util;

import com.iplanet.am.util.Cache;
import java.util.Set;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache {
    private static IndexCache instance = new IndexCache();

    private Cache hostIndexCache = new Cache(100000); // TOFIX
    private Cache pathIndexCache = new Cache(100000); // TOFIX
    private Cache pathParentIndexCache = new Cache(100000); // TOFIX

    private IndexCache() {
    }

    public static IndexCache getInstance() {
        return instance;
    }

    public void cacheHostIndex(String idx, Set<String> objects) {
        Set<Object> set = (Set<Object>)hostIndexCache.get(idx);
        if ((set == null) || set.isEmpty()) {
            hostIndexCache.put(idx, objects);
        } else {
            set.addAll(objects);
        }
    }

    public Set<Object> getHostIndex(String idx) {
        return (Set<Object>)hostIndexCache.get(idx);
    }

    public void cachePathIndex(String idx, Set<String> objects) {
        Set<Object> set = (Set<Object>)pathIndexCache.get(idx);
        if ((set == null) || set.isEmpty()) {
            pathIndexCache.put(idx, objects);
        } else {
            set.addAll(objects);
        }
    }

    public Set<Object> getPathIndex(String idx) {
        return (Set<Object>)pathIndexCache.get(idx);
    }

    public void cachePathParentIndex(String idx, Set<String> objects) {
        Set<Object> set = (Set<Object>)pathParentIndexCache.get(idx);
        if ((set == null) || set.isEmpty()) {
            pathParentIndexCache.put(idx, objects);
        } else {
            set.addAll(objects);
        }
    }

    public Set<Object> getPathParentIndex(String idx) {
        return (Set<Object>)pathParentIndexCache.get(idx);
    }
}

