package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.OrSubject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OrViewSubject extends ContainerViewSubject implements Serializable {

    public EntitlementSubject getEntitlementSubject() {
        OrSubject os = new OrSubject();

        Set<EntitlementSubject> eSubjects = new HashSet<EntitlementSubject>();
        for (ViewSubject vs : getViewSubjects()) {
            EntitlementSubject es = vs.getEntitlementSubject();
            eSubjects.add(es);
        }
        os.setESubjects(eSubjects);

        return os;
    }

    public String getOperatorString() {
        return "OR";
    }
    
    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("OR (");

        if (getViewSubjects().size() > 0) {
            for (Iterator<ViewSubject> i = getViewSubjects().iterator(); i.hasNext();) {
                b.append(i.next().toString());
                if (i.hasNext()) {
                    b.append(",");
                }
            }
        }
        b.append(")");

        return b.toString();
    }
}
