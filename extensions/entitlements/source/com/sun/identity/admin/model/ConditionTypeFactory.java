package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConditionTypeFactory implements Serializable {
    private Map<String,ConditionType> entitlementConditionToConditionTypeMap;
    private Map<String,ConditionType> viewConditionToConditionTypeMap;

    public ConditionType getConditionType(Class c) {
        ConditionType ct;
        String className = c.getName();

        ct = entitlementConditionToConditionTypeMap.get(className);
        if (ct == null) {
            ct = viewConditionToConditionTypeMap.get(className);
        }

        return ct;
    }

    public ViewCondition getViewCondition(EntitlementCondition ec) {
        if (ec == null) {
            return null;
        }

        ConditionType ct = getConditionType(ec.getClass());
        assert (ct != null);
        ViewCondition vc = ct.newViewCondition(ec, this);

        return vc;
    }

    public List<ConditionType> getConditionTypes() {
        return new ArrayList<ConditionType>(viewConditionToConditionTypeMap.values());
    }

    public Map<String, ConditionType> getEntitlementConditionToConditionTypeMap() {
        return entitlementConditionToConditionTypeMap;
    }

    public void setEntitlementConditionToConditionTypeMap(Map<String, ConditionType> entitlementConditionToConditionTypeMap) {
        this.entitlementConditionToConditionTypeMap = entitlementConditionToConditionTypeMap;
    }

    public Map<String, ConditionType> getViewConditionToConditionTypeMap() {
        return viewConditionToConditionTypeMap;
    }

    public void setViewConditionToConditionTypeMap(Map<String, ConditionType> viewConditionToConditionTypeMap) {
        this.viewConditionToConditionTypeMap = viewConditionToConditionTypeMap;
    }
}
