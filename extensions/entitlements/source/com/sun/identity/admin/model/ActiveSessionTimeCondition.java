package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;

public class ActiveSessionTimeCondition 
    extends ViewCondition
    implements Serializable {

    private boolean terminateSession = false;
    private int timeMultiplier = 60;
    private int timeCount = 0;

    public EntitlementCondition getEntitlementCondition() {
        return null; // TODO
    }

    public boolean isTerminateSession() {
        return terminateSession;
    }

    public void setTerminateSession(boolean terminateSession) {
        this.terminateSession = terminateSession;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(int timeCount) {
        this.timeCount = timeCount;
    }

    public int getTimeMultiplier() {
        return timeMultiplier;
    }

    public void setTimeMultiplier(int timeMultiplier) {
        this.timeMultiplier = timeMultiplier;
    }

}
