package com.sun.identity.admin.model;

import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.dao.RealmDao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public class RealmsBean implements Serializable {
    private List<RealmBean> realmBeans;
    private RealmBean realmBean;
    private RealmDao realmDao;

    public void setRealmDao(RealmDao realmDao) {
        this.realmDao = realmDao;
        setRealmBeans(realmDao.getRealmBeans());
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
}
