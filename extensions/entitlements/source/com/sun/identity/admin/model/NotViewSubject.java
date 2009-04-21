package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.NotSubject;
import java.io.Serializable;

public class NotViewSubject extends ContainerViewSubject implements Serializable {

    public EntitlementSubject getEntitlementSubject() {
        NotSubject ns = new NotSubject();

        if (getViewSubjects().size() != 0) {
            ns.setESubject(getViewSubjects().get(0).getEntitlementSubject());
        }

        return ns;
    }

    public String getOperatorString() {
        return "NOT";
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("NOT (");
        if (getViewSubjects().size() > 0) {
            b.append(getViewSubjects().get(0).toString());
        }
        b.append(")");

        return b.toString();
    }

}
