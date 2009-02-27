package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class SubjectsBean implements Serializable {
    private Set<SubjectBean> subjectBeans = new LinkedHashSet<SubjectBean>();
    private boolean expanded = true;
    private boolean draggable = false;

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
        // TODO - dummy data
    }

    public Set<SubjectBean> getSubjectBeans() {
        return subjectBeans;
    }

    public void setSubjectBeans(Set<SubjectBean> subjectBeans) {
        this.subjectBeans = subjectBeans;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
}
