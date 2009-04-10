package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Entitlement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewEntitlement implements Serializable {
    private List<Resource> resources = new ArrayList<Resource>();
    private List<Resource> exceptions = new ArrayList<Resource>();
    private List<Action> actions = new ArrayList<Action>();
    private ViewApplication viewApplication;

    public List<Resource> getResources() {
        return resources;
    }

    public List<Resource> getExceptions() {
        return exceptions;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Resource[] getResourceArray() {
        return resources.toArray(new Resource[0]);
    }

    public void setResourceArray(Resource[] resourceArray) {
        resources = Arrays.asList(resourceArray);
    }

    public Entitlement getEntitlement() {
        Entitlement e = new Entitlement();

        e.setResourceNames(getResourceSet());
        e.setExcludedResourceNames(getExceptionSet());
        e.setActionValues(getActionMap());
        e.setApplicationName(viewApplication.getName());
        
        return e;
    }

    private Set<String> getResourceSet() {
        Set<String> resourceSet = new HashSet<String>();

        for (Resource r: resources) {
            resourceSet.add(r.getName());
        }

        return resourceSet;
    }

    private Set<String> getExceptionSet() {
        Set<String> exceptionSet = new HashSet<String>();

        for (Resource r: exceptions) {
            exceptionSet.add(r.getName());
        }

        return exceptionSet;
    }

    private Map<String,Boolean> getActionMap() {
        Map<String,Boolean> actionMap = new HashMap<String,Boolean>();

        for (Action a: actions) {
            actionMap.put(a.getName(), a.getValue());
        }

        return actionMap;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public ViewApplication getViewApplication() {
        return viewApplication;
    }

    public void setViewApplication(ViewApplication viewApplication) {
        this.viewApplication = viewApplication;
    }
}
