package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewEntitlement implements Serializable {
    private List<Resource> resources = new ArrayList<Resource>();
    private List<Resource> exceptions = new ArrayList<Resource>();

    public List<Resource> getResources() {
        return resources;
    }

    public List<Resource> getExceptions() {
        return exceptions;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
