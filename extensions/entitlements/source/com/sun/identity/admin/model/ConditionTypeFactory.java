/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: ConditionTypeFactory.java,v 1.4 2009-08-04 19:41:56 farble1670 Exp $
 */

package com.sun.identity.admin.model;

import com.sun.identity.admin.ManagedBeanResolver;
import com.sun.identity.entitlement.EntitlementCondition;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;

public class ConditionTypeFactory implements Serializable {
    private Map<String,ConditionType> entitlementConditionToConditionTypeMap;
    private Map<String,ConditionType> viewConditionToConditionTypeMap;

    public ConditionType getConditionType(Class c) {
        ConditionType ct;
        String className = c.getName();

        ct = entitlementConditionToConditionTypeMap.get(className);
        if (ct == null) {
            ct = viewConditionToConditionTypeMap.get(className);
        }

        return ct;
    }

    public ViewCondition getViewCondition(EntitlementCondition ec) {
        if (ec == null) {
            return null;
        }

        ConditionType ct = getConditionType(ec.getClass());
        assert (ct != null);
        ViewCondition vc = ct.newViewCondition(ec, this);

        return vc;
    }

    public List<ConditionType> getConditionTypes() {
        return new ArrayList<ConditionType>(viewConditionToConditionTypeMap.values());
    }

    public Map<String, ConditionType> getEntitlementConditionToConditionTypeMap() {
        return entitlementConditionToConditionTypeMap;
    }

    public void setEntitlementConditionToConditionTypeMap(Map<String, ConditionType> entitlementConditionToConditionTypeMap) {
        this.entitlementConditionToConditionTypeMap = entitlementConditionToConditionTypeMap;
    }

    public Map<String, ConditionType> getViewConditionToConditionTypeMap() {
        return viewConditionToConditionTypeMap;
    }

    public void setViewConditionToConditionTypeMap(Map<String, ConditionType> viewConditionToConditionTypeMap) {
        this.viewConditionToConditionTypeMap = viewConditionToConditionTypeMap;
    }

    public static ConditionTypeFactory getInstance() {
        ManagedBeanResolver mbr = new ManagedBeanResolver();
        ConditionTypeFactory cf = (ConditionTypeFactory)mbr.resolve("conditionTypeFactory");
        return cf;
    }

    public Map<String,ConditionType> getConditionTypeNameMap() {
        Map<String,ConditionType> m = new HashMap<String,ConditionType>();
        for (ConditionType ct: getConditionTypes()) {
            m.put(ct.getName(), ct);
        }

        return m;
    }

    public List<SelectItem> getConditionTypeNameItems() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (ConditionType ct: getConditionTypes()) {
            SelectItem si = new SelectItem(ct.getName(), ct.getTitle());
            items.add(si);
        }

        return items;
    }
}
