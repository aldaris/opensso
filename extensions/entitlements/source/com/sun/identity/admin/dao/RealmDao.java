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
 * $Id: RealmDao.java,v 1.3 2009-06-04 11:49:11 veiming Exp $
 */

package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.RealmBean;
import com.sun.identity.common.Constants;
import com.sun.identity.sm.DNMapper;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.SMSException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RealmDao implements Serializable {

    private String getPattern(String filter) {
        String pattern;
        if (filter == null || filter.length() == 0) {
            pattern = "*";
        } else {
            pattern = "*" + filter + "*";
        }

        return pattern;
    }

    public List<RealmBean> getRealmBeans() {
        return getRealmBeans(null, "");
    }

    public List<RealmBean> getRealmBeans(String base, String filter) {
        String pattern = getPattern(filter);
        if ((base == null) || (base.length() == 0)) {
            base = getBaseRealmName();
        }

        try {
            SSOToken ssot = new Token().getSSOToken();
            OrganizationConfigManager orgMgr = new OrganizationConfigManager(ssot, base);
            Set<String> names = orgMgr.getSubOrganizationNames(pattern, true);
            List<RealmBean> realmBeans = new ArrayList<RealmBean>();
            realmBeans.add(getBaseRealmBean());
            for (String name: names) {
                RealmBean rb = new RealmBean();
                rb.setName(base+name);
                realmBeans.add(rb);
            }
            return realmBeans;
        } catch (SMSException smse) {
            throw new RuntimeException(smse);
        }
    }

    public RealmBean getBaseRealmBean() {
        RealmBean rb = new RealmBean();
        rb.setName(getBaseRealmName());
        return rb;
    }

    public String getBaseRealmName() {
        String startDN = "/";
        try {
            Token t = new Token();
            SSOToken ssot = t.getSSOToken();
            String org = ssot.getProperty(Constants.ORGANIZATION);
            startDN = DNMapper.orgNameToRealmName(org);
        } catch (SSOException ssoe) {
            throw new RuntimeException(ssoe);
        }
        return startDN;
    }
}
