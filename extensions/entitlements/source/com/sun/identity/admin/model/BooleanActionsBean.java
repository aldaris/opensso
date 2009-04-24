package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BooleanActionsBean implements Serializable {
    private List<Action> actions = new ArrayList<Action>();
    private boolean addPopupVisible = false;
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

    public String getActionsToString() {
        StringBuffer b = new StringBuffer();

        for (Iterator<Action> i = actions.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append(",");
            }

        }

        return b.toString();

    }

    public String getActionsToFormattedString() {
        StringBuffer b = new StringBuffer();

        for (Iterator<Action> i = actions.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append("\n");
            }

        }

        return b.toString();

    }
}
