package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.BooleanAction;
import java.io.Serializable;
import javax.faces.event.ValueChangeEvent;

public class BooleanActionHandler implements Serializable {
    private BooleanAction booleanAction;

    public void selectListener(ValueChangeEvent event) {
        Boolean b = (Boolean) event.getNewValue();
        if (b != null) {
            booleanAction.setAllow(b.booleanValue());
        }
    }

    public BooleanAction getBooleanAction() {
        return booleanAction;
    }

    public void setBooleanAction(BooleanAction booleanAction) {
        this.booleanAction = booleanAction;
    }
}
