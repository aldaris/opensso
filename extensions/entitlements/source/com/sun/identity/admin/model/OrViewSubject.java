package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.OrSubject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class OrViewSubject extends ContainerViewSubject implements Serializable {
    public EntitlementSubject getEntitlementSubject() {
        OrSubject os = new OrSubject();

        Set<EntitlementSubject> eSubjects = new HashSet<EntitlementSubject>();
        for (ViewSubject vs: getViewSubjects()) {
            EntitlementSubject es = vs.getEntitlementSubject();
            eSubjects.add(es);
        }
        os.setESubjects(eSubjects);

        return os;
    }
}
