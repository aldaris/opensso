package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResourceExceptionBean implements Serializable {
    private String part = "";

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }
}
