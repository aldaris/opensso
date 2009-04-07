package com.sun.identity.admin.model;

import com.sun.identity.entitlement.Entitlement;
import com.sun.identity.entitlement.EntitlementCondition;
import com.sun.identity.entitlement.EntitlementSubject;
import com.sun.identity.entitlement.Privilege;
import com.sun.identity.entitlement.ResourceAttributes;
import com.sun.identity.entitlement.opensso.OpenSSOPrivilege;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrivilegeBean implements Serializable {
    private String name = null;
    private String description = null;
    private List<Resource> resources = new ArrayList<Resource>();
    private ViewCondition viewCondition = null;
    private ViewSubject viewSubject = null;
    private List<Action> actions;

    public PrivilegeBean() {
        // empty
    }

    public PrivilegeBean(Privilege p) {
        name = p.getName();
        description = null; // TODO

        // TODO: populate other fields from privilege
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Privilege toPrivilege() {
        // subjects
        // TODO
        EntitlementSubject eSubject = null;

        // resources / actions
        // TODO
        Entitlement entitlement = null;

        // conditions
        EntitlementCondition eCondition = null;
        if (getViewCondition() != null) {
            eCondition = getViewCondition().getEntitlementCondition();
        }

        // resource attrs
        // TODO
        Set<ResourceAttributes> attrs = null;


        Privilege privilege = new OpenSSOPrivilege(
                name,
                entitlement, //TODO: use scalar entitlement
                eSubject,
                eCondition,
                attrs);

        return privilege;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public ViewCondition getViewCondition() {
        return viewCondition;
    }

    public void setViewCondition(ViewCondition viewCondition) {
        this.viewCondition = viewCondition;
    }

    public ViewSubject getViewSubject() {
        return viewSubject;
    }

    public void setViewSubject(ViewSubject viewSubject) {
        this.viewSubject = viewSubject;
    }
}
