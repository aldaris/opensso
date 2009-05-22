package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.VirtualSubject;
import com.sun.identity.entitlement.VirtualSubject.VirtualId;

public class VirtualViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        VirtualId vid = VirtualId.valueOf(getName());
        VirtualSubject vs = vid.newVirtualSubject();
        return vs;
    }

    @Override
    public String getTitle() {
        Resources r = new Resources();
        String title;

        title = r.getString(this, getName() + ".title");
        if (title == null) {
            title = getName();
        }

        return title;
    }
}
