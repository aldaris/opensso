package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public class UserSubject
    extends ChooserSubject
    implements Serializable {

    public EntitlementSubject getSubject() {
        com.sun.identity.entitlement.UserSubject eUserSubject = new com.sun.identity.entitlement.UserSubject(this.getName());
        return eUserSubject;
    }
}
