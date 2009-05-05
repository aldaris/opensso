package com.sun.identity.admin.model;

import com.sun.identity.entitlement.AndSubject;
import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AndViewSubject extends ContainerViewSubject implements Serializable {
    public EntitlementSubject getEntitlementSubject() {
        AndSubject as = new AndSubject();

        Set<EntitlementSubject> eSubjects = new HashSet<EntitlementSubject>();
        for (ViewSubject vs : getViewSubjects()) {
            EntitlementSubject es = vs.getEntitlementSubject();
            eSubjects.add(es);
        }
        as.setESubjects(eSubjects);

        return as;
    }
}
