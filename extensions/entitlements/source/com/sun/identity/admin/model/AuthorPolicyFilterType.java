package com.sun.identity.admin.model;

public class AuthorPolicyFilterType extends PolicyFilterType {
    public PolicyFilter newPolicyFilter() {
        PolicyFilter pf = new AuthorPolicyFilter();
        pf.setPolicyFilterType(this);

        return pf;
    }
}
