package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.PolicyDao;
import java.io.Serializable;
import java.util.List;

public class PolicyManageBean implements Serializable {
    private List<PrivilegeBean> privilegeBeans;
    private PolicyDao policyDao;

    public List<PrivilegeBean> getPrivilegeBeans() {
        return privilegeBeans;
    }

    public void setPolicyDao(PolicyDao policyDao) {
        this.policyDao = policyDao;
        privilegeBeans = policyDao.getPrivilegeBeans();
    }
}
