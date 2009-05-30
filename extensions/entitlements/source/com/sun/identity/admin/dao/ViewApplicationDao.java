package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.Token;
import com.sun.identity.admin.model.ViewApplication;
import com.sun.identity.admin.model.ViewApplicationType;
import com.sun.identity.entitlement.Application;
import com.sun.identity.entitlement.ApplicationManager;
import com.sun.identity.entitlement.EntitlementException;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.security.AdminTokenAction;
import java.io.Serializable;
import java.security.AccessController;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.Subject;

public class ViewApplicationDao implements Serializable {

    private ViewApplicationTypeDao viewApplicationTypeDao;

    public void setViewApplicationTypeDao(ViewApplicationTypeDao viewApplicationTypeDao) {
        this.viewApplicationTypeDao = viewApplicationTypeDao;
    }

    public Map<String, ViewApplication> getViewApplications() {
        Map<String, ViewApplication> viewApplications = new HashMap<String, ViewApplication>();

        ManagedBeanResolver mbr = new ManagedBeanResolver();
        Map<String, ViewApplicationType> entitlementApplicationTypeToViewApplicationTypeMap = (Map<String, ViewApplicationType>) mbr.resolve("entitlementApplicationTypeToViewApplicationTypeMap");

        Token token = new Token();
        Subject adminSubject = token.getAdminSubject();

        // TODO realm
        for (String name : ApplicationManager.getApplicationNames(adminSubject, "/")) {
            // TODO: realm
            Application a = ApplicationManager.getApplication(adminSubject, "/", name);
            if (a.getResources() == null || a.getResources().size() == 0) {
                // TODO: log
                continue;
            }

            // application type
            ViewApplicationType vat = entitlementApplicationTypeToViewApplicationTypeMap.get(a.getApplicationType().getName());
            if (vat == null) {
                // TODO: log
                continue;
            }

            ViewApplication va = new ViewApplication(a);
            viewApplications.put(va.getName(), va);
        }

        return viewApplications;
    }

    public void setViewApplication(ViewApplication va) {
        try {
            Application a = va.toApplication(viewApplicationTypeDao);
            // TODO: realm
            SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
                    AdminTokenAction.getInstance()); //TODO
            Subject adminSubject = SubjectUtils.createSubject(adminToken);

            ApplicationManager.saveApplication(adminSubject, "/", a);
        } catch (EntitlementException ee) {
            throw new RuntimeException(ee);
        }
    }

    public Application getApplication(ViewApplication va) {
        String name = va.getName();
        Token token = new Token();
        Subject adminSubject = token.getAdminSubject();
        // TODO: realm
        Application a = ApplicationManager.getApplication(adminSubject, "/", name);
        return a;
    }
}
