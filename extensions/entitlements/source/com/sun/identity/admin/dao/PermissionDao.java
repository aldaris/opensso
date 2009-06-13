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
 * $Id: PermissionDao.java,v 1.6 2009-06-13 00:43:08 farble1670 Exp $
 */
package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.Permission;
import com.sun.identity.admin.model.RealmBean;
import com.sun.identity.delegation.DelegationEvaluator;
import com.sun.identity.delegation.DelegationException;
import com.sun.identity.delegation.DelegationPermission;
import com.sun.identity.entitlement.opensso.EntitlementService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static com.sun.identity.console.base.model.AMAdminConstants.*;

public class PermissionDao implements Serializable {

    private enum AccessLevel {

        READ,
        WRITE,
        DELEGATE;
    }
    private static final Set<String> serviceNames = new HashSet<String>();
    private static final Map<String, Map<AccessLevel, Set<Permission>>> permissionMap = new HashMap<String, Map<AccessLevel, Set<Permission>>>();


    static {
        serviceNames.add(null);
        serviceNames.add(EntitlementService.SERVICE_NAME);

        // for each realm, for each access level, call out permissions

        Map<AccessLevel, Set<Permission>> accessMap;
        Set<Permission> permissions;

        //
        // realm (no service, null key)
        //
        accessMap = new HashMap<AccessLevel, Set<Permission>>();

        permissions = new HashSet<Permission>();
        permissions.add(Permission.HOME);
        permissions.add(Permission.NEWS);
        permissions.add(Permission.FEDERATION);
        accessMap.put(AccessLevel.READ, permissions);

        permissions = new HashSet<Permission>();
        permissions.add(Permission.SAMLV2_HOSTED_IDP_CREATE);
        permissions.add(Permission.SAMLV2_HOSTED_SP_CREATE);
        permissions.add(Permission.SAMLV2_REMOTE_IDP_CREATE);
        permissions.add(Permission.SAMLV2_REMOTE_SP_CREATE);
        accessMap.put(AccessLevel.WRITE, permissions);

        permissions = new HashSet<Permission>();
        permissions.add(Permission.REFERRAL_CREATE);
        accessMap.put(AccessLevel.DELEGATE, permissions);

        permissionMap.put(null, accessMap);

        //
        // openssoEntitlements service
        //
        accessMap = new HashMap<AccessLevel, Set<Permission>>();

        permissions = new HashSet<Permission>();
        permissions.add(Permission.POLICY);
        permissions.add(Permission.POLICY_MANAGE);
        accessMap.put(AccessLevel.READ, permissions);

        permissions = new HashSet<Permission>();
        permissions.add(Permission.POLICY_CREATE);
        permissions.add(Permission.POLICY_EDIT);
        accessMap.put(AccessLevel.WRITE, permissions);

        permissionMap.put(EntitlementService.SERVICE_NAME, accessMap);
    }

    public List<Permission> getPermissions(RealmBean realmBean) {
        List<Permission> permissions = new ArrayList<Permission>();

        for (String serviceName : serviceNames) {
            List<Permission> ps = getPermissions(realmBean, serviceName);
            permissions.addAll(ps);
        }

        return permissions;
    }

    private List<Permission> getPermissions(RealmBean realmBean, String serviceName) {
        boolean read = isAllowed(realmBean, PERMISSION_READ, serviceName);
        boolean write = isAllowed(realmBean, PERMISSION_MODIFY, serviceName);
        // TODO: null svc for delegation check?
        boolean delegate = isAllowed(realmBean, PERMISSION_DELEGATE, serviceName);

        List<Permission> permissions = new ArrayList<Permission>();

        if (read) {
            Map<AccessLevel, Set<Permission>> accessMap = permissionMap.get(serviceName);
            Set<Permission> p = accessMap.get(AccessLevel.READ);
            if (p != null) {
                permissions.addAll(p);
            }
        }
        if (write) {
            Map<AccessLevel, Set<Permission>> accessMap = permissionMap.get(serviceName);
            Set<Permission> p = accessMap.get(AccessLevel.WRITE);
            if (p != null) {
                permissions.addAll(p);
            }
        }
        if (delegate) {
            Map<AccessLevel, Set<Permission>> accessMap = permissionMap.get(serviceName);
            Set<Permission> p = accessMap.get(AccessLevel.DELEGATE);
            if (p != null) {
                permissions.addAll(p);
            }
        }

        return permissions;
    }

    private boolean isAllowed(RealmBean realmBean, String permission, String serviceName) {
        try {
            DelegationEvaluator de = new DelegationEvaluator();
            DelegationPermission dp = new DelegationPermission();
            dp.setVersion("*");
            dp.setSubConfigName("default");

            // TODO: access level?
            dp.setOrganizationName(realmBean.getName());

            Set<String> actions = Collections.singleton(permission);
            dp.setActions(actions);

            if (serviceName != null) {
                dp.setServiceName(serviceName);
            }

            SSOToken t = new Token().getSSOToken();

            boolean allowed = de.isAllowed(t, dp, Collections.EMPTY_MAP);
            return allowed;
        } catch (DelegationException de) {
            throw new RuntimeException(de);
        } catch (SSOException ssoe) {
            throw new RuntimeException(ssoe);
        }
    }
}
