package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlResourceExceptionsBean implements Serializable {
    private boolean excepted = false;
    private boolean shown = true;

    private List<UrlResourceExceptionBean> urlResourceExceptionBeans = new ArrayList<UrlResourceExceptionBean>();

    public UrlResourceExceptionsBean() {
        urlResourceExceptionBeans.add(new UrlResourceExceptionBean());
    }
    
    public boolean isExcepted() {
        return excepted;
    }

    public void setExcepted(boolean excepted) {
        this.excepted = excepted;
    }

    public List<UrlResourceExceptionBean> getUrlResourceExceptionBeans() {
        return urlResourceExceptionBeans;
    }

    public void setExceptions(List<UrlResourceExceptionBean> urlResourceExceptionBeans) {
        this.urlResourceExceptionBeans = urlResourceExceptionBeans;
    }

    /**
     * @return the shown
     */
    public boolean isShown() {
        return shown;
    }

    /**
     * @param shown the shown to set
     */
    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getShownActionText() {
        if (shown) {
            return "Hide";
        } else {
            return "Show";
        }
    }

  
}
