package com.sun.identity.admin.model;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class UrlResourceConverter implements Converter {

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        UrlResource urlResource = new UrlResource();
        urlResource.setName(value);

        return urlResource;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value.toString();
    }
}
