package com.sun.identity.admin.model;

import java.io.Serializable;
import javax.faces.application.FacesMessage;

public class MessageBean implements Serializable {
    private String summary;
    private String detail;
    private FacesMessage.Severity severity;

    public MessageBean() {
        // nothing
    }
    public MessageBean(FacesMessage fm) {
        this.summary = fm.getSummary();
        if (fm.getDetail() != null && !fm.getDetail().equals(fm.getSummary())) {
            this.detail = fm.getDetail();
        }
        this.severity = fm.getSeverity();
    }

    public String getSummary() {
        return summary;
    }

    public String getDetail() {
        return detail;
    }

    public boolean isError() {
        return getSeverity().equals(FacesMessage.SEVERITY_ERROR);
    }

    public boolean isInfo() {
        return getSeverity().equals(FacesMessage.SEVERITY_INFO);
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public FacesMessage.Severity getSeverity() {
        return severity;
    }

    public void setSeverity(FacesMessage.Severity severity) {
        this.severity = severity;
    }

    public FacesMessage toFacesMessage() {
        FacesMessage fm = new FacesMessage();
        fm.setDetail(detail);
        fm.setSummary(summary);
        fm.setSeverity(severity);

        return fm;
    }
}
