package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResourcesAddBean implements Serializable {
    private String pattern;
    private boolean visible = false;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
