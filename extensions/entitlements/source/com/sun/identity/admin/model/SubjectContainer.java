package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import java.util.List;

public interface SubjectContainer extends MultiPanelBean {
    public List<ViewSubject> getViewSubjects();
    public SubjectDao getSubjectDao();
    public String getName();
    public String getTemplate();
    public boolean isActive();
    public void setActive(boolean active);
    public int getNumberSelected();
}
