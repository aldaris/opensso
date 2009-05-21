package com.sun.identity.admin.model;

public class ResourcesPolicySummary extends PolicySummary {

    public ResourcesPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Resources";
    }

    public String getValue() {
        int resCount = getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getResources().size();
        int exCount = getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getExceptions().size();

        // TODO: localize
        return Integer.toString(resCount+exCount);
    }

    public boolean isExpandable() {
        int resCount = getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getResources().size();
        int exCount = getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getExceptions().size();

        return  resCount+exCount > 0;
    }

    public String getIcon() {
        return "../image/url.png";
    }

    public String getTemplate() {
        return "/admin/facelet/template/policy-summary-resources.xhtml";
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.RESOURCES;
    }

}
