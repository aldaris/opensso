package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.RoleSubject;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleSubjectDao implements SubjectDao, Serializable {

    public List<ViewSubject> getViewSubjects() {
        List<ViewSubject> roleSubjects = new ArrayList<ViewSubject>();
        RoleSubject rsb;

        // TODO - dummy data
        rsb = new RoleSubject();
        rsb.setName("employee");
        roleSubjects.add(rsb);

        rsb = new RoleSubject();
        rsb.setName("manager");
        roleSubjects.add(rsb);

        rsb = new RoleSubject();
        rsb.setName("developer");
        roleSubjects.add(rsb);

        rsb = new RoleSubject();
        rsb.setName("qa");
        roleSubjects.add(rsb);

        rsb = new RoleSubject();
        rsb.setName("pubs");
        roleSubjects.add(rsb);

        return roleSubjects;
    }
}
