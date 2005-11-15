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
 * $Id: ServiceSchemaManager.java,v 1.2 2005-11-15 04:10:36 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.iplanet.am.util.Debug;
import com.iplanet.am.util.XMLUtils;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.ums.IUMSConstants;

/**
 * The class <code>ServiceSchemaManager</code> provides interfaces to manage
 * the service's schema. It provides access to <code>ServiceSchema</code>,
 * which represents a single "schema" in the service.
 */
public class ServiceSchemaManager {

    private SSOToken token;

    private String serviceName;

    private String version;

    private ServiceSchemaManagerImpl ssm;

    private static Debug debug = SMSEntry.debug;

    /**
     * Constructor for backward compatibility. Chooses on of the versions.
     * 
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     * @throws SSOException
     *             if the single sign on token is invalid or expired
     */
    public ServiceSchemaManager(String serviceName, SSOToken token)
            throws SMSException, SSOException {
        this(token, serviceName, ServiceManager.serviceDefaultVersion(token,
                serviceName));
    }

    /**
     * iPlanet-PUBLIC-CONSTRUCTOR Creates an instance of
     * <code>ServiceSchemaManager</code> for the given service and version
     * pair. It requires an user identity, that will used to perform operations
     * with. It is assumed that the application calling this constructor should
     * authenticate the user.
     * 
     * @param token
     *            single sign on token of the user identity on whose behalf the
     *            operations are performed.
     * @param serviceName
     *            the name of the service.
     * @param version
     *            the version of the service.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     * @throws SSOException
     *             if the single sign on token is invalid or expired
     */
    public ServiceSchemaManager(SSOToken token, String serviceName,
            String version) throws SMSException, SSOException {
        if (token == null || serviceName == null || version == null) {
            throw new IllegalArgumentException(SMSEntry.bundle
                    .getString(IUMSConstants.SMS_INVALID_PARAMETERS));
        }
        SMSEntry.validateToken(token);
        this.token = token;
        this.serviceName = serviceName;
        this.version = version;
        ssm = ServiceSchemaManagerImpl.getInstance(token, serviceName, version);
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
     * iPlanet-PUBLIC-METHOD Returns the version of the service.
     * 
     * @return the version of the service
     */
    public String getVersion() {
        return (version);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the I18N properties file name for the
     * service.
     * 
     * @return the I18N properties file name for the service
     */
    public String getI18NFileName() {
        return (ssm.getI18NFileName());
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the I18N properties file name for the service
     * 
     * @param url
     *            properties file name
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     * @throws SSOException
     *             if the single sign on token is invalid or expired
     */
    public void setI18NFileName(String url) throws SMSException, SSOException {
        SMSEntry.validateToken(token);
        String tmpS = ssm.getI18NFileName();
        ssm.setI18NFileName(url);
        try {
            replaceSchema(ssm.getDocument());
        } catch (SMSException se) {
            ssm.setI18NFileName(tmpS);
            throw se;
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the URL of the JAR file that contains the
     * I18N properties file. The method could return null, in which case the
     * properties file should be in <code>CLASSPATH</code>.
     * 
     * @return the URL of the JAR file containing the <code>I18N</code>
     *         properties file.
     */
    public String getI18NJarURL() {
        return (ssm.getI18NJarURL());
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the URL of the JAR file that contains the I18N
     * properties
     * 
     * @param url
     *            URL
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     * @throws SSOException
     *             if the single sign on token is invalid or expired
     */

    public void setI18NJarURL(String url) throws SMSException, SSOException {
        SMSEntry.validateToken(token);
        String tmpS = ssm.getI18NJarURL();
        ssm.setI18NJarURL(url);
        try {
            replaceSchema(ssm.getDocument());
        } catch (SMSException se) {
            ssm.setI18NJarURL(tmpS);
            throw se;
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the service's hierarchy.
     * 
     * @return service hierarchy in slash format.
     */
    public String getServiceHierarchy() {
        return (ssm.getServiceHierarchy());
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the service's hierarchy
     * 
     * @param newhierarchy
     *            service hierarchy
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     * @throws SSOException
     *             if the single sign on token is invalid or expired
     */
    public void setServiceHierarchy(String newhierarchy) throws SMSException,
            SSOException {
        SMSEntry.validateToken(token);
        String tmpS = getServiceHierarchy();
        ssm.setServiceHierarchy(newhierarchy);
        try {
            replaceSchema(ssm.getDocument());
        } catch (SMSException e) {
            ssm.setServiceHierarchy(tmpS);
            throw e;
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns i18nKey of the schema.
     * 
     * @return i18nKey of the schema.
     */
    public String getI18NKey() {
        return (ssm.getI18NKey());
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the i18nKey of the schema.
     * 
     * @param i18nKey
     *            <code>i18nKey</code> of the schema.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation.
     * @throws SSOException
     *             if the single sign on token is invalid or expired.
     */
    public void setI18NKey(String i18nKey) throws SMSException, SSOException {
        SMSEntry.validateToken(token);
        String tmp = ssm.getI18NKey();
        ssm.setI18NKey(i18nKey);

        try {
            replaceSchema(ssm.getDocument());
        } catch (SMSException e) {
            ssm.setI18NKey(tmp);
            throw e;
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns URL of the view bean for the service
     * 
     * @return URL for view bean
     */
    public String getPropertiesViewBeanURL() {
        return (ssm.getPropertiesViewBeanURL());
    }

    /**
     * iPlanet-PUBLIC-METHOD Sets the URL of the view bean for the service.
     * 
     * @param url
     *            of the view bean for the service.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation.
     * @throws SSOException
     *             if the single sign on token is invalid or expired.
     */
    public void setPropertiesViewBeanURL(String url) throws SMSException,
            SSOException {
        SMSEntry.validateToken(token);
        String tmpS = ssm.getPropertiesViewBeanURL();
        ssm.setPropertiesViewBeanURL(url);
        try {
            replaceSchema(ssm.getDocument());
        } catch (SMSException e) {
            ssm.setPropertiesViewBeanURL(tmpS);
            throw e;
        }
    }

    /**
     * iPlanet_PUBLIC-METHOD Returns the revision number of the service schema.
     * 
     * @return the revision number of the service schema
     */
    public int getRevisionNumber() {
        return (ssm.getRevisionNumber());
    }

    /**
     * iPlanet_PUBLIC-METHOD Sets the revision number for the service schema.
     * 
     * @param revisionNumber
     *            revision number of the service schema.
     * @throws SMSException
     *             if there is a problem setting the value in the data store.
     * @throws SSOException
     *             If the user has an invalid SSO token.
     */
    public void setRevisionNumber(int revisionNumber) throws SMSException,
            SSOException {
        SMSEntry.validateToken(token);
        int tmpS = ssm.getRevisionNumber();
        ssm.setRevisionNumber(revisionNumber);
        try {
            replaceSchema(ssm.getDocument());
        } catch (SMSException e) {
            ssm.setRevisionNumber(tmpS);
            throw (e);
        }
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the schema types available with this
     * service.
     * 
     * @return set of <code>SchemaTypes</code> in this service.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public Set getSchemaTypes() throws SMSException {
        SMSEntry.validateToken(token);
        return (ssm.getSchemaTypes());
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the configuration schema for the given
     * schema type
     * 
     * @param type
     *            schema type.
     * @return service schema.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getSchema(String type) throws SMSException {
        SchemaType t = null;
        if (type.equalsIgnoreCase("role")
                || type.equalsIgnoreCase("filteredrole")
                || type.equalsIgnoreCase("realm")) {
            t = SchemaType.DYNAMIC;
        } else if (type.equalsIgnoreCase("user")) {
            t = SchemaType.USER;
        } else {
            t = new SchemaType(type);
        }
        return (getSchema(t));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the configuration schema for the given
     * schema type
     * 
     * @param type
     *            schema type.
     * @return service schema.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getSchema(SchemaType type) throws SMSException {
        SMSEntry.validateToken(token);
        ServiceSchemaImpl ss = ssm.getSchema(type);
        if ((ss == null) && type.equals(SchemaType.USER)) {
            type = SchemaType.DYNAMIC;
            ss = ssm.getSchema(type);
        }
        if (ss != null) {
            return (new ServiceSchema(ss, "", type, this));
        }
        return (null);
    }

    /**
     * Returns the organization creation configuration schema if present; else
     * returns <code>null</code>
     * 
     * @return service schema.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getOrganizationCreationSchema() throws SMSException {
        SMSEntry.validateToken(token);
        ServiceSchemaImpl ss = ssm.getSchema(SchemaType.ORGANIZATION);
        if (ss != null) {
            ServiceSchemaImpl ssi = ss.getOrgAttrSchema();
            if (ssi != null) {
                return (new ServiceSchema(ssi, "", SchemaType.ORGANIZATION,
                        this, true));
            }
        }
        return (null);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the attribute schemas for the given schema
     * type excluding status and service identifier attributes.
     * 
     * @param type
     *            schema type.
     * @return service schema.
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public Set getServiceAttributeNames(SchemaType type) throws SMSException {
        SMSEntry.validateToken(token);
        ServiceSchema ss = getSchema(type);
        return (ss.getServiceAttributeNames());
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the global service configuration schema.
     * 
     * @return the global service configuration schema
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getGlobalSchema() throws SMSException {
        SMSEntry.validateToken(token);
        return (getSchema(SchemaType.GLOBAL));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the organization service configuration
     * schema.
     * 
     * @return the organization service configuration schema
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getOrganizationSchema() throws SMSException {
        SMSEntry.validateToken(token);
        return (getSchema(SchemaType.ORGANIZATION));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the dynamic service configuration schema.
     * 
     * @return the dynamic service configuration schema
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getDynamicSchema() throws SMSException {
        SMSEntry.validateToken(token);
        return (getSchema(SchemaType.DYNAMIC));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the user service configuration schema.
     * 
     * @return the user service configuration schema
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getUserSchema() throws SMSException {
        SMSEntry.validateToken(token);
        return (getSchema(SchemaType.USER));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the policy service configuration schema.
     * 
     * @return the policy service configuration schema
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public ServiceSchema getPolicySchema() throws SMSException {
        SMSEntry.validateToken(token);
        return (getSchema(SchemaType.POLICY));
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the service schema in XML for this service.
     * 
     * @return the service schema in XML for this service
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     */
    public InputStream getSchema() throws SMSException {
        SMSEntry.validateToken(token);
        return (ssm.getSchema());
    }

    /**
     * iPlanet-PUBLIC-METHOD Replaces the existing service schema with the given
     * schema defined by the XML input stream that follows the SMS DTD.
     * 
     * @param xmlServiceSchema
     *            the XML format of the service schema
     * @throws SMSException
     *             if an error occurred while trying to perform the operation
     * @throws SSOException
     *             if the single sign on token is invalid or expired
     * @throws IOException
     *             if an error occurred with the <code> InputStream </code>
     */
    public void replaceSchema(InputStream xmlServiceSchema)
            throws SSOException, SMSException, IOException {
        SMSEntry.validateToken(token);
        CachedSMSEntry smsEntry = ssm.getCachedSMSEntry();
        smsEntry.writeXMLSchema(token, xmlServiceSchema);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns true if the given object equals this
     * object.
     * 
     * @param o
     *            object for comparison.
     * @return true if the given object equals this object.
     */
    public boolean equals(Object o) {
        if (o instanceof ServiceSchemaManager) {
            ServiceSchemaManager ssm = (ServiceSchemaManager) o;
            if (serviceName.equals(ssm.serviceName)
                    && version.equals(ssm.version)) {
                return (true);
            }
        }
        return (false);
    }

    /**
     * iPlanet-PUBLIC-METHOD Returns the string representation of the Service
     * Schema.
     * 
     * @return the string representation of the Service Schema.
     */
    public String toString() {
        return (ssm.toString());
    }

    /**
     * iPlanet-PUBLIC-METHOD Registers for changes to service's schema. The
     * object will be called when schema for this service and version is
     * changed.
     * 
     * @param listener
     *            callback object that will be invoked when schema changes.
     * @return an ID of the registered listener.
     */
    public String addListener(ServiceListener listener) {
        return (ssm.addListener(listener));
    }

    /**
     * iPlanet-PUBLIC-METHOD Removes the listener from the service for the given
     * listener ID. The ID was issued when the listener was registered.
     * 
     * @param listenerID
     *            the listener ID issued when the listener was registered
     */
    public void removeListener(String listenerID) {
        ssm.removeListener(listenerID);
    }

    /**
     * Returns the last modified time stamp of this service schema. This method
     * is expensive because it does not cache the modified time stamp but goes
     * directly to the data store to obtain the value of this entry
     * 
     * @return The last modified time stamp as a string with the format of
     *         <code>yyyyMMddhhmmss</code>
     * @throws SMSException if there is an error trying to read from the
     *         datastore.
     * @throws SSOException if the single sign-on token of the user is invalid.
     */
    public String getLastModifiedTime() throws SMSException, SSOException {
        CachedSMSEntry ce = ssm.getCachedSMSEntry();
        SMSEntry e = ce.getSMSEntry();
        String vals[] = e.getAttributeValues(SMSEntry.ATTR_MODIFY_TIMESTAMP,
                true);
        String mTS = null;
        if (vals != null) {
            mTS = vals[0];
        }
        return mTS;
    }

    // ================= Plugin Interface Methods ========

    /**
     * Returns the names of the plugin interfaces used by the service
     * 
     * @return service's plugin interface names
     */
    public Set getPluginInterfaceNames() {
        return (ssm.getPluginInterfaceNames());
    }

    /**
     * Returns the <code>PluginInterface</code> object of the service for the
     * specified plugin interface name
     * 
     * @param pluginInterfaceName
     *            name of the plugin interface
     * @return plugin interface configured for the service; else
     *         <code>null</code>
     */
    public PluginInterface getPluginInterface(String pluginInterfaceName) {
        return (ssm.getPluginInterface(pluginInterfaceName));
    }

    /**
     * Adds a new plugin interface objct to service's schema.
     * 
     * @param interfaceName
     *            name for the plugin interface
     * @param interfaceClass
     *            fully qualified interface class name
     * @param i18nKey
     *            I18N key that will by used by UI to get messages to display
     *            the interface name
     */
    public void addPluginInterface(String interfaceName, String interfaceClass,
            String i18nKey) throws SMSException, SSOException {
        SMSEntry.validateToken(token);
        if ((interfaceName == null) || (interfaceClass == null)) {
            throw (new IllegalArgumentException());
        }
        StringBuffer sb = new StringBuffer(100);
        sb.append("<").append(SMSUtils.PLUGIN_INTERFACE).append(" ").append(
                SMSUtils.NAME).append("=\"").append(interfaceName)
                .append("\" ").append(SMSUtils.PLUGIN_INTERFACE_CLASS).append(
                        "=\"").append(interfaceClass).append("\"");
        if (i18nKey != null) {
            sb.append(" ").append(SMSUtils.I18N_KEY).append("=\"").append(
                    i18nKey).append("\"");
        }
        sb.append("></").append(SMSUtils.PLUGIN_INTERFACE).append(">");
        // Construct XML document
        Document pluginDoc = SMSSchema.getXMLDocument(sb.toString(), false);
        Node node = XMLUtils.getRootNode(pluginDoc, SMSUtils.PLUGIN_INTERFACE);

        // Added to XML document and write it
        Document schemaDoc = ssm.getDocumentCopy();
        Node pluginNode = schemaDoc.importNode(node, true);
        Node schemaNode = XMLUtils.getRootNode(schemaDoc, SMSUtils.SCHEMA);
        schemaNode.appendChild(pluginNode);
        replaceSchema(schemaDoc);
    }

    /**
     * Removes the plugin interface object from the service schema.
     * 
     * @param interfacename Name of the plugin class.
     */
    public void removePluginInterface(String interfacename)
            throws SMSException, SSOException {
        SMSEntry.validateToken(token);
        Document schemaDoc = ssm.getDocumentCopy();
        Node schemaNode = XMLUtils.getRootNode(schemaDoc, SMSUtils.SCHEMA);
        // Get the plugin interface node
        Node pluginNode = XMLUtils.getNamedChildNode(schemaNode,
                SMSUtils.PLUGIN_INTERFACE, SMSUtils.NAME, interfacename);
        if (pluginNode != null) {
            schemaNode.removeChild(pluginNode);
            replaceSchema(schemaDoc);
        }
    }

    // -----------------------------------------------------------
    // Plugin Schema
    // -----------------------------------------------------------
    /**
     * Returns the names of plugins configured for the plugin interface. If
     * organization is <code>null</code>, returns the plugins configured for
     * the "root" organization.
     */
    public Set getPluginSchemaNames(String interfaceName, String orgName)
            throws SMSException {
        SMSEntry.validateToken(token);
        // Construct the DN to get CachedSubEntries
        StringBuffer sb = new StringBuffer(100);
        sb.append("ou=").append(interfaceName).append(",").append(
                CreateServiceConfig.PLUGIN_CONFIG_NODE).append("ou=").append(
                version).append(",").append("ou=").append(serviceName).append(
                ",").append(SMSEntry.SERVICES_RDN).append(",").append(
                DNMapper.orgNameToDN(orgName));
        CachedSubEntries cse = CachedSubEntries.getInstance(token, sb
                .toString());
        try {
            return (cse.getSubEntries(token));
        } catch (SSOException s) {
            debug.error("ServiceSchemaManager: Unable to get "
                    + "Plugin Schema Names", s);
        }
        return (Collections.EMPTY_SET);
    }

    /**
     * Returns the PluginSchema object given the schema name and the interface
     * name for the specified organization. If organization is
     * <code>null</code>, returns the PluginSchema for the "root" organization.
     */
    public PluginSchema getPluginSchema(String pluginSchemaName,
            String interfaceName, String orgName) throws SMSException {
        SMSEntry.validateToken(token);
        return (new PluginSchema(token, serviceName, version, pluginSchemaName,
                interfaceName, orgName));
    }

    // -----------------------------------------------------------
    // Internal protected method
    // -----------------------------------------------------------
    SSOToken getSSOToken() {
        return (token);
    }

    protected Document getDocumentCopy() throws SMSException {
        return (ssm.getDocumentCopy());
    }

    protected synchronized void replaceSchema(Document document)
            throws SSOException, SMSException {
        CachedSMSEntry smsEntry = ssm.getCachedSMSEntry();
        SMSSchema smsSchema = new SMSSchema(document);
        smsEntry.writeXMLSchema(token, smsSchema.getSchema());
    }

    // -----------------------------------------------------------
    // Static method to create a new service schema
    // -----------------------------------------------------------
    static void createService(SSOToken token, SMSSchema smsSchema)
            throws SMSException, SSOException {
        // Service node
        SMSEntry smsEntry = new SMSEntry(token, ServiceManager
                .getServiceNameDN(smsSchema.getServiceName()));

        if (smsEntry.isNewEntry()) {
            // create this entry
            smsEntry.addAttribute(SMSEntry.ATTR_OBJECTCLASS, SMSEntry.OC_TOP);
            smsEntry.addAttribute(SMSEntry.ATTR_OBJECTCLASS,
                    SMSEntry.OC_SERVICE);
            smsEntry.save();
        }
        // Version node
        CachedSMSEntry cEntry = CachedSMSEntry.getInstance(token,
                ServiceManager.getServiceNameDN(smsSchema.getServiceName(),
                        smsSchema.getServiceVersion()), null);
        smsEntry = cEntry.getSMSEntry();
        String[] schema = new String[1];
        if ((smsEntry.getAttributeValues(SMSEntry.ATTR_SCHEMA) == null)
                || ((smsEntry.getAttributeValues(SMSEntry.ATTR_SCHEMA))[0]
                        .equalsIgnoreCase(SMSSchema.getDummyXML(smsSchema
                                .getServiceName(), smsSchema
                                .getServiceVersion())))) {
            schema[0] = smsSchema.getSchema();
            smsEntry.setAttribute(SMSEntry.ATTR_SCHEMA, schema);
        } else {
            // Throw service already exists exception
            Object[] args = { smsSchema.getServiceName(),
                    smsSchema.getServiceVersion() };
            throw (new SMSException(IUMSConstants.UMS_BUNDLE_NAME,
                    IUMSConstants.SMS_service_already_exists, args));
        }
        if (smsEntry.isNewEntry()) {
            // add object classes
            smsEntry.addAttribute(SMSEntry.ATTR_OBJECTCLASS, SMSEntry.OC_TOP);
            smsEntry.addAttribute(SMSEntry.ATTR_OBJECTCLASS,
                    SMSEntry.OC_SERVICE);
        }
        smsEntry.save(token);
        cEntry.refresh(smsEntry);
    }
}
