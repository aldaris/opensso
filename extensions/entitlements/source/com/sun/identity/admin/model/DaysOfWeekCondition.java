package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.OrCondition;
import com.sun.identity.entitlement.TimeCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.faces.model.SelectItem;

public class DaysOfWeekCondition
    extends ViewCondition
    implements Serializable {

    public static String[] DAYS = new String[] { "mon", "tue", "wed", "thu", "fri", "sat", "sun" };
    private String[] selectedDays = new String[7];

    public EntitlementCondition getEntitlementCondition() {
        OrCondition oc = new OrCondition();
        Set<EntitlementCondition> orConditions = new HashSet<EntitlementCondition>();
        for (String day: selectedDays) {
            TimeCondition tc = new TimeCondition();
            tc.setStartDay(day);
            tc.setEndDay(day);
            orConditions.add(tc);
        }
        oc.setEConditions(orConditions);

        return oc;
    }

    public List<SelectItem> getDayItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();

        for (String day: DAYS) {
            Resources r = new Resources();
            String label = r.getString(this.getClass(), day);
            SelectItem si = new SelectItem(day, label);
            items.add(si);
        }

        return items;
    }

    public String[] getSelectedDays() {
        return selectedDays;
    }

    public void setSelectedDays(String[] selectedDays) {
        this.selectedDays = selectedDays;
    }

    @Override
    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append(getTitle());
        b.append(":{");

        for (int i = 0; i < selectedDays.length; i++) {
            Resources r = new Resources();
            String label = r.getString(this.getClass(), selectedDays[i]);
            b.append(label);
            if (i < selectedDays.length-1) {
                b.append(",");
            }
        }
        b.append("}");

        return b.toString();
    }
}
