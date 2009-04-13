package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.admin.model.ViewApplicationsBean;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;

public class PolicyDao implements Serializable {
    private ViewApplicationsBean viewApplicationsBean;

    public List<PrivilegeBean> getPrivilegeBeans() {
        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        List<PrivilegeBean> privilegeBeans = null;

        try {
            Set<String> privilegeNames = pm.getPrivilegeNames();
            privilegeBeans = new ArrayList<PrivilegeBean>();
            for (String privilegeName : privilegeNames) {
                Privilege p = pm.getPrivilege(privilegeName);
                PrivilegeBean pb = new PrivilegeBean(p, viewApplicationsBean.getViewApplications());
                privilegeBeans.add(pb);
            }
        } catch (EntitlementException ee) {
            // TODO: handle exception
            ee.printStackTrace();
            return null;
        }

        return privilegeBeans;

    }

    public void setViewApplicationsBean(ViewApplicationsBean viewApplicationsBean) {
        this.viewApplicationsBean = viewApplicationsBean;
    }
}
