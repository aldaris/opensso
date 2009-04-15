package com.sun.identity.admin.model;

public class FederationCreateIdpBean {
    private boolean local;
    private boolean meta;

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isMeta() {
        return meta;
    }

    public void setMeta(boolean meta) {
        this.meta = meta;
    }
}
