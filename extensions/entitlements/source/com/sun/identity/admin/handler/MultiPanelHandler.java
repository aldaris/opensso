package com.sun.identity.admin.handler;

import javax.faces.event.ActionEvent;

public interface MultiPanelHandler {
    public void panelExpandListener(ActionEvent event);
    public void panelRemoveListener(ActionEvent event);
}
