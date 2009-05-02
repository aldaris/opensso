package com.sun.identity.admin.model;

public enum PolicyWizardStep {
    NAME (0),
    RESOURCES (1),
    SUBJECTS (2),
    ADVANCED (3),
    SUMMARY (4);

    private final int stepNumber;

    PolicyWizardStep(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int toInt() {
        return stepNumber;
    }
}
