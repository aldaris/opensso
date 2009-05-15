package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.IdType;
import java.util.Map;

public class IdRepoGroupSubjectDao extends IdRepoSubjectDao {
    protected IdType getIdType() {
        return IdType.GROUP;
    }

    @Override
    protected void decorate(ViewSubject vs, Map attrs) {
        super.decorate(vs, attrs);

        // TODO?
    }
}
