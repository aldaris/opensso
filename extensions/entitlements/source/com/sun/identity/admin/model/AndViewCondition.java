package com.sun.identity.admin.model;

import com.sun.identity.entitlement.AndCondition;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AndViewCondition
    extends BaseViewCondition
    implements Serializable {

    private List<ViewCondition> andViewConditions = new ArrayList<ViewCondition>();

    public EntitlementCondition getEntitlementCondition() {
        AndCondition ac = new AndCondition();
        Set<EntitlementCondition> ecs = new HashSet<EntitlementCondition>();
        ac.setEConditions(ecs);

        for (ViewCondition vc: getAndViewConditions()) {
            EntitlementCondition ec = vc.getEntitlementCondition();
            ecs.add(ec);
        }

        return ac;
    }

    public List<ViewCondition> getAndViewConditions() {
        return andViewConditions;
    }
}
