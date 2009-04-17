package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.PolicyManageBean;
import com.sun.identity.admin.model.PrivilegeBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class PolicyManageHandler implements Serializable {
    private PolicyManageBean policyManageBean;

    public PrivilegeBean getPrivilegeBean(ActionEvent event) {
        PrivilegeBean pb = (PrivilegeBean) event.getComponent().getAttributes().get("privilegeBean");
        assert(pb != null);

        return pb;
    }
    public PolicyManageBean getPolicyManageBean() {
        return policyManageBean;
    }

    public void setPolicyManageBean(PolicyManageBean policyManageBean) {
        this.policyManageBean = policyManageBean;
    }

    public void sortTableListener(ActionEvent event) {
        policyManageBean.getPolicyManageTableBean().sort();
    }
}

