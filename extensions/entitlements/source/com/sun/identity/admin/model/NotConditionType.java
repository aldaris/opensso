package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.NotCondition;
import java.io.Serializable;

public class NotConditionType
        extends ConditionType
        implements Serializable {

    public ViewCondition newViewCondition() {
        ViewCondition vc = new NotViewCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert (ec instanceof NotCondition);
        NotCondition nc = (NotCondition) ec;

        NotViewCondition nvc = (NotViewCondition) newViewCondition();

        if (nc.getECondition() != null) {
            ViewCondition vc = conditionTypeFactory.getViewCondition(nc.getECondition());
            nvc.addViewCondition(vc);
        }

        return nvc;
    }
}
