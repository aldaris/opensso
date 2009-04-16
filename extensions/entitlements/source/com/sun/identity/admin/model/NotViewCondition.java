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


    @Override
    public String getToString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("NOT (");
        if (getViewConditions().size() > 0) {
            b.append(getViewConditions().get(0).toString());
        }
        b.append(")");

        return b.toString();
    }
}