package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.UserSubject;
import java.io.Serializable;

public class UserSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new UserViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert(es instanceof UserSubject);
        UserSubject us = (UserSubject)es;

        UserViewSubject uvs = (UserViewSubject)newViewSubject();
        uvs.setName(us.getID());

        return uvs;
    }
}
