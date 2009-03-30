package com.sun.identity.admin.model;

import java.io.Serializable;

public class TimeRangeConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new TimeRangeCondition();
        vc.setConditionType(this);

        return vc;
    }
}
