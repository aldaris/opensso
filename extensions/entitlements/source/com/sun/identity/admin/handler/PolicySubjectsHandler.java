package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DropEvent;
import javax.faces.event.ActionEvent;

public interface PolicySubjectsHandler extends MultiPanelHandler {
    public void subjectDropListener(DropEvent dropEvent);
    public void allOfSubjectListener(ActionEvent event);
    public void anyOfSubjectListener(ActionEvent event);
    public boolean validateSubjects();
}
