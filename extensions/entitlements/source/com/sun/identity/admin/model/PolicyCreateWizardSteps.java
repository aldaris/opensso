package com.sun.identity.admin.model;

public enum PolicyCreateWizardSteps {
    NAME_DESCRIPTION_TYPE (0),
    RESOURCES (1),
    SUBJECTS (2);

    private int step;

    PolicyCreateWizardSteps(int step) {
        this.step = step;
    }
}
