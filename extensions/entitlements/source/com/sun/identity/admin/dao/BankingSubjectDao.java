package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.BankingSubjectType;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.entitlement.BankingSubject;
import com.sun.identity.entitlement.BankingSubject.Banker;
import java.util.ArrayList;
import java.util.List;

public class BankingSubjectDao extends SubjectDao {
    public  List<ViewSubject> getViewSubjects() {
        return getViewSubjects(null);
    }

    public List<ViewSubject> getViewSubjects(String filter) {
        // TODO: filter?
        List<ViewSubject> vss = new ArrayList<ViewSubject>();
        for (Banker b: BankingSubject.Banker.values()) {
            BankingSubjectType bst = (BankingSubjectType)getSubjectType();
            ViewSubject vs = bst.newViewSubject(b.toString());
            vss.add(vs);
        }

        return vss;
    }

    public void decorate(ViewSubject vs) {
        // TODO?
    }
}
