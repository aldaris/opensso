package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResource implements Resource, Serializable {
    private boolean selected = false;
    private UrlResourceExceptionsBean urlResourceExceptionsBean = new UrlResourceExceptionsBean();
    private String pattern;

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
        return urlResourceExceptionsBean.isExcepted();
    }

    public void setExcepted(boolean excepted) {
        urlResourceExceptionsBean.setExcepted(excepted);
    }

    public UrlResourceExceptionsBean getUrlResourceExceptionsBean() {
        return urlResourceExceptionsBean;
    }

    public void setUrlResourceExceptionsBean(UrlResourceExceptionsBean exceptionsBean) {
        this.urlResourceExceptionsBean = exceptionsBean;
    }

    @Override
    public String toString() {
        return pattern;
    }
}
