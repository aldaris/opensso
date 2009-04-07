package com.sun.identity.admin.model;

import java.io.Serializable;

public class UserSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new UserViewSubject();
        vs.setSubjectType(this);

        return vs;
    }
}
