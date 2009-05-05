package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.NotSubject;
import java.io.Serializable;

public class NotViewSubject extends ContainerViewSubject implements Serializable {

    public EntitlementSubject getEntitlementSubject() {
        NotSubject ns = new NotSubject();

        if (getViewSubjects() != null && getViewSubjects().size() != 0) {
            ns.setESubject(getViewSubjects().get(0).getEntitlementSubject());
        }

        return ns;
    }
}
