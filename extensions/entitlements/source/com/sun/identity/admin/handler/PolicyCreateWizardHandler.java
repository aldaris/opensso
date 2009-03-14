package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.Application;
import com.sun.identity.admin.model.PolicyCreateWizardBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

public class PolicyCreateWizardHandler
        extends WizardHandler
        implements Serializable {

    @Override
    public String finishAction() {
        getWizardBean().reset();
        return "policy-created";
    }

    @Override
    public String cancelAction() {
        getWizardBean().reset();
        return "policy-create-canceled";
    }

    @Override
    public void nextListener(ActionEvent event) {
        super.nextListener(event);

        int step = getStep(event);
        switch (step) {
        }
    }

    public void applicationChanged(ValueChangeEvent event) {
        String name = (String) event.getNewValue();
        Application a = getPolicyCreateWizardBean().getApplications().get(name);
        getPolicyCreateWizardBean().setApplication(a);
    }

    private PolicyCreateWizardBean getPolicyCreateWizardBean() {
        return (PolicyCreateWizardBean)getWizardBean();
    }
}
