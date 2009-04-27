package com.sun.identity.admin.model;

import com.sun.identity.entitlement.IdRepoUserSubject;
import com.sun.identity.entitlement.EntitlementSubject;

public class IdRepoUserViewSubject extends ViewSubject {
    private String cn;

    public EntitlementSubject getEntitlementSubject() {
        IdRepoUserSubject idus = new IdRepoUserSubject();
        idus.setID(getName());

        return idus;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    @Override
    public String getTitle() {
        if (cn != null) {
            return cn;
        }
        return super.getTitle();
    }
}
