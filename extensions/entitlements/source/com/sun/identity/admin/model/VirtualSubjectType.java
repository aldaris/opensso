package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.VirtualSubject;
import com.sun.identity.entitlement.VirtualSubject.VirtualId;
import java.io.Serializable;

public class VirtualSubjectType
        extends SubjectType
        implements Serializable {

    public ViewSubject newViewSubject() {
        ViewSubject vs = new VirtualViewSubject();
        vs.setSubjectType(this);

        return vs;
    }

    public ViewSubject newViewSubject(String name) {
        ViewSubject vs = new VirtualViewSubject();
        vs.setSubjectType(this);
        vs.setName(VirtualId.valueOf(name).toString());

        return vs;
    }


    public ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf) {
        assert (es instanceof VirtualSubject);
        VirtualSubject virtualSubject = (VirtualSubject)es;

        ViewSubject vs = newViewSubject();
        vs.setName(virtualSubject.getVirtualId().toString());

        return vs;
    }
}
