package com.sun.identity.admin.handler;

public class PolicyCreateWizardHandler extends WizardHandler {
    @Override
    public String finishAction() {
        return "policy-created";
    }
}
