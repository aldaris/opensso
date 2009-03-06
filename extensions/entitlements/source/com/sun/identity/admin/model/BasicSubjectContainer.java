package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BasicSubjectContainer implements SubjectContainer, Serializable {
    private SubjectDao subjectDao;
    private SubjectType subjectType;
    private List<ViewSubject> viewSubjects = new ArrayList<ViewSubject>();

    public SubjectDao getSubjectDao() {
        return subjectDao;
    }

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
        viewSubjects.addAll(subjectDao.getViewSubjects());
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType;
    }

    public List<ViewSubject> getViewSubjects() {
        return viewSubjects;
    }

    public void setViewSubjects(List<ViewSubject> viewSubjects) {
        this.viewSubjects = viewSubjects;
    }

}
