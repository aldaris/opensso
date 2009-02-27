package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.SubjectBean;
import com.sun.identity.admin.model.SubjectChooserBean;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.faces.event.ActionEvent;

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

    public void addSelectedListener(ActionEvent event) {
        Set<SubjectBean> available = subjectChooserBean.getAvailableSubjectsBean().getSubjectBeans();
        Set<SubjectBean> selected = subjectChooserBean.getSelectedSubjectsBean().getSubjectBeans();

        setMove(available, selected);
        setSelected(available, false);
        setSelected(selected, false);
    }

    public void addAllListener(ActionEvent event) {
        Set<SubjectBean> available = subjectChooserBean.getAvailableSubjectsBean().getSubjectBeans();
        Set<SubjectBean> selected = subjectChooserBean.getSelectedSubjectsBean().getSubjectBeans();

        selected.addAll(available);
        available.clear();
        setSelected(selected, false);
    }

    public void removeSelectedListener(ActionEvent event) {
        Set<SubjectBean> available = subjectChooserBean.getAvailableSubjectsBean().getSubjectBeans();
        Set<SubjectBean> selected = subjectChooserBean.getSelectedSubjectsBean().getSubjectBeans();

        setMove(selected, available);
        setSelected(available, false);
        setSelected(selected, false);
    }

    public void removeAllListener(ActionEvent event) {
        Set<SubjectBean> available = subjectChooserBean.getAvailableSubjectsBean().getSubjectBeans();
        Set<SubjectBean> selected = subjectChooserBean.getSelectedSubjectsBean().getSubjectBeans();

        available.addAll(selected);
        selected.clear();
        setSelected(available, false);
    }

    private void setMove(Set<SubjectBean> src, Set<SubjectBean> dest) {
        Set<SubjectBean> moved = new LinkedHashSet();

        for (SubjectBean sb: src) {
            if (sb.isSelected()) {
                moved.add(sb);
            }
        }

        src.removeAll(moved);
        dest.addAll(moved);
    }

    private void setSelected(Set<SubjectBean> sbs, boolean selected) {
        for (SubjectBean sb : sbs) {
            sb.setSelected(selected);
        }
    }
}
