package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.Application;
import com.sun.identity.admin.model.ApplicationType;
import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.UrlResource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationDao implements Serializable {
    private Map<String,ApplicationType> applicationTypes;

    public Map<String,Application> getApplications() {
        Map<String,Application> apps = new HashMap<String,Application>();
        Application a;
        List<Resource> resources;
        UrlResource urlr;

        ApplicationType urlAt = applicationTypes.get("url");

        //TODO - dummy data
        a = new Application();
        a.setName("paycheck");
        a.setApplicationType(urlAt);
        resources = new ArrayList<Resource>();

        urlr = new UrlResource();
        urlr.setPattern("http://paycheck.central.sun.com");
        resources.add(urlr);

        urlr = new UrlResource();
        urlr.setPattern("https://paycheck.central.sun.com/private");
        resources.add(urlr);

        a.setDefaultResources(resources);
        apps.put(a.getName(), a);

        //TODO - dummy data
        a = new Application();
        a.setName("namefinder");
        a.setApplicationType(urlAt);
        resources = new ArrayList<Resource>();

        urlr = new UrlResource();
        urlr.setPattern("http://namefinder.central.sun.com");
        resources.add(urlr);

        urlr = new UrlResource();
        urlr.setPattern("https://namefinder.central.sun.com/private");
        resources.add(urlr);

        a.setDefaultResources(resources);
        apps.put(a.getName(), a);


        return apps;
    }

    public void setApplicationTypes(Map<String,ApplicationType> applicationTypes) {
        this.applicationTypes = applicationTypes;
    }
}