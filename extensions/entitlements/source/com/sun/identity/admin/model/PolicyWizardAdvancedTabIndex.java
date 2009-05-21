package com.sun.identity.admin.model;

import java.util.HashMap;
import java.util.Map;

public enum PolicyWizardAdvancedTabIndex {

    ACTIONS(0),
    CONDITIONS(1),
    USER_ATTRIBUTES(2),
    RESOURCE_ATTRIBUTES(3);

    private final int tabIndex;

    private static final Map<Integer,PolicyWizardAdvancedTabIndex> intValues = new HashMap<Integer,PolicyWizardAdvancedTabIndex>() {
        {
            put(ACTIONS.toInt(), ACTIONS);
            put(CONDITIONS.toInt(), CONDITIONS);
            put(USER_ATTRIBUTES.toInt(), USER_ATTRIBUTES);
            put(RESOURCE_ATTRIBUTES.toInt(), RESOURCE_ATTRIBUTES);
        }
    };

    PolicyWizardAdvancedTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public int toInt() {
        return tabIndex;
    }

    public static PolicyWizardAdvancedTabIndex valueOf(int i) {
        return intValues.get(Integer.valueOf(i));
    }
}
