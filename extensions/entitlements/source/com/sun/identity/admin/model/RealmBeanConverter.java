package com.sun.identity.admin.model;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class RealmBeanConverter implements Converter {

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        RealmBean rb = new RealmBean();
        rb.setName(value);
        return rb;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((RealmBean)value).getName();
    }
}
