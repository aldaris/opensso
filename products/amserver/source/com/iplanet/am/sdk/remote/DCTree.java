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
 * $Id: DCTree.java,v 1.1 2005-11-01 00:29:32 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.remote;

import java.rmi.RemoteException;

import com.iplanet.am.sdk.AMException;
import com.iplanet.am.sdk.AMSDKBundle;
import com.iplanet.am.sdk.DCTreeInterface;
import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOToken;
import com.sun.identity.jaxrpc.SOAPClient;

public class DCTree implements DCTreeInterface {

    private SOAPClient client;

    private static DCTree instance = null;

    private static Debug debug = DirectoryManager.debug;

    public DCTree() {
        client = DirectoryManager.getInstance().client;

    }

    public static DCTree getInstance() {
        if (instance == null) {
            debug.message("DCTree.getInstance(): Creating a new "
                    + "Instance of DCTree()");
            instance = new DCTree();
        }
        return instance;
    }

    public String getOrganizationDN(SSOToken token, String domainName)
            throws AMException {
        try {
            Object[] objs = { token.getTokenID().toString(), domainName };
            return ((String) client.send(client.encodeMessage(
                    "getOrgDNFromDomain", objs), null));
        } catch (AMRemoteException amrex) {
            debug.error(
                    "DirectoryManager.getOrgDNFromDomain: caught exception=",
                    amrex);
            throw DirectoryManager.convertException(amrex);
        } catch (RemoteException rex) {
            debug.error(
                    "DirectoryManager.getOrgDNFromDomain: caught exception=",
                    rex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        } catch (Exception ex) {
            debug.error(
                    "DirectoryManager.getOrgDNFromDomain: caught exception=",
                    ex);
            throw new AMException(AMSDKBundle.getString("1000"), "1000");
        }
    }
}
