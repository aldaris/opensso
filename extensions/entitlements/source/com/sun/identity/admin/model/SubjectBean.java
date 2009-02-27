package com.sun.identity.admin.model;

import java.io.Serializable;

public class SubjectBean implements Serializable {
    private String name;
    private boolean selected = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        SubjectBean other = (SubjectBean)o;
        if (other.getName().equals(name)) {
            return true;
        }
        return false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
