package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Privilege;

public class ModifierPolicyFilter extends PatternPolicyFilter {
    public String getPrivilegeAttributeName() {
        return Privilege.LAST_MODIFIED_BY_ATTRIBUTE;
    }

}
