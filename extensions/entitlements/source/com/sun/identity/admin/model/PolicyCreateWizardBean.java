package com.sun.identity.admin.model;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.DeepCloneableArrayList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.event.PhaseListener;
import javax.faces.model.SelectItem;

public class PolicyCreateWizardBean
        extends WizardBean
        implements ResourceChooserClient, Serializable {

    private String name;
    private String description;
    private List<Resource> resources = new ArrayList<Resource>();
    private Application application;
    private Map<String, Application> applications;
    private List<Action> actions;
    private List<ViewCondition> viewConditions = new ArrayList<ViewCondition>();
    private List<SubjectContainer> subjectContainers;
    private Effect dropConditionEffect;
    private Effect dropSubjectContainerEffect;

    public List<Resource> getResources() {
        return resources;
    }

    public List<Resource> getSelectedResources() {
        return getResources();
    }

    public List<Resource> getAvailableResources() {
        return application.getDefaultResources();
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
        actions = new DeepCloneableArrayList(application.getDefaultActions()).deepClone();
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

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ViewCondition> getViewConditions() {
        return viewConditions;
    }

    public void setViewConditions(List<ViewCondition> viewConditions) {
        this.viewConditions = viewConditions;
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

    public List<SubjectContainer> getSubjectContainers() {
        return subjectContainers;
    }

    public void setSubjectContainers(List<SubjectContainer> subjectContainers) {
        this.subjectContainers = subjectContainers;
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
        for (int i = viewConditions.size() - 1; i >= 0; i--) {
            ViewCondition lastCondition = viewConditions.get(i);
            if (lastCondition.isVisible()) {
                return lastCondition;
            }
        }

        return null;
    }
}
