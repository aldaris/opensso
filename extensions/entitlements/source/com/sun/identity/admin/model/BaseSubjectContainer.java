package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSubjectContainer implements SubjectContainer {
    private List<ViewSubject> viewSubjects = new ArrayList<ViewSubject>();
    private String name;
    private String template;
    private boolean active = false;
    private SubjectDao subjectDao;
    private boolean expanded = true;
    private String expandText;
    private String expandImage;

    public BaseSubjectContainer() {
        setExpanded(true);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public SubjectDao getSubjectDao() {
        return subjectDao;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
        if (expanded) {
            this.expandText = "Hide";
            this.expandImage = "../image/hide.png";
        } else {
            this.expandText = "Show";
            this.expandImage = "../image/show.png";
        }
    }

    public String getExpandText() {
        return expandText;
    }

    public String getExpandImage() {
        return expandImage;
    }

    public int getNumberSelected() {
        return viewSubjects.size();
    }
}
