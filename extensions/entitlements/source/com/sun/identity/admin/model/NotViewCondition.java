package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;

public class NotViewCondition
    extends ContainerViewCondition
    implements Serializable {

    public NotViewCondition() {
        super();
    }

    public EntitlementCondition getEntitlementCondition() {
        // TODO
        return null;
    }

    @Override
    public void addViewCondition(ViewCondition vc) {
        assert(getViewConditionsSize() < 1);
        super.addViewCondition(vc);
    }
}