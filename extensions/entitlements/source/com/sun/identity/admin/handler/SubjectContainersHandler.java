package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.SubjectContainer;
import java.io.Serializable;
import java.util.List;

public class SubjectContainersHandler implements Serializable {
    private List<SubjectContainer> subjectContainers;

    public List<SubjectContainer> getSubjectContainers() {
        return subjectContainers;
    }

    public void setSubjectContainers(List<SubjectContainer> subjectContainers) {
        this.subjectContainers = subjectContainers;
    }
}
