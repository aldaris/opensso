package com.sun.identity.admin.dao;

import com.sun.identity.admin.model.Action;
import com.sun.identity.admin.model.BooleanAction;
import com.sun.identity.admin.model.ViewApplicationType;
import com.sun.identity.entitlement.ApplicationType;
import com.sun.identity.entitlement.ApplicationTypeManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewApplicationTypeDao implements Serializable {
    private Map<String,ViewApplicationType> viewApplicationTypes;

    public Map<String,ViewApplicationType> getViewApplicationTypes() {
        return viewApplicationTypes;
    }

    public void setViewApplicationTypes(Map<String,ViewApplicationType> viewApplicationTypes) {
        this.viewApplicationTypes = viewApplicationTypes;

        for (String name: viewApplicationTypes.keySet()) {
            ViewApplicationType vat = viewApplicationTypes.get(name);
            ApplicationType at = ApplicationTypeManager.get(vat.getName());
            List<Action> actions = new ArrayList<Action>();
            for (String actionString: at.getActions()) {
                BooleanAction ba = new BooleanAction();
                ba.setName(actionString);
                ba.setAllow(false);
                actions.add(ba);
            }
            vat.setActions(actions);
        }
    }
}
