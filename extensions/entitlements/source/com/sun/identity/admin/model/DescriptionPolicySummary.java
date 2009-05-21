package com.sun.identity.admin.model;

public class DescriptionPolicySummary extends PolicySummary {

    public DescriptionPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Description";
    }

    public String getValue() {
        return getPolicyWizardBean().getPrivilegeBean().getDescription();
    }

    public boolean isExpandable() {
        return false;
    }

    public String getIcon() {
        // TODO
        return "../image/description.png";
    }

    public String getTemplate() {
        return null;
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.NAME;
    }

}
