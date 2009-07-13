package com.sun.identity.admin.model;

public abstract class WizardStepValidator {
    private WizardBean wizardBean;

    public WizardStepValidator(WizardBean wizardBean) {
        this.wizardBean = wizardBean;
    }

    public WizardBean getWizardBean() {
        return wizardBean;
    }

    public void setWizardBean(WizardBean wizardBean) {
        this.wizardBean = wizardBean;
    }

    public abstract boolean validate();

    public MessagesBean getMessagesBean() {
        return MessagesBean.getInstance();
    }
}
