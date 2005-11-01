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
 * $Id: ServiceConfigManagerImpl.java,v 1.1 2005-11-01 00:31:32 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.naming.event.NamingEvent;

import netscape.ldap.util.DN;

import com.iplanet.am.util.Cache;
import com.iplanet.am.util.Debug;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.ums.IUMSConstants;

/**
 * The class <code>ServiceConfigurationManagerImpl</code> provides interfaces
 * to read the service's configuration data. It provides access to
 * <code>ServiceConfigImpl</code> which represents a single "configuration" in
 * the service. It manages configuration data only for GLOBAL and ORGANIZATION
 * types.
 */
class ServiceConfigManagerImpl {
    // Instance variables
    private String serviceName;

    private String version;

    // Pointer to ServiceSchemaManangerImpl
    private ServiceSchemaManagerImpl ssm;

    // Pointer to schema changes listeners
    private HashMap listenerObjects;

    // Notification search string
    private String orgNotificationSearchString;

    private String glbNotificationSearchString;

    private String schemaNotificationSearchString;

    // Service Instances & Groups
    private CachedSubEntries instances;

    private CachedSubEntries groups;

    // LRU caches for global and org configs
    Cache globalConfigs;

    Cache orgConfigs;

    /**
     * Constructs an instance of <code>ServiceConfigManagerImpl</code> for the
     * given service and version. It requires an user identity that will be used
     * to perform read operations. It is assumed that the application calling
     * this constructor should authenticate the user.
     */
    private ServiceConfigManagerImpl(SSOToken token, String serviceName,
            String version) throws SMSException, SSOException {
        this.serviceName = serviceName;
        this.version = version;

        // Get the service DN
        String serviceDN = ServiceManager
                .getServiceNameDN(serviceName, version);

        // Get the ServiceSchemaManagerImpl
        ssm = ServiceSchemaManagerImpl.getInstance(token, serviceName, version);

        // Initialize instance variables
        listenerObjects = new HashMap();

        // Regsiter for notifications
        SMSEventListenerManager.notifyAllNodeChanges(token, this);
        DN notifyDN = new DN("ou=" + version + ",ou=" + serviceName + ","
                + SMSEntry.SERVICES_RDN);
        String sdn = notifyDN.toRFCString().toLowerCase();
        orgNotificationSearchString = CreateServiceConfig.ORG_CONFIG_NODE
                .toLowerCase()
                + sdn;
        glbNotificationSearchString = CreateServiceConfig.GLOBAL_CONFIG_NODE
                .toLowerCase()
                + sdn;
        schemaNotificationSearchString = sdn;

        // Construct Instance & Global entries nodes
        String dn = CreateServiceConfig.INSTANCES_NODE + serviceDN;
        instances = CachedSubEntries.getInstance(token, dn);
        dn = CreateServiceConfig.GLOBAL_CONFIG_NODE + serviceDN;
        groups = CachedSubEntries.getInstance(token, dn);

        // If caching is allowed, cache global & org configs
        if (SMSEntry.cacheSMSEntries) {
            globalConfigs = new Cache(1000);
            orgConfigs = new Cache(1000);
        }
    }

    /**
     * Returns ServiceSchemaManagerImpl
     */
    ServiceSchemaManagerImpl getServiceSchemaManagerImpl() {
        return (ssm);
    }

    String getName() {
        return (serviceName);
    }

    String getVersion() {
        return (version);
    }

    /**
     * Returns the service instance names
     */
    Set getInstanceNames(SSOToken t) throws SMSException, SSOException {
        return (instances.getSubEntries(t));
    }

    /**
     * Returns the configuration group names
     */
    Set getGroupNames(SSOToken t) throws SMSException, SSOException {
        return (groups.getSubEntries(t));
    }

    ServiceInstanceImpl getInstance(SSOToken token, String instanceName)
            throws SMSException, SSOException {
        return (ServiceInstanceImpl.getInstance(token, serviceName, version,
                instanceName));
    }

