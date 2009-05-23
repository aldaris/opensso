package com.sun.identity.admin.model;

import com.sun.identity.admin.dao.SubjectDao;
import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;
import java.util.Map;

public class SubjectFactory implements Serializable {
    private Map<String,SubjectType> entitlementSubjectToSubjectTypeMap;
    private Map<String,SubjectDao> viewSubjectToSubjectDaoMap;
    private Map<String,SubjectType> viewSubjectToSubjectTypeMap;
    private Map<SubjectType,SubjectContainer> subjectTypeToSubjectContainerMap;

    private SubjectType getSubjectType(EntitlementSubject es) {
        String className = es.getClass().getName();
        return entitlementSubjectToSubjectTypeMap.get(className);
    }

    public SubjectDao getSubjectDao(ViewSubject vs) {
        String className = vs.getClass().getName();
        return viewSubjectToSubjectDaoMap.get(className);
    }

    public ViewSubject getViewSubject(EntitlementSubject es) {
        if (es == null) {
            return null;
        }
        
        SubjectType st = getSubjectType(es);
        assert(st != null);
        ViewSubject vs = st.newViewSubject(es, this);

        return vs;
    }

    public SubjectType getSubjectType(String className) {
        SubjectType st = viewSubjectToSubjectTypeMap.get(className);
        return st;
    }

    public SubjectContainer getSubjectContainer(SubjectType st) {
        SubjectContainer sc = subjectTypeToSubjectContainerMap.get(st);
        return sc;
    }

    public void setViewSubjectToSubjectDaoMap(Map<String, SubjectDao> viewSubjectToSubjectDaoMap) {
        this.viewSubjectToSubjectDaoMap = viewSubjectToSubjectDaoMap;
    }

    public void setEntitlementSubjectToSubjectTypeMap(Map<String, SubjectType> entitlementSubjectToSubjectTypeMap) {
        this.entitlementSubjectToSubjectTypeMap = entitlementSubjectToSubjectTypeMap;
    }

    public void setViewSubjectToSubjectTypeMap(Map<String, SubjectType> viewSubjectToSubjectTypeMap) {
        this.viewSubjectToSubjectTypeMap = viewSubjectToSubjectTypeMap;
    }

    public void setSubjectTypeToSubjectContainerMap(Map<SubjectType, SubjectContainer> subjectTypeToSubjectContainerMap) {
        this.subjectTypeToSubjectContainerMap = subjectTypeToSubjectContainerMap;
    }
}
