package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public class AndViewSubject extends ContainerViewSubject implements Serializable {
    public EntitlementSubject getEntitlementSubject() {
            // TODO
        return null;
    }

    public String getOperatorString() {
        return "AND";
    }
}
