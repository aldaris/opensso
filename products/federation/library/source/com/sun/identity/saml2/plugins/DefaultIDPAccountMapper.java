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
 * $Id: DefaultIDPAccountMapper.java,v 1.5 2008-03-04 23:40:09 hengming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import java.security.PrivateKey;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.ResourceBundle;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.plugin.datastore.DataStoreProviderException;
import com.sun.identity.plugin.datastore.DataStoreProvider;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionProvider;
import com.sun.identity.plugin.session.SessionException;

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.NameIDInfo;
import com.sun.identity.saml2.common.AccountUtils;
import com.sun.identity.saml2.assertion.EncryptedID;
import com.sun.identity.saml2.assertion.NameID;
import com.sun.identity.saml2.assertion.AssertionFactory;
import com.sun.identity.saml2.profile.IDPCache;
import com.sun.identity.saml2.profile.IDPSession;
import com.sun.identity.saml2.profile.IDPSSOUtil;
import com.sun.identity.saml2.profile.NameIDandSPpair;

/**
 * This class <code>DefaultIDPAccountMapper</code> is the default
 * implementation of the <code>IDPAccountMapper</code> that is used
 * to map the <code>SAML</code> protocol objects to the user accounts.
 * at the <code>IdentityProvider</code> side of SAML v2 plugin.
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
     * @exception SAML2Exception if any failure.
     */
    public NameID getNameID(
        Object session,
        String hostEntityID,
        String remoteEntityID
    ) throws SAML2Exception {

        String userID = null;
        String nameIDFormat = null;
        try {
            SessionProvider sessionProv = SessionManager.getProvider();
            userID = sessionProv.getPrincipalName(session);
            String[] values = sessionProv.getProperty(session, 
                IDPSSOUtil.NAMEID_FORMAT);
            if ((values != null) && (values.length > 0)) {
                nameIDFormat = values[0]; 
            }
        } catch (SessionException se) {
            throw new SAML2Exception(SAML2Utils.bundle.getString(
                   "invalidSSOToken")); 
        }
        
        String nameIDValue = null;
        if (nameIDFormat != null &&
            nameIDFormat.equals(SAML2Constants.X509_SUBJECT_NAME)) {
            nameIDValue = userID;
        } else if (nameIDFormat != null &&
            nameIDFormat.equals(SAML2Constants.NAMEID_TRANSIENT_FORMAT))
        {
            String sessionIndex = IDPSSOUtil.getSessionIndex(session);
            if (sessionIndex != null) {
                IDPSession idpSession = 
                    (IDPSession)IDPCache.idpSessionsByIndices.get(sessionIndex);
                if (idpSession != null) {
                    List list = (List)idpSession.getNameIDandSPpairs();
                    if (list != null && !list.isEmpty()) {
                        Iterator iter = list.iterator();
                        while (iter.hasNext()) {
                            NameIDandSPpair pair =
                                (NameIDandSPpair) iter.next();
                            if (pair.getSPEntityID().equals(remoteEntityID)) {
                                nameIDValue = pair.getNameID().getValue();
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (nameIDValue == null) {
            nameIDValue = SAML2Utils.createNameIdentifier();
        }

        NameID nameID = AssertionFactory.getInstance().createNameID(); 
        nameID.setValue(nameIDValue);
        nameID.setFormat(nameIDFormat);
        nameID.setNameQualifier(hostEntityID);
        nameID.setSPNameQualifier(remoteEntityID);
        nameID.setSPProvidedID(null);
        return nameID;
    }

    /**
     * Returns the user's disntinguished name or the universal ID for the
     * corresponding  <code>SAML</code> <code>NameID</code>.
     * This method returns the universal ID or the DN based on the
     * deployment of the SAMLv2 plugin base platform.
     *
     * @param nameID <code>SAML</code> <code>NameID</code> that needs to be
     *     mapped to the user.
     * @param hostEntityID <code>EntityID</code> of the hosted provider.
     * @param remoteEntityID <code>EntityID</code> of the remote provider.
     * @param realm realm or the organization name that may be used to find
     *        the user information.
     * @return user's disntinguished name or the universal ID.
     * @exception SAML2Exception if any failure.
     */
    public String getIdentity(NameID nameID, String hostEntityID,
        String remoteEntityID, String realm) throws SAML2Exception {

        if (nameID == null) {
            return null;
        }

        if (hostEntityID == null) {
            throw new SAML2Exception(bundle.getString("nullHostEntityID")); 
        }

        if (remoteEntityID == null) {
            throw new SAML2Exception(bundle.getString("nullRemoteEntityID")); 
        }

        if (realm == null) {
            throw new SAML2Exception(bundle.getString("nullRealm")); 
        }

        if (debug.messageEnabled()) {
            debug.message("DefaultIDPAccountMapper.getIdentity: " +
                "realm = " + realm + ", hostEntityID = " + hostEntityID +
                ", remoteEntityID = " + remoteEntityID);
        }

        try {
            return dsProvider.getUserID(realm, SAML2Utils.getNameIDKeyMap(
                nameID, hostEntityID, remoteEntityID, realm, role));

        } catch (DataStoreProviderException dse) {
            debug.error(
                "DefaultIDPAccountMapper.getIdentity(NameIDMappingRequest): ",
                dse);
            throw new SAML2Exception(dse.getMessage());
        }
    }
}
