/*
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
 * $Id: AgentProfileDefaultsDao.java,v 1.1 2009-10-08 16:16:20 ggennaro Exp $
 */

package com.sun.identity.admin.dao;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import java.security.AccessController;
import java.util.Collections;
import java.util.Set;

public abstract class AgentProfileDefaultsDao {

    protected static String getDefault(String type, String attributeName)
            throws SMSException, SSOException
    {
        Set<String> set = getDefaults(type, attributeName);
        return ((set != null) && !set.isEmpty()) ? set.iterator().next() : null;
    }


    @SuppressWarnings("unchecked")
    protected static Set<String> getDefaults(String type, String attributeName)
            throws SMSException, SSOException
    {
        ServiceSchema schema = getServiceSchema(type);
        if (schema != null) {
            AttributeSchema as = schema.getAttributeSchema(attributeName);
            if (as != null) {
                return as.getDefaultValues();
            }
        }
        return Collections.EMPTY_SET;
    }

    @SuppressWarnings("unchecked")
    protected static ServiceSchema getServiceSchema(String type)
            throws SMSException, SSOException
    {
        SSOToken adminToken
                = (SSOToken) 
                  AccessController.doPrivileged(AdminTokenAction.getInstance());
        ServiceSchemaManager sm
                = new ServiceSchemaManager("AgentService", adminToken);

        if (sm == null) {
            return null;
        }

        ServiceSchema orgSchema = sm.getOrganizationSchema();
        if (orgSchema == null) {
            return null;
        }

        return orgSchema.getSubSchema(type);
    }

}
