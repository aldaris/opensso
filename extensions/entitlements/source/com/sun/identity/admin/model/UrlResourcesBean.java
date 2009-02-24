package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UrlResourcesBean implements Serializable {
    private List<UrlResourceBean> urlResourceBeans = new ArrayList<UrlResourceBean>();
    private String searchFilter;
    private boolean searchPopup = false;

    public UrlResourcesBean() {
        UrlResourceBean ur;

        // TODO: dummy data
        ur = new UrlResourceBean();
        ur.setPattern("http://www.sun.com");
        urlResourceBeans.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://paycheck.sun.com/profile");
        urlResourceBeans.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://cal-amer.sun.com");
        urlResourceBeans.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://im-amer.sun.com/jtb");
        urlResourceBeans.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://sunweb.central:8080");
        urlResourceBeans.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://www.bankofamerica.com/account");
        urlResourceBeans.add(ur);

        ur = new UrlResourceBean();
        ur.setPattern("http://www.att.com/login");
        urlResourceBeans.add(ur);
        // TODO
    }

    public List<UrlResourceBean> getUrlResourceBeans() {
        return urlResourceBeans;
    }

    public void setUrlResourceBeans(List<UrlResourceBean> urlResourceBeans) {
        this.urlResourceBeans = urlResourceBeans;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    /**
     * @return the searchPopup
     */
    public boolean isSearchPopup() {
        return searchPopup;
    }

    /**
     * @param searchPopup the searchPopup to set
     */
    public void setSearchPopup(boolean searchPopup) {
        this.searchPopup = searchPopup;
    }

}
