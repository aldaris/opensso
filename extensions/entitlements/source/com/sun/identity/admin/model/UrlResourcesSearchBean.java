package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResourcesSearchBean implements Serializable {
    private boolean visible = false;
    private String filter;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
