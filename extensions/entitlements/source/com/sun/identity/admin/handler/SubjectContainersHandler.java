package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.sun.identity.admin.model.MultiPanelBean;
import com.sun.identity.admin.model.SubjectContainer;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;

public class SubjectContainersHandler implements MultiPanelHandler, Serializable {
    private List<SubjectContainer> selectedSubjectContainers;
    private List<SubjectContainer> availableSubjectContainers;

    public void dropListener(DropEvent dropEvent) {
        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            SubjectContainer dragValue = (SubjectContainer)dropEvent.getTargetDragValue();
            assert(!selectedSubjectContainers.contains(dragValue));
            dragValue.setActive(true);
            selectedSubjectContainers.add(dragValue);
        }
    }

    public void expandListener(ActionEvent event) {
        MultiPanelBean mpb = (SubjectContainer) event.getComponent().getAttributes().get("bean");
        assert(mpb != null);
        mpb.setExpanded(!mpb.isExpanded());
    }

    public void removeListener(ActionEvent event) {
        SubjectContainer subjectContainer = (SubjectContainer) event.getComponent().getAttributes().get("bean");
        assert(subjectContainer != null);
        selectedSubjectContainers.remove(subjectContainer);
        subjectContainer.setActive(false);
    }

    public List<SubjectContainer> getSelectedSubjectContainers() {
        return selectedSubjectContainers;
    }

    public void setSelectedSubjectContainers(List<SubjectContainer> selectedSubjectContainers) {
        this.selectedSubjectContainers = selectedSubjectContainers;
    }

    public List<SubjectContainer> getAvailableSubjectContainers() {
        return availableSubjectContainers;
    }

    public void setAvailableSubjectContainers(List<SubjectContainer> availableSubjectContainers) {
        this.availableSubjectContainers = availableSubjectContainers;
    }
}
