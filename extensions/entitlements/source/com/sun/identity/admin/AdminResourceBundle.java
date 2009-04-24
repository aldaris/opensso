package com.sun.identity.admin;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

public class AdminResourceBundle {
    public static ResourceBundle getResourceBundle() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Locale l = fc.getViewRoot().getLocale();
        ResourceBundle rb = ResourceBundle.getBundle("com.sun.identity.admin.Messages", l);

        return rb;
    }

    public static String getString(String key) {
        ResourceBundle rb = getResourceBundle();
        return rb.getString(key);
    }

    public static String getString(String key, Object ... params) {
        ResourceBundle rb = getResourceBundle();
        String msg = rb.getString(key);
        String fmt = MessageFormat.format(msg, params);

        return fmt;

    }

}
