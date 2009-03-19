package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.UserSubject;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSubjectContainerDao implements SubjectContainerDao, Serializable {

    public List<ViewSubject> getViewSubjects() {
        List<ViewSubject> userSubjects = new ArrayList<ViewSubject>();
        UserSubject usb;

        // TODO - dummy data
        usb = new UserSubject();
        usb.setName("bob");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("bill");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("bud");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("benny");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("ben");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("brandon");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("sally");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("sarah");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("sylvia");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("stephanie");
        userSubjects.add(usb);

        usb = new UserSubject();
        usb.setName("sophie");
        userSubjects.add(usb);

        return userSubjects;
    }
}
