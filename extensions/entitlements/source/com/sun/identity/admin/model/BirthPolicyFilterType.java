package com.sun.identity.admin.model;

public class BirthPolicyFilterType extends PolicyFilterType {
    public PolicyFilter newPolicyFilter() {
        PolicyFilter pf = new BirthPolicyFilter();
        pf.setPolicyFilterType(this);

        return pf;
    }
}
