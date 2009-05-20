package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Privilege;

public class DescriptionPolicyFilter extends PatternPolicyFilter {
    public String getPrivilegeAttributeName() {
        return Privilege.DESCRIPTION_ATTRIBUTE;
    }

}
