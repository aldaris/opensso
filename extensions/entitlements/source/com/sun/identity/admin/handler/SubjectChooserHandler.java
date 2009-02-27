package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.SubjectChooserBean;
import java.io.Serializable;

public class SubjectChooserHandler implements Serializable {
    private SubjectsHandler availableSubjectsHandler;
    private SubjectsHandler selectedSubjectsHandler;
    private SubjectChooserBean subjectChooserBean;

    public SubjectsHandler getAvailableSubjectsHandler() {
        return availableSubjectsHandler;
    }

    public void setAvailableSubjectsHandler(SubjectsHandler availableSubjectsHandler) {
        this.availableSubjectsHandler = availableSubjectsHandler;
    }

    public SubjectsHandler getSelectedSubjectsHandler() {
        return selectedSubjectsHandler;
    }

    public void setSelectedSubjectsHandler(SubjectsHandler selectedSubjectsHandler) {
        this.selectedSubjectsHandler = selectedSubjectsHandler;
    }

    public SubjectChooserBean getSubjectChooserBean() {
        return subjectChooserBean;
    }

    public void setSubjectChooserBean(SubjectChooserBean subjectChooserBean) {
        this.subjectChooserBean = subjectChooserBean;

        this.availableSubjectsHandler = new SubjectsHandler();
        availableSubjectsHandler.setSubjectsBean(subjectChooserBean.getAvailableSubjectsBean());

        this.selectedSubjectsHandler = new SubjectsHandler();
        selectedSubjectsHandler.setSubjectsBean(subjectChooserBean.getSelectedSubjectsBean());
    }

}
