package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class ApplicationsBean implements Serializable {
    private List<ApplicationBean> applications = new ArrayList<ApplicationBean>();
    private ApplicationBean applicationBean;

    public ApplicationsBean() {
        ApplicationBean ab = null;

        // TODO: for testing
        ab = new ApplicationBean();
        ab.setName("Paycheck");
        applications.add(ab);

        ab = new ApplicationBean();
        ab.setName("VacationTool");
        applications.add(ab);

        ab = new ApplicationBean();
        ab.setName("Namefinder");
        applications.add(ab);

        applicationBean = ab;
        // TODO
    }

    public List<SelectItem> getApplicationSelectItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();

        for (ApplicationBean ab: applications) {
            items.add(new SelectItem(ab.getName(), ab.getName()));
        }

        return items;
    }

    public void setApplicationSelectItems() {
        // nothing
    }

    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }
}
