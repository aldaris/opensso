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
 * $Id: Compliance.java,v 1.1 2005-11-01 00:29:31 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.remote;

import java.rmi.RemoteException;

import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMSDKBundle;
import com.iplanet.am.sdk.ComplianceInterface;
import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.jaxrpc.SOAPClient;

public class Compliance implements ComplianceInterface {

    private SOAPClient client;

    private static Debug debug = DirectoryManager.debug;

    private static Compliance instance;

    public Compliance() {
        client = DirectoryManager.getInstance().client;
    }

    public static Compliance getInstance() {
        if (instance == null) {
            debug.message("DCTree.getInstance(): Creating a new "
                    + "Instance of DCTree()");
            instance = new Compliance();
        }
        return instance;
    }

    public boolean isAncestorOrgDeleted(SSOToken token, String dn,
            int profileType) throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), dn,
                    new Integer(profileType) };
            Boolean res = ((Boolean) client.send(client.encodeMessage(
                    "isAncestorOrgDeleted", objs), null));
            return res.booleanValue();
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.isAncestorOrgDeleted: caught exception=",
                    amrex);
            throw DirectoryManager.convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.isAncestorOrgDeleted: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.isAncestorOrgDeleted: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public void verifyAndDeleteObject(SSOToken token, String profileDN)
            throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), profileDN };
            client.send(client.encodeMessage("verifyAndDeleteObject", objs),
                    null);
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.verifyAndDeleteObject: caught " +
                    "exception=", amrex);
            throw DirectoryManager.convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.verifyAndDeleteObject: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.verifyAndDeleteObject: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }

    public String getDeletedObjectFilter(int objectType) throws AMException,
            SSOException {
        try {
            Object[] objs = { new Integer(objectType) };
            return ((String) client.send(client.encodeMessage(
                    "getDeletedObjectFilter", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error("DirectoryManager.getDeletedObjectFilter: caught " +
                    "exception=", amrex);
            throw DirectoryManager.convertException(amrex);
        } catch (RemoteException rex) {
            debug.error("DirectoryManager.getDeletedObjectFilter: caught " +
                    "exception=", rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error("DirectoryManager.getDeletedObjectFilter: caught " +
                    "exception=", ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }
}
