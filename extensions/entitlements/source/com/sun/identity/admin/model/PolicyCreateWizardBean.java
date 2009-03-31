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

    private PrivilegeBean privilegeBean = new PrivilegeBean();
    
    private Application application;
    private Map<String, Application> applications;
    private Effect dropConditionEffect;
    private Effect dropSubjectContainerEffect;
    private Effect policyNameInputEffect;
    private Effect policyNameMessageEffect;
    private PolicyCreateSummary policyCreateSummary = new PolicyCreateSummary();
    private int advancedTabsetIndex = 0;

    public PolicyCreateWizardBean() {
        policyCreateSummary.setPolicyCreateWizardBean(this);
    }

    public List<Resource> getSelectedResources() {
        return getPrivilegeBean().getResources();
    }

    public List<Resource> getAvailableResources() {
        return application.getDefaultResources();
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
        getPrivilegeBean().setActions(new DeepCloneableArrayList<Action>(application.getDefaultActions()).deepClone());
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
        ViewCondition lastCondition = privilegeBean.getLastViewCondition();

        if (lastCondition == null) {
            return false;
        }
        if (lastCondition.getConditionType().isExpression()) {
            return false;
        }

        return true;
    }

    public boolean isAndConditionDraggable() {
        ViewCondition lastCondition = privilegeBean.getLastViewCondition();

        if (lastCondition == null) {
            return false;
        }
        if (lastCondition.getConditionType().isExpression()) {
            return false;
        }

        return true;
    }

    public boolean isNotConditionDraggable() {
        ViewCondition lastCondition = privilegeBean.getLastViewCondition();

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
        ViewCondition lastCondition = privilegeBean.getLastViewCondition();

        if (lastCondition == null) {
            return true;
        }
        if (lastCondition.getConditionType().isExpression()) {
            return true;
        }

        return false;

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

    public PrivilegeBean getPrivilegeBean() {
        return privilegeBean;
    }

    public Effect getPolicyNameMessageEffect() {
        return policyNameMessageEffect;
    }

    public void setPolicyNameMessageEffect(Effect policyNameMessageEffect) {
        this.policyNameMessageEffect = policyNameMessageEffect;
    }

    public Effect getPolicyNameInputEffect() {
        return policyNameInputEffect;
    }

    public void setPolicyNameInputEffect(Effect policyNameInputEffect) {
        this.policyNameInputEffect = policyNameInputEffect;
    }
}
