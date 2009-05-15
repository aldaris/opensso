package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.IdType;
import java.util.Map;

public class IdRepoRoleSubjectDao extends IdRepoSubjectDao {
    protected IdType getIdType() {
        return IdType.ROLE;
    }

    @Override
    protected void decorate(ViewSubject vs, Map attrs) {
        super.decorate(vs, attrs);

        // TODO?
    }
}
