package com.sun.identity.admin.model;

import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.dao.RealmDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import org.apache.commons.collections.comparators.NullComparator;

public class RealmsBean implements Serializable {
    private List<RealmBean> realmBeans;
    private RealmBean realmBean;
    private RealmDao realmDao;
    private RealmBean baseRealmBean;
    private boolean realmSelectPopupVisible = false;
    private RealmBean realmSelectPopupRealmBean;
    private String realmSelectPopupFilter;

    public void resetRealmSelectPopup() {
        realmSelectPopupVisible = false;
        realmSelectPopupRealmBean = null;
    }

    public void setRealmDao(RealmDao realmDao) {
        this.realmDao = realmDao;
        setRealmBeans(realmDao.getRealmBeans());
        setRealmBean(realmBeans.get(0));
        baseRealmBean = realmDao.getBaseRealmBean();
    }

    public List<SelectItem> getRealmBeanItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (RealmBean rb: realmBeans) {
            items.add(new SelectItem(rb, rb.getTitle()));
        }
        return items;
    }

    public static RealmsBean getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        RealmsBean realmsBean = (RealmsBean)mbr.resolve("realmsBean");
        return realmsBean;
    }

    public List<RealmBean> getRealmBeans() {
        return realmBeans;
    }

    public void setRealmBeans(List<RealmBean> realmBeans) {
        this.realmBeans = realmBeans;
    }

    public RealmBean getRealmBean() {
        return realmBean;
    }

    public void setRealmBean(RealmBean realmBean) {
        this.realmBean = realmBean;
    }

    public RealmBean getBaseRealmBean() {
        return baseRealmBean;
    }

    public boolean isRealmSelectPopupVisible() {
        return realmSelectPopupVisible;
    }

    public void setRealmSelectPopupVisible(boolean realmSelectPopupVisible) {
        this.realmSelectPopupVisible = realmSelectPopupVisible;
    }

    public RealmBean getRealmSelectPopupRealmBean() {
        return realmSelectPopupRealmBean;
    }

    public void setRealmSelectPopupRealmBean(RealmBean realmSelectPopupRealmBean) {
        this.realmSelectPopupRealmBean = realmSelectPopupRealmBean;
    }

    public String getRealmSelectPopupFilter() {
        return realmSelectPopupFilter;
    }

    public void setRealmSelectPopupFilter(String realmSelectPopupFilter) {
        if (realmSelectPopupFilter == null) {
            realmSelectPopupFilter = "";
        }
        NullComparator n = new NullComparator();
        if (n.compare(this.realmSelectPopupFilter, realmSelectPopupFilter) != 0) {
            this.realmSelectPopupFilter = realmSelectPopupFilter;
            setRealmBeans(realmDao.getRealmBeans(null,realmSelectPopupFilter));
        }
    }
}
