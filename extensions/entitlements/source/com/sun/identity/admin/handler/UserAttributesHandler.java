package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DropEvent;
import com.sun.identity.admin.model.AttributesBean;
import com.sun.identity.admin.model.ViewAttribute;

public class UserAttributesHandler extends AttributesHandler {
    public UserAttributesHandler(AttributesBean ab) {
        super(ab);
    }

    public void dropListener(DropEvent dropEvent) {
        int type = dropEvent.getEventType();
        if (type == DndEvent.DROPPED) {
            Object dragValue = dropEvent.getTargetDragValue();
            assert (dragValue != null);
            ViewAttribute va = (ViewAttribute)dragValue;

            getAttributesBean().getViewAttributes().add(va);
        }
    }

}
