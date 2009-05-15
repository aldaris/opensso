package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.IdRepoGroupSubject;

public class IdRepoGroupViewSubject extends IdRepoViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        IdRepoGroupSubject idgs = new IdRepoGroupSubject();
        idgs.setID(getName());

        return idgs;
    }
}
