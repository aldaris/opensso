package com.sun.identity.admin.model;

public class ActionsPolicySummary extends PolicySummary {

    public ActionsPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Actions";
    }

    public String getValue() {
        int count = getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getBooleanActionsBean().getActions().size();
        return Integer.toString(count);
    }

    public boolean isExpandable() {
        int count = getPolicyWizardBean().getPrivilegeBean().getViewEntitlement().getBooleanActionsBean().getActions().size();
        return count > 0;
    }

    public String getIcon() {
        return "../image/action.png";
    }

    public String getTemplate() {
        return "/admin/facelet/template/policy-summary-actions.xhtml";
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.ADVANCED;
    }

    @Override
    public PolicyWizardAdvancedTabIndex getAdvancedTabIndex() {
        return PolicyWizardAdvancedTabIndex.ACTIONS;
    }
}
