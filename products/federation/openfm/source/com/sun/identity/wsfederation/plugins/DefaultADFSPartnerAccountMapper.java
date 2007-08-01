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
 * $Id: DefaultADFSPartnerAccountMapper.java,v 1.2 2007-08-01 21:04:51 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.wsfederation.plugins;

import com.sun.identity.shared.datastruct.CollectionHelper;
import com.sun.identity.authentication.service.AuthD;
import com.sun.identity.authentication.util.ISAuthConstants;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.ServiceConfig;
import com.sun.identity.saml.assertion.NameIdentifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This default <code>PartnerAccountMapper</code> for ADFS simply extracts
 * the username portion of an incoming UPN and matches it to the uid attribute
 * in the repository.
 */
public class DefaultADFSPartnerAccountMapper 
    extends DefaultLibrarySPAccountMapper {
    private String UID = "uid";

     /**
      * Default constructor
      */
    public DefaultADFSPartnerAccountMapper() {
        super();
        debug.message("DefaultADFSPartnerAccountMapper.constructor: ");
    }
    
    /**
     * This method simply extracts the username portion of the UPN and returns
     * a Map with a single entry: "uid" => {username}
     * @param nameID NameIdentifier for the subject
     * @param hostEntityID entity ID of the identity provider
     * @param remoteEntityID entity ID of the service provider
     */
    protected Map getSearchParameters(NameIdentifier nameID, 
        String hostEntityID, String remoteEntityID) 
    {
        String classMethod = 
            "DefaultADFSPartnerAccountMapper.getSearchParameters";
        Map keyMap = new HashMap();  

        // name comes as a upn of form login@domain, where login is windows 
        // login name - e.g. alansh, and domain is windows domain - 
        // e.g. adatum.com
        String upn = nameID.getName();
        if (upn != null && upn.length() > 0 ) {
            int atSign = upn.indexOf('@');
            if ( atSign == -1 )
            {
                debug.error(classMethod + "No @ in name");
            }
            else
            {
                String name = upn.substring(0,atSign);
                String domain = upn.substring(atSign+1);

                if ( debug.messageEnabled() )
                {
                    debug.message(classMethod + "name is "+name);
                    debug.message(classMethod + "domain is "+domain);
                }
                HashSet set = new HashSet();
                set.add(name);
                
                keyMap.put(UID, set); 
            }
        } else {
            debug.error(classMethod + "name is null");
        }
        return keyMap;
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
