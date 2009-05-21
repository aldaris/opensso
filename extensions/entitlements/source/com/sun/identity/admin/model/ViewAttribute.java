package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import java.io.Serializable;

public abstract class ViewAttribute implements Serializable {
    private String name;
    private boolean nameEditable = false;

    public String getName() {
        return name;
    }

    public void setEditable(boolean editable) {
        setNameEditable(editable);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
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

    public boolean isNameEditable() {
        if (name == null || name.length() == 0) {
            return true;
        }
        return nameEditable;
    }

    public void setNameEditable(boolean nameEditable) {
        this.nameEditable = nameEditable;
    }
}
