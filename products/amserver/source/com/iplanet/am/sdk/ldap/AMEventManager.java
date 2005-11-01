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
 * $Id: AMEventManager.java,v 1.1 2005-11-01 00:29:28 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.am.sdk.ldap;

import java.util.Map;

import netscape.ldap.LDAPv2;

import com.iplanet.am.sdk.AMEventManagerException;
import com.iplanet.am.sdk.AMSDKBundle;
import com.iplanet.am.util.Debug;
import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.ldap.event.EventService;

/**
 * This <code>AMEventManager</code> adds Listeners to the EventService and
 * defines all the constants and other parameters needed to add the listeners.
 * Once initialized, it adds two separate listeners which listen to
 * notifications on ACI changes and entry modification/renaming/deletion events.
 * 
 * <p>
 * <b>NOTE:</b> This class is not a singleton class. So it should not be
 * instantiated directly. It is only instantiated when an instance of
 * AMCacheManager is created and since AMCacheManager class is a singleton it is
 * guaranted to have only one instance of this class.
 * <p>
 */
class AMEventManager {
    protected static final int EVENT_SCOPE = LDAPv2.SCOPE_SUB;

    protected static String EVENT_BASE_NODE = SystemProperties
            .get("com.iplanet.am.rootsuffix");

    protected static Debug debug = Debug.getInstance("amEventService");

    private EventService evtService = null;

    private Map listeners;

    /**
     * Constructor initializes the underlying UMS EventService and adds the
     * listeners
     */
    protected AMEventManager(Map listeners) throws AMEventManagerException {
        this.listeners = listeners;
    }

    /**
     * This method starts the EventService of the UMS and registers two
     * listeners AMEntryEventListener and AMACIEventListener to the EventService
     * inorder to receive notifications. Both the above listeners implement the
     * <code>com.iplanet.services.ldap.event.
     * IDSListener</code> interface.
     * <p>
     * 
     * NOTE: This method should be invoked only once.
     * 
     * @throws AMEventManagerException
     *             when encounters errors in starting the underlying
     *             EventService.
     */
    protected void start() throws AMEventManagerException {
        // Get a handle to the (singleton object) Event Manager
        try {
            if (debug.messageEnabled()) {
                debug.message("AMEventManager.start() - Getting EventService"
                        + " instance");
            }
            evtService = EventService.getEventService();
        } catch (Exception e) {
            debug.error("AMEventManager.start() Unable to get EventService ",e);
            throw new AMEventManagerException(AMSDKBundle.getString("501"),
                    "501");
        }

        // Initialize the listeners
        AMACIEventListener l1 = 
            (AMACIEventListener) evtService.getIDSListeners(
                    "com.iplanet.am.sdk.ldap.AMACIEventListener");
        AMEntryEventListener l2 = 
            (AMEntryEventListener) evtService.getIDSListeners(
                    "com.iplanet.am.sdk.ldap.AMEntryEventListener");
        if (l1 == null) {
            debug.error("AMEventManager.start: l1 is null");
        }
        if (l2 == null) {
            debug.error("AMEventManager.start: l2 is null");
        }
        l1.setListeners(listeners);
        l2.setListeners(listeners);
    }
}
