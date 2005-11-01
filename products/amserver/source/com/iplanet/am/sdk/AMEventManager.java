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
 * $Id: AMEventManager.java,v 1.1 2005-11-01 00:29:04 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk;

// Java packages
import java.security.AccessController;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOToken;
import com.sun.identity.security.AdminTokenAction;

/**
 * This <code>AMEventManager</code> call Identity Repository plugins to add
 * listeners for changes.
 * 
 * <p>
 * <b>NOTE:</b> This class is not a singleton class. So it should not be
 * instantiated directly. It is only instantiated when an instance of
 * AMCacheManager is created and since AMCacheManager class is a singleton it is
 * guaranted to have only one instance of this class.
 * <p>
 */
class AMEventManager {
    protected static AMDirectoryManager instance;

    protected static Debug debug = Debug.getInstance("amProfileListener");

    /**
     * Constructor
     */
    protected AMEventManager(AMDirectoryManager mgr)
            throws AMEventManagerException {
        instance = mgr;
    }

    /**
     * This method registgers <code>AMIdRepoEventListener</code> and to
     * backend datastores via <code>AMDirectoryManager</code> inorder to
     * receive notifications. The listener implement
     * <code>com.iplanet.am.sdk.AMObjectListener</code> interface.
     * <p>
     * NOTE: This method should be invoked only once.
     * 
     * @throws AMEventManagerException
     *             when encounters errors in starting the underlying
     *             EventService.
     */
    protected void start() throws AMEventManagerException {
        if (debug.messageEnabled()) {
            debug.message("In AMEventManager.start() Starting event service "
                    + "thread...");
        }

        // Add a listener to listen for events.
        try {
            SSOToken token = (SSOToken) AccessController
                    .doPrivileged(AdminTokenAction.getInstance());
            if (debug.messageEnabled()) {
                debug.message("AMEventManager.start() Adding EntryEvent & "
                        + "ACIEvent Listeners..");
            }

            // Add Event Listener
            instance.addListener(token, new AMIdRepoListener());
        } catch (Exception le) {
            debug.error("AMEventManager.start() Exception occurred while "
                    + "starting event service thread", le);
            throw new AMEventManagerException(AMSDKBundle.getString("502"),
                    "502");
        }
    }
}
