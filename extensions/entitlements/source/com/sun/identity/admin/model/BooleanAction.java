package com.sun.identity.admin.model;

import com.sun.identity.admin.DeepCloneable;
import com.sun.identity.admin.Resources;
import com.sun.identity.admin.handler.BooleanActionHandler;
import java.io.Serializable;

public class BooleanAction
        extends Action
        implements Serializable {

    private boolean allow = false;
    private BooleanActionHandler booleanActionHandler;

    public BooleanAction() {
        booleanActionHandler = new BooleanActionHandler();
        booleanActionHandler.setBooleanAction(this);
    }

    public Boolean getValue() {
        return Boolean.valueOf(allow);
    }

    public DeepCloneable deepClone() {
        BooleanAction clone = new BooleanAction();
        clone.setName(getName());
        clone.setAllow(allow);

        return clone;
    }

    public BooleanActionHandler getBooleanActionHandler() {
        return booleanActionHandler;
    }

    public void setBooleanActionHandler(BooleanActionHandler booleanActionHandler) {
        this.booleanActionHandler = booleanActionHandler;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BooleanAction)) {
            return false;
        }
        BooleanAction other = (BooleanAction)o;

        if (!getName().equals(other.getName())) {
            return false;
        }

        if (!getValue().equals(other.getValue())) {
            return false;
        }

        return true;
    }

    public String toString() {
        return getTitle() + ": " + getValueTitle();
    }

    private String getValueTitle() {
        Resources r = new Resources();
        String title = r.getString(this, "allow."+allow);
        return title;
    }

    @Override
    public String getTitle() {
        Resources r = new Resources();
        String title = r.getString(this, "title."+getName(), getName());
        if (title == null) {
            title = getName();
        }
        return title;
    }
}
