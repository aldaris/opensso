package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResourcesBean implements Serializable {
    private boolean addPopupVisible = false;
    private String addPopupName;
    private String addExceptionPopupName;
    private String searchFilter;
    private boolean addExceptionPopupVisible;
    private UrlResource addExceptionPopupResource;

    public boolean isAddPopupVisible() {
        return addPopupVisible;
    }

    public void setAddPopupVisible(boolean addPopupVisible) {
        this.addPopupVisible = addPopupVisible;
    }

    public String getAddPopupName() {
        return addPopupName;
    }

    public void setAddPopupName(String addPopupName) {
        this.addPopupName = addPopupName;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public boolean isAddExceptionPopupVisible() {
        return addExceptionPopupVisible;
    }

    public void setAddExceptionPopupVisible(boolean addExceptionPopupVisible) {
        this.addExceptionPopupVisible = addExceptionPopupVisible;
    }

    public UrlResource getAddExceptionPopupResource() {
        return addExceptionPopupResource;
    }

    public void setAddExceptionPopupResource(UrlResource addExceptionPopupResource) {
        this.addExceptionPopupResource = addExceptionPopupResource;
    }

    public String getAddExceptionPopupName() {
        return addExceptionPopupName;
    }

    public void setAddExceptionPopupName(String addExceptionPopupName) {
        this.addExceptionPopupName = addExceptionPopupName;
    }
}
