package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResource extends Resource implements Serializable {
    public boolean isSuffixable() {
        return getName().endsWith("*");
    }

    public String getPrefix() {
        if (!isSuffixable()) {
            throw new RuntimeException("resource is not suffixable");
        }
        String prefix = getName().substring(0, getName().length()-1);
        return prefix;
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
}
