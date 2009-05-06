package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;

public class PolicyFilterHolder implements Serializable {
    private PolicyFilter policyFilter;
    private Map<String,PolicyFilterType> policyFilterTypes;
    private PolicyFilterType policyFilterType;

    public String getPolicyFilterTypeName() {
        return getPolicyFilterType().getName();
    }

    public void setPolicyFilterTypeName(String name) {
        setPolicyFilterType(getPolicyFilterTypes().get(name));
    }

    public List<SelectItem> getPolicyFilterTypeNameItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (PolicyFilterType pft: getPolicyFilterTypes().values()) {
            items.add(new SelectItem(pft.getName(), pft.getTitle()));
        }

        return items;
    }

    public PolicyFilter getPolicyFilter() {
        return policyFilter;
    }

    public void setPolicyFilter(PolicyFilter policyFilter) {
        this.policyFilter = policyFilter;
    }

    public Map<String, PolicyFilterType> getPolicyFilterTypes() {
        return policyFilterTypes;
    }

    public void setPolicyFilterTypes(Map<String, PolicyFilterType> policyFilterTypes) {
        this.policyFilterTypes = policyFilterTypes;
        if (policyFilterTypes != null && policyFilterTypes.size() > 0) {
            setPolicyFilterType(policyFilterTypes.values().iterator().next());
        }
    }

    public PolicyFilterType getPolicyFilterType() {
        return policyFilterType;
    }

    public void setPolicyFilterType(PolicyFilterType policyFilterType) {
        this.policyFilterType = policyFilterType;
        policyFilter = policyFilterType.newPolicyFilter();
    }

}
