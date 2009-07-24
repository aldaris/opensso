package com.sun.identity.admin.model;

import com.sun.identity.admin.Resources;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public abstract class LinkBean implements Serializable {

    public static final LinkBean HOME;
    public static final LinkBean POLICY_CREATE;
    public static final LinkBean POLICY_MANAGE;
    public static final LinkBean REFERRAL_CREATE;
    public static final LinkBean REFERRAL_MANAGE;
    public static final LinkBean SAMLV2_HOSTED_IDP_CREATE;
    public static final LinkBean SAMLV2_REMOTE_IDP_CREATE;
    public static final LinkBean SAMLV2_HOSTED_SP_CREATE;
    public static final LinkBean SAMLV2_REMOTE_SP_CREATE;
    public static final LinkBean COMMON_TASKS;

    static {
        HOME = new CommandLinkBean();
        HOME.setValue("home");
        HOME.setIconUri("/admin/image/home.png");

        POLICY_CREATE = new CommandLinkBean();
        POLICY_CREATE.setValue("policy-create");
        POLICY_CREATE.setIconUri("/admin/image/new.png");

        POLICY_MANAGE = new CommandLinkBean();
        POLICY_MANAGE.setValue("policy-manage");
        POLICY_MANAGE.setIconUri("/admin/image/manage.png");

        REFERRAL_CREATE = new CommandLinkBean();
        REFERRAL_CREATE.setValue("referral-create");
        REFERRAL_CREATE.setIconUri("/admin/image/new.png");

        REFERRAL_MANAGE = new CommandLinkBean();
        REFERRAL_MANAGE.setValue("referral-manage");
        REFERRAL_MANAGE.setIconUri("/admin/image/manage.png");

        SAMLV2_HOSTED_IDP_CREATE = new CommandLinkBean();
        SAMLV2_HOSTED_IDP_CREATE.setValue("samlv2-hosted-idp-create");
        SAMLV2_HOSTED_IDP_CREATE.setIconUri("/admin/image/new.png");

        SAMLV2_REMOTE_IDP_CREATE = new CommandLinkBean();
        SAMLV2_REMOTE_IDP_CREATE.setValue("samlv2-remote-idp-create");
        SAMLV2_REMOTE_IDP_CREATE.setIconUri("/admin/image/new.png");

        SAMLV2_HOSTED_SP_CREATE = new CommandLinkBean();
        SAMLV2_HOSTED_SP_CREATE.setValue("samlv2-hosted-sp-create");
        SAMLV2_HOSTED_SP_CREATE.setIconUri("/admin/image/new.png");

        SAMLV2_REMOTE_SP_CREATE = new CommandLinkBean();
        SAMLV2_REMOTE_SP_CREATE.setValue("samlv2-remote-sp-create");
        SAMLV2_REMOTE_SP_CREATE.setIconUri("/admin/image/new.png");

        COMMON_TASKS = new ContextLinkBean();
        COMMON_TASKS.setValue("/");
        COMMON_TASKS.setIconUri("/admin/image/home.png");
    }

    private String value;
    private String iconUri;

    public String getTitle() {
        String title;
        Resources r = new Resources();
        title = r.getString(this.getClass().getSuperclass(), getValue() + ".title");
        if (title == null) {
            title = value;
        }
        return title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public abstract String getRedirect();

    public void redirect() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect(getRedirect());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }
}
