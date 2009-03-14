package com.sun.identity.admin.handler;

import javax.faces.event.ActionEvent;

public interface MultiPanelHandler {
    public void expandListener(ActionEvent event);
    public void removeListener(ActionEvent event);
}
