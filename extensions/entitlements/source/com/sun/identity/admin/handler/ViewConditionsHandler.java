package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.QueuedActionBean;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Fade;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.model.Tree;
import com.sun.identity.admin.model.MultiPanelBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.PrivilegeBean;
import com.sun.identity.admin.model.ViewCondition;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

public class ViewConditionsHandler
        implements MultiPanelHandler, Serializable {

    private PrivilegeBean privilegeBean;
    private QueuedActionBean queuedActionBean;

    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (MultiPanelBean) event.getComponent().getAttributes().get("bean");
        assert (mpb != null);

        Effect e;
        if (mpb.isExpanded()) {
            e = new SlideUp();
        } else {
            e = new SlideDown();
        }
        e.setSubmit(true);
        e.setTransitory(false);
        mpb.setExpandEffect(e);
    }

    public void removeListener(ActionEvent event) {
        MultiPanelBean mpb = (MultiPanelBean) event.getComponent().getAttributes().get("bean");
        assert (mpb != null);

        Effect e;

        e = new Fade();
        e.setSubmit(true);
        e.setTransitory(false);
        mpb.setPanelEffect(e);

        ViewCondition vc = (ViewCondition) mpb;
        addRemoveAction(vc);
    }

    public void handleRemove(ViewCondition vc) {
        Tree ct = new Tree(privilegeBean.getViewCondition());
        ViewCondition rootVc = (ViewCondition)ct.remove(vc);
        privilegeBean.setViewCondition(rootVc);

    }

    private void addRemoveAction(ViewCondition vc) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(false);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{viewConditionsHandler.handleRemove}");
        pea.setParameters(new Class[]{ViewCondition.class});
        pea.setArguments(new Object[]{vc});

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }

    public void setPrivilegeBean(PrivilegeBean privilegeBean) {
        this.privilegeBean = privilegeBean;
    }

    public ViewCondition getViewCondition(ActionEvent event) {
        ViewCondition vc = (ViewCondition) event.getComponent().getAttributes().get("viewCondition");
        assert(vc != null);

        return vc;
    }

    public void titlePopupListener(ActionEvent event) {
        ViewCondition vc = getViewCondition(event);
        vc.setTitlePopupVisible(!vc.isTitlePopupVisible());
    }

}
