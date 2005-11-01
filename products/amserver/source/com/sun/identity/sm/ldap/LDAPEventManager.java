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
 * $Id: LDAPEventManager.java,v 1.1 2005-11-01 00:31:36 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm.ldap;

import java.security.AccessController;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import netscape.ldap.LDAPConnection;
import netscape.ldap.controls.LDAPPersistSearchControl;

import com.iplanet.am.util.Debug;
import com.iplanet.services.ldap.DSConfigMgr;
import com.iplanet.services.ldap.LDAPUser;
import com.iplanet.services.ldap.ServerInstance;
import com.iplanet.services.ldap.event.DSEvent;
import com.iplanet.services.ldap.event.IDSEventListener;
import com.sun.identity.authentication.internal.AuthPrincipal;
import com.sun.identity.security.AdminDNAction;
import com.sun.identity.sm.SMSEntry;
import com.sun.identity.sm.SMSObjectListener;
import com.sun.identity.sm.SMSUtils;

/**
 * This class registers a persistant search with the event service for any
 * changes to SMS object classes
 */
public class LDAPEventManager implements IDSEventListener {

    // Listener ID
    protected static String listenerID;

    protected static ServerInstance serInstance;

    // Notification list
    protected static Map changeListeners = new HashMap();

    protected static Debug eventDebug = Debug.getInstance("amSMSEvent");

    Map listeners = null;

    protected static final int OPERATIONS = LDAPPersistSearchControl.ADD
            | LDAPPersistSearchControl.MODIFY | LDAPPersistSearchControl.DELETE
            | LDAPPersistSearchControl.MODDN;

    // ********** Work Around for Dead lock issue while starting EventService
    // Avoid initializing SMSEntry.
    protected static final String OC_SERVICE = "sunService";

    protected static final String OC_SERVICE_COMP = "sunServiceComponent";

    protected static final String SEARCH_FILTER = "(|(objectclass="
            + OC_SERVICE + ")(objectclass=" + OC_SERVICE_COMP + "))";

    // Admin SSOToken
    protected static Principal adminPrincipal = new AuthPrincipal(
            (String) AccessController.doPrivileged(new AdminDNAction()));

    protected static boolean initialized;

    static void initialize() {
        try {
            serInstance = DSConfigMgr.getDSConfigMgr().getServerInstance(
                    LDAPUser.Type.AUTH_ADMIN);
            if (eventDebug.messageEnabled()) {
                eventDebug.message("LDAPEventManager:initialize "
                        + "Initialized LDAPEvent listener");
            }
            initialized = true;
        } catch (Exception e) {
            eventDebug.error("LDAPEventManager:initialize "
                    + "Unable to init LDAP listener", e);
        }
    }

    public LDAPEventManager() {
        if (!initialized) {
            initialize();
        }
    }

    static synchronized String addObjectChangeListener(
            SMSObjectListener changeListener) {
        String id = SMSUtils.getUniqueID();
        if (!initialized) {
            // Setup persistant search connections
            initialize();
        }
        changeListeners.put(id, changeListener);
        return (id);
    }

    static synchronized void removeObjectChangeListener(String id) {
        changeListeners.remove(id);
    }

    public synchronized void entryChanged(DSEvent dsEvent) {
        // Process entry changed events
        int event = dsEvent.getEventType();
        String dn = dsEvent.getID();
        switch (event) {
        case DSEvent.OBJECT_ADDED:
            event = SMSObjectListener.ADD;
            break;
        case DSEvent.OBJECT_REMOVED:
        case DSEvent.OBJECT_RENAMED:
            event = SMSObjectListener.DELETE;
            break;
        case DSEvent.OBJECT_CHANGED:
            event = SMSObjectListener.MODIFY;
            break;
        }
        if (eventDebug.messageEnabled()) {
            eventDebug.message("SMSEventListener::entry changed " + "for: "
                    + dn + " sending object changed notifications");
        }

        // Call SMSEntry to send notifications
        SMSEntry.objectChanged(dn, event);
    }

    public void eventError(String errorStr) {
        eventDebug.error("SMSEventListener.eventError(): " + errorStr);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.services.ldap.event.IDSEventListener#allEntriesChanged()
     */
    public void allEntriesChanged() {
        eventDebug.error("LDAPEventManager: received all entries "
                + "changed event from EventService");
        // Get Change Listeners and send send notification
        for (Iterator items = changeListeners.values().iterator(); items
                .hasNext();) {
            SMSObjectListener ocl = (SMSObjectListener) items.next();
            ocl.allObjectsChanged();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.services.ldap.event.IDSEventListener#getBase()
     */
    public String getBase() {
        return serInstance.getBaseDN();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.services.ldap.event.IDSEventListener#getFilter()
     */
    public String getFilter() {
        return SEARCH_FILTER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.services.ldap.event.IDSEventListener#getOperations()
     */
    public int getOperations() {
        return OPERATIONS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.services.ldap.event.IDSEventListener#getScope()
     */
    public int getScope() {
        return LDAPConnection.SCOPE_SUB;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iplanet.services.ldap.event.IDSEventListener#setListeners()
     */
    public void setListeners(Map listener) {
        this.listeners = listener;
    }

}
