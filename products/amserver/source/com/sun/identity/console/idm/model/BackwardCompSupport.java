/* The contents of this file are subject to the terms
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
 * $Id: BackwardCompSupport.java,v 1.1 2007-02-07 20:21:57 jonnelson Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.console.idm.model;

import com.iplanet.am.sdk.AMObject;
import com.sun.identity.common.admin.AdminInterfaceUtils;
import com.sun.identity.console.base.model.AMModelBase;
import com.sun.identity.idm.IdType;
import com.sun.identity.sm.AttributeSchema;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* - NEED NOT LOG - */

public class BackwardCompSupport {
    private static BackwardCompSupport instance = new BackwardCompSupport();

    private Map mapIdTypeToServiceName = new HashMap();
    private Map mapIdTypeToSchemaType = new HashMap();
    private Map mapIdTypeToSubSchemaName = new HashMap();

    private String namingAttribute = null;

    private BackwardCompSupport() {
        mapIdTypeToServiceName.put(IdType.USER.getName(),
            "iPlanetAMUserService");
        mapIdTypeToSchemaType.put(IdType.USER.getName(), "user");

        mapIdTypeToServiceName.put(IdType.AGENT.getName(),
            "iPlanetAMAgentService");
        mapIdTypeToSchemaType.put(IdType.AGENT.getName(), "user");

        mapIdTypeToServiceName.put(IdType.GROUP.getName(),
            "iPlanetAMEntrySpecificService");
        mapIdTypeToSubSchemaName.put(IdType.GROUP.getName(), "Group");

        mapIdTypeToServiceName.put(IdType.ROLE.getName(),
            "iPlanetAMEntrySpecificService");
        mapIdTypeToSubSchemaName.put(IdType.ROLE.getName(), "Role");

        mapIdTypeToServiceName.put(IdType.FILTEREDROLE.getName(),
            "iPlanetAMEntrySpecificService");
        mapIdTypeToSubSchemaName.put(IdType.FILTEREDROLE.getName(),
            "FilteredRole");

        namingAttribute = AdminInterfaceUtils.getNamingAttribute(
            AMObject.USER, AMModelBase.debug);
    }

    public static BackwardCompSupport getInstance() {
        return instance;
    }

    public String getServiceName(String idType) {
        return (String)mapIdTypeToServiceName.get(idType);
    }

    public String getSchemaType(String idType) {
        return (String)mapIdTypeToSchemaType.get(idType);
    }

    public String getSubSchemaName(String idType) {
        return (String)mapIdTypeToSubSchemaName.get(idType);
    }

    public void beforeDisplay(String idType, Set attributeSchemas) {
        if (idType != null) {
            if (idType.equalsIgnoreCase(IdType.USER.getName())) {
                beforeDisplayUser(attributeSchemas);
            }
        }
    }

    private void beforeDisplayUser(Set attributeSchemas) {
        for (Iterator iter = attributeSchemas.iterator(); iter.hasNext(); ) {
            AttributeSchema as = (AttributeSchema)iter.next();
            String name = as.getName();
            if (name.equals(namingAttribute) || name.equals("ChangePassword")) {
                iter.remove();
            }
        }
    }

    public void beforeCreate(
        String idType,
        String entityName,
        Map values
    ) {
        if (idType.equalsIgnoreCase(IdType.USER.getName())) {
            beforeCreateUser(idType, entityName, values);
        }
    }

    public void beforeModify(
        String idType,
        String entityName,
        Map values
    ) {
        if (idType.equalsIgnoreCase(IdType.USER.getName())) {
            beforeModifyUser(idType, entityName, values);
        }
    }

    private void beforeCreateUser(
        String idType,
        String entityName,
        Map values
    ) {
        Set set = new HashSet(2);
        set.add(entityName);
        values.put(namingAttribute, set);
    }

    private void beforeModifyUser(
        String idType,
        String entityName,
        Map values
    ) {
        Set set = new HashSet(2);
        set.add(entityName);
        values.put(namingAttribute, set);
    }
}
