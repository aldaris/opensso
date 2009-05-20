package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.AttributesBean;
import com.sun.identity.admin.model.StaticViewAttribute;
import com.sun.identity.admin.model.ViewAttribute;
import java.io.Serializable;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

public class AttributesHandler implements Serializable {
    private AttributesBean attributesBean;

    public AttributesHandler(AttributesBean attributesBean) {
        this.attributesBean = attributesBean;
    }

    public AttributesBean getAttributesBean() {
        return attributesBean;
    }

    public void setAttributesBean(AttributesBean attributesBean) {
        this.attributesBean = attributesBean;
    }

    protected ViewAttribute getViewAttribute(FacesEvent event) {
        ViewAttribute va = (ViewAttribute) event.getComponent().getAttributes().get("viewAttribute");
        assert (va != null);

        return va;
    }

    public void removeListener(ActionEvent event) {
        ViewAttribute va = getViewAttribute(event);
        attributesBean.getViewAttributes().remove(va);
    }

    public void addListener(ActionEvent event) {
        ViewAttribute va = attributesBean.newViewAttribute();
        va.setEditable(true);
        attributesBean.getViewAttributes().add(va);
    }

    public void addPopupOkListener(ActionEvent event) {
        StaticViewAttribute sva = new StaticViewAttribute(attributesBean);
        attributesBean.getViewAttributes().add(sva);

        // TODO, verify name/val not null

        attributesBean.reset();
    }

    public void addPopupCancelListener(ActionEvent event) {
        attributesBean.reset();
    }

    public void editNameListener(ActionEvent event) {
        ViewAttribute va = (ViewAttribute)getViewAttribute(event);
        va.setNameEditable(true);
    }

    public void nameEditedListener(ValueChangeEvent event) {
        ViewAttribute va = (ViewAttribute)getViewAttribute(event);
        va.setNameEditable(false);
    }
}
