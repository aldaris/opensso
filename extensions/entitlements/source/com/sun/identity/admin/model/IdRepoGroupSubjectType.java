package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import com.sun.identity.entitlement.IdRepoRoleSubject;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.IdRepoGroupSubject;
import java.io.Serializable;

public class IdRepoGroupSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new IdRepoGroupViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert(es instanceof IdRepoGroupSubject);
        IdRepoGroupSubject gs = (IdRepoGroupSubject)es;

        IdRepoGroupViewSubject idgs = (IdRepoGroupViewSubject)newViewSubject();
        idgs.setName(gs.getID());

        SubjectDao sd = stf.getSubjectDao(idgs);
        sd.decorate(idgs);

        return idgs;
    }

}
