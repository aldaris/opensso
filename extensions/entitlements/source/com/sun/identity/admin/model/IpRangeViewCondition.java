package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.IPCondition;
import java.io.Serializable;

public class IpRangeViewCondition
    extends ViewCondition
    implements Serializable {

    private int[] startIp = new int[4];
    private int[] endIp = new int[4];

    public EntitlementCondition getEntitlementCondition() {
        IPCondition ipc = new IPCondition();

        ipc.setStartIp(getIpString(startIp));
        ipc.setEndIp(getIpString(endIp));

        return ipc;
    }

    private String getIpString(int[] parts) {
        String s = parts[0]+"."+parts[1]+"."+parts[2]+"."+parts[3];
        return s;
    }

    public int[] getEndIp() {
        return endIp;
    }

    public void setEndIp(int[] endIp) {
        this.endIp = endIp;
    }

    public int[] getStartIp() {
        return startIp;
    }

    public void setStartIp(int[] startIp) {
        this.startIp = startIp;
    }

    @Override
    public String toString() {
        return getTitle() + ":{" + getIpString(startIp) + ">" + getIpString(endIp) + "}";
    }
}
