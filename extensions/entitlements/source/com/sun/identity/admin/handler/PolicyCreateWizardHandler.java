package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Appear;
import com.sun.identity.admin.model.Application;
import com.sun.identity.admin.model.ConditionType;
import com.sun.identity.admin.model.PolicyCreateWizardBean;
import com.sun.identity.admin.model.SubjectContainer;
import com.sun.identity.admin.model.SubjectContainerType;
import com.sun.identity.admin.model.ViewCondition;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.PrivilegeManager;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.security.auth.Subject;

public class PolicyCreateWizardHandler
        extends WizardHandler
        implements Serializable {

    @Override
    public String finishAction() {
        PolicyCreateWizardBean pcwb = getPolicyCreateWizardBean();
        Privilege privilege = pcwb.getPrivilegeBean().toPrivilige();
 
        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PrivilegeManager pm = PrivilegeManager.getInstance(authSubject);

        try {
            pm.addPrivilege(privilege);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        pcwb.reset();
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

    public void conditionDropListener(DropEvent dropEvent) {
        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            ConditionType ct = (ConditionType) dropEvent.getTargetDragValue();

            // TODO: implicit or?

            ViewCondition vc = ct.newViewCondition();
            getPolicyCreateWizardBean().getPrivilegeBean().getViewConditions().add(vc);

            Effect e = new Appear();
            e.setTransitory(true);
            e.setSubmit(true);
            getPolicyCreateWizardBean().setDropConditionEffect(e);
        }
    }

    public void subjectContainerDropListener(DropEvent dropEvent) {
        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            SubjectContainerType sct = (SubjectContainerType) dropEvent.getTargetDragValue();
            assert (sct != null);

            SubjectContainer sc = sct.newSubjectContainer();
            getPolicyCreateWizardBean().getPrivilegeBean().getSubjectContainers().add(sc);

            Effect e = new Appear();
            e.setTransitory(true);
            e.setSubmit(true);
            getPolicyCreateWizardBean().setDropSubjectContainerEffect(e);
        }
    }

    public void applicationChanged(ValueChangeEvent event) {
        String name = (String) event.getNewValue();
        Application a = getPolicyCreateWizardBean().getApplications().get(name);
        getPolicyCreateWizardBean().setApplication(a);
    }

    private PolicyCreateWizardBean getPolicyCreateWizardBean() {
        return (PolicyCreateWizardBean) getWizardBean();
    }

    protected int getGotoAdvancedTabIndex(ActionEvent event) {
        String i = (String) event.getComponent().getAttributes().get("gotoAdvancedTabIndex");
        int index = Integer.parseInt(i);

        return index;
    }

    @Override
    public void gotoStepListener(ActionEvent event) {
        super.gotoStepListener(event);

        // TODO, enumerate the tabs
        if (getGotoStep(event) == 3) {
            int i = getGotoAdvancedTabIndex(event);
            getPolicyCreateWizardBean().setAdvancedTabsetIndex(i);
        }
    }
}
