package com.sun.identity.admin.model;

import java.util.HashMap;
import java.util.Map;

public enum PolicyWizardStep {

    NAME(0),
    RESOURCES(1),
    SUBJECTS(2),
    ADVANCED(3),
    SUMMARY(4);
    private final int stepNumber;
    private static final Map<Integer, PolicyWizardStep> intValues = new HashMap<Integer, PolicyWizardStep>() {
        {
            put(NAME.toInt(), NAME);
            put(RESOURCES.toInt(), RESOURCES);
            put(SUBJECTS.toInt(), SUBJECTS);
            put(ADVANCED.toInt(), ADVANCED);
            put(SUMMARY.toInt(), SUMMARY);
        }
    };

    PolicyWizardStep(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public int toInt() {
        return stepNumber;
    }

    public static PolicyWizardStep valueOf(int i) {
        return intValues.get(Integer.valueOf(i));
    }
}
