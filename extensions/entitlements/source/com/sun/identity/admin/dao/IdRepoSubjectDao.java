package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.AMIdentityRepository;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;
import com.sun.identity.security.AdminTokenAction;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public abstract class IdRepoSubjectDao extends SubjectDao implements Serializable {

    private int timeout = 5;
    private int limit = 100;

    private SSOToken getSSOToken() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        try {
            SSOTokenManager manager = SSOTokenManager.getInstance();
            SSOToken ssoToken = manager.createSSOToken(request);
            manager.validateToken(ssoToken);
            return ssoToken;
        } catch (SSOException ssoe) {
            throw new RuntimeException(ssoe);
        }
    }

    private SSOToken getAdminSSOToken() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        return (SSOToken) AccessController.doPrivileged(AdminTokenAction.getInstance());
    }

    protected abstract IdType getIdType();

    protected abstract ViewSubject newViewSubject(AMIdentity ami);

    public List<ViewSubject> getViewSubjects() {
        return getViewSubjects("");
    }

    protected AMIdentity getAMIdentity(String name) {
        try {
            return IdUtils.getIdentity(getAdminSSOToken(), name);
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
            AMIdentityRepository repo = new AMIdentityRepository(getSSOToken(), realmName);
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
