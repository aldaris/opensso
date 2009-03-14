package com.sun.identity.admin.model;

public abstract class ConditionType {
    private String name;
    private String template;

    public abstract ViewCondition newCondition();
}
