package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.SubjectsBean;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.sun.identity.admin.model.SubjectBean;
import java.io.Serializable;

public class SubjectsHandler implements Serializable {
    private SubjectsBean subjectsBean;

    public SubjectsBean getSubjectsBean() {
        return subjectsBean;
    }

    public void setSubjectsBean(SubjectsBean subjectsBean) {
        this.subjectsBean = subjectsBean;
    }

    public void dropListener(DropEvent dropEvent) {
        SubjectsBean srcSubjectsBean = (SubjectsBean) dropEvent.getTargetDropValue();
        SubjectBean dragSubjectBean = (SubjectBean) dropEvent.getTargetDragValue();

        if (srcSubjectsBean == subjectsBean) {
            // no drag onto ourself
            return;
        }

        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            subjectsBean.getSubjectBeans().add(dragSubjectBean);
            srcSubjectsBean.getSubjectBeans().remove(dragSubjectBean);
        }
    }
}
