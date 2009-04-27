package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.OrCondition;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class OrViewCondition
    extends ContainerViewCondition
    implements Serializable {

    public OrViewCondition() {
        super();
    }

    public EntitlementCondition getEntitlementCondition() {
        OrCondition oc = new OrCondition();
        Set<EntitlementCondition> ecs = new HashSet<EntitlementCondition>();
        oc.setEConditions(ecs);

        for (ViewCondition vc: getViewConditions()) {
            EntitlementCondition ec = vc.getEntitlementCondition();
            ecs.add(ec);
        }

        return oc;
    }
}
