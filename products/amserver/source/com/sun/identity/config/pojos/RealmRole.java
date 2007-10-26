package com.sun.identity.config.pojos;


public class RealmRole {

    public static final int ROLES_OFF = 0;
    public static final int READ_ALL_LOG_FILES = 1;
    public static final int WRITE_ALL_LOG_FILES = 2;
    public static final int READ_WRITE_ALL_LOG_FILES = 4;
    public static final int READ_WRITE_POLICY_PROPERTIES_ONLY = 8;
    public static final int READ_WRITE_ALL_REALMS_AND_POLICY_PROPERTIES = 16;

    private String name;
    private int flag = ROLES_OFF;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}
