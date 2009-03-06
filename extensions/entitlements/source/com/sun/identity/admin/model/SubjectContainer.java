package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import java.util.List;

public interface SubjectContainer {
    public List<ViewSubject> getViewSubjects();
    public SubjectType getSubjectType();
    public SubjectDao getSubjectDao();
}
