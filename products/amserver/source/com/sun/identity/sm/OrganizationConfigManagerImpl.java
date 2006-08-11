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
 * $Id: OrganizationConfigManagerImpl.java,v 1.4 2006-08-11 00:42:26 rarcot Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.event.NamingEvent;

import netscape.ldap.LDAPDN;
import netscape.ldap.util.DN;

import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.ums.IUMSConstants;

/**
 * The class <code>OrganizationConfigManagerImpl</code> provides interfaces to
 * read the service's configuration data. It provides access to
 * <code>OrganizationConfigImpl</code> which represents a single
 * "configuration" in the service. It manages configuration data only for GLOBAL
 * and ORGANIZATION types.
 */
class OrganizationConfigManagerImpl {

    // Instance variables
    private String orgDN;

    private CachedSubEntries subEntries = null;

    // Pointer to schema changes listeners
    private HashMap listenerObjects;

    // Notification search string
    private String orgNotificationSearchString;

    /**
     * Constructs an instance of <code>OrganizationConfigManagerImpl</code>
     * for the given organization. It requires an user identity that will be
     * used to perform read operations. It is assumed that the application
     * calling this constructor should authenticate the user.
     */
    private OrganizationConfigManagerImpl(String orgDN, SSOToken token)
            throws SMSException {
        this.orgDN = orgDN;

        // Initialize instance variables
        listenerObjects = new HashMap(2);

        // Register for notifications
        SMSEventListenerManager.notifyAllNodeChanges(token, this);

        if (!orgDN.startsWith(SMSEntry.SERVICES_RDN)) {
            DN notifyDN = new DN(SMSEntry.SERVICES_RDN + "," + orgDN);
            orgNotificationSearchString = notifyDN.toRFCString().toLowerCase();
        } else {
            orgNotificationSearchString = orgDN;
        }
    }

    /**
     * Returns organization name as DN
     */
    String getOrgDN() {
        return (orgDN);
    }

    /**
     * Returns a set of service names that are assigned to this realm
     */
    Set getAssignedServices(SSOToken token) throws SMSException {
        try {
            HashSet answer = new HashSet();
            // Get service names and iterate through them
            CachedSubEntries se = null;
            if (orgDN.equals(DNMapper.serviceDN)) {
                se = CachedSubEntries.getInstance(token, orgDN);
            } else {
                se = CachedSubEntries
                        .getInstance(token, "ou=services," + orgDN);
            }
            for (Iterator names = se.getSubEntries(token).iterator(); names
                    .hasNext();) {
                String serviceName = (String) names.next();
                ServiceConfigManagerImpl scmi = ServiceConfigManagerImpl
                        .getInstance(token, serviceName, ServiceManager
                                .serviceDefaultVersion(token, serviceName));
                try {
                    ServiceConfigImpl sci = scmi.getOrganizationConfig(token,
                            orgDN, null);
                    if (sci != null && !sci.isNewEntry()) {
                        answer.add(serviceName);
                    }
                } catch (SMSException smse) {
                    if (smse.getExceptionCode() != 
                        SMSException.STATUS_NO_PERMISSION) 
                    {
                        throw (smse);
                    }
                }
            }
            return (answer);
        } catch (SSOException ssoe) {
            debug.error("OrganizationConfigManagerImpl.getAssignedServices "
                    + "Unable to get assigned services", ssoe);
            throw (new SMSException(SMSEntry.bundle
                    .getString("sms-INVALID_SSO_TOKEN"),
                    "sms-INVALID_SSO_TOKEN"));
        }
    }

    /**
     * Returns the names of all suborganizations.
     */
    Set getSubOrganizationNames(SSOToken token) throws SMSException {
        return (getSubOrganizationNames(token, "*", false));
    }

    /**
     * Returns the names of suborganizations that match the given pattern.
     */
    Set getSubOrganizationNames(SSOToken token, String pattern,
            boolean recursive) throws SMSException {

        try {
            if (subEntries == null) {
                subEntries = CachedSubEntries.getInstance(token, orgDN);
            }
            return (subEntries.searchSubOrgNames(token, pattern, recursive));
        } catch (SSOException ssoe) {
            debug.error("OrganizationConfigManagerImpl: Unable to "
                    + "get sub organization names", ssoe);
            throw (new SMSException(SMSEntry.bundle
                    .getString("sms-INVALID_SSO_TOKEN"),
                    "sms-INVALID_SSO_TOKEN"));
        }
    }

