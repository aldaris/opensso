package com.sun.identity.admin.model;

import com.sun.identity.admin.ManagedBeanResolver;

public class PolicyEditWizardBean extends PolicyWizardBean {
    public static PolicyEditWizardBean getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        PolicyEditWizardBean pewb = (PolicyEditWizardBean)mbr.resolve("policyEditWizardBean");
        return pewb;
    }
}
