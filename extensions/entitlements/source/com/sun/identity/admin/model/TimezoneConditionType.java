package com.sun.identity.admin.model;

import java.io.Serializable;

public class TimezoneConditionType
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new TimezoneCondition();
        vc.setConditionType(this);

        return vc;
    }
}
