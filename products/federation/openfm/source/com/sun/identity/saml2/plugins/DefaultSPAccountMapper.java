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
 * $Id: DefaultSPAccountMapper.java,v 1.1 2006-10-30 23:18:08 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import java.util.Map;
import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.authentication.service.AuthD;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.sm.ServiceConfigManager;

/**
 * This class <code>DefaultSPAccountMapper</code> is the default 
 * implementation of the <code>DefaultLibrarySPAccountMapper</code> that is used
 * to map the <code>SAML</code> protocol objects to the user accounts.
 * at the <code>ServiceProvider</code> side of SAML v2 plugin.
 * Custom implementations may extend from this class to override some
 * of these implementations if they choose to do so.
 */
public class DefaultSPAccountMapper extends DefaultLibrarySPAccountMapper {

     /**
      * Default constructor
      */
     public DefaultSPAccountMapper() {
         super();
         debug.message("DefaultSPAccountMapper.constructor: ");
     }

    /**
     * Checks if dynamical profile creation or ignore profile is enabled.
     * @param realm realm to check the dynamical profile creation attributes.
     * @return true if dynamical profile creation or ignore profile is enabled,
     * false otherwise.
     */
    protected boolean isDynamicalOrIgnoredProfile(String realm) {
        try {
            OrganizationConfigManager orgConfigMgr = AuthD.getAuth().
                getOrgConfigManager(realm);
            ServiceConfig svcConfig = orgConfigMgr.getServiceConfig(
                ISAuthConstants.AUTH_SERVICE_NAME);
            Map attrs = svcConfig.getAttributes();
            String tmp = CollectionHelper.getMapAttr(
                attrs, ISAuthConstants.DYNAMIC_PROFILE);
            if (debug.messageEnabled()) {
                debug.message("dynamicalCreationEnabled, attr=" + tmp);
            }
            if (tmp != null && (tmp.equalsIgnoreCase("createAlias")
                || tmp.equalsIgnoreCase("true")
                || tmp.equalsIgnoreCase("ignore"))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            debug.error("dynamicalCreationEnabled, unable to get attribute", e);
            return false;
        }
    }

}
