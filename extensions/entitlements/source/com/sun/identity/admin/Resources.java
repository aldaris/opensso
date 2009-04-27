package com.sun.identity.admin;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class Resources {

    private Locale locale = null;

    public Resources() {
        locale = getLocaleFromFaces();
    }

    public Resources(Locale locale) {
        this.locale = locale;
    }

    public Resources(ServletRequest request) {
        this.locale = getLocaleFromRequest(request);
    }

    private Locale getLocaleFromFaces() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) {
            throw new RuntimeException("faces context is not available");
        }

        Locale l = fc.getViewRoot().getLocale();
        return l;
    }

    private Locale getLocaleFromRequest(ServletRequest request) {
        return request.getLocale();
    }

    public ResourceBundle getResourceBundle() {
        ResourceBundle rb = ResourceBundle.getBundle("com.sun.identity.admin.Messages", locale);
        return rb;
    }

    public String getString(String key) {
        ResourceBundle rb = getResourceBundle();
        return rb.getString(key);
    }

    public String getString(String key, Object... params) {
        ResourceBundle rb = getResourceBundle();
        String msg = rb.getString(key);
        String fmt = MessageFormat.format(msg, params);

        return fmt;

    }
}
