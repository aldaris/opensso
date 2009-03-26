package com.sun.identity.admin.model;

import java.io.Serializable;

public class NotConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new NotViewCondition();
        vc.setConditionType(this);

        return vc;
    }
}
