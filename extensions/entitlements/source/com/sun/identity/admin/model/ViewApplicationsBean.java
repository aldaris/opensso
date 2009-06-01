package com.sun.identity.admin.model;

import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.dao.ViewApplicationDao;
import java.io.Serializable;
import java.util.Map;

public class ViewApplicationsBean implements Serializable {
    private ViewApplicationDao viewApplicationDao;
    private Map<String,ViewApplication> viewApplications;

    public void setViewApplicationDao(ViewApplicationDao viewApplicationDao) {
        this.viewApplicationDao = viewApplicationDao;
        viewApplications = viewApplicationDao.getViewApplications();
    }

    public Map<String, ViewApplication> getViewApplications() {
        return viewApplications;
    }

    public void reset() {
        viewApplications = getViewApplicationDao().getViewApplications();
    }

    public ViewApplicationDao getViewApplicationDao() {
        return viewApplicationDao;
    }

    public static ViewApplicationsBean getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        ViewApplicationsBean vasb = (ViewApplicationsBean)mbr.resolve("viewApplicationsBean");
        return vasb;
    }
}
