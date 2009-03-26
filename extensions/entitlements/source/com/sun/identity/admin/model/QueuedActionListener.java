package com.sun.identity.admin.model;

import com.sun.identity.admin.model.QueuedActionBean;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class QueuedActionListener implements PhaseListener {
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    public void beforePhase(PhaseEvent pe) {
        checkForOperations(true, pe);
    }

    public void afterPhase(PhaseEvent pe) {
        checkForOperations(false, pe);
    }

    private void checkForOperations(boolean doBeforePhase, PhaseEvent evt) {
        FacesContext fc = FacesContext.getCurrentInstance();
        QueuedActionBean qab = (QueuedActionBean)fc.getApplication().createValueBinding("#{queuedActionBean}").getValue(fc);
        List<PhaseEventAction> invoked = new ArrayList<PhaseEventAction>();
        for (PhaseEventAction pea : qab.getPhaseEventActions()) {
            if (pea.getPhaseId() == evt.getPhaseId()) {
                if (pea.isDoBeforePhase() == doBeforePhase) {
                    javax.faces.application.Application a = fc.getApplication();
                    MethodBinding mb = a.createMethodBinding(pea.getAction(), pea.getParameters());
                    if (mb != null) {
                        mb.invoke(fc, pea.getArguments());
                        invoked.add(pea);
                    }
                }
            }
        }
        qab.getPhaseEventActions().removeAll(invoked);
    }
}