package com.sun.identity.admin.model;

public class ModifierPolicyFilterType extends PolicyFilterType {
    public PolicyFilter newPolicyFilter() {
        PolicyFilter pf = new ModifierPolicyFilter();
        pf.setPolicyFilterType(this);

        return pf;
    }
}
