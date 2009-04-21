package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.GroupSubject;

public class GroupViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        GroupSubject gs = new GroupSubject();
        gs.setID(getName());

        return gs;
    }
}
