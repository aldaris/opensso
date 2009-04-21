package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.RoleSubject;
import java.io.Serializable;

public class RoleSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new RoleViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert(es instanceof RoleSubject);
        RoleSubject rs = (RoleSubject)es;

        RoleViewSubject rvs = (RoleViewSubject)newViewSubject();
        rvs.setName(rs.getID());

        return rvs;
    }
}
