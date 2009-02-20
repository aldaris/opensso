package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlResourcesExceptionsBean implements Serializable {
    private boolean excepted = false;
    private List<UrlResourceExceptionBean> urlResourceExceptionBeans = new ArrayList<UrlResourceExceptionBean>();

    public UrlResourcesExceptionsBean() {
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
}
