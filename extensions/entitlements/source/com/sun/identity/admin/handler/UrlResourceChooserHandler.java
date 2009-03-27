package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.ResourceChooserClient;
import com.sun.identity.admin.model.UrlResource;
import com.sun.identity.admin.model.UrlResourceChooserBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

public class UrlResourceChooserHandler implements Serializable {
    private UrlResourceChooserBean urlResourceChooserBean;

    private Object getBean(FacesEvent event) {
        Object o = event.getComponent().getAttributes().get("bean");
        assert(o != null);

        return o;
    }

    private UrlResource getUrlResource(FacesEvent event) {
        UrlResource ur = (UrlResource) event.getComponent().getAttributes().get("urlResource");
        assert(ur != null);

        return ur;
    }

    private ResourceChooserClient getResourceChooserClient(FacesEvent event) {
        ResourceChooserClient rcc = (ResourceChooserClient) event.getComponent().getAttributes().get("resourceChooserClient");
        assert(rcc != null);

        return rcc;
    }

    public void selectListener(ValueChangeEvent event) {
        Boolean b = (Boolean) event.getNewValue();
        boolean selected = b.booleanValue();
        UrlResource ur = getUrlResource(event);
        if (selected) {
            getResourceChooserClient(event).getSelectedResources().add(ur);
        } else {
            getResourceChooserClient(event).getSelectedResources().remove(ur);
        }
    }

    public void addListener(ActionEvent event) {
        urlResourceChooserBean.setAddVisible(true);
    }

    public void addOkListener(ActionEvent event) {
        // TODO
        String pattern = urlResourceChooserBean.getAddPattern();
        UrlResource ur = new UrlResource();
        ur.setPattern(pattern);
        ResourceChooserClient rcc = (ResourceChooserClient)getBean(event);
        rcc.getSelectedResources().add(ur);

        urlResourceChooserBean.setAddVisible(false);
    }

    public void addCancelListener(ActionEvent event) {
        urlResourceChooserBean.setAddVisible(false);
    }
    
    public void searchListener(ActionEvent event) {
        urlResourceChooserBean.setSearchVisible(true);
    }

    public void searchOkListener(ActionEvent event) {
        // TODO
        urlResourceChooserBean.setSearchVisible(false);
    }

    public void SearchCancelListener(ActionEvent event) {
        urlResourceChooserBean.setSearchVisible(false);
    }

    public void exceptionsAddListener(ActionEvent event) {
        UrlResource ur = (UrlResource)getBean(event);
        ur.getExceptions().add(new UrlResource());
    }

    public void exceptionsFinishListener(ActionEvent event) {
        UrlResource ur = (UrlResource)getBean(event);
        ur.setExceptionsShown(false);
    }

    public void exceptionsShowListener(ActionEvent event) {
        UrlResource ur = getUrlResource(event);
        ur.setExceptionsShown(!ur.isExceptionsShown());
    }

    public void setUrlResourceChooserBean(UrlResourceChooserBean urlResourceChooserBean) {
        this.urlResourceChooserBean = urlResourceChooserBean;
    }
}
