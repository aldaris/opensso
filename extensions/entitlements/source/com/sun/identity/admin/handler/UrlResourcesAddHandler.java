package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResourceBean;
import com.sun.identity.admin.model.UrlResourcesAddBean;
import com.sun.identity.admin.model.UrlResourcesBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class UrlResourcesAddHandler implements Serializable {
    private UrlResourcesAddBean urlResourcesAddBean;
    private UrlResourcesBean urlResourcesBean;

    public void addListener(ActionEvent event) {
        getUrlResourcesAddBean().setVisible(true);
    }

    public void okListener(ActionEvent event) {
        // TODO
        String pattern = urlResourcesAddBean.getPattern();
        UrlResourceBean urb = new UrlResourceBean();
        urb.setPattern(pattern);
        urlResourcesBean.getUrlResourceBeans().add(urb);

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

    public UrlResourcesBean getUrlResourcesBean() {
        return urlResourcesBean;
    }

    public void setUrlResourcesBean(UrlResourcesBean urlResourcesBean) {
        this.urlResourcesBean = urlResourcesBean;
    }
}
