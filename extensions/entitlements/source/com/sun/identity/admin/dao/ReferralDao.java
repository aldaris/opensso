package com.sun.identity.admin.dao;

import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.RealmBean;
import com.sun.identity.admin.model.RealmsBean;
import com.sun.identity.admin.model.ReferralBean;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.ReferralPrivilege;
import com.sun.identity.entitlement.ReferralPrivilegeManager;
import java.io.Serializable;
import javax.security.auth.Subject;

public class ReferralDao implements Serializable {

    private ReferralPrivilegeManager getReferralPrivilegeManager() {
        RealmBean rb = RealmsBean.getInstance().getRealmBean();
        Subject adminSubject = new Token().getAdminSubject();

        ReferralPrivilegeManager rpm = new ReferralPrivilegeManager(rb.getName(), adminSubject);
        return rpm;
    }

    public void add(ReferralBean rb) {
        ReferralPrivilegeManager rpm = getReferralPrivilegeManager();
        ReferralPrivilege rp = rb.toReferrealPrivilege();

        try {
            rpm.add(rp);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }
}
