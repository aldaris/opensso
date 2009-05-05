package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.NotCondition;
import java.io.Serializable;

public class NotViewCondition
    extends ContainerViewCondition
    implements Serializable {

    public NotViewCondition() {
        super();
    }

    public EntitlementCondition getEntitlementCondition() {
        NotCondition nc = new NotCondition();
        if (getViewConditions() != null && getViewConditions().size() > 0) {
            EntitlementCondition ec = getViewConditions().get(0).getEntitlementCondition();
            nc.setECondition(ec);
        }

        return nc;
    }

    @Override
    public void addViewCondition(ViewCondition vc) {
        assert(getViewConditionsSize() < 1);
        super.addViewCondition(vc);
    }
}