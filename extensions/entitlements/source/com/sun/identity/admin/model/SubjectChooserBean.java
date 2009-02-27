package com.sun.identity.admin.model;

import java.io.Serializable;

public class SubjectChooserBean implements Serializable {
    private SubjectsBean availableSubjectsBean = new SubjectsBean();
    private SubjectsBean selectedSubjectsBean = new SubjectsBean();
    private boolean expanded = true;

    public SubjectsBean getAvailableSubjectsBean() {
        return availableSubjectsBean;
    }

    public void setAvailableSubjectsBean(SubjectsBean availableSubjectsBean) {
        this.availableSubjectsBean = availableSubjectsBean;
    }

    public SubjectsBean getSelectedSubjectsBean() {
        return selectedSubjectsBean;
    }

    public void setSelectedSubjectsBean(SubjectsBean selectedSubjectsBean) {
        this.selectedSubjectsBean = selectedSubjectsBean;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
