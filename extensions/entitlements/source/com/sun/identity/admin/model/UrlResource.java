package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResource extends Resource implements Serializable {
    private boolean selected;

    public boolean isExceptable() {
        return getName().endsWith("*");
    }

    public String getExceptionPrefix() {
        return getName().substring(0, getName().length()-1);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected = false;
    }

    public UrlResource deepClone() {
        UrlResource ur = new UrlResource();
        ur.setName(getName());
        ur.setSelected(selected);
        
        return ur;
    }
}
