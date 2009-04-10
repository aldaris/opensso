package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResource extends Resource implements Serializable {
    public boolean isExceptable() {
        return getName().endsWith("*");
    }

    public String getExceptionPrefix() {
        return getName().substring(0, getName().length()-1);
    }

    public UrlResource deepClone() {
        UrlResource ur = new UrlResource();
        ur.setName(getName());
        
        return ur;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static UrlResource valueOf(String s) {
        UrlResource ur = new UrlResource();
        ur.setName(s);

        return ur;
    }
}
