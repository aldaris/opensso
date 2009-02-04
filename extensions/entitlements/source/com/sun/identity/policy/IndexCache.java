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
 * $Id: IndexCache.java,v 1.1 2009-02-04 10:03:51 veiming Exp $
 */

package com.sun.identity.policy;

import com.iplanet.am.util.Cache;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceListener;
import java.security.AccessController;
import java.util.Set;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache implements ServiceListener {
    private static IndexCache instance = new IndexCache();

    private Cache hostIndexCache = new Cache(100000); // TOFIX
    private Cache pathIndexCache = new Cache(100000); // TOFIX
    private Cache pathParentIndexCache = new Cache(100000); // TOFIX

    static {
        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        try {
            ServiceConfigManager mgr = new ServiceConfigManager(
                "PolicyIndex", adminToken);
            mgr.addListener(instance);
        } catch (SSOException e) {
            //TOFIX
        } catch (SMSException e) {
            //TOFIX
        }
    }

    private IndexCache() {
    }

    public static IndexCache getInstance() {
        return instance;
    }

    public synchronized void cacheHostIndex(String idx, Set<Policy> objects) {
        Set<Policy> set = (Set<Policy>)hostIndexCache.get(idx);
        if ((set == null) || set.isEmpty()) {
            hostIndexCache.put(idx, objects);
        } else {
            set.addAll(objects);
        }
    }

    public Set<Policy> getHostIndex(String idx) {
        return (Set<Policy>)hostIndexCache.get(idx);
    }

    public synchronized void cachePathIndex(String idx, Set<Policy> objects) {
        Set<Policy> set = (Set<Policy>)pathIndexCache.get(idx);
        if ((set == null) || set.isEmpty()) {
            pathIndexCache.put(idx, objects);
        } else {
            set.addAll(objects);
        }
    }

    public Set<Policy> getPathIndex(String idx) {
        return (Set<Policy>)pathIndexCache.get(idx);
    }

    public synchronized void cachePathParentIndex(
        String idx,
        Set<Policy> objects
    ) {
        Set<Policy> set = (Set<Policy>)pathParentIndexCache.get(idx);
        if ((set == null) || set.isEmpty()) {
            pathParentIndexCache.put(idx, objects);
        } else {
            set.addAll(objects);
        }
    }

    public Set<Policy> getPathParentIndex(String idx) {
        return (Set<Policy>)pathParentIndexCache.get(idx);
    }

    private synchronized void clearCaches() {
        hostIndexCache = new Cache(100000); // TOFIX
        pathIndexCache = new Cache(100000); // TOFIX
        pathParentIndexCache = new Cache(100000); // TOFIX
    }

    public void globalConfigChanged(
        String serviceName,
        String version,
        String groupName,
        String serviceComponent,
        int type
    ) {
        clearCaches();
    }

    public void schemaChanged(String serviceName, String version) {
        //no-op
    }

    public void organizationConfigChanged(
        String serviceName,
        String version,
        String orgName,
        String groupName,
        String serviceComponent,
        int type
    ) {
        //no-op
    }
}

