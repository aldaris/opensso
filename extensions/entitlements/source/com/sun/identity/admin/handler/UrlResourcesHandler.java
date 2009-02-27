package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResourceBean;
import com.sun.identity.admin.model.UrlResourcesBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class UrlResourcesHandler implements Serializable {
    private UrlResourcesBean urlResourcesBean;

    public UrlResourcesBean getUrlResourcesBean() {
        return urlResourcesBean;
    }

    public void setUrlResourcesBean(UrlResourcesBean urlResourcesBean) {
        this.urlResourcesBean = urlResourcesBean;
    }

    public void exceptedListener(ActionEvent event) {
        String pattern = getResourcePattern(event);
        UrlResourceBean ur = getResource(pattern);
    }

    private String getResourcePattern(ActionEvent event) {
        String pattern = (String) event.getComponent().getAttributes().get("url-pattern");
        assert(pattern != null);

        return pattern;
    }

    private UrlResourceBean getResource(String pattern) {
        UrlResourcesBean urs = getUrlResourcesBean();
        for (UrlResourceBean urb: urs.getUrlResourceBeans()) {
            if (urb.getPattern().equals(pattern)) {
                return urb;
            }
        }

        return null;
    }

}
