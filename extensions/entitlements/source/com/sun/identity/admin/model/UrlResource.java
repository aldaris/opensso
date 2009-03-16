package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlResource implements Resource, Serializable {
    private boolean selected = false;
    private List<Resource> exceptions = new ArrayList<Resource>();
    private String pattern;
    private boolean excepted;
    private boolean exceptionsShown;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isExcepted() {
        return excepted;
    }

    public void setExcepted(boolean excepted) {
        this.excepted = excepted;
    }

    @Override
    public String toString() {
        return pattern;
    }

    public List<Resource> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<Resource> exceptions) {
        this.exceptions = exceptions;
    }

    public boolean isExceptionsShown() {
        return exceptionsShown && excepted;
    }

    public void setExceptionsShown(boolean exceptionsShown) {
        this.exceptionsShown = exceptionsShown;
    }

    public boolean isExceptable() {
        return pattern.endsWith("*");
    }

    @Override
    public boolean equals(Object o) {
        UrlResource other = (UrlResource)o;
        if (other.pattern.equals(pattern)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return pattern.hashCode();
    }
}
