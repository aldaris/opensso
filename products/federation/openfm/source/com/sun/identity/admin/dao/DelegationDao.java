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
 * $Id: DelegationDao.java,v 1.2 2009-11-02 22:30:50 farble1670 Exp $
 */
package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.DelegationBean;
import com.sun.identity.admin.model.RealmBean;
import com.sun.identity.admin.model.RealmsBean;
import com.sun.identity.admin.model.SubjectType;
import com.sun.identity.entitlement.Application;
import com.sun.identity.entitlement.ApplicationManager;
import com.sun.identity.entitlement.ApplicationPrivilege;
import com.sun.identity.entitlement.ApplicationPrivilegeManager;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import javax.security.auth.Subject;
import java.util.List;

public class DelegationDao implements Serializable {
    public List<SubjectType> getSubjectTypes() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        Map<String, SubjectType> viewSubjectToSubjectTypeMap =
                (Map<String, SubjectType>) mbr.resolve("viewSubjectToSubjectTypeMap");

        Token token = new Token();
        Subject adminSubject = token.getAdminSubject();
        RealmBean realmBean = RealmsBean.getInstance().getRealmBean();
        List<SubjectType> subjectTypes = new ArrayList<SubjectType>();

        Application a = ApplicationManager.getApplication(adminSubject, realmBean.getName(), "sunAMDelegationService");
        if (a.getResources() == null || a.getResources().size() == 0) {
            return subjectTypes;
        }

        for (String ess: a.getSubjects()) {
            SubjectType st = viewSubjectToSubjectTypeMap.get(ess);
            subjectTypes.add(st);
        }

        return subjectTypes;
    }


    private ApplicationPrivilegeManager getApplicationPrivilegeManager() {
        SSOToken t = new Token().getSSOToken();
        Subject s = SubjectUtils.createSubject(t);
        RealmBean realmBean = RealmsBean.getInstance().getRealmBean();
        ApplicationPrivilegeManager apm = ApplicationPrivilegeManager.getInstance(realmBean.getName(), s);

        return apm;
    }

    public void set(DelegationBean db) {
        if (exists(db)) {
            modify(db);
        } else {
            add(db);
        }
    }

    public boolean exists(DelegationBean db) {
        return exists(db.getName());
    }

    public boolean exists(String name) {
        ApplicationPrivilegeManager apm = getApplicationPrivilegeManager();

        try {
            return (apm.getPrivilege(name) != null);
        } catch (EntitlementException ee) {
            return false;
        }
    }

    public void add(DelegationBean db) {
        ApplicationPrivilegeManager apm = getApplicationPrivilegeManager();

        try {
            apm.addPrivilege(db.toApplicationPrivilege());
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }


    public void modify(DelegationBean db) {
        ApplicationPrivilegeManager apm = getApplicationPrivilegeManager();

        try {
            apm.replacePrivilege(db.toApplicationPrivilege());
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public static DelegationDao getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        DelegationDao ddao = (DelegationDao) mbr.resolve("delegationDao");
        return ddao;
    }
}
