package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.List;

public class PolicyManageBean implements Serializable {
    private List<PrivilegeBean> privilegeBeans;

    public List<PrivilegeBean> getPrivilegeBeans() {
        return privilegeBeans;
    }

    public void setPrivilegeBeans(List<PrivilegeBean> privilegeBeans) {
        this.privilegeBeans = privilegeBeans;
    }
}
