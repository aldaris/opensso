package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.OrCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OrViewCondition
    extends BaseViewCondition
    implements Serializable {

    private List<ViewCondition> orViewConditions = new ArrayList<ViewCondition>();

    public EntitlementCondition getEntitlementCondition() {
        OrCondition oc = new OrCondition();
        Set<EntitlementCondition> ecs = new HashSet<EntitlementCondition>();
        oc.setEConditions(ecs);

        for (ViewCondition vc: getOrViewConditions()) {
            EntitlementCondition ec = vc.getEntitlementCondition();
            ecs.add(ec);
        }

        return oc;
    }

    public List<ViewCondition> getOrViewConditions() {
        return orViewConditions;
    }
}
