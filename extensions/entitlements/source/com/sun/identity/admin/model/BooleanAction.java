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
    }

    public boolean isDeny() {
        return deny;
    }

    public void setDeny(boolean deny) {
        this.deny = deny;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public List getValues() {
        if (allow) {
            return Collections.singletonList(Boolean.TRUE);
        } else if (deny) {
            return Collections.singletonList(Boolean.FALSE);
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
