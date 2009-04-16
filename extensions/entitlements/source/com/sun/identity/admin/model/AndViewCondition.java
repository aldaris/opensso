package com.sun.identity.admin.model;

import com.sun.identity.entitlement.AndCondition;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AndViewCondition
    extends ContainerViewCondition
    implements Serializable {

    public AndViewCondition() {
        super();
    }

    public EntitlementCondition getEntitlementCondition() {
        AndCondition ac = new AndCondition();
        Set<EntitlementCondition> ecs = new HashSet<EntitlementCondition>();
        ac.setEConditions(ecs);

        for (ViewCondition vc: getViewConditions()) {
            EntitlementCondition ec = vc.getEntitlementCondition();
            ecs.add(ec);
        }

        return ac;
    }

    @Override
    public String getToString() {
        return toString();
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append("AND (");

        if (getViewConditions().size() > 0) {
            for (Iterator<ViewCondition> i = getViewConditions().iterator(); i.hasNext();) {
                b.append(i.next().toString());
                if (i.hasNext()) {
                    b.append(",");
                }
            }
        }
        b.append(")");

        return b.toString();
    }

}
