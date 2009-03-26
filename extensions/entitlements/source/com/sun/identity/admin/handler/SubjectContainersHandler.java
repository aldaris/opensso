package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Fade;
import com.icesoft.faces.context.effects.SlideDown;
import com.icesoft.faces.context.effects.SlideUp;
import com.sun.identity.admin.model.MultiPanelBean;
import com.sun.identity.admin.model.PhaseEventAction;
import com.sun.identity.admin.model.QueuedActionBean;
import com.sun.identity.admin.model.SubjectContainer;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;

public class SubjectContainersHandler implements MultiPanelHandler, Serializable {

    private List<SubjectContainer> subjectContainers;
    private QueuedActionBean queuedActionBean;

    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (SubjectContainer) event.getComponent().getAttributes().get("bean");
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
        SubjectContainer subjectContainer = (SubjectContainer) event.getComponent().getAttributes().get("bean");
        assert (subjectContainer != null);

        Effect e = new Fade();
        e.setSubmit(true);
        e.setTransitory(false);
        subjectContainer.setPanelEffect(e);

        addRemoveAction(subjectContainer);
    }

    public void handleRemove(SubjectContainer sc) {
        subjectContainers.remove(sc);
    }

    private void addRemoveAction(SubjectContainer sc) {
        PhaseEventAction pea = new PhaseEventAction();
        pea.setDoBeforePhase(false);
        pea.setPhaseId(PhaseId.RENDER_RESPONSE);
        pea.setAction("#{subjectContainersHandler.handleRemove}");
        pea.setParameters(new Class[] { SubjectContainer.class });
        pea.setArguments(new Object[] { sc });

        queuedActionBean.getPhaseEventActions().add(pea);
    }

    public List<SubjectContainer> getSubjectContainers() {
        return subjectContainers;
    }

    public void setSubjectContainers(List<SubjectContainer> subjectContainers) {
        this.subjectContainers = subjectContainers;
    }

    public void setQueuedActionBean(QueuedActionBean queuedActionBean) {
        this.queuedActionBean = queuedActionBean;
    }
}
