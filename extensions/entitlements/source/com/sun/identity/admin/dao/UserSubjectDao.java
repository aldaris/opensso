package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.UserSubject;
import com.sun.identity.admin.model.ViewSubject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserSubjectDao implements SubjectDao, Serializable {

    public List<ViewSubject> getViewSubjects() {
        List<ViewSubject> UserSubjectBean = new ArrayList<ViewSubject>();
        UserSubject usb;

        // TODO - dummy data
        usb = new UserSubject();
        usb.setName("bob");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("bill");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("bud");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("benny");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("ben");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("brandon");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("sally");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("sarah");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("sylvia");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("stephanie");
        UserSubjectBean.add(usb);

        usb = new UserSubject();
        usb.setName("sophie");
        UserSubjectBean.add(usb);

        return UserSubjectBean;
    }
}
