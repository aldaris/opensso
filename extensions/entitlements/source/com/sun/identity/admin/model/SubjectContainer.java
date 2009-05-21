package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.dao.SubjectDao;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.collections.comparators.NullComparator;

public class SubjectContainer implements MultiPanelBean, Serializable {

    private SubjectDao subjectDao;
    private SubjectType subjectType;
    private List<ViewSubject> viewSubjects;
    private boolean panelExpanded = false;
    private Effect panelExpandEffect;
    private Effect panelEffect;
    private boolean panelVisible = false;
    private String filter = "";
    private boolean searchVisible = false;

    public boolean isVisible() {
        if (filter != null && filter.length() > 0) {
            return true;
        }
        if (viewSubjects.size() > 0) {
            return true;
        }

        return false;
    }

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
        reset();
    }

    private void reset() {
        viewSubjects = subjectDao.getViewSubjects(filter);
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        if (filter == null) {
            filter = "";
        }
        NullComparator n = new NullComparator();
        if (n.compare(this.filter, filter) != 0) {
            this.filter = filter;
            reset();
        }
    }

    public boolean isPanelExpanded() {
        return panelExpanded;
    }

    public void setPanelExpanded(boolean panelExpanded) {
        this.panelExpanded = panelExpanded;
    }

    public Effect getPanelExpandEffect() {
        return panelExpandEffect;
    }

    public void setPanelExpandEffect(Effect panelExpandEffect) {
        this.panelExpandEffect = panelExpandEffect;
    }

    public Effect getPanelEffect() {
        return panelEffect;
    }

    public void setPanelEffect(Effect panelEffect) {
        this.panelEffect = panelEffect;
    }

    public boolean isPanelVisible() {
        return panelVisible;
    }

    public void setPanelVisible(boolean panelVisible) {
        this.panelVisible = panelVisible;
    }

    public boolean isSearchVisible() {
        return searchVisible;
    }

    public void setSearchVisible(boolean searchVisible) {
        this.searchVisible = searchVisible;
    }
}
