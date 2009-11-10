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
 * $Id: ReferralDao.java,v 1.5 2009-11-10 20:26:35 farble1670 Exp $
 */
package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.FilterHolder;
import com.sun.identity.admin.model.RealmBean;
import com.sun.identity.admin.model.RealmsBean;
import com.sun.identity.admin.model.ReferralBean;
import com.sun.identity.admin.model.ViewApplicationsBean;
import com.sun.identity.entitlement.ApplicationPrivilege.Action;
import com.sun.identity.entitlement.ApplicationPrivilegeManager;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ReferralPrivilege;
import com.sun.identity.entitlement.ReferralPrivilegeManager;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.entitlement.util.SearchFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;

public class ReferralDao implements Serializable {

    private int timeout;
    private int limit;

    private ApplicationPrivilegeManager getApplicationPrivilegeManager() {
        SSOToken t = new Token().getSSOToken();
        Subject s = SubjectUtils.createSubject(t);
        RealmBean realmBean = RealmsBean.getInstance().getRealmBean();
        ApplicationPrivilegeManager apm = ApplicationPrivilegeManager.getInstance(realmBean.getName(), s);

        return apm;
    }

    private ReferralPrivilegeManager getReferralPrivilegeManager() {
        RealmBean rb = RealmsBean.getInstance().getRealmBean();
        Subject adminSubject = new Token().getAdminSubject();

        ReferralPrivilegeManager rpm = new ReferralPrivilegeManager(rb.getName(), adminSubject);
        return rpm;
    }

    private String getPattern(String filter) {
        String pattern;
        if (filter == null || filter.length() == 0) {
            pattern = "*";
        } else {
            pattern = "*" + filter + "*";
        }

        return pattern;
    }

    private Set<SearchFilter> getPrivilegeSearchFilters(List<FilterHolder> filterHolders) {
        Set<SearchFilter> psfs = new HashSet<SearchFilter>();

        for (FilterHolder fh : filterHolders) {
            List<SearchFilter> l = fh.getViewFilter().getSearchFilters();
            if (l != null) {
                // TODO: list should never be null
                psfs.addAll(l);
            }
        }

        return psfs;
    }

    public ReferralBean getReferralBean(String referralName) {
        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();
        ApplicationPrivilegeManager apm = getApplicationPrivilegeManager();

        try {
            ReferralPrivilege rp = rpm.getReferral(referralName);
            boolean writable = apm.hasPrivilege(rp, Action.MODIFY);
            ReferralBean rb = new ReferralBean(
                    rp,
                    writable,
                    ViewApplicationsBean.getInstance().getViewApplications());

            return rb;
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public List<ReferralBean> getReferralBeans(String filter, List<FilterHolder> policyFilterHolders) {
        Set<SearchFilter> psfs = getPrivilegeSearchFilters(policyFilterHolders);
        String pattern = getPattern(filter);
        psfs.add(new SearchFilter(Privilege.NAME_ATTRIBUTE, pattern));

        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();
        ApplicationPrivilegeManager apm = getApplicationPrivilegeManager();
        List<ReferralBean> referralBeans = null;

        try {
            Set<String> referralNames;
            referralNames = rpm.searchReferralPrivilegeNames(psfs, limit, timeout);

            referralBeans = new ArrayList<ReferralBean>();
            for (String referralName : referralNames) {
                ReferralPrivilege rp = rpm.getReferral(referralName);
                boolean writable = apm.hasPrivilege(rp, Action.MODIFY);
                ReferralBean rb = new ReferralBean(
                        rp,
                        writable,
                        ViewApplicationsBean.getInstance().getViewApplications());
                referralBeans.add(rb);
            }
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }

        return referralBeans;

    }

    public void add(ReferralBean rb) {
        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();
        ReferralPrivilege rp = rb.toReferrealPrivilege();

        try {
            rpm.add(rp);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public void set(ReferralBean rb) {
        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();
        ReferralPrivilege rp = rb.toReferrealPrivilege();

        try {
            rpm.modify(rp);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public boolean referralExists(ReferralBean rb) {
        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();
        try {
            ReferralPrivilege rp = rpm.getReferral(rb.getName());
            return true;
        } catch (EntitlementException ee) {
            return false;
        }

    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void remove(String name) {
        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();

        try {
            rpm.delete(name);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public static ReferralDao getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        ReferralDao rdao = (ReferralDao) mbr.resolve("referralDao");
        return rdao;
    }
}
