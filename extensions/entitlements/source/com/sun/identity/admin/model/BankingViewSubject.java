package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.identity.entitlement.BankingSubject;
import com.sun.identity.entitlement.EntitlementSubject;

public class BankingViewSubject extends ViewSubject {
    public EntitlementSubject getEntitlementSubject() {
        BankingSubject bs = new BankingSubject();
        bs.setID(getName());

        return bs;
    }

    @Override
    public String getTitle() {
        Resources r = new Resources();
        String title = r.getString(this, getName() + ".title");

        return title;
    }
}
