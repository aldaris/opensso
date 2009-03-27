package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PriviligeBean implements Serializable {
    private String name;
    private String description;
    private List<Resource> resources = new ArrayList<Resource>();
    private List<ViewCondition> viewConditions = new ArrayList<ViewCondition>();
    private List<SubjectContainer> subjectContainers = new ArrayList<SubjectContainer>();
    private List<Action> actions;

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

    public List<Resource> getResources() {
        return resources;
    }

    public List<ViewCondition> getViewConditions() {
        return viewConditions;
    }

    public List<SubjectContainer> getSubjectContainers() {
        return subjectContainers;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
