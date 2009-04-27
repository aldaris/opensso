package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.TimeCondition;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

public class DateRangeCondition 
    extends ViewCondition
    implements Serializable {

    private Date startDate = new Date();
    private Date endDate = new Date();

    public EntitlementCondition getEntitlementCondition() {
        TimeCondition tc = new TimeCondition();

        String startDateString = getEDateString(startDate);
        tc.setStartDate(startDateString);

        String endDateString = getEDateString(endDate);
        tc.setEndDate(endDateString);

        return tc;
    }

    private String getEDateString(Date date) {
        StringBuffer b = new StringBuffer();
        Formatter f = new Formatter(b);

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        f.format("%4d-%02d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        return b.toString();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return getTitle() + ":{" + getEDateString(startDate) + " > " + getEDateString(endDate) + "}";
    }
}
