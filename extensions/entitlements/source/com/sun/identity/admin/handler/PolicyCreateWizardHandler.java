package com.sun.identity.admin.handler;

import java.io.Serializable;

public class PolicyCreateWizardHandler extends WizardHandler implements Serializable {
    @Override
    public String finishAction() {
        return "policy-created";
    }

    @Override
    public String cancelAction() {
        getWizardBean().reset();
        return "policy-create-canceled";
    }
}
