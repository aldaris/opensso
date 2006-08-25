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
 * $Id: ServiceInstanceImpl.java,v 1.2 2006-08-25 21:21:30 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.ums.IUMSConstants;
import com.sun.identity.shared.debug.Debug;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class <code>ServiceInstanceImpl</code> provides methods to get
 * service's instance variables.
 */
class ServiceInstanceImpl {
    // Cached SMS entry
    private String name;

    private String group;

    private String uri;

    private CachedSMSEntry smsEntry;

    // Instance attributes
    private Map attributes;

    private ServiceInstanceImpl(String name, CachedSMSEntry entry) {
        this.name = name;
        smsEntry = entry;
        smsEntry.addServiceListener(this);
        update();
    }

    String getName() {
        return (name);
    }

    String getGroup() {
        return (group);
    }

    String getURI() {
        return (uri);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("\nService Instance: ").append(name).append("\n\tGroup: ")
                .append(getGroup()).append("\n\tURI: ").append(getURI())
                .append("\n\tAttributes: ").append(attributes);
        return (sb.toString());
    }

    Map getAttributes() {
        return (SMSUtils.copyAttributes(attributes));
    }

    SMSEntry getSMSEntry() {
        return (smsEntry.getClonedSMSEntry());
    }

    void refresh(SMSEntry newEntry) throws SMSException {
        smsEntry.refresh(newEntry);
    }

    void updateupdateAndNotifyListeners() {
        update();
    }

    void update() {
        // Read the attributes
        attributes = SMSUtils.getAttrsFromEntry(smsEntry.getSMSEntry());

        // Get the group attribute
        group = SMSUtils.DEFAULT;
        String[] groups = smsEntry.getSMSEntry().getAttributeValues(
                SMSEntry.ATTR_SERVICE_ID);
        if (groups != null) {
            group = groups[0];
        }

        // Get the URI
        uri = null;
        String[] uris = smsEntry.getSMSEntry().getAttributeValues(
                SMSEntry.ATTR_LABELED_URI);
        if (uris != null) {
            uri = uris[0];
        }
    }

    // ----------------------------------------------------------
    // Protected static methods
    // ----------------------------------------------------------
    static ServiceInstanceImpl getInstance(SSOToken token, String serviceName,
            String version, String iName) throws SMSException, SSOException {
        if (debug.messageEnabled()) {
            debug
                    .message("ServiceInstanceImpl::getInstance: called: "
                            + serviceName + "(" + version + ")" + " Instance: "
                            + iName);
        }
        String cName = getCacheName(serviceName, version, iName);
        // Check the cache
        ServiceInstanceImpl answer = getFromCache(cName, serviceName, version,
                iName, token);
        if (answer != null) {
            // Check if the entry has to be updated
            if (!SMSEntry.cacheSMSEntries) {
                // Since the SMSEntries are not to be cached, read the entry
                answer.update();
            }
            return (answer);
        }

        // Construct the service instance
        synchronized (serviceInsMutex) {
            if ((answer = getFromCache(cName, serviceName, version, iName,
                    token)) == null) {
                // Still not present in cache, create and add to cache
                CachedSMSEntry entry = checkAndUpdatePermission(cName,
                        serviceName, version, iName, token);
                answer = new ServiceInstanceImpl(iName, entry);
                Map sudoServiceInstances = new HashMap(serviceInstances);
                sudoServiceInstances.put(cName, answer);
                serviceInstances = sudoServiceInstances;
            }
        }
        if (debug.messageEnabled()) {
            debug
                    .message("ServiceInstanceImpl::getInstance: success: "
                            + serviceName + "(" + version + ")" + " Instance: "
                            + iName);
        }
        return (answer);
    }

    // Clears the cache
    static void clearCache() {
        serviceInstances = new HashMap();
    }

    static String getCacheName(String sName, String version, String ins) {
        StringBuffer sb = new StringBuffer(100);
        sb.append(sName).append(version).append(ins);
        return (sb.toString().toLowerCase());
    }

    static ServiceInstanceImpl getFromCache(String cacheName, String sName,
            String version, String iName, SSOToken t) throws SMSException,
            SSOException {
        ServiceInstanceImpl answer = (ServiceInstanceImpl) serviceInstances
                .get(cacheName);
        if (answer != null && !answer.smsEntry.isValid()) {
            // CachedSMSEntry is invalid. Recreate this instance
            answer = null;
        }
        if (answer != null) {
            // Check if the user has permissions
            Set principals = (Set) userPrincipals.get(cacheName);
            if (!principals.contains(t.getTokenID().toString())) {
                // Check if Principal has permission to read entry
                checkAndUpdatePermission(cacheName, sName, version, iName, t);
            }
        }
        return (answer);
    }

    static synchronized CachedSMSEntry checkAndUpdatePermission(
            String cacheName, String serviceName, String version, String iName,
            SSOToken t) throws SMSException, SSOException {
        // Construct the DN
        String dn = "ou=" + iName + "," + CreateServiceConfig.INSTANCES_NODE
                + ServiceManager.getServiceNameDN(serviceName, version);
        CachedSMSEntry entry = CachedSMSEntry.getInstance(t, dn, null);
        if (entry.isNewEntry()) {
            String[] args = { iName };
            throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    "sms-no-such-instance", args));
        }
        Set sudoPrincipals = (Set) userPrincipals.get(cacheName);
        if (sudoPrincipals == null) {
            sudoPrincipals = new HashSet();
        } else {
            sudoPrincipals = new HashSet(sudoPrincipals);
        }
        sudoPrincipals.add(t.getTokenID().toString());
        Map sudoUserPrincipals = new HashMap(userPrincipals);
        sudoUserPrincipals.put(cacheName, sudoPrincipals);
        userPrincipals = sudoUserPrincipals;
        return (entry);
    }

    private static Map serviceInstances = new HashMap();

    private static Map userPrincipals = new HashMap();

    private static final String serviceInsMutex = "ServiceInstanceMutex";

    private static Debug debug = SMSEntry.debug;
}
