package com.sun.identity.admin.model;

import com.sun.identity.admin.DeepCloneable;
import java.io.Serializable;

public abstract class Resource implements Serializable, DeepCloneable {
    private String name;
    private boolean visible = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Resource) {
            return ((Resource)o).getName().equals(name);
        }

        return false;
    }
}
