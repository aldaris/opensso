package com.sun.identity.admin.model;

import java.io.Serializable;

public class PolicyCreateSummary implements Serializable {
    private PolicyCreateWizardBean policyCreateWizardBean;

    public PolicyCreateWizardBean getPolicyCreateWizardBean() {
        return policyCreateWizardBean;
    }

    public void setPolicyCreateWizardBean(PolicyCreateWizardBean policyCreateWizardBean) {
        this.policyCreateWizardBean = policyCreateWizardBean;
    }

    public int getResourceCount() {
        return policyCreateWizardBean.getPrivilegeBean().getViewEntitlement().getResources().size();
    }

    public int getViewConditionSize() {
        Tree ct = new Tree(policyCreateWizardBean.getPrivilegeBean().getViewCondition());
        int count = ct.size();
        return count;
    }

    public int getActionsSize() {
        return policyCreateWizardBean.getPrivilegeBean().getViewEntitlement().getActions().size();
    }

    public int getViewSubjectSize() {
        Tree subjectTree = new Tree(policyCreateWizardBean.getPrivilegeBean().getViewSubject());
        int count = subjectTree.size();

        return count;
    }
}
