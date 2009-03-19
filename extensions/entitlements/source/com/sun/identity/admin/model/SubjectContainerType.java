package com.sun.identity.admin.model;

import java.io.Serializable;
import com.sun.identity.admin.dao.SubjectContainerDao;

public abstract class SubjectContainerType implements Serializable {
    private String name;
    private String template;
    private SubjectContainerDao subjectContainerDao;
    private String subjectIconUri;

    public abstract SubjectContainer newSubjectContainer();

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

    public SubjectContainerDao getSubjectContainerDao() {
        return subjectContainerDao;
    }

    public void setSubjectContainerDao(SubjectContainerDao subjectContainerDao) {
        this.subjectContainerDao = subjectContainerDao;
    }

    public String getSubjectIconUri() {
        return subjectIconUri;
    }

    public void setSubjectIconUri(String subjectIconUri) {
        this.subjectIconUri = subjectIconUri;
    }
}
