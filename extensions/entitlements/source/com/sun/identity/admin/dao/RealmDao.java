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
        return getRealmBeans("/", "");
    }

    public List<RealmBean> getRealmBeans(String base, String filter) {
        String pattern = getPattern(filter);
        if ((base == null) || (base.length() == 0)) {
            base = getStartDN();
        }

        try {
            SSOToken ssot = new Token().getSSOToken();
            OrganizationConfigManager orgMgr = new OrganizationConfigManager(ssot, base);
            Set<String> names = orgMgr.getSubOrganizationNames(pattern, true);
            List<RealmBean> realmBeans = new ArrayList<RealmBean>();
            RealmBean baseRb = new RealmBean();
            baseRb.setName("/");
            realmBeans.add(baseRb);
            for (String name: names) {
                RealmBean rb = new RealmBean();
                rb.setName("/"+name);
                realmBeans.add(rb);
            }
            return realmBeans;
        } catch (SMSException smse) {
            throw new RuntimeException(smse);
        }
    }

    public String getStartDN() {
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
