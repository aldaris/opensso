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
 * $Id: SMSCommon.java,v 1.1 2007-08-14 23:33:54 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

import com.iplanet.sso.SSOToken;
import com.sun.identity.qatest.common.TestCommon;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class has helper functions related to service management
 */
public class SMSCommon extends TestCommon {
    
    private SSOToken adminToken;

    /**
     * Class constructor. Sets class variables.
     */
    public SMSCommon(SSOToken token)
    throws Exception{
        super("SMSCommon");
        adminToken = token;
    }
    
    /**
     * Method updates a given attribute in any sepcified service
     */
    public void updateServiceAttribute(String serviceName,
            String attributeName, Set set, String type)
    throws Exception {
        ServiceConfigManager scm = new ServiceConfigManager(serviceName,
                adminToken);
        ServiceConfig sc = null;
        if (type.equals("Global"))
            sc = scm.getGlobalConfig(null);
        else if (type.equals("Organization"))
            sc = scm.getOrganizationConfig(realm, null);
        Map map = new HashMap();
        map.put(attributeName, set);
        sc.removeAttribute(attributeName);
        sc.setAttributes(map);
    }

    /**
     * Method removes values for a given attribute in any sepcified service
     */
    public void removeServiceAttributeValues(String serviceName,
            String attributeName, String type)
    throws Exception {
        ServiceConfigManager scm = new ServiceConfigManager(serviceName,
                adminToken);
        ServiceConfig sc = null;
        if (type.equals("Global"))
            sc = scm.getGlobalConfig(null);
        else if (type.equals("Organization"))
            sc = scm.getOrganizationConfig(realm, null);
        sc.removeAttribute(attributeName);
    }
}
