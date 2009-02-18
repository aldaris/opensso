package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.WizardBean;
import com.sun.identity.admin.model.WizardStepBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class WizardHandler implements Serializable {
    private WizardBean wizardBean = null;

    public WizardBean getWizardBean() {
        return wizardBean;
    }

    public void setWizardBean(WizardBean wizardBean) {
        this.wizardBean = wizardBean;
    }

    private int getStep(ActionEvent event) {
        String val = (String) event.getComponent().getAttributes().get("step");
        int step = Integer.parseInt(val);

        return step;
    }

    private int getSteps(ActionEvent event) {
        String val = (String) event.getComponent().getAttributes().get("steps");
        int steps = Integer.parseInt(val);

        return steps;
    }

    public void expandListener(ActionEvent event) {
        int step = getStep(event);
        int steps = getSteps(event);

        assert(step <= steps-1);
        
        for (int i = 0; i < steps; i++) {
            WizardStepBean ws = getWizardBean().getWizardStepBeans()[i];
            if (i != step) {
                ws.setExpanded(false);
            }
        }
    }

    public void nextListener(ActionEvent event) {
        int step = getStep(event);
        int steps = getSteps(event);

        assert (step <= steps-1);

        WizardStepBean current = getWizardBean().getWizardStepBeans()[step];
        current.setExpanded(false);

        WizardStepBean next = getWizardBean().getWizardStepBeans()[step+1];
        next.setEnabled(true);
        next.setExpanded(true);
    }

    public void previousListener(ActionEvent event) {
        int step = getStep(event);
        int steps = getSteps(event);

        assert (step != 0);
        assert (step <= steps-1);

        WizardStepBean current = getWizardBean().getWizardStepBeans()[step];
        current.setExpanded(false);

        WizardStepBean previous = getWizardBean().getWizardStepBeans()[step-1];
        previous.setExpanded(true);
    }

    public String finishAction() {
        return "finish";
    }
}
