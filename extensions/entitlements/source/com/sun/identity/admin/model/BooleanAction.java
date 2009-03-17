package com.sun.identity.admin.model;

import com.sun.identity.admin.DeepCloneable;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class BooleanAction
    extends BaseAction
    implements Action, Serializable {

    private boolean allow;
    private boolean deny;
    private boolean ignore;

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
        this.deny = false;
        this.ignore = false;
    }

    public boolean isDeny() {
        return deny;
    }

    public void setDeny(boolean deny) {
        this.deny = deny;
        this.allow = false;
        this.ignore = false;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        this.allow = false;
        this.deny = false;
    }

    public Object getValue() {
        if (allow) {
            return Boolean.TRUE;
        } else if (deny) {
            return Boolean.FALSE;
        } else {
            return null;
        }
    }

    public DeepCloneable deepClone() {
        BooleanAction clone = new BooleanAction();
        clone.setName(getName());
        clone.setAllow(allow);
        clone.setDeny(deny);
        clone.setIgnore(ignore);

        return clone;
    }
}
