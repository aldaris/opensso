package com.sun.identity.admin.model;

import java.io.Serializable;

public class IPConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new IPViewCondition();
        vc.setConditionType(this);

        return vc;
    }
}
