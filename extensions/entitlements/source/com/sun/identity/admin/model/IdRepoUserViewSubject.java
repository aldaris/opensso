package com.sun.identity.admin.model;

import com.sun.identity.entitlement.IdRepoUserSubject;
import com.sun.identity.entitlement.EntitlementSubject;

public class IdRepoUserViewSubject extends IdRepoViewSubject {
    private String employeeNumber;
    private String sn;

    public EntitlementSubject getEntitlementSubject() {
        IdRepoUserSubject idus = new IdRepoUserSubject();
        idus.setID(getName());

        return idus;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
