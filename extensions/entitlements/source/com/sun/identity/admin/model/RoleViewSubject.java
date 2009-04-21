package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.RoleSubject;

public class RoleViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        RoleSubject rs = new RoleSubject();
        rs.setID(getName());

        return rs;
    }
}
