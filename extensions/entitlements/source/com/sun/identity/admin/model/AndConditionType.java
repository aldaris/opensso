package com.sun.identity.admin.model;

import com.sun.identity.entitlement.AndCondition;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;

public class AndConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new AndViewCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert(ec instanceof AndCondition);
        AndCondition ac = (AndCondition)ec;

        AndViewCondition avc = (AndViewCondition)newViewCondition();

        for (EntitlementCondition childEc: ac.getEConditions()) {
            ViewCondition vc = conditionTypeFactory.getViewCondition(childEc);
            avc.addViewCondition(vc);
        }

        return avc;
    }
}
