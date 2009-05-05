package com.sun.identity.admin.model;

import com.sun.identity.entitlement.AndSubject;
import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public class AndSubjectType 
    extends SubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        AndViewSubject avs = new AndViewSubject();
        avs.setSubjectType(this);

        return avs;
    }

    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert (es instanceof AndSubject);
        AndSubject as = (AndSubject) es;

        AndViewSubject avs = (AndViewSubject) newViewSubject();

        if (as.getESubjects() != null) {
            for (EntitlementSubject childEs : as.getESubjects()) {
                ViewSubject vs = stf.getViewSubject(childEs);
                avs.addViewSubject(vs);
            }
        }

        return avs;
    }

}
