package com.sun.identity.admin.model;

import com.sun.identity.entitlement.DNSNameCondition;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;

public class DnsNameConditionType 
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new DnsNameViewCondition();
        vc.setConditionType(this);

        return vc;
    }

    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert(ec instanceof DNSNameCondition);
        DNSNameCondition dnsnc = (DNSNameCondition)ec;

        DnsNameViewCondition dnsnvc = (DnsNameViewCondition)newViewCondition();
        dnsnvc.setDomainNameMask(dnsnc.getDomainNameMask());

        return dnsnvc;
    }
}
