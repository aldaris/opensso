package com.sun.identity.admin.handler;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.validator.ValidatorException;

public interface PolicyNameHandler {
    public void validatePolicyName(FacesContext context, UIComponent component, Object value) throws ValidatorException;
    public void applicationChanged(ValueChangeEvent event);
}
