package com.sun.identity.admin.model;

import java.io.Serializable;

public class BankingResource extends Resource implements Serializable {
    private ViewSubject owner = null;

    public BankingResource deepClone() {
        BankingResource br = new BankingResource();
        br.setName(getName());

        return br;
    }

    public ViewSubject getOwner() {
        return owner;
    }

    public void setOwner(ViewSubject owner) {
        this.owner = owner;
    }
}
