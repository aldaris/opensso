package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.identity.entitlement.EntitlementSubject;
import java.io.Serializable;

public abstract class SubjectType implements Serializable {
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

    @Override
    public String toString() {
        return getName();
    }

    public String getTitle() {
        Resources r = new Resources();
        String title = r.getString(this, "title");
        if (title == null) {
            title = getName();
        }
        return title;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SubjectType)) {
            return false;
        }
        SubjectType st = (SubjectType)other;

        return st.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}