    /**
     * Returns the global configuration for the given service instance.
     */
    ServiceConfigImpl getGlobalConfig(SSOToken token, String instanceName)
            throws SMSException, SSOException {
        // Get global schema
        ServiceSchemaImpl ss = ssm.getSchema(SchemaType.GLOBAL);
        if (ss == null) {
            return (null);
        }
        // Get group name
        String groupName = (instanceName == null) ? SMSUtils.DEFAULT
                : getInstance(token, instanceName).getGroup();
        String cacheName = null;
        ServiceConfigImpl answer = null;
        // Check the cache
        if (SMSEntry.cacheSMSEntries) {
            StringBuffer sb = new StringBuffer(50);
            cacheName = sb.append(token.getTokenID().toString()).append(
                    groupName).toString().toLowerCase();
            if ((answer = (ServiceConfigImpl) globalConfigs.get(cacheName)) 
                    != null) 
            {
                return (answer);
            }
        }
        // Construct the sub-config
        String gdn = constructServiceConfigDN(groupName,
                CreateServiceConfig.GLOBAL_CONFIG_NODE, null);
        answer = ServiceConfigImpl.getInstance(token, this, ss, gdn, null,
                groupName, "", true);
        // Add to cache if needed
        if (SMSEntry.cacheSMSEntries) {
            globalConfigs.put(cacheName, answer);
        }
        return (answer);
    }

    /**
     * Returns the organization configuration for the given organization and
     * instance name.
     */
    ServiceConfigImpl getOrganizationConfig(SSOToken token, String orgName,
            String instanceName) throws SMSException, SSOException {
        // Get organization schema
        ServiceSchemaImpl ss = ssm.getSchema(SchemaType.ORGANIZATION);
        if (ss == null) {
            return (null);
        }
        // Construct the group name
        String groupName = (instanceName == null) ? SMSUtils.DEFAULT
                : getInstance(token, instanceName).getGroup();
        String cacheName = null;
        ServiceConfigImpl answer = null;
        // Check the cache
        String orgdn = DNMapper.orgNameToDN(orgName);
        if (SMSEntry.cacheSMSEntries) {
            StringBuffer sb = new StringBuffer(50);
            cacheName = sb.append(token.getTokenID().toString()).append(
                    groupName).append(orgdn).toString().toLowerCase();
            if ((answer = (ServiceConfigImpl) orgConfigs.get(cacheName)) 
                    != null) 
            {
                return (answer);
            }
        }
        // Construct org config
        String orgDN = constructServiceConfigDN(groupName,
                CreateServiceConfig.ORG_CONFIG_NODE, orgdn);
        answer = ServiceConfigImpl.getInstance(token, this, ss, orgDN, orgName,
                groupName, "", false);
        if (answer == null)
            return null;
        // Add to cache if needed
        if (SMSEntry.cacheSMSEntries) {
            orgConfigs.put(cacheName, answer);
        }
        return (answer);
    }

