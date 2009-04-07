package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.RoleViewSubject;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleSubjectDao extends SubjectDao implements Serializable {

    public List<ViewSubject> getViewSubjects() {
        List<ViewSubject> roleSubjects = new ArrayList<ViewSubject>();
        RoleViewSubject rvs;

        // TODO - dummy data
        rvs = new RoleViewSubject();
        rvs.setName("employee");
        rvs.setSubjectType(getSubjectType());
        roleSubjects.add(rvs);

        rvs = new RoleViewSubject();
        rvs.setName("manager");
        rvs.setSubjectType(getSubjectType());
        roleSubjects.add(rvs);

        rvs = new RoleViewSubject();
        rvs.setName("developer");
        rvs.setSubjectType(getSubjectType());
        roleSubjects.add(rvs);

        rvs = new RoleViewSubject();
        rvs.setName("qa");
        rvs.setSubjectType(getSubjectType());
        roleSubjects.add(rvs);

        rvs = new RoleViewSubject();
        rvs.setName("pubs");
        rvs.setSubjectType(getSubjectType());
        roleSubjects.add(rvs);

        return roleSubjects;
    }
}
