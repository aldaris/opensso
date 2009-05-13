package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.TimeCondition;
import java.util.ArrayList;
import java.util.List;

public abstract class TimeConditionType extends ConditionType {

    public abstract ViewCondition newViewCondition(TimeCondition tc);

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert (ec instanceof TimeCondition);
        TimeCondition tc = (TimeCondition) ec;
        TimeConditionType tct;
        List<ViewCondition> timeViewConditions = new ArrayList<ViewCondition>();

        if (tc.getStartDate() != null && tc.getStartDate().length() > 0) {
            tct = (TimeConditionType) conditionTypeFactory.getConditionType(DateRangeCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            timeViewConditions.add(vc);
        }

        if (tc.getStartTime() != null && tc.getStartTime().length() > 0) {
            tct = (TimeConditionType) conditionTypeFactory.getConditionType(TimeRangeCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            timeViewConditions.add(vc);
        }

        if (tc.getStartDay() != null && tc.getStartDay().length() > 0) {
            tct = (TimeConditionType) conditionTypeFactory.getConditionType(DaysOfWeekCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            timeViewConditions.add(vc);
        }

        if (tc.getEnforcementTimeZone() != null && tc.getEnforcementTimeZone().length() > 0) {
            tct = (TimeConditionType) conditionTypeFactory.getConditionType(TimezoneCondition.class);
            assert (tct != null);
            ViewCondition vc = tct.newViewCondition(tc);
            timeViewConditions.add(vc);
        }

        ViewCondition newVc = null;

        if (timeViewConditions.size() > 1) {
            ConditionType ct = conditionTypeFactory.getConditionType(AndViewCondition.class);
            assert (ct != null);
            AndViewCondition avc = (AndViewCondition) ct.newViewCondition();
            avc.setViewConditions(timeViewConditions);
            newVc = avc;
        } else if (timeViewConditions.size() == 1) {
            newVc = timeViewConditions.get(0);
        }

        return newVc;
    }
}
