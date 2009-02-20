package com.sun.identity.admin.model;

import java.io.Serializable;

public class ApplicationBean implements Serializable {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
