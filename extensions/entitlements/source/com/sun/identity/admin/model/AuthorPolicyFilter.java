package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Privilege;

public class AuthorPolicyFilter extends PatternPolicyFilter {
    public String getPrivilegeAttributeName() {
        return Privilege.CREATED_BY_ATTRIBUTE;
    }

}
