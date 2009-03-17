package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.Action;
import com.sun.identity.admin.model.Application;
import com.sun.identity.admin.model.PolicyCreateWizardBean;
import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.SubjectContainer;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.OrSubject;
import com.sun.identity.entitlement.Privilige;
import com.sun.identity.entitlement.PriviligeManager;
import com.sun.identity.entitlement.ResourceAttributes;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.security.auth.Subject;

public class PolicyCreateWizardHandler
        extends WizardHandler
        implements Serializable {

    @Override
    public String finishAction() {
        PolicyCreateWizardBean pcwb = getPolicyCreateWizardBean();

        String name = pcwb.getName();
        // TODO: where do we set the description
        String description = pcwb.getDescription();

        Set<EntitlementSubject> eSubjects = new HashSet<EntitlementSubject>();
        for (SubjectContainer sc : pcwb.getSelectedSubjectContainers()) {
            List<ViewSubject> viewSubjects = sc.getViewSubjects();
            for (ViewSubject vs : viewSubjects) {
                eSubjects.add(vs.getSubject());
            }
        }
        EntitlementSubject orSubject = new OrSubject(eSubjects);


        List<Action> actions = pcwb.getActions();
        List<Resource> resources = pcwb.getSelectedResources();
        Set<Entitlement> entitlements = new HashSet<Entitlement>();
        for (Resource r: resources) {
            Entitlement e = r.getEntitlement(actions);
            entitlements.add(e);
        }

        EntitlementCondition eCondition = null;
        
        Set<ResourceAttributes> attrs = null;
        
        Privilige privilige = new Privilige(
                name,
                entitlements,
                orSubject,
                eCondition,
                attrs);

        // TODO: add SSO token to public credentials
        Subject authSubject = new Subject();
        PriviligeManager pm = PriviligeManager.getInstance(authSubject);

        try {
            pm.addPrivilige(privilige);
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

    public void applicationChanged(ValueChangeEvent event) {
        String name = (String) event.getNewValue();
        Application a = getPolicyCreateWizardBean().getApplications().get(name);
        getPolicyCreateWizardBean().setApplication(a);
    }

    private PolicyCreateWizardBean getPolicyCreateWizardBean() {
        return (PolicyCreateWizardBean) getWizardBean();
    }
}
