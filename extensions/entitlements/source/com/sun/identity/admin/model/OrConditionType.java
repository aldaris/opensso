package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.OrCondition;
import java.io.Serializable;

public class OrConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new OrViewCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert(ec instanceof OrCondition);
        OrCondition oc = (OrCondition)ec;

        OrViewCondition ovc = (OrViewCondition)newViewCondition();

        for (EntitlementCondition childEc: oc.getEConditions()) {
            ViewCondition vc = conditionTypeFactory.getViewCondition(childEc);
            ovc.addViewCondition(vc);
        }

        return ovc;
    }
}
