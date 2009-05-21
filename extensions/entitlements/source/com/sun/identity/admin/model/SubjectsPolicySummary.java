package com.sun.identity.admin.model;

public class SubjectsPolicySummary extends PolicySummary {

    public SubjectsPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public String getLabel() {
        // TODO: localize
        return "Subjects";
    }

    public String getValue() {
        return Integer.toString(getPolicyWizardBean().getPrivilegeBean().getViewSubject().getSizeLeafs());
    }

    public String getIcon() {
        return "../image/role.png";
    }

    public String getTemplate() {
        return "/admin/facelet/template/policy-summary-subjects.xhtml";
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.SUBJECTS;
    }

}
