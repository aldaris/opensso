package com.sun.identity.admin.model;

import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import java.io.Serializable;
import java.util.List;

public abstract class PolicyFilter implements Serializable {
    private String name;
    private PolicyFilterType policyFilterType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PolicyFilterType getPolicyFilterType() {
        return policyFilterType;
    }

    public void setPolicyFilterType(PolicyFilterType policyFilterType) {
        this.policyFilterType = policyFilterType;
    }

    public String getTitle() {
        // TODO
        return getName();
    }

    public abstract List<PrivilegeSearchFilter> getPrivilegeSearchFilters();
}
