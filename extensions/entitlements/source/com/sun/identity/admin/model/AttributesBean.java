package com.sun.identity.admin.model;

import com.sun.identity.admin.handler.AttributesHandler;
import com.sun.identity.entitlement.ResourceAttributes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AttributesBean implements Serializable {
    private List<ViewAttribute> viewAttributes = new ArrayList<ViewAttribute>();
    private boolean addPopupVisible;
    private String addPopupName;
    private AttributesHandler attributesHandler;

    public AttributesBean() {
        reset();
    }

    public abstract Set<ResourceAttributes> toResourceAttributesSet();

    public void reset() {
        addPopupVisible = false;
        addPopupName = null;
    }

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

    public List<ViewAttribute> getViewAttributes() {
        return viewAttributes;
    }

    public AttributesHandler getAttributesHandler() {
        return attributesHandler;
    }

    public void setAttributesHandler(AttributesHandler attributesHandler) {
        this.attributesHandler = attributesHandler;
    }

}
