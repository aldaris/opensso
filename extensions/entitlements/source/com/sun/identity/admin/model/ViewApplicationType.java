package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.List;

public class ViewApplicationType implements Serializable {
    private String name;
    private List<Action> actions;
    private String resourceTemplate;
    private String resourceClassName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getResourceTemplate() {
        return resourceTemplate;
    }

    public void setResourceTemplate(String resourceTemplate) {
        this.resourceTemplate = resourceTemplate;
    }

    public String getResourceClassName() {
        return resourceClassName;
    }

    public void setResourceClassName(String resourceClassName) {
        this.resourceClassName = resourceClassName;
    }
}
