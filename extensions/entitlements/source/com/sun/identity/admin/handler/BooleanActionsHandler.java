package com.sun.identity.admin.handler;

import com.sun.identity.admin.dao.ViewApplicationDao;
import com.sun.identity.admin.model.BooleanAction;
import com.sun.identity.admin.model.BooleanActionsBean;
import com.sun.identity.admin.model.ViewApplication;
import com.sun.identity.admin.model.ViewApplicationsBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class BooleanActionsHandler
        implements Serializable {

    private BooleanActionsBean booleanActionsBean;

    protected BooleanAction getBooleanAction(ActionEvent event) {
        BooleanAction ba = (BooleanAction) event.getComponent().getAttributes().get("booleanAction");
        assert (ba != null);

        return ba;
    }

    protected ViewApplicationsBean getViewApplicationsBean(ActionEvent event) {
        ViewApplicationsBean vab = (ViewApplicationsBean) event.getComponent().getAttributes().get("viewApplicationsBean");
        assert (vab != null);

        return vab;
    }

    protected ViewApplicationDao getViewApplicationDao(ActionEvent event) {
        ViewApplicationDao vadao = (ViewApplicationDao) event.getComponent().getAttributes().get("viewApplicationDao");
        assert (vadao != null);

        return vadao;
    }

    public void removeListener(ActionEvent event) {
        BooleanAction ba = getBooleanAction(event);
        booleanActionsBean.getActions().remove(ba);
    }

    public void addListener(ActionEvent event) {
        booleanActionsBean.setAddPopupVisible(!booleanActionsBean.isAddPopupVisible());
    }

    public void addPopupOkListener(ActionEvent event) {
        BooleanAction ba = new BooleanAction();
        ba.setName(booleanActionsBean.getAddPopupName());
        ba.setAllow(true);
        booleanActionsBean.getActions().add(ba);

        ViewApplication va = booleanActionsBean.getViewApplication();
        va.getActions().add(ba);
        getViewApplicationDao(event).setViewApplication(va);

        getViewApplicationsBean(event).reset();

        booleanActionsBean.setAddPopupName(null);
        booleanActionsBean.setAddPopupVisible(false);
    }

    public void addPopupCancelListener(ActionEvent event) {
        booleanActionsBean.setAddPopupName(null);
        booleanActionsBean.setAddPopupVisible(false);
    }

    public void setBooleanActionsBean(BooleanActionsBean booleanActionsBean) {
        this.booleanActionsBean = booleanActionsBean;
    }
}
