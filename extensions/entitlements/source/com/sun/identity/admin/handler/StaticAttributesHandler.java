package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.AttributesBean;
import com.sun.identity.admin.model.StaticViewAttribute;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

public class StaticAttributesHandler extends AttributesHandler {
    public StaticAttributesHandler(AttributesBean ab) {
        super(ab);
    }

    public void editValueListener(ActionEvent event) {
        StaticViewAttribute sva = (StaticViewAttribute)getViewAttribute(event);
        sva.setValueEditable(true);
    }

    public void valueEditedListener(ValueChangeEvent event) {
        StaticViewAttribute sva = (StaticViewAttribute)getViewAttribute(event);
        sva.setValueEditable(false);
    }
}
