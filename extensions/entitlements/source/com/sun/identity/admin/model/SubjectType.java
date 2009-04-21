package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;

public abstract class SubjectType {
    private String name;
    private String template;
    private String subjectIconUri;
    private boolean expression;

    public abstract ViewSubject newViewSubject();
    public abstract ViewSubject newViewSubject(EntitlementSubject es, SubjectFactory stf);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public boolean isExpression() {
        return expression;
    }

    public void setExpression(boolean expression) {
        this.expression = expression;
    }

    public String getSubjectIconUri() {
        return subjectIconUri;
    }

    public void setSubjectIconUri(String subjectIconUri) {
        this.subjectIconUri = subjectIconUri;
    }
}