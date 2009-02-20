package com.sun.identity.admin.model;

import java.io.Serializable;

public class UrlResourceBean implements Serializable {
    private boolean selected = false;
    private UrlResourcesExceptionsBean urlResourceExceptionsBean = new UrlResourcesExceptionsBean();
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

    public UrlResourcesExceptionsBean getUrlResourceExceptionsBean() {
        return urlResourceExceptionsBean;
    }

    public void setUrlResourceExceptionsBean(UrlResourcesExceptionsBean exceptionsBean) {
        this.urlResourceExceptionsBean = exceptionsBean;
    }
}
