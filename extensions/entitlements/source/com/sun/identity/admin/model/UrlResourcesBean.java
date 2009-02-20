package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlResourcesBean implements Serializable {
    private List<UrlResourceBean> urlResources = new ArrayList<UrlResourceBean>();

    public UrlResourcesBean() {
        UrlResourceBean ur;

        // TODO: dummy data
        ur = new UrlResourceBean();
        ur.setPattern("http://www.sun.com");
        urlResources.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://paycheck.sun.com/profile");
        urlResources.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://cal-amer.sun.com");
        urlResources.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://im-amer.sun.com/jtb");
        urlResources.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://sunweb.central:8080");
        urlResources.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://www.bankofamerica.com/account");
        urlResources.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://www.att.com/login");
        urlResources.add(ur);
        // TODO
    }

    public List<UrlResourceBean> getUrlResources() {
        return urlResources;
    }

    public void setUrlResources(List<UrlResourceBean> urlResources) {
        this.urlResources = urlResources;
    }

}
