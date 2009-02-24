package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResourceExceptionBean;
import com.sun.identity.admin.model.UrlResourceExceptionsBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class UrlResourceExceptionsHandler implements Serializable {
    private UrlResourceExceptionsBean getBean(ActionEvent event) {
        UrlResourceExceptionsBean eb = (UrlResourceExceptionsBean) event.getComponent().getAttributes().get("bean");
        assert(eb != null);

        return eb;
    }

    public void addListener(ActionEvent event) {
        UrlResourceExceptionsBean esb = getBean(event);
        esb.getUrlResourceExceptionBeans().add(new UrlResourceExceptionBean());
    }

    public void finishListener(ActionEvent event) {
        UrlResourceExceptionsBean esb = getBean(event);
        esb.setShown(false);
    }

    public void showListener(ActionEvent event) {
        UrlResourceExceptionsBean esb = getBean(event);
        esb.setShown(!esb.isShown());
    }
}
