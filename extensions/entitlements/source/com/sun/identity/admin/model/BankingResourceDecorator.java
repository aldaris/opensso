package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import java.util.List;

public class BankingResourceDecorator extends ResourceDecorator {

    private SubjectDao subjectDao;

    public void setSubjectDao(SubjectDao subjectDao) {
        this.subjectDao = subjectDao;
    }

    public void decorate(Resource r) {
        assert (r instanceof BankingResource);
        BankingResource br = (BankingResource) r;

        if (br.equals(br.ALL_ACCOUNTS)) {
            ViewSubject vs = subjectDao.getSubjectType().newViewSubject();
            vs.setName("*");
            br.setOwner(vs);
        } else {

            List<ViewSubject> vss = subjectDao.getViewSubjects(br.getName());
            assert (vss.size() <= 1);

            if (vss.size() == 1) {
                br.setOwner(vss.get(0));
            }
        }
    }
}
