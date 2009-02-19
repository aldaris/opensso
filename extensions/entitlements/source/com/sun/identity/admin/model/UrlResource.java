package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResource implements Serializable {
    private boolean selected = false;
    private String urlPattern;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }
}
