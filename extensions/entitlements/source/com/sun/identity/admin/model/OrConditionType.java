package com.sun.identity.admin.model;

import java.io.Serializable;

public class OrConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new OrViewCondition();
        vc.setConditionType(this);

        return vc;
    }
}
