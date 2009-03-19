package com.sun.identity.admin.model;

import java.io.Serializable;

public class UserSubjectContainerType 
    extends SubjectContainerType
    implements Serializable {

    public SubjectContainer newSubjectContainer() {
        SubjectContainer sc = new UserSubjectContainer();
        sc.setSubjectContainerType(this);
        sc.setName(getName());
        sc.setSubjectContainerDao(getSubjectContainerDao());

        return sc;
    }
}
