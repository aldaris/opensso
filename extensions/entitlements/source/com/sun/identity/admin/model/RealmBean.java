package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import java.io.Serializable;

public class RealmBean implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        Resources r = new Resources();
        String title = r.getString(this, getName() + ".title", getName());
        if (title == null) {
            title = r.getString(this, "_none.title", getName());
        }

        return title;
    }

    @Override
    public String toString() {
        return getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RealmBean)) {
            return false;
        }
        RealmBean other = (RealmBean)o;
        return getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
