package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.PolicyDao;
import java.io.Serializable;
import java.util.List;

public class PolicyManageBean implements Serializable {
    private List<PrivilegeBean> privilegeBeans;
    private PolicyDao policyDao;
    private PolicyManageTableBean policyManageTableBean;
    private boolean viewOptionsPopupVisible = false;

    public List<PrivilegeBean> getPrivilegeBeans() {
        return privilegeBeans;
    }

    public void reset() {
        privilegeBeans = policyDao.getPrivilegeBeans();
        policyManageTableBean = new PolicyManageTableBean(privilegeBeans);
    }

    public void setPolicyDao(PolicyDao policyDao) {
        this.policyDao = policyDao;
        privilegeBeans = policyDao.getPrivilegeBeans();
        policyManageTableBean = new PolicyManageTableBean(privilegeBeans);
    }

    public PolicyManageTableBean getPolicyManageTableBean() {
        return policyManageTableBean;
    }

    public boolean isViewOptionsPopupVisible() {
        return viewOptionsPopupVisible;
    }

    public void setViewOptionsPopupVisible(boolean viewOptionsPopupVisible) {
        this.viewOptionsPopupVisible = viewOptionsPopupVisible;
    }
}
