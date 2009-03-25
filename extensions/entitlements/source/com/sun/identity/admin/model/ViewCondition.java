package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.util.List;

public interface ViewCondition extends MultiPanelBean {
    public ConditionType getConditionType();
    public void setConditionType(ConditionType ct);
    public String getName();
    public EntitlementCondition getEntitlementCondition();
}
