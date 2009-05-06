package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Privilege;

public class BirthPolicyFilter extends DatePolicyFilter {
    public String getPrivilegeAttributeName() {
        return Privilege.CREATION_DATE_ATTRIBUTE;
    }
}
