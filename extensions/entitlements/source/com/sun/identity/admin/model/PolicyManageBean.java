package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.PolicyDao;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PolicyManageBean implements Serializable {
    private List<PrivilegeBean> privilegeBeans;
    private PolicyDao policyDao;
    private PolicyManageTableBean policyManageTableBean;

    public List<PrivilegeBean> getPrivilegeBeans() {
        return privilegeBeans;
    }

    public void setPolicyDao(PolicyDao policyDao) {
        this.policyDao = policyDao;
        privilegeBeans = policyDao.getPrivilegeBeans();
        policyManageTableBean = new PolicyManageTableBean(privilegeBeans);
    }

    public PolicyManageTableBean getPolicyManageTableBean() {
        return policyManageTableBean;
    }
}
