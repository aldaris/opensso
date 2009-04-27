package com.sun.identity.admin.model;

import com.sun.identity.admin.DeepCloneable;

public abstract class Action implements DeepCloneable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract Object getValue();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public int hashCode() {
        return (getName()+getValue()).hashCode();
    }

    @Override
    public abstract String toString();

    public String getTitle() {
        return getName();
    }
}
