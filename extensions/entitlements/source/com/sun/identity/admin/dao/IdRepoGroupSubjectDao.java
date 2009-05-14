package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.IdRepoGroupViewSubject;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdType;

public class IdRepoGroupSubjectDao extends IdRepoSubjectDao {
    protected IdType getIdType() {
        return IdType.GROUP;
    }

    protected ViewSubject newViewSubject(AMIdentity ami) {
        IdRepoGroupViewSubject gvs = (IdRepoGroupViewSubject)getSubjectType().newViewSubject();
        gvs.setName(ami.getUniversalId());

        return gvs;
    }

    public void decorate(ViewSubject vs) {
        // TODO
        // any group decoration?
        // members?
    }

}
