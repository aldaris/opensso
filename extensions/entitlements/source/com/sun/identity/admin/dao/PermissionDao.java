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
 * $Id: PermissionDao.java,v 1.4 2009-06-09 22:40:37 farble1670 Exp $
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

    private static final String ENTITLEMENT_SERVICE_NAME = "openssoEntitlement";
    private static final Map<String,Set<Permission>> permissionMap = new HashMap<String,Set<Permission>>();

    static {
        Set<Permission> readPermissions = new HashSet<Permission>();
        readPermissions.add(Permission.HOME);
        readPermissions.add(Permission.NEWS);
        readPermissions.add(Permission.POLICY);
        readPermissions.add(Permission.POLICY_MANAGE);

        Set<Permission> writePermissions = new HashSet<Permission>();
        writePermissions.add(Permission.POLICY_CREATE);
        writePermissions.add(Permission.POLICY_EDIT);

        Set<Permission> delegatePermissions = new HashSet<Permission>();
        delegatePermissions.add(Permission.REFERRAL_CREATE);

        permissionMap.put(PERMISSION_READ, readPermissions);
        permissionMap.put(PERMISSION_MODIFY, writePermissions);
        permissionMap.put(PERMISSION_DELEGATE, delegatePermissions);
    }

    public List<Permission> getPermissions(RealmBean realmBean) {
        boolean read = isAllowed(realmBean, PERMISSION_READ, ENTITLEMENT_SERVICE_NAME);
        boolean write = isAllowed(realmBean, PERMISSION_MODIFY, ENTITLEMENT_SERVICE_NAME);
        // TODO: null svc?
        boolean delegate = isAllowed(realmBean, PERMISSION_DELEGATE, null);

        List<Permission> permissions = new ArrayList<Permission>();

        if (read) {
            permissions.addAll(permissionMap.get(PERMISSION_READ));
        }
        if (write) {
            permissions.addAll(permissionMap.get(PERMISSION_MODIFY));
        }
        if (delegate) {
            permissions.addAll(permissionMap.get(PERMISSION_DELEGATE));
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
