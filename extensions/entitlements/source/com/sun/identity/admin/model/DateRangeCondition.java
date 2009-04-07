package com.sun.identity.admin.model;

import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.TimeCondition;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class DateRangeCondition 
    extends ViewCondition
    implements Serializable {

    private Date startDate;
    private Date endDate;

    public EntitlementCondition getEntitlementCondition() {
        TimeCondition tc = new TimeCondition();

        StringBuffer start = new StringBuffer();
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);

        start.append(startCal.get(Calendar.YEAR));
        start.append("-");
        start.append(startCal.get(Calendar.MONTH));
        start.append("-");
        start.append(startCal.get(Calendar.DAY_OF_MONTH));

        tc.setStartDate(start.toString());

        StringBuffer end = new StringBuffer();
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        start.append(endCal.get(Calendar.YEAR));
        start.append("-");
        start.append(endCal.get(Calendar.MONTH));
        start.append("-");
        start.append(endCal.get(Calendar.DAY_OF_MONTH));

        tc.setEndDate(start.toString());

        return tc;
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

}
