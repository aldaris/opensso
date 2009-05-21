package com.sun.identity.admin.model;

import com.sun.identity.admin.model.AttributesBean.AttributeType;
import static com.sun.identity.admin.model.AttributesBean.AttributeType.*;

public class UserAttributesPolicySummary extends AttributesPolicySummary {

    public UserAttributesPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public AttributeType getAttributeType() {
        return USER;
    }

    public String getLabel() {
        // TODO: localize
        return "User Attributes";
    }

    public String getTemplate() {
        return "/admin/facelet/template/policy-summary-user-attributes.xhtml";
    }

    @Override
    public PolicyWizardAdvancedTabIndex getAdvancedTabIndex() {
        return PolicyWizardAdvancedTabIndex.USER_ATTRIBUTES;
    }
}
