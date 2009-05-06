package com.sun.identity.admin.model;

import java.io.Serializable;

public abstract class PolicyFilterType implements Serializable {
    private String name;
    private String template;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        // TODO
        return getName();
    }

    public abstract PolicyFilter newPolicyFilter();

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
