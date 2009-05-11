package com.sun.identity.admin.model;

import com.sun.identity.entitlement.TimeCondition;
import java.io.Serializable;
import java.util.TimeZone;

public class TimezoneConditionType
    extends TimeConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new TimezoneCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(TimeCondition tc) {
        TimezoneCondition tzc = (TimezoneCondition)newViewCondition();
        tzc.setTimezoneId(tc.getEnforcementTimeZone());

        return tzc;
    }
}
