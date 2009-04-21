package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.GroupSubject;
import java.io.Serializable;

public class GroupSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new GroupViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert(es instanceof GroupSubject);
        GroupSubject gs = (GroupSubject)es;

        GroupViewSubject gvs = (GroupViewSubject)newViewSubject();
        gvs.setName(gs.getID());

        return gvs;
    }
}
