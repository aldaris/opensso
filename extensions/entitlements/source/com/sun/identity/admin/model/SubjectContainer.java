package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectContainerDao;
import java.util.List;

public interface SubjectContainer extends MultiPanelBean {
    public List<ViewSubject> getViewSubjects();
    public void setSubjectContainerDao(SubjectContainerDao scDao);
    public String getName();
    public void setName(String name);
    public String getTemplate();
    public int getNumberSelected();
    public SubjectContainerType getSubjectContainerType();
    public void setSubjectContainerType(SubjectContainerType sc);
}
