package com.sun.identity.admin.model;

import java.io.Serializable;

public class RoleSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new RoleViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

}
