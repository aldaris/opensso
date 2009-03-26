package com.sun.identity.admin.model;

import java.io.Serializable;

public class AndConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new AndViewCondition();
        vc.setConditionType(this);

        return vc;
    }
}
