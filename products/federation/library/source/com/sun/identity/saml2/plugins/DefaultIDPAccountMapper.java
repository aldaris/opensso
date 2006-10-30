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
 * $Id: DefaultIDPAccountMapper.java,v 1.1 2006-10-30 23:16:29 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import java.util.Set;
import java.util.Iterator;
import java.util.ResourceBundle;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.plugin.datastore.DataStoreProviderException;
import com.sun.identity.plugin.datastore.DataStoreProvider;
import com.sun.identity.plugin.session.SessionManager;
import com.sun.identity.plugin.session.SessionException;

import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.common.NameIDInfo;
import com.sun.identity.saml2.common.AccountUtils;
import com.sun.identity.saml2.assertion.EncryptedID;
import com.sun.identity.saml2.assertion.NameID;
import com.sun.identity.saml2.assertion.AssertionFactory;

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

     protected static final String NULL = "null";
     protected static final String DELIM = "|";

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
        try {
            userID = SessionManager.getProvider().
                getPrincipalName(session);
        } catch (SessionException se) {
             throw new SAML2Exception(bundle.getString(
                   "invalidSSOToken")); 
        }
        NameIDInfo info = AccountUtils.getAccountFederation(
                            userID, hostEntityID, remoteEntityID);
        if (info == null) {
            return null;
        } 
        return getNameID(info);
    }

    /**
     * Returns the <code>NameID</code> object from the <code>NameIDInfo</code>
     * object.
     * @param info the <code>NameIDInfo</code> object.
     * @return the <code>NameID</code>.
     * @exception SAML2Exception if any failure.
     */
    protected NameID getNameID(NameIDInfo info) throws SAML2Exception {
        
        NameID nameID = AssertionFactory.getInstance().createNameID(); 

        String nameIDValue = info.getNameIDValue();
        if(!NULL.equals(nameIDValue)) {
           nameID.setValue(nameIDValue);   
        }

        String nameQualifier =info.getNameQualifier();
        if(!NULL.equals(nameQualifier)) {
           nameID.setNameQualifier(nameQualifier);   
        }

        String format = info.getFormat();
        if(!NULL.equals(format)) {
           nameID.setFormat(format);
        }

        String spNameIDValue = info.getSPNameIDValue();
        if(!NULL.equals(spNameIDValue)) {
           nameID.setSPProvidedID(spNameIDValue);
        }

        String spNameQualifier = info.getSPNameQualifier();
        if(!NULL.equals(spNameQualifier)) {
           nameID.setSPNameQualifier(spNameQualifier);
        }

        return nameID;

    }

}