    /**
     * Returns the PluginConfig for configured for the serivce
     */
    PluginConfigImpl getPluginConfig(SSOToken token, String name,
            String schemaName, String interfaceName, String orgName)
            throws SMSException, SSOException {
        PluginSchemaImpl psi = PluginSchemaImpl.getInstance(token, serviceName,
                version, schemaName, interfaceName, orgName);
        // If null, throw an exception
        if (psi == null) {
            throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    "sms-invalid-plugin-schema-name", null));
        }
        // Construct the DN
        StringBuffer groupName = new StringBuffer(100);
        groupName.append(name).append(",ou=").append(schemaName).append(",ou=")
                .append(interfaceName);
        String dn = constructServiceConfigDN(groupName.toString(),
                CreateServiceConfig.PLUGIN_CONFIG_NODE, DNMapper
                        .orgNameToDN(orgName));
        return (PluginConfigImpl.getInstance(token, psi, dn, orgName));
    }

    /**
     * Register for changes to service's configuration. The object will be
     * called when configuration for this service and version is changed.
     */
    synchronized String addListener(ServiceListener listener) {
        String id = SMSUtils.getUniqueID();
        synchronized (listenerObjects) {
            listenerObjects.put(id, listener);
        }
        return (id);
    }

    /**
     * Unregisters the listener from the service for the given listener ID. The
     * ID was issued when the listener was registered.
     */
    synchronized void removeListener(String listenerID) {
        synchronized (listenerObjects) {
            listenerObjects.remove(listenerID);
        }
    }

    // Used by ServiceInstance
    boolean containsGroup(SSOToken token, String groupName)
            throws SMSException, SSOException {
        return (groups.contains(token, groupName));
    }

    void entryChanged(String dn, int type) {
        // Check for listeners
        if (listenerObjects.size() == 0) {
            if (SMSEntry.eventDebug.messageEnabled()) {
                SMSEntry.eventDebug.message("ServiceConfigManagerImpl:"
                        + "entryChanged No listeners registered DN: " + dn
                        + "\nService name: " + serviceName);
            }
            return;
        }

        // check for service name, version and type
        boolean globalConfig = false;
        boolean orgConfig = false;
        int orgIndex = 0;
        int index = 0;
        if ((index = dn.indexOf(orgNotificationSearchString)) != -1) {
            orgConfig = true;
            orgIndex = orgNotificationSearchString.length();
        } else if ((index = dn.indexOf(glbNotificationSearchString)) != -1) {
            globalConfig = true;
            orgIndex = glbNotificationSearchString.length();
        } else if ((index = dn.indexOf(schemaNotificationSearchString + ","
                + SMSEntry.getRootSuffix())) != -1) {
            globalConfig = true;
            orgConfig = true;
            orgIndex = schemaNotificationSearchString.length();
        } else if (serviceName.equalsIgnoreCase("sunidentityrepositoryservice")
                && (dn.startsWith(SMSEntry.ORG_PLACEHOLDER_RDN) || dn
                        .equalsIgnoreCase(DNMapper.serviceDN))) {
            // Since sunIdentityRepositoryService has realm creation
            // attributes, we need to send notification
            orgConfig = true;
        } else {
            if (SMSEntry.eventDebug.messageEnabled()) {
                SMSEntry.eventDebug.message(
                        "ServiceConfigManagerImpl:entryChanged Changed DN " +
                        "does not match the service. DN: " + dn + 
                        "\nService name: " + serviceName);
            }
            return;
        }

        // Get the group and component name
        String groupName = "";
        String compName = "";
        if (index > 1) {
            String rdns[] = (new DN(dn.substring(0, index - 1)))
                    .explodeDN(true);
            groupName = rdns[rdns.length - 1];
            for (int i = rdns.length - 2; i > -1; i--) {
                compName = compName + "/" + rdns[i];
            }
            if (compName.length() == 0) {
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
        String orgName = dn;
        if ((index > 0) || (orgConfig && globalConfig)) {
            orgName = dn.substring(index + orgIndex + 1);
        }
        if (globalConfig) {
            notifyGlobalConfigChange(groupName, compName, type);
            if (SMSEntry.eventDebug.messageEnabled()) {
                SMSEntry.eventDebug.message("" +
                        "ServiceConfigManagerImpl:entryChanged Sending " +
                        "global config change notifications for DN "+ dn);
            }
        }
        if (orgConfig) {
            notifyOrgConfigChange(orgName, groupName, compName, type);
            if (SMSEntry.eventDebug.messageEnabled()) {
                SMSEntry.eventDebug.message(
                        "ServiceConfigManagerImpl:entryChanged Sending org " +
                        "config change notifications for DN " + dn);
            }
        }
    }

    void notifyGlobalConfigChange(String groupName, String comp, int type) {
        HashMap lo;
        synchronized (listenerObjects) {
            lo = (HashMap) listenerObjects.clone();
        }
        Iterator items = lo.values().iterator();
        while (items.hasNext()) {
            ServiceListener sl = (ServiceListener) items.next();
            sl.globalConfigChanged(serviceName, version, groupName, comp, type);
        }
    }

    void notifyOrgConfigChange(String orgName, String groupName, String comp,
            int type) {
        HashMap lo;
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

    String constructServiceConfigDN(String groupName, String configName,
            String orgName) throws SMSException {
        StringBuffer sb = new StringBuffer(50);
        sb.append("ou=").append(groupName).append(SMSEntry.COMMA).append(
                configName).append("ou=").append(version)
                .append(SMSEntry.COMMA).append("ou=").append(serviceName)
                .append(SMSEntry.COMMA).append(SMSEntry.SERVICES_RDN).append(
                        SMSEntry.COMMA);
        if ((orgName == null) || (orgName.length() == 0)) {
            orgName = SMSEntry.baseDN;
        } else if (DN.isDN(orgName)) {
            // Do nothing
        } else if (orgName.startsWith("/")) {
            orgName = DNMapper.orgNameToDN(orgName);
        } else {
            String[] args = { orgName };
            throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    "sms-invalid-org-name", args));
        }
        sb.append(orgName);
        return (sb.toString());
    }

    // ---------------------------------------------------------
    // Static Protected Methods
    // ---------------------------------------------------------
    protected static ServiceConfigManagerImpl getInstance(SSOToken token,
            String serviceName, String version) throws SSOException,
            SMSException {
        if (debug.messageEnabled()) {
            debug.message("ServiceConfigMgrImpl::getInstance: called: "
                    + serviceName + "(" + version + ")");
        }
        // Construct the cache name, and check in cache
        String cName = ServiceManager.getCacheIndex(serviceName, version);
        ServiceConfigManagerImpl answer = getFromCache(cName, serviceName,
                version, token);
        if (answer != null) {
            return (answer);
        }

        // Not in cache, construct the entry and add to cache
        synchronized (configMgrMutex) {
            if ((answer = getFromCache(cName, serviceName, version, token)) 
                    == null) 
            {
                checkAndUpdatePermission(cName, serviceName, version, token);
                answer = new ServiceConfigManagerImpl(token, serviceName,
                        version);
                Map sudoConfigMgrImpls = new HashMap(configMgrImpls);
                sudoConfigMgrImpls.put(cName, answer);
                configMgrImpls = sudoConfigMgrImpls;
            }
        }

        if (debug.messageEnabled()) {
            debug.message("ServiceConfigMgrImpl::getInstance: success: "
                    + serviceName + "(" + version + ")");
        }
        return (answer);
    }

    static ServiceConfigManagerImpl getFromCache(String cacheName,
            String sName, String version, SSOToken t) throws SMSException,
            SSOException {
        ServiceConfigManagerImpl answer = 
            (ServiceConfigManagerImpl) configMgrImpls.get(cacheName);
        if (answer != null) {
            // Check if the user has permissions
            Set principals = (Set) userPrincipals.get(cacheName);
            if (!principals.contains(t.getTokenID().toString())) {
                // Check if Principal has permission to read entry
                checkAndUpdatePermission(cacheName, sName, version, t);
            }
        }
        return (answer);
    }

    static synchronized void checkAndUpdatePermission(String cacheName,
            String sName, String version, SSOToken t) throws SMSException,
            SSOException {
        String dn = ServiceManager.getServiceNameDN(sName, version);
        CachedSMSEntry.getInstance(t, dn, null);
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
    }

    private static Map configMgrImpls = new HashMap();

    private static final String configMgrMutex = "ConfigMgrMutex";

    private static Map userPrincipals = new HashMap();

    private static Debug debug = SMSEntry.debug;
}
