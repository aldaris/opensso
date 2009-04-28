package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.ConditionTypeFactory;
import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.admin.model.SubjectFactory;
import com.sun.identity.admin.model.ViewApplicationsBean;
import com.sun.identity.entitlement.Application;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;

public class PolicyDao implements Serializable {

    private ViewApplicationsBean viewApplicationsBean;
    private ConditionTypeFactory conditionTypeFactory;
    private SubjectFactory subjectFactory;


    private String getPattern(String filter) {
        String pattern;
        if (filter == null || filter.length() == 0) {
            pattern = "*";
        } else {
            pattern = "*" + filter + "*";
        }

        return pattern;
    }

    public List<PrivilegeBean> getPrivilegeBeans() {
        return getPrivilegeBeans(null);
    }

    public List<PrivilegeBean> getPrivilegeBeans(String filter) {
        String pattern = getPattern(filter);

        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        List<PrivilegeBean> privilegeBeans = null;

        try {
            Set<String> privilegeNames = pm.getPrivilegeNames(pattern);
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

    public List<String> getPrivilegeNames() {
        return getPrivilegeNames(null);
    }

    public List<String> getPrivilegeNames(String filter) {
        String pattern = getPattern(filter);

        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        List<PrivilegeBean> privilegeBeans = null;

        Set<String> privilegeNames = null;
        try {
            privilegeNames = pm.getPrivilegeNames(pattern);
        } catch (EntitlementException ee) {
            // TODO: handle exception
            ee.printStackTrace();
            return null;
        }

        return new ArrayList<String>(privilegeNames);

    }

    private PrivilegeManager getPrivilegeManager() {
        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        return pm;
    }

    public void removePrivilege(String name) {
        PrivilegeManager pm = getPrivilegeManager();

        try {
            pm.removePrivilege(name);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public boolean privilegeExists(Privilege p) {
        return privilegeExists(p.getName());
    }

    public boolean privilegeExists(String name) {
        PrivilegeManager pm = getPrivilegeManager();

        try {
            return (pm.getPrivilege(name) != null);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public void setPrivilege(Privilege p) {
        validateAction(p.getEntitlement());
        if (privilegeExists(p)) {
            modifyPrivilege(p);
        } else {
            addPrivilege(p);
        }
    }

    public void addPrivilege(Privilege p) {
        PrivilegeManager pm = getPrivilegeManager();

        try {
            pm.addPrivilege(p);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    private void validateAction(Entitlement e) {
        Application app = e.getApplication();
        Set<String> validActionName = app.getActions().keySet();

        Map<String, Boolean> actionValues = e.getActionValues();
        for (String actionName : actionValues.keySet()) {
            if (!validActionName.contains(actionName)) {
                app.addAction(actionName, actionValues.get(actionName));
            }
        }
    }

    public void modifyPrivilege(Privilege p) {
        PrivilegeManager pm = getPrivilegeManager();

        try {
            pm.modifyPrivilege(p);
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
