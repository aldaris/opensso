package com.sun.identity.admin.model;

import java.io.Serializable;

public abstract class ViewAttribute implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        // TODO
        return getName();
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ViewAttribute)) {
            return false;
        }
        ViewAttribute va = (ViewAttribute)o;
        return va.getName().equals(getName());
    }
}
