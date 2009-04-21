package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.UserSubject;

public class UserViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        UserSubject us = new UserSubject();
        us.setID(getName());

        return us;
    }
}
