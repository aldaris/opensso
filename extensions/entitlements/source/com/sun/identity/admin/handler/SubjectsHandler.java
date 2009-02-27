package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.SubjectsBean;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.sun.identity.admin.model.SubjectBean;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import com.icesoft.faces.component.ext.RowSelectorEvent;

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
        Set<SubjectBean> dragged = new HashSet<SubjectBean>();

        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            for (SubjectBean sb : srcSubjectsBean.getSubjectBeans()) {
                if (sb.isSelected()) {
                    dragged.add(sb);
                    subjectsBean.getSubjectBeans().add(sb);
                    sb.setSelected(false);
                }
            }
            srcSubjectsBean.getSubjectBeans().removeAll(dragged);
        }
    }

    public void selectionListener(RowSelectorEvent event) {
        subjectsBean.setDraggable(false);
        for (SubjectBean sb : subjectsBean.getSubjectBeans()) {
            if (sb.isSelected()) {
                subjectsBean.setDraggable(true);
                break;
            }
        }
    }
}
