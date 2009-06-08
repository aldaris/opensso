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
 * $Id: ViewId.java,v 1.2 2009-06-08 21:02:02 farble1670 Exp $
 */

package com.sun.identity.admin.model;

import java.util.HashMap;
import java.util.Map;

public enum ViewId {
    HOME("/admin/facelet/home.xhtml"),
    POLICY("/admin/facelet/policy.xhtml"),
    POLICY_CREATE("/admin/facelet/policy-create.xhtml"),
    POLICY_MANAGE("/admin/facelet/policy-manage.xhtml"),
    POLICY_EDIT("/admin/facelet/policy-edit.xhtml"),
    REFERRAL_CREATE("/admin/facelet/referral-create.xhtml"),
    NEWS("/admin/facelet/news.xhtml"),
    PERMISSION_DENIED("/admin/facelet/permission-denied.xhtml");

    private static final Map<String, ViewId> idValues = new HashMap<String, ViewId>() {
        {
            put(HOME.getId(), HOME);
            put(POLICY.getId(), POLICY);
            put(POLICY_CREATE.getId(), POLICY_CREATE);
            put(POLICY_MANAGE.getId(), POLICY_MANAGE);
            put(POLICY_EDIT.getId(), POLICY_EDIT);
            put(REFERRAL_CREATE.getId(), REFERRAL_CREATE);
            put(NEWS.getId(), NEWS);
            put(PERMISSION_DENIED.getId(), PERMISSION_DENIED);
        }
    };

    private String id;

    ViewId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ViewId valueOfId(String id) {
        ViewId vid = idValues.get(id);
        return vid;
    }

    public Permission getPermission() {
        return Permission.valueOf(this.toString());
    }
}
