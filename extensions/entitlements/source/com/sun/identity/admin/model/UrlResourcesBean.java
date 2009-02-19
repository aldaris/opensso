package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlResourcesBean implements Serializable {
    private List<UrlResource> urlResources = new ArrayList<UrlResource>();

    public UrlResourcesBean() {
        UrlResource ur;

        // TODO: dummy data
        ur = new UrlResource();
        ur.setUrlPattern("http://www.sun.com");
        urlResources.add(ur);

        ur = new UrlResource();
        ur.setUrlPattern("http://paycheck.sun.com/profile");
        urlResources.add(ur);

        ur = new UrlResource();
        ur.setUrlPattern("http://cal-amer.sun.com");
        urlResources.add(ur);
        // TODO
    }

    public List<UrlResource> getUrlResources() {
        return urlResources;
    }

    public void setUrlResources(List<UrlResource> urlResources) {
        this.urlResources = urlResources;
    }

}
