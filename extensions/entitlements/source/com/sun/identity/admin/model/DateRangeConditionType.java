package com.sun.identity.admin.model;

import java.io.Serializable;

public class DateRangeConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new DateRangeCondition();
        vc.setConditionType(this);

        return vc;
    }
}
