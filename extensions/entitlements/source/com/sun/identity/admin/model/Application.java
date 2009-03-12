package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.List;

public class Application implements Serializable {
    private String name;
    private ApplicationType applicationType;
    private List<Resource> defaultResources;
    private List<Action> defaultActions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    public List<Resource> getDefaultResources() {
        return defaultResources;
    }

    public void setDefaultResources(List<Resource> defaultResources) {
        this.defaultResources = defaultResources;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Action> getDefaultActions() {
        return defaultActions;
    }

    public void setDefaultActions(List<Action> defaultActions) {
        this.defaultActions = defaultActions;
    }

}
