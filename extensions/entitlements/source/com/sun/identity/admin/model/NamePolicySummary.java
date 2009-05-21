package com.sun.identity.admin.model;

public class NamePolicySummary extends PolicySummary {

    public NamePolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Name";
    }

    public String getValue() {
        return getPolicyWizardBean().getPrivilegeBean().getName();
    }

    public boolean isExpandable() {
        return false;
    }

    public String getIcon() {
        // TODO
        return "../image/edit.png";
    }

    public String getTemplate() {
        return null;
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.NAME;
    }
}
