package com.sun.identity.admin.model;

import java.io.Serializable;

public class NotSubjectType 
    extends ExpressionSubjectType
    implements Serializable {

    public ViewSubject newViewSubject() {
        NotViewSubject nvs = new NotViewSubject();
        nvs.setSubjectType(this);

        return nvs;
    }
}
