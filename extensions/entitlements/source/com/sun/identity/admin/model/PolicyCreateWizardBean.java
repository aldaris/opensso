package com.sun.identity.admin.model;

import com.sun.identity.admin.DeepCloneableArrayList;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;

public class PolicyCreateWizardBean
    extends WizardBean
    implements ResourceChooserClient, Serializable {

    private List<Resource> resources = new ArrayList<Resource>();
    private Application application;
    private Map<String,Application> applications;
    private List<Action> actions;

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

        for (Application a: getApplications().values()) {
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

}
