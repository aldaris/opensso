package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.FederationCreateIdpBean;
import javax.faces.event.ValueChangeEvent;

public class FederationCreateIdpHandler {
    private FederationCreateIdpBean federationCreateIdpBean;

    public void localRemoteListener(ValueChangeEvent event) {
        Boolean b = (Boolean) event.getNewValue();
        federationCreateIdpBean.setLocal(b);
    }

    public void setFederationCreateIdpBean(FederationCreateIdpBean federationCreateIdpBean) {
        this.federationCreateIdpBean = federationCreateIdpBean;
    }
}
