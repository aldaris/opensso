package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.admin.model.VirtualSubjectType;
import com.sun.identity.entitlement.VirtualSubject;
import com.sun.identity.entitlement.VirtualSubject.VirtualId;
import java.util.ArrayList;
import java.util.List;

public class VirtualSubjectDao extends SubjectDao {
    public  List<ViewSubject> getViewSubjects() {
        return getViewSubjects(null);
    }

    public List<ViewSubject> getViewSubjects(String filter) {
        // TODO: filter?
        List<ViewSubject> vss = new ArrayList<ViewSubject>();
        for (VirtualId vid: VirtualSubject.VirtualId.values()) {
            VirtualSubjectType vst = (VirtualSubjectType)getSubjectType();
            ViewSubject vs = vst.newViewSubject(vid.toString());
            vss.add(vs);
        }

        return vss;
    }

    public void decorate(ViewSubject vs) {
        // TODO?
    }
}
