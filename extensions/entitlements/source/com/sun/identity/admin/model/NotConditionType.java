package com.sun.identity.admin.model;

import java.io.Serializable;

public class NotConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newCondition() {
        ViewCondition vc = new NotCondition();
        vc.setConditionType(this);

        return vc;
    }
}
