package com.sun.identity.admin.model;

import com.sun.identity.admin.handler.AttributesHandler;
import com.sun.identity.entitlement.ResourceAttribute;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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

    public abstract Set<ResourceAttribute> toResourceAttributesSet();

    public void reset() {
        addPopupVisible = false;
        addPopupName = null;
    }

    public abstract ViewAttribute newViewAttribute();

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

    public String getToString() {
        return getListToString(viewAttributes);
    }

    public String getToFormattedString() {
        return getListToFormattedString(viewAttributes);
    }

    public String getListToString(List list) {
        StringBuffer b = new StringBuffer();

        for (Iterator<Resource> i = list.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append(",");
            }

        }

        return b.toString();
    }

    public String getListToFormattedString(List list) {
        StringBuffer b = new StringBuffer();

        for (Iterator<Resource> i = list.iterator(); i.hasNext();) {
            b.append(i.next());
            if (i.hasNext()) {
                b.append("\n");
            }

        }

        return b.toString();
    }
}
