package com.sun.identity.admin.model;

public class StaticAttributesPolicySummary extends PolicySummary {

    public StaticAttributesPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Resource Attributes";
    }

    public String getValue() {
        int count = getPolicyWizardBean().getPrivilegeBean().getStaticAttributesBean().getViewAttributes().size();
        return Integer.toString(count);
    }

    public boolean isExpandable() {
        int count = getPolicyWizardBean().getPrivilegeBean().getStaticAttributesBean().getViewAttributes().size();
        return count > 0;
    }

    public String getIcon() {
        return "../image/device.png";
    }

    public String getTemplate() {
        return "/admin/facelet/template/policy-summary-static-attributes.xhtml";
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.ADVANCED;
    }

    @Override
    public PolicyWizardAdvancedTabIndex getAdvancedTabIndex() {
        return PolicyWizardAdvancedTabIndex.RESOURCE_ATTRIBUTES;
    }
}
