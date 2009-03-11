package com.sun.identity.admin.handler;

import com.sun.identity.admin.dao.ApplicationDao;
import com.sun.identity.admin.model.Application;
import java.io.Serializable;
import java.util.Map;

public class ApplicationHandler implements Serializable {

    private Map<String,Application> applications;
    private ApplicationDao applicationDao;


    public ApplicationDao getApplicationDao() {
        return applicationDao;
    }

    public void setApplicationDao(ApplicationDao applicationDao) {
        this.applicationDao = applicationDao;
        getApplications().putAll(applicationDao.getApplications());
    }

    public Map<String, Application> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, Application> applications) {
        this.applications = applications;
    }
}
