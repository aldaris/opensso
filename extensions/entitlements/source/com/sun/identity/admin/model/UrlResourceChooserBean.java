package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResourceChooserBean implements Serializable {
    private boolean addVisible;
    private String addPattern;
    private String searchFilter;
    private boolean searchVisible;

    public boolean isAddVisible() {
        return addVisible;
    }

    public void setAddVisible(boolean addVisible) {
        this.addVisible = addVisible;
    }

    public String getAddPattern() {
        return addPattern;
    }

    public void setAddPattern(String addPattern) {
        this.addPattern = addPattern;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public boolean isSearchVisible() {
        return searchVisible;
    }

    public void setSearchVisible(boolean searchVisible) {
        this.searchVisible = searchVisible;
    }

}
