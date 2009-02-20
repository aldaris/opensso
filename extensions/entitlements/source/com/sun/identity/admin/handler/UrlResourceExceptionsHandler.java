package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResourceExceptionBean;
import com.sun.identity.admin.model.UrlResourcesExceptionsBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class UrlResourceExceptionsHandler implements Serializable {
    private UrlResourcesExceptionsBean getBean(ActionEvent event) {
        UrlResourcesExceptionsBean eb = (UrlResourcesExceptionsBean) event.getComponent().getAttributes().get("bean");
        assert(eb != null);

        return eb;
    }

    public void addListener(ActionEvent event) {
        UrlResourcesExceptionsBean esb = getBean(event);
        esb.getUrlResourceExceptionBeans().add(new UrlResourceExceptionBean());
    }
}
