package com.sun.identity.admin.model;

import java.io.Serializable;

public class OrConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newCondition() {
        ViewCondition vc = new OrCondition();
        vc.setConditionType(this);

        return vc;
    }
}
