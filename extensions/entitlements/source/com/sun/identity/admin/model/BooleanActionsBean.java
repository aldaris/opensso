package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.List;

public class BooleanActionsBean implements Serializable {
    private List<Action> actions;
    private boolean addPopupVisible;
    private String addPopupName;

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public boolean isAddPopupVisible() {
        return addPopupVisible;
    }

    public void setAddPopupVisible(boolean addPopupVisible) {
        this.addPopupVisible = addPopupVisible;
    }

    public String getAddPopupName() {
        return addPopupName;
    }

    public void setAddPopupName(String addPopupName) {
        this.addPopupName = addPopupName;
    }
}
