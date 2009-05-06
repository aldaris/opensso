package com.sun.identity.admin.model;

public class ModifiedPolicyFilterType extends PolicyFilterType {
    public PolicyFilter newPolicyFilter() {
        PolicyFilter pf = new ModifiedPolicyFilter();
        pf.setPolicyFilterType(this);

        return pf;
    }
}
