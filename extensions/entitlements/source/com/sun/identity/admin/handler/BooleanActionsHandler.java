package com.sun.identity.admin.handler;

import com.sun.identity.admin.model.Action;
import com.sun.identity.admin.model.BooleanAction;
import java.io.Serializable;
import java.util.List;
import javax.faces.event.ActionEvent;

public class BooleanActionsHandler
        implements Serializable {

    private List<Action> actions;

    protected BooleanAction getBooleanAction(ActionEvent event) {
        BooleanAction ba = (BooleanAction) event.getComponent().getAttributes().get("booleanAction");
        assert(ba != null);

        return ba;
    }

    public void actionRemoveListener(ActionEvent event) {
        BooleanAction ba = getBooleanAction(event);
        actions.remove(ba);
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
