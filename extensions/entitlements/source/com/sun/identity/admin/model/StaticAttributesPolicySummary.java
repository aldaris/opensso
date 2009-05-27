package com.sun.identity.admin.model;

import com.sun.identity.admin.model.AttributesBean.AttributeType;
import static com.sun.identity.admin.model.AttributesBean.AttributeType.*;

public class StaticAttributesPolicySummary extends AttributesPolicySummary {

    public StaticAttributesPolicySummary(PolicyWizardBean policyWizardBean) {
        super(policyWizardBean);
    }

    public AttributeType getAttributeType() {
        return STATIC;
    }

    public String getLabel() {
        // TODO: localize
        return "Resource Attributes";
    }

    public String getTemplate() {
        return "/admin/facelet/template/policy-summary-static-attributes.xhtml";
    }

    @Override
    public PolicyWizardAdvancedTabIndex getAdvancedTabIndex() {
        return PolicyWizardAdvancedTabIndex.RESOURCE_ATTRIBUTES;
    }
}