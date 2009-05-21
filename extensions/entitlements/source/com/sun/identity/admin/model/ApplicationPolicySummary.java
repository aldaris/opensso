package com.sun.identity.admin.model;

public class ApplicationPolicySummary extends PolicySummary {

    public ApplicationPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Application";
    }

    public String getValue() {
        return getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getViewApplication().getTitle();
    }

    public String getIcon() {
        // TODO
        return "../image/application.png";
    }

    public String getTemplate() {
        return null;
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.NAME;
    }

}
