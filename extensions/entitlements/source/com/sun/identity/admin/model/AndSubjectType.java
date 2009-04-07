package com.sun.identity.admin.model;

import java.io.Serializable;

public class AndSubjectType 
    extends ExpressionSubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        AndViewSubject avs = new AndViewSubject();
        avs.setSubjectType(this);

        return avs;
    }
}
