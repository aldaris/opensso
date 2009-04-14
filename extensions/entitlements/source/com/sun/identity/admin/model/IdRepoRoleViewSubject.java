package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.RoleSubject;

public class IdRepoRoleViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        RoleSubject roleSubject = new RoleSubject();
        roleSubject.setRole(getName());

        return roleSubject;
    }
}
