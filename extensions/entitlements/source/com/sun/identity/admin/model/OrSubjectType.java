package com.sun.identity.admin.model;

import java.io.Serializable;

public class OrSubjectType 
    extends ExpressionSubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        OrViewSubject ovs = new OrViewSubject();
        ovs.setSubjectType(this);

        return ovs;
    }
}
