package com.sun.identity.admin.model;

import java.io.Serializable;

public abstract class LinkBean implements Serializable {
    public static final LinkBean HOME;
    public static final LinkBean POLICY_CREATE;
    public static final LinkBean POLICY_MANAGE;

    static {
        // TODO: localize text

        HOME = new CommandLinkBean();
        HOME.setText("Go Home");
        HOME.setValue("home");
        HOME.setIconUri("/admin/image/home.png");

        POLICY_CREATE = new CommandLinkBean();
        POLICY_CREATE.setText("Create A Policy");
        POLICY_CREATE.setValue("policy-create");
        POLICY_CREATE.setIconUri("/admin/image/new.png");

        POLICY_MANAGE = new CommandLinkBean();
        POLICY_MANAGE.setText("Manage Policies");
        POLICY_MANAGE.setValue("policy-manage");
        POLICY_MANAGE.setIconUri("/admin/image/manage.png");
    }

    private String text;
    private String value;
    private String iconUri;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
