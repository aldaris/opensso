package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.ConditionTypeFactory;
import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.admin.model.SubjectFactory;
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
    private ConditionTypeFactory conditionTypeFactory;
    private SubjectFactory subjectFactory;

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
                PrivilegeBean pb = new PrivilegeBean(
                        p,
                        viewApplicationsBean.getViewApplications(),
                        subjectFactory,
                        conditionTypeFactory);
                privilegeBeans.add(pb);
            }
        } catch (EntitlementException ee) {
            // TODO: handle exception
            ee.printStackTrace();
            return null;
        }

        return privilegeBeans;

    }

    public void removePrivilege(String name) {
        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        try {
            pm.removePrivilege(name);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public void addPrivilege(Privilege p) {
        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        try {
            pm.addPrivilege(p);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public void setViewApplicationsBean(ViewApplicationsBean viewApplicationsBean) {
        this.viewApplicationsBean = viewApplicationsBean;
    }

    public void setConditionTypeFactory(ConditionTypeFactory conditionTypeFactory) {
        this.conditionTypeFactory = conditionTypeFactory;
    }

    public void setSubjectFactory(SubjectFactory subjectFactory) {
        this.subjectFactory = subjectFactory;
    }
}
