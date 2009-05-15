package com.sun.identity.admin.model;

import com.sun.identity.entitlement.IdRepoRoleSubject;
import com.sun.identity.entitlement.EntitlementSubject;

public class IdRepoRoleViewSubject extends IdRepoViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        IdRepoRoleSubject idrs = new IdRepoRoleSubject();
        idrs.setID(getName());

        return idrs;
    }
}
