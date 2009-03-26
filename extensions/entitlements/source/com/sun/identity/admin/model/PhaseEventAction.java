package com.sun.identity.admin.model;

import javax.faces.event.PhaseId;

public class PhaseEventAction {
    private PhaseId phaseId;
    private boolean doBeforePhase;
    private String action;
    private Object[] arguments;
    private Class[] parameters;

    public PhaseId getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(PhaseId phaseId) {
        this.phaseId = phaseId;
    }

    public boolean isDoBeforePhase() {
        return doBeforePhase;
    }

    public void setDoBeforePhase(boolean doBeforePhase) {
        this.doBeforePhase = doBeforePhase;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Class[] getParameters() {
        return parameters;
    }

    public void setParameters(Class[] parameters) {
        this.parameters = parameters;
    }

}
