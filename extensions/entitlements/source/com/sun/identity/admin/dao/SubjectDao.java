package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.SubjectType;
import com.sun.identity.admin.model.ViewSubject;
import java.util.List;

public abstract class SubjectDao {
    private SubjectType subjectType;

    public abstract List<ViewSubject> getViewSubjects();
    public abstract List<ViewSubject> getViewSubjects(String pattern);
    public abstract void decorate(ViewSubject vs);

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }
}
