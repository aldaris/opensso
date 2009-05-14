package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.IdRepoRoleViewSubject;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdType;
import java.io.Serializable;

public class IdRepoGroupSubjectDao extends IdRepoSubjectDao implements Serializable {
    protected IdType getIdType() {
        return IdType.ROLE;
    }

    protected ViewSubject newViewSubject(AMIdentity ami) {
        IdRepoRoleViewSubject rvs = (IdRepoRoleViewSubject)getSubjectType().newViewSubject();
        rvs.setName(ami.getUniversalId());
        
        return rvs;
    }

    public void decorate(ViewSubject vs) {
        // TODO
        // any role decoration?
    }

}
