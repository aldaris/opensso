package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.RealmsBean;
import java.io.Serializable;
import javax.faces.event.ValueChangeEvent;

public class RealmsHandler implements Serializable {
    private RealmsBean realmsBean;

    public void realmChanged(ValueChangeEvent event) {
        // TODO: reset all
    }

    public void setRealmsBean(RealmsBean realmsBean) {
        this.realmsBean = realmsBean;
    }
}