    /**
     * Registers for changes to organization's configuration. The object will be
     * called when configuration for this organization is changed.
     * 
     * @param listener
     *            callback object that will be invoked when organization
     *            configuration has changed
     * @return an ID of the registered listener.
     */

    synchronized String addListener(ServiceListener listener) {
        String id = SMSUtils.getUniqueID();
        synchronized (listenerObjects) {
            listenerObjects.put(id, listener);
        }
        return (id);
    }

    /**
     * Removes the listener from the organization for the given listener ID. The
     * ID was issued when the listener was registered.
     * 
     * @param listenerID
     *            the listener ID issued when the listener was registered
     */
    public void removeListener(String listenerID) {
        synchronized (listenerObjects) {
            listenerObjects.remove(listenerID);
        }
    }

    void entryChanged(String dn, int type) {
        // Check for listeners
        if (listenerObjects.size() == 0) {
            if (SMSEntry.eventDebug.messageEnabled()) {
                SMSEntry.eventDebug.message("OrgConfigMgrImpl::entryChanged"
                        + " No listeners registered: " + dn
                        + "\norgNotificationSearchString: "
                        + orgNotificationSearchString);
            }
            return;
        }

        // check for service name, version and type
        int index = 0;
        int orgIndex = 0;

        // From realm tree, orgNotificationSearchString will be
        // ou=services,o=hpq,ou=services,dc=iplanet,dc=com
        if (SMSEntry.eventDebug.messageEnabled()) {
            SMSEntry.eventDebug.message("OrgConfigMgrImpl::entryChanged "
                    + " DN: " + dn + "\norgNotificationSearchString: "
                    + orgNotificationSearchString);
        }

        // Check if the DN matches with organization name
        if ((index = dn.indexOf(orgNotificationSearchString)) != -1) {
            orgIndex = SMSEntry.SERVICES_RDN.length();

            // Initialize parameters
            String serviceName = "";
            String version = "";
            String groupName = "";
            String compName = "";

            // Get the DN ignoring the organization name
            if (index != 0) {
                String ndn = dn.substring(0, index - 1);

                // Needs to check if the DN has more realm names
                String rdns[] = LDAPDN.explodeDN(ndn, false);
                int size = (rdns == null) ? 0 : rdns.length;
                if ((size != 0) && (rdns[size - 1].startsWith("o="))) {
                    // More realm names are present, changes not meant for
                    // this organization
                    if (SMSEntry.eventDebug.messageEnabled()) {
                        SMSEntry.eventDebug.message(
                            "OrgConfigMgrImpl::entryChanged  Notification " +
                            "not sent since realms names donot match. \nDN: " +
                            dn + " And orgNotificationSearchString: " + 
                            orgNotificationSearchString);
                    }
                    return;
                }

                // Get the version, service, group and component name
                rdns = LDAPDN.explodeDN(ndn, true); 
                if (size > 0) {
                    serviceName = rdns[size - 1];
                }
                if (size > 1) {
                    version = rdns[size - 2];
                }
                if (size >= 4) {
                    groupName = rdns[size - 4];
                }

                // The subconfig names should be "/" separated and left to right
                if (size >= 5) {
                    StringBuffer sbr = new StringBuffer();
                    for (int i = size - 4; i >= 0; i--) {
                        sbr.append('/').append(rdns[i]);
                    }
                    compName = sbr.toString();
                } else {
                    compName = "/";
                }
            }

            // Convert changeType from JNDI to netscape.ldap
            switch (type) {
            case NamingEvent.OBJECT_ADDED:
                type = ServiceListener.ADDED;
                break;
            case NamingEvent.OBJECT_REMOVED:
                type = ServiceListener.REMOVED;
                break;
            default:
                type = ServiceListener.MODIFIED;
            }

            // Get organization name
            String orgName = dn.substring(index + orgIndex + 1);

            if (SMSEntry.eventDebug.messageEnabled()) {
                SMSEntry.eventDebug.message("OrganizationConfigManagerImpl:"
                        + "entryChanged() serviceName " + serviceName);
                SMSEntry.eventDebug.message("OrganizationConfigManagerImpl:"
                        + "entryChanged() version " + version);
                SMSEntry.eventDebug.message("OrganizationConfigManagerImpl:"
                        + "entryChanged() orgName " + orgName);
                SMSEntry.eventDebug.message("OrganizationConfigManagerImpl:"
                        + "entryChanged() groupName " + groupName);
                SMSEntry.eventDebug.message("OrganizationConfigManagerImpl:"
                        + "entryChanged() compName " + compName);
                SMSEntry.eventDebug.message("OrganizationConfigManagerImpl:"
                        + "entryChanged() type " + type);
            }

            // Send notifications to listeners
            notifyOrgConfigChange(serviceName, version, orgName, groupName,
                    compName, type);
        }
    }

