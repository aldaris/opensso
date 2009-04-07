package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.UserViewSubject;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSubjectDao extends SubjectDao implements Serializable {

    public List<ViewSubject> getViewSubjects() {
        List<ViewSubject> userSubjects = new ArrayList<ViewSubject>();
        UserViewSubject uvs;

        // TODO - dummy data
        uvs = new UserViewSubject();
        uvs.setName("bob");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("bill");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("bud");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("benny");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("ben");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("brandon");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("sally");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("sarah");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("sylvia");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("stephanie");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        uvs = new UserViewSubject();
        uvs.setName("sophie");
        uvs.setSubjectType(getSubjectType());
        userSubjects.add(uvs);

        return userSubjects;
    }
}
