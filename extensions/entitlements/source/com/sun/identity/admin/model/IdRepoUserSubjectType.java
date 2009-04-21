package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import com.sun.identity.admin.subject.IdRepoUserSubject;
import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public class IdRepoUserSubjectType
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new IdRepoUserViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert(es instanceof IdRepoUserSubject);
        IdRepoUserSubject us = (IdRepoUserSubject)es;

        IdRepoUserViewSubject idus = (IdRepoUserViewSubject)newViewSubject();
        idus.setName(us.getID());

        SubjectDao sd = stf.getSubjectDao(idus);
        sd.decorate(idus);

        return idus;
    }
}
