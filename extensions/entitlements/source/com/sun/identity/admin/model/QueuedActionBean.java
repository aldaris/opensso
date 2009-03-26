package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueuedActionBean implements Serializable {
    private List<PhaseEventAction> phaseEventActions = new ArrayList<PhaseEventAction>();

    public List<PhaseEventAction> getPhaseEventActions() {
        return phaseEventActions;
    }
}
