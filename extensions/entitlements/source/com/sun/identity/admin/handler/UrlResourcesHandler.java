package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.UrlResource;
import com.sun.identity.admin.model.UrlResourcesBean;
import com.sun.identity.admin.model.ViewEntitlement;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

public class UrlResourcesHandler implements Serializable {
    private UrlResourcesBean urlResourcesBean;

    private UrlResource getUrlResource(FacesEvent event) {
        UrlResource ur = (UrlResource) event.getComponent().getAttributes().get("urlResource");
        assert(ur != null);

        return ur;
    }

    private ViewEntitlement getViewEntitlement(FacesEvent event) {
        ViewEntitlement ve = (ViewEntitlement) event.getComponent().getAttributes().get("viewEntitlement");
        assert(ve != null);

        return ve;
    }

    private List<Resource> getAvailableResources(FacesEvent event) {
        List<Resource> ar = (List<Resource>) event.getComponent().getAttributes().get("availableResources");
        assert(ar != null);

        return ar;
    }

    public void selectListener(ValueChangeEvent event) {
        String[] resourceNames = (String[]) event.getNewValue();

        ViewEntitlement ve = getViewEntitlement(event);
        ve.getResources().clear();
        List<Resource> ar = getAvailableResources(event);

        for (String resourceName: resourceNames) {
            Resource r = new UrlResource();
            r.setName(resourceName);
            ve.getResources().add(r);
        }
    }

    public void addListener(ActionEvent event) {
        urlResourcesBean.setAddPopupVisible(true);
    }

    public void addPopupOkListener(ActionEvent event) {
        String name = urlResourcesBean.getAddPopupName();
        UrlResource ur = new UrlResource();
        ur.setName(name);
        ur.setSelected(true);

        ViewEntitlement ve = getViewEntitlement(event);
        List<Resource> ar = getAvailableResources(event);

        ve.getResources().add(ur);
        ar.add(ur);

        urlResourcesBean.setAddPopupName(null);
        urlResourcesBean.setAddPopupVisible(false);
    }

    public void addExceptionPopupOkListener(ActionEvent event) {
        String name = urlResourcesBean.getAddExceptionPopupName();
        String prefix = urlResourcesBean.getAddExceptionPopupResource().getExceptionPrefix();

        UrlResource ur = new UrlResource();
        ur.setName(prefix+name);
        ur.setSelected(true);

        ViewEntitlement ve = getViewEntitlement(event);
        ve.getExceptions().add(ur);

        urlResourcesBean.setAddExceptionPopupName(null);
        urlResourcesBean.setAddExceptionPopupVisible(false);
    }

    public void removeExceptionListener(ActionEvent event) {
        UrlResource ur = getUrlResource(event);
        ViewEntitlement ve = getViewEntitlement(event);

        ve.getExceptions().remove(ur);
    }

    public void addPopupCancelListener(ActionEvent event) {
        urlResourcesBean.setAddPopupVisible(false);
    }

    public void addExceptionPopupCancelListener(ActionEvent event) {
        urlResourcesBean.setAddExceptionPopupVisible(false);
    }

    public void addExceptionListener(ActionEvent event) {
        UrlResource ur = getUrlResource(event);
        urlResourcesBean.setAddExceptionPopupResource(ur);
        urlResourcesBean.setAddExceptionPopupVisible(true);
    }

    public void setUrlResourcesBean(UrlResourcesBean urlResourcesBean) {
        this.urlResourcesBean = urlResourcesBean;
    }

    public void searchFilterChangedListener(ValueChangeEvent event) {
        String searchFilter = (String)event.getNewValue();
        List<Resource> availableResources = getAvailableResources(event);

        for (Resource r: availableResources) {
            if (!r.getName().startsWith(searchFilter)) {
                r.setVisible(false);
            } else {
                r.setVisible(true);
            }
        }
    }

}
