package com.sun.identity.admin.model;

import java.util.HashMap;
import java.util.Map;

public enum FromAction {
    HOME("home"),
    POLICY("policy"),
    POLICY_CREATE("policy-create"),
    POLICY_MANAGE("policy-manage"),
    POLICY_EDIT("policy-edit"),
    REFERRAL_CREATE("referral-create"),
    PERMISSION_DENIED("permission-denied");

    private static final Map<String,FromAction> actionValues = new HashMap<String,FromAction>() {
        {
            put(HOME.getAction(), HOME);
            put(POLICY.getAction(), POLICY);
            put(POLICY_CREATE.getAction(), POLICY_CREATE);
            put(POLICY_MANAGE.getAction(), POLICY_MANAGE);
            put(POLICY_EDIT.getAction(), POLICY_EDIT);
            put(REFERRAL_CREATE.getAction(), REFERRAL_CREATE);
            put(PERMISSION_DENIED.getAction(), PERMISSION_DENIED);
        }
    };

    private String action;

    FromAction(String action) {
        this.action = action;
    }

    public Permission toPermission() {
        return Permission.valueOf(this.toString());
    }

    public String getAction() {
        return action;
    }

    public static FromAction valueOfAction(String action) {
        return actionValues.get(action);
    }
}
