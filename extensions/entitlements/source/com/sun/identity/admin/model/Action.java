package com.sun.identity.admin.model;

import com.sun.identity.admin.DeepCloneable;
import com.sun.identity.admin.Resources;

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
        Resources r = new Resources();
        String title = r.getString(this, "title."+getName(), getName());
        if (title == null) {
            title = r.getString(this, "title._none", getName());
        }
        if (title == null) {
            title = getName();
        }
        return title;
    }
}
