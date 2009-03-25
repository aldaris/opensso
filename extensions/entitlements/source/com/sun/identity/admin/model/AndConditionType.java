package com.sun.identity.admin.model;

import java.io.Serializable;

public class AndConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newCondition() {
        ViewCondition vc = new AndCondition();
        vc.setConditionType(this);

        return vc;
    }
}
