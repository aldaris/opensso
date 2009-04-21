package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.UserSubject;

public class IdRepoUserViewSubject extends ViewSubject {
    private String cn;

    public EntitlementSubject getEntitlementSubject() {
        UserSubject userSubject = new UserSubject();
        userSubject.setID(getName());

        return userSubject;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }
}
