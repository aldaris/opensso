package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResource;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class UrlResourceExceptionsHandler implements Serializable {
    private UrlResource getBean(ActionEvent event) {
        UrlResource ur = (UrlResource) event.getComponent().getAttributes().get("bean");
        assert(ur != null);

        return ur;
    }

    public void addListener(ActionEvent event) {
        UrlResource ur = getBean(event);
        ur.getExceptions().add(new UrlResource());
    }

    public void finishListener(ActionEvent event) {
        UrlResource ur = getBean(event);
        ur.setExceptionsShown(false);
    }

    public void showListener(ActionEvent event) {
        UrlResource ur = getBean(event);
        ur.setExceptionsShown(!ur.isExceptionsShown());
    }
}
