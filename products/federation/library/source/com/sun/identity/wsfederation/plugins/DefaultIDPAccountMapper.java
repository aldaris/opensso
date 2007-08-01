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
 * $Id: DefaultIDPAccountMapper.java,v 1.2 2007-08-01 21:04:05 superpat7 Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.wsfederation.plugins;

import com.sun.identity.saml.assertion.NameIdentifier;
import com.sun.identity.saml.common.SAMLException;
import com.sun.identity.saml2.profile.IDPSSOUtil;

import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;
import com.sun.identity.plugin.session.SessionException;

import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.wsfederation.common.WSFederationConstants;


import com.sun.identity.wsfederation.common.WSFederationException;
import com.sun.identity.wsfederation.common.WSFederationUtils;
import com.sun.identity.wsfederation.jaxb.entityconfig.IDPSSOConfigElement;
import com.sun.identity.wsfederation.meta.WSFederationMetaManager;
import com.sun.identity.wsfederation.meta.WSFederationMetaUtils;

/**
 * This class <code>DefaultIDPAccountMapper</code> is the default
 * implementation of the <code>IDPAccountMapper</code> that is used
 * to map the <code>SAML</code> protocol objects to the user accounts.
 * at the <code>IdentityProvider</code> side of the WS-Federation 
 * implementation.
 * Custom implementations may extend from this class to override some
 * of these implementations if they choose to do so.
 */

public class DefaultIDPAccountMapper extends DefaultAccountMapper 
     implements IDPAccountMapper {

     public DefaultIDPAccountMapper() {
         debug.message("DefaultIDPAccountMapper.constructor");
         role = IDP;
     }

    /**
     * Returns the user's <code>NameID</code>information that contains
     * account federation with the corresponding remote and local entities.
     *
     * @param ssoToken Single Sign On Token of the user.
     * @param hostEntityID <code>EntityID</code> of the hosted provider.
     * @param remoteEntityID <code>EntityID</code> of the remote provider.
     * @return the <code>NameID</code> corresponding to the authenticated user.
     *         null if the authenticated user does not container account
     *              federation information.
     * @exception WSFederationException if any failure.
     */
    public NameIdentifier getNameID(
        Object session,
        String realm,
        String hostEntityID,
        String remoteEntityID
    ) throws WSFederationException {
        String userID = null;
        try {
            SessionProvider sessionProv = SessionManager.getProvider();
            String[] values = sessionProv.getProperty(session,"UserId");
            if ((values != null) && (values.length > 0)) {
                userID = values[0]; 
            }
        } catch (SessionException se) {
            throw new WSFederationException(WSFederationUtils.bundle.getString(
                   "invalidSSOToken")); 
        }
        
        NameIdentifier nameID = null;
        try {        
            IDPSSOConfigElement idpConfig = 
                WSFederationMetaManager.getIDPSSOConfig(realm, hostEntityID);

            // Need to check that this is safe in e.g. LDAP case!
            String upn = userID + "@" 
                + WSFederationMetaUtils.getAttribute(idpConfig,
                WSFederationConstants.UPN_DOMAIN);

            nameID = new NameIdentifier(upn, null, 
                WSFederationConstants.CLAIMS_UPN_URI);
        }
        catch (SAMLException se){
            throw new WSFederationException(se);
        }

        return nameID;
    }
}
