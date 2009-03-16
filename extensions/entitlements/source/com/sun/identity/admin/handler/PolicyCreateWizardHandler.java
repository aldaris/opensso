package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.Action;
import com.sun.identity.admin.model.Application;
import com.sun.identity.admin.model.PolicyCreateWizardBean;
import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.SubjectContainer;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

public class PolicyCreateWizardHandler
        extends WizardHandler
        implements Serializable {

    @Override
    public String finishAction() {
        PolicyCreateWizardBean pcwb = getPolicyCreateWizardBean();

        String name = pcwb.getName();
        String description = pcwb.getDescription();

        List<ViewSubject> viewSubjects = new ArrayList<ViewSubject>();
        for (SubjectContainer sc: pcwb.getSelectedSubjectContainers()) {
            List<ViewSubject> vs = sc.getViewSubjects();
            viewSubjects.addAll(vs);
        }

        List<Resource> resources = pcwb.getSelectedResources();
        List<Action> actions = pcwb.getActions();

        // TODO: create entitlement objects & save them

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
