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
        return policyCreateWizardBean.getResources().size();
    }

    public int getConditionCount() {
        int count = 0;

        // don't count operators or functions
        for (ViewCondition vc: policyCreateWizardBean.getViewConditions()) {
            if (!vc.getConditionType().isExpression()) {
                count++;
            }
        }

        return count;
    }

    public int getActionCount() {
        return policyCreateWizardBean.getActions().size();
    }

    public int getSubjectCount() {
        int count = 0;
        for (SubjectContainer sc: policyCreateWizardBean.getSubjectContainers()) {
            count += sc.getViewSubjects().size();
        }

        return count;
    }
}
