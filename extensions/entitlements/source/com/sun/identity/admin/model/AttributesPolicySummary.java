package com.sun.identity.admin.model;

import com.sun.identity.admin.model.AttributesBean.AttributeType;
import java.util.ArrayList;
import java.util.List;

public abstract class AttributesPolicySummary extends PolicySummary {

    public AttributesPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public abstract AttributeType getAttributeType();

    public int getCount() {
        int count = 0;
        for (AttributesBean ab: getPolicyWizardBean().getPrivilegeBean().getAttributesBeans()) {
            if (ab.getAttributeType() == getAttributeType()) {
                int i = ab.getViewAttributes().size();
                count += i;
            }
        }
        return count;
    }

    protected List<ViewAttribute> getViewAttributes() {
        List<ViewAttribute> vas = new ArrayList<ViewAttribute>();
        for (AttributesBean ab: getPolicyWizardBean().getPrivilegeBean().getAttributesBeans()) {
            if (ab.getAttributeType() == getAttributeType()) {
                vas.addAll(ab.getViewAttributes());
            }
        }
        return vas;
    }

    public String getToFormattedString() {
        List<ViewAttribute> vas = getViewAttributes();
        String f = AttributesBean.getToFormattedString(vas);
        return f;
    }

    public String getValue() {
        int count = getCount();
        return Integer.toString(count);
    }

    public boolean isExpandable() {
        int count = getCount();
        return count > 0;
    }

    public String getIcon() {
        return "../image/device.png";
    }

    public PolicyWizardStep getGotoStep() {
        return PolicyWizardStep.ADVANCED;
    }
}
