package com.sun.identity.admin.handler;

import java.io.Serializable;

public class NavigationHandler implements Serializable {
    public String homeAction() {
        return "home";
    }

    public String policyCreateAction() {
        return "policy-create";
    }

    public String policyManageAction() {
        return "policy-manage";
    }

    public String newsAction() {
        return "news";
    }

    public String federationAction() {
        return "federation";
    }
}
