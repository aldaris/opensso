package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.DNSNameCondition;
import java.io.Serializable;

public class DnsNameViewCondition
    extends ViewCondition
    implements Serializable {

    private String domainNameMask;

    public EntitlementCondition getEntitlementCondition() {
        DNSNameCondition dnsNameCondition = new DNSNameCondition();
        dnsNameCondition.setDomainNameMask(getDomainNameMask());

        return dnsNameCondition;
    }

    public String getDomainNameMask() {
        return domainNameMask;
    }

    public void setDomainNameMask(String domainNameMask) {
        this.domainNameMask = domainNameMask;
    }

    @Override
    public String toString() {
        return super.toString() + ":{" + domainNameMask + "}";
    }
}
