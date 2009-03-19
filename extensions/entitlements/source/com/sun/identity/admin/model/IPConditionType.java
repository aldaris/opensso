package com.sun.identity.admin.model;

import java.io.Serializable;

public class IPConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newCondition() {
        ViewCondition vc = new IPCondition();
        vc.setConditionType(this);

        return vc;
    }
}
