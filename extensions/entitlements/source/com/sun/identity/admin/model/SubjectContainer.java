package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.dao.SubjectDao;
import java.io.Serializable;
import java.util.List;

public class SubjectContainer implements MultiPanelBean, Serializable {
    private SubjectDao subjectDao;
    private SubjectType subjectType;
    private List<ViewSubject> viewSubjects;
    private boolean expanded = true;
    private Effect expandEffect;
    private Effect panelEffect;
    private boolean visible = false;
    private String filter;

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;

        viewSubjects = subjectDao.getViewSubjects();
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Effect getExpandEffect() {
        return expandEffect;
    }

    public void setExpandEffect(Effect expandEffect) {
        this.expandEffect = expandEffect;
    }

    public Effect getPanelEffect() {
        return panelEffect;
    }

    public void setPanelEffect(Effect panelEffect) {
        this.panelEffect = panelEffect;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;

        if (filter == null || filter.length() == 0) {
            filter = "*";
        }
        viewSubjects = subjectDao.getViewSubjects(filter);
    }
}
