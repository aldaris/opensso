package com.sun.identity.admin.model;

import java.io.Serializable;

public class DaysOfWeekConditionType
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new DaysOfWeekCondition();
        vc.setConditionType(this);

        return vc;
    }
}
