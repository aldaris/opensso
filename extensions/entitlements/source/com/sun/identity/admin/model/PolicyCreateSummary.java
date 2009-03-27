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
        return policyCreateWizardBean.getPriviligeBean().getResources().size();
    }

    public int getConditionCount() {
        int count = 0;

        // don't count operators or functions
        for (ViewCondition vc: policyCreateWizardBean.getPriviligeBean().getViewConditions()) {
            if (!vc.getConditionType().isExpression()) {
                count++;
            }
        }

        return count;
    }

    public int getActionCount() {
        return policyCreateWizardBean.getPriviligeBean().getActions().size();
    }

    public int getSubjectCount() {
        int count = 0;
        for (SubjectContainer sc: policyCreateWizardBean.getPriviligeBean().getSubjectContainers()) {
            count += sc.getViewSubjects().size();
        }

        return count;
    }
}
