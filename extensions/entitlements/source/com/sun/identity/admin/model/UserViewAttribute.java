package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;

public class UserViewAttribute extends ViewAttribute {
    public UserViewAttribute() {
        super();
    }

    @Override
    public String getTitle() {
        Resources r = new Resources();
        String t = r.getString(this, getName() + ".title");
        if (t != null) {
            return t;
        }
        return getName();
    }
}
