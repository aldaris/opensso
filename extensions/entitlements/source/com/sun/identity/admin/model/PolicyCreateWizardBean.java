package com.sun.identity.admin.model;

import com.sun.identity.admin.ManagedBeanResolver;

public class PolicyCreateWizardBean extends PolicyWizardBean {
    public static PolicyCreateWizardBean getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        PolicyCreateWizardBean pcwb = (PolicyCreateWizardBean)mbr.resolve("policyCreateWizardBean");
        return pcwb;
    }
}
