package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.IPCondition;
import java.io.Serializable;

public class IPViewCondition
    extends ViewCondition
    implements Serializable {

    private int[] startIp = new int[4];
    private int[] endIp = new int[4];

    public EntitlementCondition getEntitlementCondition() {
        IPCondition ipc = new IPCondition();

        ipc.setStartIp(getStartIpString());
        ipc.setEndIp(getEndIpString());

        return ipc;
    }

    public String getStartIpString() {
        String s = startIp[0]+"."+startIp[1]+"."+startIp[2]+"."+startIp[3];
        return s;
    }

    public String getEndIpString() {
        String s = endIp[0]+"."+endIp[1]+"."+endIp[2]+"."+endIp[3];
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
}
