package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.sun.identity.admin.model.IdRepoUserViewSubject;
import com.sun.identity.admin.model.ViewSubject;
import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdRepoException;
import com.sun.identity.idm.IdSearchControl;
import com.sun.identity.idm.IdSearchOpModifier;
import com.sun.identity.idm.IdSearchResults;
import com.sun.identity.idm.IdType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IdRepoUserSubjectDao extends IdRepoSubjectDao {

    private String namingAttribute;

    protected IdType getIdType() {
        return IdType.USER;
    }

    public String getNamingAttribute() {
        return namingAttribute;
    }

    public void setNamingAttribute(String namingAttribute) {
        this.namingAttribute = namingAttribute;
    }

    @Override
    protected IdSearchResults getIdSearchResults(IdSearchControl idsc, String pattern) {
        if (!pattern.equals("*")) {
            pattern = "*";
        }

        return super.getIdSearchResults(idsc, pattern);
    }

    @Override
    protected IdSearchControl getIdSearchControl(String pattern) {
        IdSearchControl idsc = super.getIdSearchControl(pattern);
        Map searchMap = new HashMap();

        searchMap.put(getNamingAttribute(), Collections.singleton(pattern));
        searchMap.put("cn", Collections.singleton(pattern));
        searchMap.put("sn", Collections.singleton(pattern));
        searchMap.put("employeeNumber", Collections.singleton(pattern));

        idsc.setSearchModifiers(IdSearchOpModifier.OR, searchMap);
        return idsc;
    }

    @Override
    protected void decorate(ViewSubject vs, Map attrs) {
        super.decorate(vs, attrs);

        if (attrs != null) {
            IdRepoUserViewSubject idus = (IdRepoUserViewSubject) vs;
            Set snSet = (Set) attrs.get("sn");
            if (snSet != null && snSet.size() > 0) {
                String sn = (String) snSet.iterator().next();
                idus.setSn(sn);
            }
            Set enSet = (Set) attrs.get("employeeNumber");
            if (enSet != null && enSet.size() > 0) {
                String en = (String) enSet.iterator().next();
                idus.setEmployeeNumber(en);
            }
        }
    }
}
