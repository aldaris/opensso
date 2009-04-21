package com.sun.identity.admin.model;

import com.sun.identity.admin.subject.IdRepoRoleSubject;
import com.sun.identity.entitlement.EntitlementSubject;

public class IdRepoRoleViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        IdRepoRoleSubject idrs = new IdRepoRoleSubject();
        idrs.setID(getName());

        return idrs;
    }
}
