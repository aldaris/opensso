package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResourcesSearchBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class UrlResourcesSearchHandler implements Serializable {
    private UrlResourcesSearchBean urlResourcesSearchBean;

    public void searchListener(ActionEvent event) {
        urlResourcesSearchBean.setVisible(!urlResourcesSearchBean.isVisible());
    }

    public void okListener(ActionEvent event) {
        // TODO
        urlResourcesSearchBean.setVisible(false);
    }

    public void cancelListener(ActionEvent event) {
        urlResourcesSearchBean.setVisible(false);
    }

    public UrlResourcesSearchBean getUrlResourcesSearchBean() {
        return urlResourcesSearchBean;
    }

    public void setUrlResourcesSearchBean(UrlResourcesSearchBean urlResourcesSearchBean) {
        this.urlResourcesSearchBean = urlResourcesSearchBean;
    }
}
