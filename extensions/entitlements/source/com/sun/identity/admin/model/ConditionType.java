package com.sun.identity.admin.model;

public abstract class ConditionType {
    private String name;
    private String template;

    public abstract ViewCondition newCondition();

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
}
