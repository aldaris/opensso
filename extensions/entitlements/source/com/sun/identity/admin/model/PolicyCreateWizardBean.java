package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.DeepCloneableArrayList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;

public class PolicyCreateWizardBean
        extends WizardBean
        implements ResourceChooserClient, Serializable {

    private PriviligeBean priviligeBean = new PriviligeBean();
    
    private Application application;
    private Map<String, Application> applications;
    private Effect dropConditionEffect;
    private Effect dropSubjectContainerEffect;
    private PolicyCreateSummary policyCreateSummary = new PolicyCreateSummary();
    private int advancedTabsetIndex = 0;

    public PolicyCreateWizardBean() {
        policyCreateSummary.setPolicyCreateWizardBean(this);
    }

    public List<Resource> getSelectedResources() {
        return getPriviligeBean().getResources();
    }

    public List<Resource> getAvailableResources() {
        return application.getDefaultResources();
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
        getPriviligeBean().setActions(new DeepCloneableArrayList<Action>(application.getDefaultActions()).deepClone());
    }

    public List<SelectItem> getApplicationItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();

        for (Application a : getApplications().values()) {
            items.add(new SelectItem(a, a.getName()));
        }

        return items;
    }

    public Map<String, Application> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, Application> applications) {
        this.applications = applications;
    }

    public Effect getDropConditionEffect() {
        return dropConditionEffect;
    }

    public void setDropConditionEffect(Effect dropConditionEffect) {
        this.dropConditionEffect = dropConditionEffect;
    }

    public Effect getDropSubjectContainerEffect() {
        return dropSubjectContainerEffect;
    }

    public void setDropSubjectContainerEffect(Effect dropSubjectContainerEffect) {
        this.dropSubjectContainerEffect = dropSubjectContainerEffect;
    }

    public boolean isOrConditionDraggable() {
        ViewCondition lastCondition = getLastVisibleCondition();
        if (lastCondition == null) {
            return false;
        }
        if (lastCondition.getConditionType().isExpression()) {
            return false;
        }

        return true;
    }

    public boolean isAndConditionDraggable() {
        ViewCondition lastCondition = getLastVisibleCondition();
        if (lastCondition == null) {
            return false;
        }
        if (lastCondition.getConditionType().isExpression()) {
            return false;
        }

        return true;
    }

    public boolean isNotConditionDraggable() {
        ViewCondition lastCondition = getLastVisibleCondition();
        if (lastCondition == null) {
            return true;
        }
        if (lastCondition instanceof NotViewCondition) {
            return false;
        }
        if (lastCondition instanceof AndViewCondition || lastCondition instanceof OrViewCondition) {
            return true;
        }

        return false;
    }

    public boolean isConditionTypesDraggable() {
        ViewCondition lastCondition = getLastVisibleCondition();

        if (lastCondition == null) {
            return true;
        }
        if (lastCondition.getConditionType().isExpression()) {
            return true;
        }

        return false;

    }
    public ViewCondition getLastVisibleCondition() {
        for (int i = getPriviligeBean().getViewConditions().size() - 1; i >= 0; i--) {
            ViewCondition lastCondition = getPriviligeBean().getViewConditions().get(i);
            if (lastCondition.isVisible()) {
                return lastCondition;
            }
        }

        return null;
    }

    public PolicyCreateSummary getPolicyCreateSummary() {
        return policyCreateSummary;
    }

    public int getAdvancedTabsetIndex() {
        return advancedTabsetIndex;
    }

    public void setAdvancedTabsetIndex(int advancedTabsetIndex) {
        this.advancedTabsetIndex = advancedTabsetIndex;
    }

    public PriviligeBean getPriviligeBean() {
        return priviligeBean;
    }
}
