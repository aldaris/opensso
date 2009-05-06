package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Privilege;

public class ModifiedPolicyFilter extends DatePolicyFilter {
    public String getPrivilegeAttributeName() {
        return Privilege.LAST_MODIFIED_DATE_ATTRIBUTE;
    }
}
