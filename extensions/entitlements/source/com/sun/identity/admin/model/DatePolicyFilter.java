package com.sun.identity.admin.model;

import com.sun.identity.entitlement.util.PrivilegeSearchFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;

public abstract class DatePolicyFilter extends PolicyFilter {

    public Verb getVerb() {
        return verb;
    }

    public void setVerb(Verb verb) {
        this.verb = verb;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public abstract String getPrivilegeAttributeName();

    public static enum Verb {
        WITHIN_LAST,
        EXACTLY,
        BEFORE,
        AFTER,
        TODAY,
        YESTERDAY,
        THIS_WEEK,
        THIS_MONTH,
        THIS_YEAR;

        public String getTitle() {
            // TODO
            return toString();
        }

        public List<SelectItem> getItems() {
            List<SelectItem> items = new ArrayList<SelectItem>();
            for (Verb v: values()) {
               items.add(new SelectItem(v, v.getTitle()));
            }

            return items;
        }
    }

    public static enum Unit {
        MINUTES(60*1000),
        HOURS(60*60*1000),
        DAYS(24*60*60*1000),
        WEEKS(7*24*60*60*1000),
        MONTHS(30*24*60*60*1000),
        YEARS(365*24*60*60*1000);

        private long multiplier;

        Unit(int multiplier) {
            this.multiplier = multiplier;
        }

        public String getTitle() {
            // TODO
            return toString();
        }

        public List<SelectItem> getItems() {
            List<SelectItem> items = new ArrayList<SelectItem>();
            for (Unit u: values()) {
               items.add(new SelectItem(u, u.getTitle()));
            }

            return items;
        }

        public long getMultiplier() {
            return multiplier;
        }
    }

    private Verb verb = Verb.WITHIN_LAST;
    private Unit unit = Unit.DAYS;
    private int value = 30;
    private Date date = new Date();

    public String getVerbString() {
        return verb.toString();
    }

    public void setVerbString(String verbString) {
        verb = Verb.valueOf(verbString);
    }

    public String getUnitString() {
        return unit.toString();
    }

    public void setUnitString(String unitString) {
        unit = Unit.valueOf(unitString);
    }

    public boolean isValueShown() {
        if (verb == Verb.WITHIN_LAST) {
            return true;
        }

        return false;
    }

    public boolean isDateShown() {
        if (verb == Verb.EXACTLY || verb == Verb.AFTER || verb == Verb.BEFORE) {
            return true;
        }

        return false;
    }

    public List<PrivilegeSearchFilter> getPrivilegeSearchFilters() {
        List<PrivilegeSearchFilter> psfs = new ArrayList<PrivilegeSearchFilter>();

        if (verb == Verb.WITHIN_LAST) {
            String attrName = getPrivilegeAttributeName();
            long longValue = System.currentTimeMillis() - unit.getMultiplier()*value;
            int op = PrivilegeSearchFilter.GREATER_THAN_OPERATOR;
            psfs.add(new PrivilegeSearchFilter(attrName, longValue, op));
        }

        return psfs;
    }

}
