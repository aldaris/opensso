package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.TimeCondition;
import java.io.Serializable;

public class TimeRangeCondition 
    extends BaseViewCondition
    implements Serializable {

    private int startHour;
    private int startMinute;
    private String startPeriod;
    private int endHour;
    private int endMinute;
    private String endPeriod;

    public EntitlementCondition getEntitlementCondition() {
        TimeCondition tc = new TimeCondition();

        StringBuffer startTime = new StringBuffer();
        if (startPeriod.equals("AM")) {
            startTime.append(startHour);
        } else {
            startTime.append(startHour+12);
        }
        startTime.append(":");
        startTime.append(startMinute);

        StringBuffer endTime = new StringBuffer();
        if (endPeriod.equals("AM")) {
            endTime.append(endHour);
        } else {
            endTime.append(endHour+12);
        }
        endTime.append(":");
        endTime.append(endMinute);

        tc.setStartTime(startTime.toString());
        tc.setEndTime(endTime.toString());

        return tc;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public String getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(String startPeriod) {
        this.startPeriod = startPeriod;
    }

    public String getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(String endPeriod) {
        this.endPeriod = endPeriod;
    }
}
