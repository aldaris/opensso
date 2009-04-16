package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;

public class ActiveSessionTimeConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new ActiveSessionTimeCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        // TODO
        return null;
    }
}
