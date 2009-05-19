package com.sun.identity.admin.model;

public class StaticViewAttribute extends ViewAttribute {
    private String value;

    public StaticViewAttribute() {
        super();
    }

    public StaticViewAttribute(AttributesBean ab) {
        assert(ab instanceof StaticAttributesBean);
        StaticAttributesBean sab = (StaticAttributesBean)ab;
        setName(sab.getAddPopupName());
        setValue(sab.getAddPopupValue());
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof StaticViewAttribute)) {
            return false;
        }
        StaticViewAttribute sva = (StaticViewAttribute)other;
        return sva.toString().equals(toString());
    }

    @Override
    public String toString() {
        return getTitle() + "=" + value;
    }
}
