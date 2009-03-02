package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class SubjectsBean implements Serializable {
    private Set<SubjectBean> subjectBeans = new LinkedHashSet<SubjectBean>();
    private Set<SubjectBean> filteredSubjectBeans = new LinkedHashSet<SubjectBean>();
    private boolean draggable = false;
    private String filter;

    public SubjectsBean() {
        SubjectBean sb;

        // TODO - dummy data
        sb = new SubjectBean();
        sb.setName("bob");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("bill");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("bud");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("benny");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("ben");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("brandon");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("sally");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("sarah");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("sylvia");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("stephanie");
        subjectBeans.add(sb);

        sb = new SubjectBean();
        sb.setName("sophie");
        subjectBeans.add(sb);

        filteredSubjectBeans.addAll(subjectBeans);
        // TODO - dummy data

    }

    public Set<SubjectBean> getSubjectBeans() {
        return subjectBeans;
    }

    public void setSubjectBeans(Set<SubjectBean> subjectBeans) {
        this.subjectBeans = subjectBeans;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    public Set<SubjectBean> getFilteredSubjectBeans() {
        return filteredSubjectBeans;
    }

    public void setFilteredSubjectBeans(Set<SubjectBean> filteredSubjectBeans) {
        this.filteredSubjectBeans = filteredSubjectBeans;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;

        filteredSubjectBeans.clear();
        if (filter == null || filter.length() == 0) {
            filteredSubjectBeans.addAll(subjectBeans);
            return;
        }

        for (SubjectBean sb: subjectBeans) {
            if (sb.getName().contains(filter)) {
                filteredSubjectBeans.add(sb);
            }
        }
    }
}
