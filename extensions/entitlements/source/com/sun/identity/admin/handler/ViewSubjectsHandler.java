package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Fade;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.model.MultiPanelBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.admin.model.QueuedActionBean;
import com.sun.identity.admin.model.Tree;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

public class ViewSubjectsHandler implements MultiPanelHandler, Serializable {

    private PrivilegeBean privilegeBean;
    private QueuedActionBean queuedActionBean;

    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (ViewSubject) event.getComponent().getAttributes().get("bean");
        assert (mpb != null);

        Effect e;
        if (mpb.isExpanded()) {
            e = new SlideUp();
        } else {
            e = new SlideDown();
        }

        e.setTransitory(false);
        e.setSubmit(true);
        mpb.setExpandEffect(e);
    }

    public void removeListener(ActionEvent event) {
        ViewSubject viewSubject = (ViewSubject) event.getComponent().getAttributes().get("bean");
        assert (viewSubject != null);

        Effect e = new Fade();
        e.setSubmit(true);
        e.setTransitory(false);
        viewSubject.setPanelEffect(e);

        addRemoveAction(viewSubject);
    }

    public void handleRemove(ViewSubject vs) {
        Tree subjectTree = new Tree(privilegeBean.getViewSubject());
        ViewSubject rootVs = (ViewSubject)subjectTree.remove(vs);
        privilegeBean.setViewSubject(rootVs);

    }

    private void addRemoveAction(ViewSubject vs) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(false);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{viewSubjectsHandler.handleRemove}");
        pea.setParameters(new Class[] { ViewSubject.class });
        pea.setArguments(new Object[] { vs });

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }

    public void setPrivilegeBean(PrivilegeBean privilegeBean) {
        this.privilegeBean = privilegeBean;
    }
}
