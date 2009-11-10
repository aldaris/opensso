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
 * $Id: PermissionDao.java,v 1.9 2009-11-10 19:29:05 farble1670 Exp $
 */
package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.AccessLevel;
import com.sun.identity.admin.model.Permission;
import com.sun.identity.admin.model.RealmBean;
import com.sun.identity.delegation.DelegationEvaluator;
import com.sun.identity.delegation.DelegationException;
import com.sun.identity.delegation.DelegationPermission;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissionDao implements Serializable {
    private static final Map<String, Map<AccessLevel, Set<Permission>>> permissionMap;

    static {
        permissionMap = new PermissionMapReader().read();
    }

    public List<Permission> getPermissions(RealmBean realmBean) {
        List<Permission> permissions = new ArrayList<Permission>();

        for (String app : permissionMap.keySet()) {
            List<Permission> ps = getPermissions(realmBean, app);
            permissions.addAll(ps);
        }

        return permissions;
    }

    private List<Permission> getPermissions(RealmBean realmBean, String app) {
        List<Permission> permissions = new ArrayList<Permission>();
        Map<AccessLevel, Set<Permission>> accessMap = permissionMap.get(app);
        for (AccessLevel ac : AccessLevel.values()) {
            boolean allowed = isAllowed(realmBean, ac, app);
            if (allowed) {
                Set<Permission> ps = accessMap.get(ac);
                if (ps != null) {
                    permissions.addAll(ps);
                }
            }
        }

        return permissions;
    }

    private boolean isAllowed(RealmBean realmBean, AccessLevel accessLevel, String app) {
        try {
            DelegationEvaluator de = new DelegationEvaluator();
            DelegationPermission dp = new DelegationPermission();
            dp.setVersion("1.0");
            dp.setSubConfigName("default");
            dp.setOrganizationName(realmBean.getName());

            Set<String> actions = Collections.singleton(accessLevel.getValue());
            dp.setActions(actions);

            if (!app.equals("_realm")) {
                dp.setServiceName(app);
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
