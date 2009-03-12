package com.sun.identity.admin.model;

public abstract class BaseAction implements Action {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
