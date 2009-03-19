package com.sun.identity.admin.model;

import java.io.Serializable;

public class ActiveSessionTimeConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newCondition() {
        ViewCondition vc = new ActiveSessionTimeCondition();
        vc.setConditionType(this);

        return vc;
    }
}
