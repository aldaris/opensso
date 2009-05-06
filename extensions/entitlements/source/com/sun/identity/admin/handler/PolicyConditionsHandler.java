package com.sun.identity.admin.handler;

import com.icesoft.faces.component.dragdrop.DropEvent;
import javax.faces.event.ActionEvent;

public interface PolicyConditionsHandler extends MultiPanelHandler {
    public void conditionDropListener(DropEvent dropEvent);
    public void allOfConditionListener(ActionEvent event);
    public void anyOfConditionListener(ActionEvent event);
}
