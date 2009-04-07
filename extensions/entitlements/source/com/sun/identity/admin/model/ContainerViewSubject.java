package com.sun.identity.admin.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerViewSubject extends ViewSubject implements ContainerTreeNode {
    private List<ViewSubject> viewSubjects = new ArrayList<ViewSubject>();

    public ContainerViewSubject() {
        super();
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public List getTreeNodes() {
        return viewSubjects;
    }
    
    public void addViewSubject(ViewSubject vs) {
        viewSubjects.add(vs);
    }

    public int getViewSubjectsSize() {
        return viewSubjects.size();
    }

    @Override
    public String getTitle() {
        return getName() + " (" + getViewSubjectsSize() + ")";

    }
}
