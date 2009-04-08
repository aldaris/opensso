package com.sun.identity.admin.model;

import java.io.Serializable;

public class DnsNameConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new DnsNameViewCondition();
        vc.setConditionType(this);

        return vc;
    }
}
