package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AndCondition 
    extends BaseCondition
    implements Serializable {

    private List<ViewCondition> andConditions = new ArrayList<ViewCondition>();

    public EntitlementCondition getEntitlementCondition() {
        // TODO
        return null;
    }

    public List<ViewCondition> getAndConditions() {
        return andConditions;
    }
}
