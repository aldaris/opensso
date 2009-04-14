package com.sun.identity.admin.model;

import java.io.Serializable;

public class IdRepoRoleSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new IdRepoRoleViewSubject();
        vs.setSubjectType(this);

        return vs;
    }
}
