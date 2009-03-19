package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Condition;
import java.io.Serializable;
import java.util.List;

public class IPCondition 
    extends BaseCondition
    implements Serializable {

    private int[] startIp = new int[4];
    private int[] endIp = new int[4];

    private String domainNameMask;

    public List<Condition> getCondition() {
        return null; // TODO
    }

    public int[] getEndIp() {
        return endIp;
    }

    public void setEndIp(int[] endIp) {
        this.endIp = endIp;
    }

    public String getDomainNameMask() {
        return domainNameMask;
    }

    public void setDomainNameMask(String domainNameMask) {
        this.domainNameMask = domainNameMask;
    }

    public int[] getStartIp() {
        return startIp;
    }

    public void setStartIp(int[] startIp) {
        this.startIp = startIp;
    }
}
