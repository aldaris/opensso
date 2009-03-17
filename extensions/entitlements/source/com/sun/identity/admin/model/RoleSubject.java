package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public class RoleSubject
        extends ChooserSubject
        implements Serializable {

    public EntitlementSubject getSubject() {
        com.sun.identity.entitlement.RoleSubject eRoleSubject = new com.sun.identity.entitlement.RoleSubject(this.getName());
        return eRoleSubject;
    }
}
