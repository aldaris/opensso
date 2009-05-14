package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.SubjectType;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.List;

public abstract class SubjectDao implements Serializable {
    private SubjectType subjectType;

    public abstract List<ViewSubject> getViewSubjects();
    public abstract List<ViewSubject> getViewSubjects(String filter);
    public abstract void decorate(ViewSubject vs);

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }
}
