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
 * $Id: ServiceConfigManager.java,v 1.1 2005-11-01 00:31:32 arvindp Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iplanet.am.util.XMLUtils;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.iplanet.ums.IUMSConstants;

/**
 * The class <code>ServiceConfigurationManager</code> provides interfaces to
 * manage the service's configuration data. It provides access to
 * <code>ServiceConfig</code> which represents a single "configuration" in the
 * service. It manages configuration data only for GLOBAL and ORGANIZATION
 * types.
 */
public class ServiceConfigManager {
    // Instance variables
    private SSOToken token;

    private String serviceName;

    private String version;

    // Pointer to ServiceSchemaManangerImpl
    private ServiceSchemaManagerImpl ssm;

    private ServiceConfigManagerImpl scm;

    /**
     * Constrctor to obtain an instance <code>ServiceConfigManager
     * </code> for
     * a service by providing an authenticated identity of the user.
     * 
     * @param serviceName
     *            name of the service
     * @param token
     *            single sign on token of authenticated user identity
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceConfigManager(String serviceName, SSOToken token)
            throws SMSException, SSOException {
        // Use of the service versions
        this(token, serviceName, ServiceManager.serviceDefaultVersion(token,
                serviceName));
    }

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Creates an instance of
     * <code>ServiceConfigManager</code> for the given service and version. It
     * requires an user identity, that will used to perform operations with. It
     * is assumed that the application calling this constructor should
     * authenticate the user.
     * 
     * @param token
     *            single sign on token of the user identity on whose behalf the
     *            operations are performed.
     * @param serviceName
     *            the name of the service
     * @param version
     *            the version of the service
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceConfigManager(SSOToken token, String serviceName,
            String version) throws SMSException, SSOException {
        if (token == null || serviceName == null || version == null) {
            throw new IllegalArgumentException(SMSEntry.bundle
                    .getString(IUMSConstants.SMS_INVALID_PARAMETERS));
        }
        SSOTokenManager.getInstance().validateToken(token);
        // Get the ServiceSchemaManagerImpl
        scm = ServiceConfigManagerImpl.getInstance(token, serviceName, version);
        ssm = scm.getServiceSchemaManagerImpl();

        // Copy instance variables
        this.token = token;
        this.serviceName = serviceName;
        this.version = version;
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the name of the service.
     * 
     * @return the name of the service
     */
    public String getName() {
        return (serviceName);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the service version.
     * 
     * @return the version of the service
     */
    public String getVersion() {
        return (version);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the service instance names
     * 
     * @return the service instance names
     * @throws SMSException
     *             if an error has occurred while performing the operation
     */
    public Set getInstanceNames() throws SMSException {
        try {
            return (scm.getInstanceNames(token));
        } catch (SSOException s) {
            SMSEntry.debug.error("ServiceConfigManager: Unable to "
                    + "get Instance Names", s);
        }
        return (Collections.EMPTY_SET);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the configuration group names
     * 
     * @return the service configuration group names
     * @throws SMSException
     *             if an error has occurred while performing the operation
     */
    public Set getGroupNames() throws SMSException {
        try {
            return (scm.getGroupNames(token));
        } catch (SSOException s) {
            SMSEntry.debug.error("ServiceConfigManager: Unable to "
                    + "get Group Names", s);
        }
        return (Collections.EMPTY_SET);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the service instance given the instance
     * name
     * 
     * @param instanceName
     *            the name of the service instance
     * @return service instance for the given instance name
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceInstance getInstance(String instanceName)
            throws SMSException, SSOException {
        return (new ServiceInstance(this, 
                scm.getInstance(token, instanceName)));
    }

    /**
     * iPlanet-PUBLIC-METHOD Removes the instance form the service
     * 
     * @param instanceName
     *            the service instance name
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public void removeInstance(String instanceName) throws SMSException,
            SSOException {
        getInstance(instanceName).delete();
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the global configuration for the given
     * service instance.
     * 
     * @param instanceName
     *            the service instance name
     * @return the global configuration for the given service instance
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceConfig getGlobalConfig(String instanceName)
            throws SMSException, SSOException {
        ServiceConfigImpl sci = scm.getGlobalConfig(token, instanceName);
        return ((sci == null) ? null : new ServiceConfig(this, sci));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the organization configuration for the
     * given organization and instance name.
     * 
     * @param orgName
     *            the name of the organization
     * @param instanceName
     *            the service configuration instance name
     * @return the organization configuration for the given organization
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceConfig getOrganizationConfig(String orgName,
            String instanceName) throws SMSException, SSOException {
        // Get ServiceConfigImpl
        ServiceConfigImpl sci = scm.getOrganizationConfig(token, orgName,
                instanceName);
        return ((sci == null) ? null : new ServiceConfig(this, sci));
    }

    /**
     * iPlanet-PUBLIC-METHOD Creates global configuration for the default
     * instance of the service given the configuration attributes.
     * 
     * @param attrs
     *            map of attribute values.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceConfig createGlobalConfig(Map attrs) throws SMSException,
            SSOException {
        ServiceSchemaImpl ss = ssm.getSchema(SchemaType.GLOBAL);
        if (ss == null) {
            String[] args = { serviceName };
            throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    "sms-service-does-not-have-global-schema", args));
        }
        // Check base nodes for global attributes
        String orgDN = scm.constructServiceConfigDN(SMSUtils.DEFAULT,
                CreateServiceConfig.GLOBAL_CONFIG_NODE, null);

        // Create the sub config entry
        try {
            CreateServiceConfig.createSubConfigEntry(token, orgDN, ss, null,
                    null, attrs, SMSEntry.baseDN);
        } catch (ServiceAlreadyExistsException slee) {
            // Ignore the exception
        }
        return (getGlobalConfig(null));
    }

    /**
     * iPlanet-PUBLIC-METHOD Creates organization configuration for the default
     * instance of the service given configuration attributes.
     * 
     * @param orgName
     *            name of organization.
     * @param attrs
     *            map of attribute values.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public ServiceConfig createOrganizationConfig(String orgName, Map attrs)
            throws SMSException, SSOException {
        ServiceSchemaImpl ss = ssm.getSchema(SchemaType.ORGANIZATION);
        if (ss == null) {
            String[] args = { serviceName };
            throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    "sms-service-does-not-have-org-schema", args));
        }
        // Check base nodes for org
        String orgdn = DNMapper.orgNameToDN(orgName);
        CreateServiceConfig.checkBaseNodesForOrg(token, orgdn, serviceName,
                version);
        String orgDN = scm.constructServiceConfigDN(SMSUtils.DEFAULT,
                CreateServiceConfig.ORG_CONFIG_NODE, orgdn);

        // Create the sub config entry
        try {
            CachedSMSEntry cEntry = CachedSMSEntry.getInstance(token, orgDN,
                    null);
            if (cEntry.isNewEntry()) {
                CreateServiceConfig.createSubConfigEntry(token, orgDN, ss,
                        null, null, attrs, orgName);
                // if in co-existence mode, need to register the service
                // for AMOrganization
                if (ServiceManager.isCoexistenceMode()) {
                    String smsDN = DNMapper.orgNameToDN(orgName);
                    OrgConfigViaAMSDK amsdk = new OrgConfigViaAMSDK(token,
                            DNMapper.realmNameToAMSDKName(smsDN), smsDN);
                    amsdk.assignService(serviceName);
                }
            } else if (attrs != null && !attrs.isEmpty()) {
                // Set the attributes for the service config
                ServiceConfig sc = getOrganizationConfig(orgName, null);
                sc.setAttributes(attrs);
            }
        } catch (ServiceAlreadyExistsException slee) {
            // Ignore the exception
        }

        return (getOrganizationConfig(orgName, null));
    }

    /**
     * iPlanet-PUBLIC-METHOD Adds instances, global and organization
     * configurations
     * 
     * @param in
     *            input stream of configuration data.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public void addConfiguration(InputStream in) throws SMSException,
            SSOException {
        ServiceManager sm = new ServiceManager(token);
        // Get the document and search for service name and version
        Document doc = SMSSchema.getXMLDocument(in);
        NodeList nodes = doc.getElementsByTagName(SMSUtils.SERVICE);
        for (int i = 0; (nodes != null) && (i < nodes.getLength()); i++) {
            Node serviceNode = nodes.item(i);
            String sName = XMLUtils.getNodeAttributeValue(serviceNode,
                    SMSUtils.NAME);
            String sVersion = XMLUtils.getNodeAttributeValue(serviceNode,
                    SMSUtils.VERSION);
            Node configNode;
            if (sName.equals(serviceName)
                    && (sVersion.equals(version))
                    && ((configNode = XMLUtils.getChildNode(serviceNode,
                            SMSUtils.CONFIGURATION)) != null)) {
                CreateServiceConfig.createService(sm, sName, sVersion,
                        configNode);
            }
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Deletes the global configuration data for the given
     * group name. If group name is <code>null</code>, it used the default
     * group name.
     * 
     * @param groupName
     *            name of group.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public void removeGlobalConfiguration(String groupName)
            throws SMSException, SSOException {
        if ((groupName == null) || groupName.length() == 0) {
            groupName = SMSUtils.DEFAULT;
        }
        // Construct the sub-config dn
        String gdn = scm.constructServiceConfigDN(groupName,
                CreateServiceConfig.GLOBAL_CONFIG_NODE, null);
        // Delete the entry
        CachedSMSEntry cEntry = CachedSMSEntry.getInstance(token, gdn, null);
        SMSEntry entry = cEntry.getClonedSMSEntry();
        entry.delete(token);
        cEntry.refresh(entry);
    }

    /**
     * iPlanet-PUBLIC-METHOD Deletes the organization configuration data for the
     * given organization. It removes all the groups within the organization.
     * 
     * @param orgName
     *            name of organization.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public void deleteOrganizationConfig(String orgName) throws SMSException,
            SSOException {
        removeOrganizationConfiguration(orgName, SMSUtils.DEFAULT);
    }

    /**
     * iPlanet-PUBLIC-METHOD Deletes the organization's group configuration
     * data.
     * 
     * @param orgName
     *            name of organization.
     * @param groupName
     *            name of group.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public void removeOrganizationConfiguration(String orgName,
            String groupName) throws SMSException, SSOException {
        removeOrganizationConfiguration(orgName, groupName, true);
    }

    /**
     * Deletes the organization's group configuration data.
     * 
     * @param orgName
     *            name of organization.
     * @param groupName
     *            name of group.
     * @param checkLegacyMode
     *            boolean to check if legacy or realm passed by amsdk as false.
     * @throws SMSException
     *             if an error has occurred while performing the operation
     * @throws SSOException
     *             if the user's single sign on token is invalid or expired
     */
    public void removeOrganizationConfiguration(String orgName,
            String groupName, boolean checkLegacyMode) throws SMSException,
            SSOException {
        if ((groupName == null) || groupName.length() == 0) {
            groupName = SMSUtils.DEFAULT;
        }
        // Construct the sub-config dn
        String orgdn = DNMapper.orgNameToDN(orgName);
        String odn = scm.constructServiceConfigDN(groupName,
                CreateServiceConfig.ORG_CONFIG_NODE, orgdn);
        // Delete the entry from the REALM DIT
        CachedSMSEntry cEntry = CachedSMSEntry.getInstance(token, odn, null);
        if (cEntry.isNewEntry()) {
            return;
        }
        // if in legacy/co-existence mode, need to unregister the service
        // from AMOrganization
        if (checkLegacyMode && ServiceManager.isCoexistenceMode()
                && groupName.equalsIgnoreCase(SMSUtils.DEFAULT)) {
            OrgConfigViaAMSDK amsdk = new OrgConfigViaAMSDK(token, DNMapper
                    .realmNameToAMSDKName(orgdn), orgdn);
            amsdk.unassignService(serviceName);
        }
        // Now delete the entry.
        if (!cEntry.isNewEntry()) {
            SMSEntry entry = cEntry.getClonedSMSEntry();
            entry.delete(token);
            cEntry.refresh(entry);
        }
    }

    /**
     * Returns a set of plugins configured for the given plugin interface and
     * plugin schema in a organization
     */
    public Set getPluginConfigNames(String pluginSchemaName,
            String interfaceName, String orgName) throws SMSException,
            SSOException {
        StringBuffer sb = new StringBuffer(100);
        sb.append("ou=").append(pluginSchemaName).append(",ou=").append(
                interfaceName).append(",").append(
                CreateServiceConfig.PLUGIN_CONFIG_NODE).append("ou=").append(
                version).append(",").append("ou=").append(serviceName).append(
                ",").append(SMSEntry.SERVICES_RDN).append(",").append(
                DNMapper.orgNameToDN(orgName));
        // Need to check if the user permission to read plugin names
        CachedSMSEntry.getInstance(token, sb.toString(), null);
        // Get the CachedSubEntries and return sub-entries
        CachedSubEntries cse = CachedSubEntries.getInstance(token, sb
                .toString());
        return (cse.getSubEntries(token));
    }

    /**
     * Returns the plugin configuration parameters for the service
     */
    public PluginConfig getPluginConfig(String name, String pluginSchemaName,
            String interfaceName, String orgName) throws SMSException,
            SSOException {
        PluginConfigImpl pci = scm.getPluginConfig(token, name,
                pluginSchemaName, interfaceName, orgName);
        return (new PluginConfig(name, this, pci));
    }

    /**
     * Removes the plugin configuration for the service
     */
    public void removePluginConfig(String name, String pluginSchemaName,
            String interfaceName, String orgName) throws SMSException,
            SSOException {
        PluginConfig pci = getPluginConfig(name, pluginSchemaName,
                interfaceName, orgName);
        if (pci != null) {
            pci.delete();
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Registers for changes to service's configuration.
     * The object will be called when configuration for this service and version
     * is changed.
     * 
     * @param listener
     *            callback object that will be invoked when schema changes.
     * @return an ID of the registered listener.
     */
    public String addListener(ServiceListener listener) {
        return (scm.addListener(listener));
    }

    /**
     * iPlanet-PUBLIC-METHOD Removes the listener from the service for the given
     * listener ID. The ID was issued when the listener was registered.
     * 
     * @param listenerID
     *            the listener ID issued when the listener was registered
     */
    public void removeListener(String listenerID) {
        scm.removeListener(listenerID);
    }

    /**
     * iPlanet-PUBLIC-METHOD Compares this object with the given object.
     * 
     * @param o
     *            object for comparison.
     * @return true if objects are equals.
     */
    public boolean equals(Object o) {
        if (o instanceof ServiceConfigManager) {
            ServiceConfigManager scm = (ServiceConfigManager) o;
            if (serviceName.equals(scm.serviceName)
                    && version.equals(scm.version)) {
                return (true);
            }
        }
        return (false);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns String representation of the service's
     * configuration data, along with instances and groups.
     * 
     * @return String representation of the service's configuration data, along
     *         with instances and groups.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nService Config Manager: ").append(serviceName).append(
                "\n\tVersion: ").append(version);

        // Print Instances with global and base DN's org attributes
        try {
            Iterator instances = getInstanceNames().iterator();
            while (instances.hasNext()) {
                String instanceName = (String) instances.next();
                sb.append(getInstance(instanceName));
                ServiceConfig config = null;
                try {
                    config = getGlobalConfig(instanceName);
                    if (config != null) {
                        sb.append("\nGlobal Configuation:\n").append(config);
                    }
                } catch (SMSException e) {
                    // Ignore the exception
                }
                try {
                    config = getOrganizationConfig(null, instanceName);
                    if (config != null) {
                        sb.append("Org Configuation:\n").append(config);
                    }
                } catch (SMSException e) {
                    // Ignore the exception
                }
            }
            sb.append("\n");
        } catch (SMSException smse) {
            sb.append(smse.getMessage());
        } catch (SSOException ssoe) {
            sb.append(ssoe.getMessage());
        }
        return (sb.toString());
    }

    // ---------------------------------------------------------
    // Protected method
    // ---------------------------------------------------------
    SSOToken getSSOToken() {
        return (token);
    }

    boolean containsGroup(String groupName) throws SMSException, SSOException {
        return (scm.containsGroup(token, groupName));
    }
}
