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
 * $Id: IndexCache.java,v 1.3 2009-02-05 23:16:50 veiming Exp $
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
import java.util.HashSet;
import java.util.Set;

/**
 * Caches the indexes which are stored in Directory Server.
 */
public class IndexCache implements ServiceListener {
    private static IndexCache instance = new IndexCache();
    private static int DEFAULT_CACHE_SIZE = 100000; //TOFIX

    private Cache hostIndexCache;
    private Cache pathIndexCache;
    private Cache pathParentIndexCache;

    static {
        instance.clearCaches();

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

    /**
     * Returns an instance.
     *
     * @return an instance.
     */
    public static IndexCache getInstance() {
        return instance;
    }

    /**
     * Caches host index.
     *
     * @param idx host index
     * @param objects Set of policies associated with this index.
     */
    public synchronized void cacheHostIndex(String idx, Set<Policy> policies) {
        Set<Policy> set = (Set<Policy>)hostIndexCache.get(idx);
        if (set == null) {
            set = new HashSet<Policy>();
            hostIndexCache.put(idx, set);
        }
        set.addAll(policies);
    }

    /**
     * Returns policies associated with a host index.
     *
     * @param idx host index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getHostIndex(String idx) {
        return (Set<Policy>)hostIndexCache.get(idx);
    }

    /**
     * Caches path index.
     *
     * @param idx path index
     * @param objects Set of policies associated with this index.
     */
    public synchronized void cachePathIndex(String idx, Set<Policy> policies) {
        Set<Policy> set = (Set<Policy>)pathIndexCache.get(idx);
        if (set == null) {
            set = new HashSet<Policy>();
            pathIndexCache.put(idx, set);
        }
        set.addAll(policies);
    }

    /**
     * Returns policies associated with a path index.
     *
     * @param idx path index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getPathIndex(String idx) {
        return (Set<Policy>)pathIndexCache.get(idx);
    }

    /**
     * Caches path parent index.
     *
     * @param idx path parent  index
     * @param objects Set of policies associated with this index.
     */
    public synchronized void cachePathParentIndex(
        String idx,
        Set<Policy> policies
    ) {
        Set<Policy> set = (Set<Policy>)pathParentIndexCache.get(idx);
        if (set == null) {
            set = new HashSet<Policy>();
            pathParentIndexCache.put(idx, set);
        }
        set.addAll(policies);
    }

    /**
     * Returns policies associated with a path parent index.
     *
     * @param idx path parent index
     * @return Set of policies associated with this index.
     */
    public Set<Policy> getPathParentIndex(String idx) {
        return (Set<Policy>)pathParentIndexCache.get(idx);
    }

    private synchronized void clearCaches() {
        hostIndexCache = new Cache(DEFAULT_CACHE_SIZE);
        pathIndexCache = new Cache(DEFAULT_CACHE_SIZE);
        pathParentIndexCache = new Cache(DEFAULT_CACHE_SIZE);
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