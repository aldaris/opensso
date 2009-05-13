package com.sun.identity.admin.model;

import java.io.Serializable;

public class BankingResource extends Resource implements Serializable {
    public BankingResource deepClone() {
        BankingResource br = new BankingResource();
        br.setName(getName());

        return br;
    }
}
