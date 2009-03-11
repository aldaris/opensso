package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.UrlResource;
import com.sun.identity.admin.model.UrlResourcesAddBean;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;

public class UrlResourcesAddHandler implements Serializable {
    private UrlResourcesAddBean urlResourcesAddBean;
    private List<Resource> resources;

    public void addListener(ActionEvent event) {
        getUrlResourcesAddBean().setVisible(true);
    }

    public void okListener(ActionEvent event) {
        // TODO
        String pattern = urlResourcesAddBean.getPattern();
        UrlResource ur = new UrlResource();
        ur.setPattern(pattern);
        getResources().add(ur);

        urlResourcesAddBean.setVisible(false);
    }

    public void cancelListener(ActionEvent event) {
        getUrlResourcesAddBean().setVisible(false);
    }

    public UrlResourcesAddBean getUrlResourcesAddBean() {
        return urlResourcesAddBean;
    }

    public void setUrlResourcesAddBean(UrlResourcesAddBean urlResourcesAddBean) {
        this.urlResourcesAddBean = urlResourcesAddBean;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