    void notifyOrgConfigChange(String serviceName, String version,
            String orgName, String groupName, String comp, int type) {

        Map lo = Collections.EMPTY_MAP;
        synchronized (listenerObjects) {
            lo = (HashMap) listenerObjects.clone();
        }
        Iterator items = lo.values().iterator();
        while (items.hasNext()) {
            ServiceListener sl = (ServiceListener) items.next();
            sl.organizationConfigChanged(serviceName, version, orgName,
                    groupName, comp, type);
        }
    }

    // ---------------------------------------------------------
    // Static Protected Methods
    // ---------------------------------------------------------
    protected static OrganizationConfigManagerImpl getInstance(SSOToken token,
        String orgName) throws SMSException {

        // Convert orgName to DN
        String orgDN = DNMapper.orgNameToDN(orgName);
        // If orgDN is the baseDN, append "ou=services" to it
        if (orgDN.equalsIgnoreCase(SMSEntry.baseDN)) {
            orgDN = DNMapper.serviceDN;
        }
        if (debug.messageEnabled()) {
            debug.message("OrganizationConfigMgrImpl::getInstance: called: " +
                "(" + orgName + ")=" + orgDN);
        }

        // check in cache for organization name
        OrganizationConfigManagerImpl answer = null;
        synchronized (configMgrMutex) {
            answer = getFromCache(orgDN, token);
        }
        if ((answer != null) && ServiceManager.isRealmEnabled()) {
            return (answer);
        }

        // Not in cache or in legacy mode, check if the realm exists
        CachedSMSEntry cEntry = null;
        try {
            // If in co-exist mode, SMS will not get updates for org
            // hence have to update the cEntry
            cEntry = checkAndUpdatePermission(orgDN, token);
            if (ServiceManager.isCoexistenceMode()) {
                cEntry.update();
            }
            if (cEntry.isNewEntry()) {
                if (debug.messageEnabled()) {
                    debug.message("OrganizationConfigManagerImpl::getInstance" +
                        " called with non-existent realm: " + orgName);
                }
                String args[] = { orgName };
                throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    "sms-REALM_NAME_NOT_FOUND", args));
            }
        } catch (SSOException s) {
            SMSEntry.debug.error("OrganizationConfigManagerImpl.getInstance", s);
            throw (new SMSException(SMSEntry.bundle.getString(
                "sms-INVALID_SSO_TOKEN"), "sms-INVALID_SSO_TOKEN"));
        }

        // Not in cache, construct the entry and add to cache
        answer = new OrganizationConfigManagerImpl(orgDN, token);
        synchronized(configMgrMutex) {
            // Check the cache again
            OrganizationConfigManagerImpl tmp;
            if ((tmp = getFromCache(orgDN, null)) == null) {
                configMgrImpls.put(orgDN, answer);
            } else {
                answer = tmp;
            }
        }
        if (debug.messageEnabled()) {
            debug.message("OrganizationConfigMgrImpl::getInstance: success: " +
                orgDN);
        }
        return (answer);
    }

    static OrganizationConfigManagerImpl getFromCache(String cacheName, 
        SSOToken t) throws SMSException {
         OrganizationConfigManagerImpl answer = (OrganizationConfigManagerImpl)
            configMgrImpls.get(cacheName);
        if ((answer != null) && (t != null)) {
            // Check if the user has permissions
            Set principals = (Set) userPrincipals.get(cacheName);
            if (!principals.contains(t.getTokenID().toString())) {
                // Principal name not in cache, need to check perm
                answer = null;
            }
        }
        return (answer);
    }

    static CachedSMSEntry checkAndUpdatePermission(String cacheName,
        SSOToken t) throws SSOException, SMSException {
        CachedSMSEntry answer = null;
        answer = CachedSMSEntry.getInstance(t, cacheName, null);
        synchronized (configMgrMutex) {
            Set sudoPrincipals = (Set) userPrincipals.get(cacheName);
            if (sudoPrincipals == null) {
                sudoPrincipals = new HashSet(2);
                userPrincipals.put(cacheName, sudoPrincipals);
            }
            sudoPrincipals.add(t.getTokenID().toString());
        }
        return (answer);
    }

    private static Map configMgrImpls = new HashMap();

    private static final String configMgrMutex = "ConfigMgrMutex";

    private static Map userPrincipals = new HashMap();

    private static Debug debug = SMSEntry.debug;
}
