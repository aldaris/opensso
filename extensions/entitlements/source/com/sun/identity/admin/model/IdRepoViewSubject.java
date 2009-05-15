package com.sun.identity.admin.model;

public abstract class IdRepoViewSubject extends ViewSubject {
    private String cn;

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    @Override
    public String getTitle() {
        if (getCn() != null) {
            return getCn();
        }
        return super.getTitle();
    }
}
