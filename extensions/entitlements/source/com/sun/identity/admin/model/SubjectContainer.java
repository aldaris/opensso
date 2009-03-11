package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import java.util.List;

public interface SubjectContainer {
    public List<ViewSubject> getViewSubjects();
    public SubjectDao getSubjectDao();
    public String getName();
    public String getTemplate();
    public boolean isActive();
    public void setActive(boolean active);
    public boolean isExpanded();
    public void setExpanded(boolean expanded);
    public String getExpandImage();
    public String getExpandText();
}
