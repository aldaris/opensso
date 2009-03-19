package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.ViewSubject;
import java.util.List;

public interface SubjectContainerDao {
    public List<ViewSubject> getViewSubjects();
}
