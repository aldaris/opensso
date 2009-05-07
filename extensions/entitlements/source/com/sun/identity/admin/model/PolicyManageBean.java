package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.PolicyDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolicyManageBean implements Serializable {

    private List<PrivilegeBean> privilegeBeans;
    private PolicyDao policyDao;
    private PolicyManageTableBean policyManageTableBean = new PolicyManageTableBean();
    private boolean viewOptionsPopupVisible = false;
    private String searchFilter;
    private List<PolicyFilterHolder> policyFilterHolders = new ArrayList<PolicyFilterHolder>();
    private Map<String,PolicyFilterType> policyFilterTypes;

    public List<PrivilegeBean> getPrivilegeBeans() {
        return privilegeBeans;
    }

    public void newPolicyFilterHolder() {
        PolicyFilterHolder pfh = new PolicyFilterHolder();
        pfh.setPolicyFilterTypes(getPolicyFilterTypes());
        policyFilterHolders.add(pfh);
    }
    public void reset() {
        privilegeBeans = policyDao.getPrivilegeBeans(searchFilter, policyFilterHolders);
        policyManageTableBean.setPrivilegeBeans(privilegeBeans);
    }

    public void setPolicyDao(PolicyDao policyDao) {
        this.policyDao = policyDao;
        reset();
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

    public String getSearchFilter() {
        return searchFilter;
    }

    public List<String> getAllPrivilegeNames() {
        return policyDao.getPrivilegeNames();
    }

    public void setSearchFilter(String searchFilter) {
        if (!searchFilter.equals(this.searchFilter)) {
            this.searchFilter = searchFilter;
            reset();
        }
    }

    public List<PolicyFilterHolder> getPolicyFilterHolders() {
        return policyFilterHolders;
    }

    public Map<String, PolicyFilterType> getPolicyFilterTypes() {
        return policyFilterTypes;
    }

    public void setPolicyFilterTypes(Map<String, PolicyFilterType> policyFilterTypes) {
        this.policyFilterTypes = policyFilterTypes;
    }
}
