package com.sun.identity.admin.handler;

import com.icesoft.faces.context.effects.Effect;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.effect.MessageErrorEffect;
import com.sun.identity.admin.model.MessageBean;
import com.sun.identity.admin.model.MessagesBean;
import com.sun.identity.admin.model.Resource;
import com.sun.identity.admin.model.UrlResource;
import com.sun.identity.admin.model.UrlResourcesBean;
import com.sun.identity.admin.model.ViewEntitlement;
import java.io.Serializable;
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
    private MessagesBean messagesBean;

    private UrlResource getUrlResource(FacesEvent event) {
        UrlResource ur = (UrlResource) event.getComponent().getAttributes().get("urlResource");
        assert (ur != null);

        return ur;
    }

    private ViewEntitlement getViewEntitlement(FacesEvent event) {
        ViewEntitlement ve = (ViewEntitlement) event.getComponent().getAttributes().get("viewEntitlement");
        assert (ve != null);

        return ve;
    }

    private List<Resource> getAvailableResources(FacesEvent event) {
        List<Resource> ar = (List<Resource>) event.getComponent().getAttributes().get("availableResources");
        assert (ar != null);

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

    private boolean isResourcesAddable(List<Resource> availableResources) {
        for (Resource r: availableResources) {
            UrlResource ur = (UrlResource)r;
            if (ur.isSuffixable()) {
                return true;
            }
        }

        return false;
    }

    public void addListener(ActionEvent event) {
        List<Resource> ar = getAvailableResources(event);
        if (isResourcesAddable(ar)) {
            urlResourcesBean.setAddPopupVisible(true);
            urlResourcesBean.setAddPopupAvailableResources(ar);
        } else {
            MessageBean mb = new MessageBean();
            Resources r = new Resources();
            mb.setSummary(r.getString(this, "noAddSummary"));
            mb.setDetail(r.getString(this, "noAddDetail"));
            mb.setSeverity(FacesMessage.SEVERITY_WARN);
            messagesBean.addMessageBean(mb);
        }
    }

    private void resetAddPopup() {
        urlResourcesBean.setAddPopupPrefix(null);
        urlResourcesBean.setAddPopupSuffix(null);
        urlResourcesBean.setAddPopupVisible(false);
    }

    public void addPopupOkListener(ActionEvent event) {
        String prefix = urlResourcesBean.getAddPopupPrefix();
        String suffix = urlResourcesBean.getAddPopupSuffix();
        UrlResource ur = new UrlResource();
        ur.setName(prefix+suffix);

        ViewEntitlement ve = getViewEntitlement(event);
        List<Resource> ar = getAvailableResources(event);

        if (!ve.getResources().contains(ur)) {
            ve.getResources().add(ur);
        }
        if (!ar.contains(ur)) {
            ar.add(ur);
        }

        resetAddPopup();
    }

    public void addExceptionPopupOkListener(ActionEvent event) {
        String name = urlResourcesBean.getAddExceptionPopupName();
        String prefix = urlResourcesBean.getAddExceptionPopupResource().getPrefix();

        UrlResource ur = new UrlResource();
        ur.setName(prefix + name);

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
        resetAddPopup();
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
        String searchFilter = (String) event.getNewValue();
        List<Resource> availableResources = getViewEntitlement(event).getAvailableResources();

        for (Resource r : availableResources) {
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

    public void setMessagesBean(MessagesBean messagesBean) {
        this.messagesBean = messagesBean;
    }
}
