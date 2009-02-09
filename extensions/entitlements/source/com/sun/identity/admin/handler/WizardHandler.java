package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.WizardBean;
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

    public void toggle(ActionEvent event) {
       String val = (String)event.getComponent().getAttributes().get("step");
       int step = Integer.parseInt(val);

       // toggle
       getWizardBean().getActive()[step] = !getWizardBean().getActive()[step];
    }
}
