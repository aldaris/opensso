package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.identity.entitlement.EntitlementCondition;

public abstract class ConditionType {
    private String name;
    private String template;
    private String conditionIconUri;
    private boolean expression;

    public abstract ViewCondition newViewCondition();
    public abstract ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory);

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

    public String getConditionIconUri() {
        return conditionIconUri;
    }

    public void setConditionIconUri(String conditionIconUri) {
        this.conditionIconUri = conditionIconUri;
    }

    public boolean isExpression() {
        return expression;
    }

    public void setExpression(boolean expression) {
        this.expression = expression;
    }

    public String getTitle() {
        Resources r = new Resources();
        String title = r.getString("conditionType."+getName());
        if (title == null) {
            title = getName();
        }
        return title;
    }
}
