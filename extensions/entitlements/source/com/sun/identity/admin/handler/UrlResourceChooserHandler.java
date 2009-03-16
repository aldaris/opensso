package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.UrlResource;
import com.sun.identity.admin.model.UrlResourceChooserBean;
import java.io.Serializable;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

public class UrlResourceChooserHandler implements Serializable {
    private UrlResourceChooserBean urlResourceChooserBean;

    private UrlResource getBean(FacesEvent event) {
        UrlResource ur = (UrlResource) event.getComponent().getAttributes().get("bean");
        assert(ur != null);

        return ur;
    }

    public void selectListener(ValueChangeEvent event) {
        Boolean b = (Boolean) event.getNewValue();
        boolean selected = b.booleanValue();
        UrlResource ur = getBean(event);
        if (selected) {
            urlResourceChooserBean.getSelectedResources().add(ur);
        } else {
            urlResourceChooserBean.getSelectedResources().remove(ur);
        }
    }

    public UrlResourceChooserBean getUrlResourceChooserBean() {
        return urlResourceChooserBean;
    }

    public void setUrlResourceChooserBean(UrlResourceChooserBean urlResourceChooserBean) {
        this.urlResourceChooserBean = urlResourceChooserBean;
    }
}
