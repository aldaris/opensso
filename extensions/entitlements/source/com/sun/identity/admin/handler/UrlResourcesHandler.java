package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.effect.MessageErrorEffect;
import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.UrlResource;
import com.sun.identity.admin.model.UrlResourcesBean;
import com.sun.identity.admin.model.ViewEntitlement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

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
        ViewEntitlement ve = getViewEntitlement(event);
        /*
        List<Resource> resources = (List<Resource>) event.getNewValue();
        ve.setResources(resources);
        */
        Resource[] resourceArray = (Resource[]) event.getNewValue();
        ve.setResourceArray(resourceArray);
   }

    public void addListener(ActionEvent event) {
        urlResourcesBean.setAddPopupVisible(true);
        List<Resource> ar = getAvailableResources(event);
        urlResourcesBean.setAddPopupAvailableResources(ar);
    }

    public void addPopupUpdateAvailableResourcesListener(ValueChangeEvent event) {
        List<Resource> ar = getAvailableResources(event);
        String filter = (String)event.getNewValue();

        List<Resource> filteredResources = filterList(ar, filter);
        urlResourcesBean.setAddPopupAvailableResources(filteredResources);
    }

    private List filterList(List l, String f) {
        if (f == null || f.length() == 0) {
            return l;
        }
        List newList = new ArrayList();
        for (Object o: l) {
            if (o.toString().startsWith(f)) {
                newList.add(o);
            }
        }

        return newList;
    }

    public void addPopupOkListener(ActionEvent event) {
        String name = urlResourcesBean.getAddPopupName();
        UrlResource ur = new UrlResource();
        ur.setName(name);

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


    public void validateResources(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        List<Resource> resources = (List<Resource>) value;

        if (resources == null || resources.size() == 0) {
            FacesMessage msg = new FacesMessage();
            // TODO: localize
            msg.setSummary("Select a resource");
            msg.setDetail("At least one resource must be selected");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);

            Effect e;

            e = new MessageErrorEffect();
            urlResourcesBean.setResourcesMessageEffect(e);

            throw new ValidatorException(msg);
        }
    }
}
