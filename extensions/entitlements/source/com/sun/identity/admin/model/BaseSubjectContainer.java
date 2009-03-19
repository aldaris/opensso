package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Appear;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.SlideDown;
import com.sun.identity.admin.dao.SubjectContainerDao;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubjectContainer implements MultiPanelBean, SubjectContainer {
    private List<ViewSubject> viewSubjects = new ArrayList<ViewSubject>();
    private String name;
    private String template;
    private SubjectContainerDao subjectContainerDao;
    private boolean expanded = true;
    private Effect expandEffect;
    private Effect panelEffect;
    private boolean visible = false;
    private SubjectContainerType subjectContainerType;

    public BaseSubjectContainer() {
        panelEffect = new Appear();
        panelEffect.setSubmit(true);
        panelEffect.setTransitory(false);
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public void setViewSubjects(List<ViewSubject> viewSubjects) {
        this.viewSubjects = viewSubjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getNumberSelected() {
        return viewSubjects.size();
    }

    public Effect getExpandEffect() {
        return expandEffect;
    }

    public void setExpandEffect(Effect expandEffect) {
        this.expandEffect = expandEffect;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Effect getPanelEffect() {
        return panelEffect;
    }

    public void setPanelEffect(Effect panelEffect) {
        this.panelEffect = panelEffect;
    }

    public SubjectContainerType getSubjectContainerType() {
        return subjectContainerType;
    }

    public void setSubjectContainerType(SubjectContainerType subjectContainerType) {
        this.subjectContainerType = subjectContainerType;
    }

    public SubjectContainerDao getSubjectContainerDao() {
        return subjectContainerDao;
    }

    public void setSubjectContainerDao(SubjectContainerDao subjectContainerDao) {
        this.subjectContainerDao = subjectContainerDao;
        viewSubjects = subjectContainerDao.getViewSubjects();
    }
}
