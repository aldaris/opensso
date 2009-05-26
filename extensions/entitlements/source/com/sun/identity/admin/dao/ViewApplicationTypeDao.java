package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOToken;
import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.admin.model.Action;
import com.sun.identity.admin.model.BooleanAction;
import com.sun.identity.admin.model.ViewApplicationType;
import com.sun.identity.entitlement.ApplicationType;
import com.sun.identity.entitlement.ApplicationTypeManager;
import com.sun.identity.entitlement.opensso.SubjectUtils;
import com.sun.identity.security.AdminTokenAction;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;

public class ViewApplicationTypeDao implements Serializable {
    public List<ViewApplicationType> getViewApplicationTypes() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        Map<String,ViewApplicationType> entitlementApplicationTypeToViewApplicationTypeMap = (Map<String,ViewApplicationType>)mbr.resolve("entitlementApplicationNameToViewApplicationTypeMap");

        SSOToken adminToken = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance()); //TODO
        Subject adminSubject = SubjectUtils.createSubject(adminToken);

        List<ViewApplicationType> viewApplicationTypes = new ArrayList<ViewApplicationType>();
        for (String entitlementApplicationType: entitlementApplicationTypeToViewApplicationTypeMap.keySet()) {
            ViewApplicationType vat = entitlementApplicationTypeToViewApplicationTypeMap.get(entitlementApplicationType);
            ApplicationType at = ApplicationTypeManager.getAppplicationType(
                adminSubject, entitlementApplicationType);
            List<Action> actions = new ArrayList<Action>();
            for (String actionName: at.getActions().keySet()) {
                Boolean value = at.getActions().get(actionName);
                BooleanAction ba = new BooleanAction();
                ba.setName(actionName);
                ba.setAllow(value.booleanValue());
                actions.add(ba);
            }
            vat.setActions(actions);
            viewApplicationTypes.add(vat);
        }

        return viewApplicationTypes;
    }
}
