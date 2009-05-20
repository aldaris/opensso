package com.sun.identity.admin.model;

public class DescriptionPolicyFilterType extends PolicyFilterType {
    public PolicyFilter newPolicyFilter() {
        PolicyFilter pf = new DescriptionPolicyFilter();
        pf.setPolicyFilterType(this);

        return pf;
    }
}
