package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.List;

public class NotViewCondition
    extends BaseViewCondition
    implements Serializable {

    private ViewCondition notCondition;

    public EntitlementCondition getEntitlementCondition() {
        // TODO
        return null;
    }

    public ViewCondition getNotCondition() {
        return notCondition;
    }

    public void setNotCondition(ViewCondition notCondition) {
        this.notCondition = notCondition;
    }

}
