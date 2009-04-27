package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.IPCondition;
import java.io.Serializable;

public class IpRangeConditionType
    extends ConditionType
    implements Serializable {
    public ViewCondition newViewCondition() {
        ViewCondition vc = new IpRangeViewCondition();
        vc.setConditionType(this);

        return vc;
    }
    
    public ViewCondition newViewCondition(EntitlementCondition ec, ConditionTypeFactory conditionTypeFactory) {
        assert(ec instanceof IPCondition);
        IPCondition ipc = (IPCondition)ec;

        IpRangeViewCondition ipvc = (IpRangeViewCondition)newViewCondition();
        ipvc.setStartIp(parseIp(ipc.getStartIp()));
        ipvc.setEndIp(parseIp(ipc.getEndIp()));

        return ipvc;
    }

    private int[] parseIp(String ipString) {
        String[] ips = ipString.split("\\.");
        assert(ips.length == 4);

        int[] ipi = new int[4];
        for (int i = 0; i < 4; i++) {
            ipi[i] = Integer.valueOf(ips[i]);
        }

        return ipi;
    }

}
