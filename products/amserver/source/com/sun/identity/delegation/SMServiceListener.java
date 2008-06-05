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
 * $Id: SMServiceListener.java,v 1.1 2008-06-05 05:03:44 arviranga Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.delegation;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceListener;
import com.sun.identity.common.GeneralTaskRunnable;
import com.sun.identity.common.SystemTimer;
import com.sun.identity.security.AdminTokenAction;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.sm.OrganizationConfigManager;
import com.sun.identity.sm.ServiceConfigManager;
import com.sun.identity.sm.ServiceManager;
import com.sun.identity.authentication.util.ISAuthConstants;
import java.security.AccessController;
import java.util.Iterator;
import java.util.Set;

/**
 * Service Configuration listener class to clean the delegation polcies
 * when realms are removed.
 */
public class SMServiceListener implements ServiceListener {
    
    private static SMServiceListener serviceListener;
    private String listenerId;
    private Debug debug = DelegationManager.debug;
    
    // private constructor
    private SMServiceListener() {
        // do nothing
    }
    
    public static SMServiceListener getInstance() {
        if (serviceListener == null) {
            serviceListener = new SMServiceListener();
        }
        return (serviceListener);
    }
    
    public void registerForNotifications() {
        if (listenerId != null) {
            // Listener already registered
            return;
        }
        SSOToken token = (SSOToken) AccessController.doPrivileged(
            AdminTokenAction.getInstance());
        try {
            // Try Delegation Service, present only in OpenSSO
            // Since Delegation Service is being added only in OpenSSO
            // check for its presense in root realm. If not present,
            // it is an upgrade from AM 7.1 and use AuthService
            ServiceConfigManager scm = null;
            OrganizationConfigManager ocm = new OrganizationConfigManager(
                token, "/");
            Set assignedServices = ocm.getAssignedServices();
            for (Iterator items = assignedServices.iterator();
                items.hasNext();) {
                if (items.toString().equalsIgnoreCase(
                    DelegationManager.DELEGATION_SERVICE)) {
                    scm = new ServiceConfigManager(
                        DelegationManager.DELEGATION_SERVICE, token);
                    break;
                }
            }
            if (scm == null) {
                // Delegation Service not found, use Auth service
                scm = new ServiceConfigManager(
                    ISAuthConstants.AUTH_SERVICE_NAME, token);
            }
            listenerId = scm.addListener(this);
        } catch (SMSException ex) {
            debug.error("Unable to register SMS notification for Delegation",
                ex);
        } catch (SSOException ex) {
            debug.error("Unable to register SMS notification for Delegation",
                ex);
        }
    }

    public void schemaChanged(String serviceName, String version) {
        // do nothing
    }

    public void globalConfigChanged(String serviceName, String version,
        String groupName, String serviceComponent, int type) {
        // do nothing
    }

    public void organizationConfigChanged(String serviceName, String version,
        String orgName, String groupName, String serviceComponent, int type) {
        // If event type is delete,
        // remove realm privileges for the organization
        if ((serviceComponent == null) || (serviceComponent.length() == 0)) {
            // Normalize the orgName to remove "ou=services"
            int index = orgName.indexOf(",ou=services,");
            if (index > 0) {
                orgName = orgName.substring(index + 13);
            }
            if (type == ServiceListener.REMOVED) {
                // Schedule the task to delete delegation policies
                DeleteDelegationPolicyTask task = new DeleteDelegationPolicyTask(
                    orgName);
                SystemTimer.getTimer().schedule(task, 1000);
            } else if (type == ServiceListener.ADDED) {
                // Create the delegation policies
                SSOToken token = (SSOToken) AccessController.doPrivileged(
                    AdminTokenAction.getInstance());
                try {
                    if (ServiceManager.isCoexistenceMode()) {
                        DelegationUtils.createRealmPrivileges(token, orgName);
                    } else {
                        OrganizationConfigManager ocm =
                            new OrganizationConfigManager(token, orgName);
                        OrganizationConfigManager parentOrg =
                            ocm.getParentOrgConfigManager();
                        DelegationUtils.copyRealmPrivilegesFromParent(
                            token, parentOrg, ocm);
                    }
                } catch (SSOException ssoe) {
                    if (debug.messageEnabled()) {
                        debug.message("Creating delegation permissions for: " +
                            orgName + " failed", ssoe);
                    }
                } catch (SMSException smse) {
                    if (debug.messageEnabled()) {
                        debug.message("Creating delegation permissions for: " +
                            orgName + " failed", smse);
                    }
                } catch (DelegationException de) {
                    if (debug.messageEnabled()) {
                        debug.message("Creating delegation permissions for: " +
                            orgName + " failed", de);
                    }
                }
            }
        }
    }
    
    private class DeleteDelegationPolicyTask extends GeneralTaskRunnable {
        
        private String realm;
        
        private DeleteDelegationPolicyTask(String realm) {
            this.realm = realm;
        }

        public void run() {
            // Check if the realm exists, if not delete the delegation rules
            SSOToken token = (SSOToken) AccessController.doPrivileged(
                AdminTokenAction.getInstance());
            try {
                // Throws SMSException is realm does not exist
                new OrganizationConfigManager(token, realm);
            } catch (SMSException e) {
                try {
                    // Realm not present, remove the delegation policies
                    if (debug.messageEnabled()) {
                        debug.message("Deleting delegation privilegs for " +
                            "realm" + realm);
                    }
                    DelegationUtils.deleteRealmPrivileges(token, realm);
                } catch (SSOException ex) {
                    if (debug.messageEnabled()) {
                        debug.message("Error deleting delegation privilegs " +
                            "for realm" + realm, ex);
                    }
                } catch (DelegationException ex) {
                    if (debug.messageEnabled()) {
                        debug.message("Error deleting delegation privilegs " +
                            "for realm" + realm, ex);
                    }
                }
            }
        }

        public boolean addElement(Object key) {
            return false;
        }

        public boolean removeElement(Object key) {
            return false;
        }

        public boolean isEmpty() {
            return true;
        }

        public long getRunPeriod() {
            return -1;
        }
    }
}
