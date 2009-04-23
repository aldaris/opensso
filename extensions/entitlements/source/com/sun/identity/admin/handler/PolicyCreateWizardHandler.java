package com.sun.identity.admin.handler;

public class PolicyCreateWizardHandler extends PolicyWizardHandler {
    public String getFinishAction() {
        return "home";
    }

    public String getCancelAction() {
        return "home";
    }
}
