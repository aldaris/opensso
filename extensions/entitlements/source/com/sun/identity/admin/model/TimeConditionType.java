package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.TimeCondition;

public abstract class TimeConditionType extends ConditionType {
    public abstract ViewCondition newViewCondition(TimeCondition tc);

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert (ec instanceof TimeCondition);
        TimeCondition tc = (TimeCondition) ec;

        ConditionType ct = conditionTypeFactory.getConditionType(AndViewCondition.class);
        assert (ct != null);
        AndViewCondition avc = (AndViewCondition) ct.newViewCondition();
        avc.setConditionType(conditionTypeFactory.getConditionType(AndConditionType.class));

        TimeConditionType tct;

        if (tc.getStartDate() != null) {
            tct = (TimeConditionType)conditionTypeFactory.getConditionType(DateRangeCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            avc.addViewCondition(vc);
        }

        if (tc.getStartTime() != null) {
            tct = (TimeConditionType)conditionTypeFactory.getConditionType(TimeRangeCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            avc.addViewCondition(vc);
        }

        if (tc.getStartDay() != null) {
            tct = (TimeConditionType)conditionTypeFactory.getConditionType(DaysOfWeekCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            avc.addViewCondition(vc);
        }

        if (tc.getEnforcementTimeZone() != null) {
            tct = (TimeConditionType)conditionTypeFactory.getConditionType(TimezoneCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            avc.addViewCondition(vc);
        }

        return avc;
    }
}
