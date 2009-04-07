package com.sun.identity.admin.model;

public class PolicyCreateSummary {
    private PolicyCreateWizardBean policyCreateWizardBean;

    public PolicyCreateWizardBean getPolicyCreateWizardBean() {
        return policyCreateWizardBean;
    }

    public void setPolicyCreateWizardBean(PolicyCreateWizardBean policyCreateWizardBean) {
        this.policyCreateWizardBean = policyCreateWizardBean;
    }

    public int getResourceCount() {
        return policyCreateWizardBean.getPrivilegeBean().getResources().size();
    }

    public int getViewConditionSize() {
        Tree ct = new Tree(policyCreateWizardBean.getPrivilegeBean().getViewCondition());
        int count = ct.size();
        return count;
    }

    public int getActionCount() {
        return policyCreateWizardBean.getPrivilegeBean().getActions().size();
    }

    public int getSubjectCount() {
        int count = 0;

        // TODO: SubjectTree.size()

        return count;
    }
}
