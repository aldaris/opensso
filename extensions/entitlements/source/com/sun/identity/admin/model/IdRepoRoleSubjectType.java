package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import com.sun.identity.admin.subject.IdRepoRoleSubject;
import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public class IdRepoRoleSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new IdRepoRoleViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert(es instanceof IdRepoRoleSubject);
        IdRepoRoleSubject rs = (IdRepoRoleSubject)es;

        IdRepoRoleViewSubject idrs = (IdRepoRoleViewSubject)newViewSubject();
        idrs.setName(rs.getID());

        SubjectDao sd = stf.getSubjectDao(idrs);
        sd.decorate(idrs);

        return idrs;
    }

}
