package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.PolicyManageBean;
import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.security.auth.Subject;

public class PolicyManageHandler implements Serializable {

    private PolicyManageBean policyManageBean;

    public PolicyManageBean getPolicyManageBean() {
        return policyManageBean;
    }

    public void setPolicyManageBean(PolicyManageBean policyManageBean) {
        this.policyManageBean = policyManageBean;
        policyManageBean.setPrivilegeBeans(loadPrivilegeBeans());

    }

    private List<PrivilegeBean> loadPrivilegeBeans() {
        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        List<PrivilegeBean> privilegeBeans = null;

        try {
            Set<String> privilegeNames = pm.getPrivilegeNames();
            privilegeBeans = new ArrayList<PrivilegeBean>();
            for (String privilegeName : privilegeNames) {
                Privilege p = pm.getPrivilege(privilegeName);
                PrivilegeBean pb = new PrivilegeBean(p);
                privilegeBeans.add(pb);
            }
        } catch (EntitlementException ee) {
            // TODO: handle exception
            ee.printStackTrace();
            return null;
        }

        return privilegeBeans;
    }
}
