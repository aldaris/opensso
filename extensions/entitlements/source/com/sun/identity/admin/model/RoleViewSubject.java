package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.UserSubject;
import java.io.Serializable;

public class RoleViewSubject 
    extends ViewSubject
    implements Serializable {

    public EntitlementSubject getEntitlementSubject() {
        UserSubject eUserSubject = new UserSubject(this.getName());
        return eUserSubject;
    }
}
