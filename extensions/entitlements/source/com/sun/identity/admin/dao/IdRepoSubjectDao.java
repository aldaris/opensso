package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class IdRepoSubjectDao extends SubjectDao {

    private int timeout = 5;
    private int limit = 100;

    protected abstract IdType getIdType();

    protected abstract ViewSubject newViewSubject(AMIdentity ami);

    public List<ViewSubject> getViewSubjects() {
        return getViewSubjects("");
    }

    protected AMIdentity getAMIdentity(String name) {
        try {
            return IdUtils.getIdentity(new Token().getAdminSSOToken(), name);
        } catch (IdRepoException idre) {
            throw new RuntimeException(idre);
        }
    }

    protected ViewSubject getViewSubject(String name) {
        AMIdentity ami = getAMIdentity(name);
        String uuid = ami.getUniversalId();
        Map attrs;

        try {
            attrs = ami.getAttributes();
        } catch (IdRepoException idre) {
            attrs = null;
        } catch (SSOException ssoe) {
            attrs = null;
        }

        ViewSubject vs = newViewSubject(ami);
        vs.setName(uuid);

        return vs;
    }

    protected IdSearchControl getIdSearchControl(String pattern) {
        IdSearchControl idsc = new IdSearchControl();
        idsc.setMaxResults(limit);
        idsc.setTimeOut(timeout);
        idsc.setAllReturnAttributes(true);

        return idsc;
    }

    protected IdSearchResults getIdSearchResults(IdSearchControl idsc, String pattern) {
        IdType idType = getIdType();
        String realmName = "/";

        try {
            AMIdentityRepository repo = new AMIdentityRepository(new Token().getSSOToken(), realmName);
            IdSearchResults results = repo.searchIdentities(idType, pattern, idsc);
            return results;
        } catch (IdRepoException e) {
            throw new RuntimeException(e);
        } catch (SSOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getPattern(String filter) {
        String pattern;
        if (filter == null || filter.length() == 0) {
            pattern = "*";
        } else {
            pattern = "*" + filter + "*";
        }

        return pattern;
    }

    public List<ViewSubject> getViewSubjects(String filter) {
        String pattern = getPattern(filter);

        List<ViewSubject> subjects = new ArrayList<ViewSubject>();

        String realmName = null;
        if (realmName == null) {
            realmName = "/";
        }

        IdSearchControl idsc = getIdSearchControl(pattern);
        IdSearchResults results = getIdSearchResults(idsc, pattern);

        for (Object o : results.getSearchResults()) {
            AMIdentity ami = (AMIdentity) o;
            String uuid = ami.getUniversalId();
            Map attrs;
            try {
                attrs = ami.getAttributes();
            } catch (IdRepoException idre) {
                attrs = null;
            } catch (SSOException ssoe) {
                attrs = null;
            }
            ViewSubject vs = newViewSubject(ami);
            vs.setName(uuid);
            subjects.add(vs);
        }

        return subjects;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
