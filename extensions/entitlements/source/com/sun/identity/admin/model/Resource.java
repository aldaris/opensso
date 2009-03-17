package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Entitlement;
import java.util.Collection;

public interface Resource {
    public String getName();
    public Entitlement getEntitlement(Collection<Action> actions);
}
