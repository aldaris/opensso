package com.sun.identity.admin.model;

import java.io.Serializable;

public class IdRepoUserSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new IdRepoUserViewSubject();
        vs.setSubjectType(this);

        return vs;
    }
}
