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
}
