package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.BooleanAction;
import com.sun.identity.admin.model.BooleanActionsBean;
import java.io.Serializable;
import javax.faces.event.ActionEvent;

public class BooleanActionsHandler
        implements Serializable {

    private BooleanActionsBean booleanActionsBean;

    protected BooleanAction getBooleanAction(ActionEvent event) {
        BooleanAction ba = (BooleanAction) event.getComponent().getAttributes().get("booleanAction");
        assert(ba != null);

        return ba;
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
        booleanActionsBean.getActions().add(ba);
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
