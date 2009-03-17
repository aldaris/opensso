package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementSubject;

public abstract class ChooserSubject implements ViewSubject {
    private String name;
    private boolean selected = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object o) {
        ChooserSubject other = (ChooserSubject)o;
        if (other.getName().equals(name)) {
            return true;
        }
        return false;
    }

    // TODO public int hashcode

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public abstract EntitlementSubject getSubject();
}
