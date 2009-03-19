package com.sun.identity.admin.model;

import java.io.Serializable;

public class RoleSubjectContainerType
    extends SubjectContainerType
    implements Serializable {

    public SubjectContainer newSubjectContainer() {
        SubjectContainer sc = new RoleSubjectContainer();
        sc.setSubjectContainerType(this);
        sc.setName(getName());
        sc.setSubjectContainerDao(getSubjectContainerDao());

        return sc;
    }
}
