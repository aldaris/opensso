package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.List;

public class UrlResource extends Resource implements Serializable {
    private List<Part> parts;

    public List<Part> getParts() {
        return parts;
    }

    public static class Part {
        private String string;
        private String value;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public boolean isExceptable() {
        return getName().endsWith("*");
    }

    public String getExceptionPrefix() {
        return getName().substring(0, getName().length()-1);
    }

    public boolean isAddable() {
        return getName().contains("*");
    }

    public UrlResource deepClone() {
        UrlResource ur = new UrlResource();
        ur.setName(getName());
        
        return ur;
    }

    public static UrlResource valueOf(String s) {
        UrlResource ur = new UrlResource();
        ur.setName(s);

        return ur;
    }

    public UrlResourceParts getUrlResourceParts() {
        return new UrlResourceParts(this);
    }
}
