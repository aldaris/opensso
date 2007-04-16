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
 * $Id: ServiceSchemaManagerImpl.java,v 1.4 2007-04-16 07:14:14 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.sm;

import com.iplanet.services.util.AMEncryption;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.shared.debug.Debug;
import com.sun.identity.shared.xml.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * The class <code>ServiceSchemaManagerImpl</code> provides the internal
 * implemation for <code>ServiceSchemaManager</code>. There should be only
 * one instance of <code>ServiceSchemaManagerImpl
 * </code> per service name and
 * version. This class implements all the "read" methods and would receive
 * notification when schema changes.
 */
class ServiceSchemaManagerImpl {
    // Instance variables
    private String serviceName;

    private String version;

    private int instanceID;

    private String i18nKey;

    private String i18nFileName;

    private String i18nJarURL;

    private String serviceHierarchy;

    private String viewBeanURL;

    private int revisionNumber;

    // Pointer to Service Manager
    private ServiceManager sm;

    // Pointer to Schema's SMS entry. This contains the schema attributes
    private CachedSMSEntry smsEntry;

    // Pointer to schema changes listeners
    private HashMap listenerObjects;

    // XML schema in String and Node formats (both can be null).
    private String xmlSchema;

    private Document document;

    private Node schemaRoot;

    // service sub-schemas & plugin interfaces
    private Map subSchemas;

    private Map pluginInterfaces;

    // Private constructor, an instance can obtained only via getInstance
    private ServiceSchemaManagerImpl(SSOToken t, String serviceName,
            String version) throws SMSException, SSOException {
        // Check sevice name and version
        ServiceManager.checkServiceNameAndVersion(t, serviceName, version);

        // Copy instance variables
        this.serviceName = serviceName;
        this.version = version;
        instanceID = SMSUtils.getInstanceID();
        listenerObjects = new HashMap();
        subSchemas = new HashMap();
        pluginInterfaces = new HashMap();

        // Construct the Schema's SMS entry
        smsEntry = CachedSMSEntry.getInstance(t, this, serviceName, version);

        // Read the schema
        update();
    }

    /**
     * Returns the Service name
     */
    public String getName() {
        return (serviceName);
    }

    /**
     * Returns the Service's version
     */
    public String getVersion() {
        return (version);
    }

    /**
     * Retuns I18N key for the service
     */
    String getI18NKey() {
        return (i18nKey);
    }

    /**
     * Sets I18N key for the service
     */
    void setI18NKey(String fn) throws SMSException, SSOException {
        i18nKey = fn;
        Node schemaNode = XMLUtils.getRootNode(getDocument(), SMSUtils.SCHEMA);
        ((Element) schemaNode).setAttribute(SMSUtils.I18N_KEY, fn);
    }

    /**
     * Retuns revision number for the service
     */
    int getRevisionNumber() {
        return (revisionNumber);
    }

    /**
     * Sets the revision number for the service
     */
    void setRevisionNumber(int revisionNumber) throws SMSException,
            SSOException {
        this.revisionNumber = revisionNumber;
        Node schemaNode = XMLUtils.getRootNode(getDocument(), SMSUtils.SCHEMA);
        ((Element) schemaNode).setAttribute(SMSUtils.REVISION_NUMBER, Integer
                .toString(revisionNumber));
    }

    /**
     * Returns the I18N properties file name for the service.
     */
    String getI18NFileName() {
        return (i18nFileName);
    }

    void setI18NFileName(String fn) throws SMSException, SSOException {
        i18nFileName = fn;
        Node schemaNode = XMLUtils.getRootNode(getDocument(), SMSUtils.SCHEMA);
        ((Element) schemaNode).setAttribute(SMSUtils.PROPERTIES_FILENAME, fn);
    }

    /**
     * Returns the URL of the JAR file that contains the I18N properties file.
     */
    String getI18NJarURL() {
        return (i18nJarURL);
    }

    void setI18NJarURL(String url) throws SMSException, SSOException {
        i18nJarURL = url;
        Node schemaNode = XMLUtils.getRootNode(getDocument(), SMSUtils.SCHEMA);
        ((Element) schemaNode).setAttribute(SMSUtils.RESOURCE_BUNDLE_URL, url);
    }

    /**
     * Returns the service's hiearchy
     */
    String getServiceHierarchy() {
        return (serviceHierarchy);
    }

    void setServiceHierarchy(String newhierarchy) throws SMSException,
            SSOException {
        serviceHierarchy = newhierarchy;
        Node schemaNode = XMLUtils.getRootNode(getDocument(), SMSUtils.SCHEMA);
        ((Element) schemaNode).setAttribute(SMSUtils.SERVICE_HIERARCHY,
                newhierarchy);
    }

    /**
     * Returns URL of the view bean for the service
     */
    String getPropertiesViewBeanURL() {
        return (viewBeanURL);
    }

    void setPropertiesViewBeanURL(String newhierarchy) throws SMSException,
            SSOException {
        viewBeanURL = newhierarchy;
        Node schemaNode = XMLUtils.getRootNode(getDocument(), SMSUtils.SCHEMA);
        ((Element) schemaNode).setAttribute(SMSUtils.PROPERTIES_VIEW_BEAN_URL,
                newhierarchy);
    }

    /**
     * Returns the SchemaTypes available with this service
     */
    Set getSchemaTypes() throws SMSException {
        return (new HashSet(subSchemas.keySet()));
    }

    ServiceSchemaImpl getSchema(SchemaType type) {
        ServiceSchemaImpl answer = (ServiceSchemaImpl) subSchemas.get(type);
        return (answer);
    }

    /**
     * Returns the service schema in XML for this service.
     */
    InputStream getSchema() {
        return (new ByteArrayInputStream(xmlSchema.getBytes()));
    }

    CachedSMSEntry getCachedSMSEntry() {
        return (smsEntry);
    }

    public boolean equals(Object o) {
        if (o instanceof ServiceSchemaManagerImpl) {
            ServiceSchemaManagerImpl ssm = (ServiceSchemaManagerImpl) o;
            if (serviceName.equals(ssm.serviceName)
                    && version.equals(ssm.version)
                    && (instanceID == ssm.instanceID)) {
                return (true);
            }
        }
        return (false);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append("\nService Schema Manager: ").append(serviceName).append(
                "\n\tVersion: ").append(version);
        sb.append("\nI18n file name: " + getI18NFileName());
        sb.append("\nI18n Jar URL: " + getI18NJarURL());
        sb.append("\nService hierarchy: " + getServiceHierarchy());
        sb.append("\nProperty View Bean: " + getPropertiesViewBeanURL());

        ServiceSchemaImpl ss;
        if ((ss = getSchema(SchemaType.GLOBAL)) != null) {
            sb.append("\nGlobal Schema:\n").append(ss.toString());
        }
        if ((ss = getSchema(SchemaType.ORGANIZATION)) != null) {
            sb.append("Organization Schema:\n").append(ss.toString());
        }
        if ((ss = getSchema(SchemaType.DYNAMIC)) != null) {
            sb.append("Dynamic Schema:\n").append(ss.toString());
        }
        if ((ss = getSchema(SchemaType.USER)) != null) {
            sb.append("User Schema:\n").append(ss.toString());
        }
        if ((ss = getSchema(SchemaType.POLICY)) != null) {
            sb.append("Policy Schema:\n").append(ss.toString());
        }
        return (sb.toString());
    }

    /**
     * Register for changes to service's schema. The object will be called when
     * schema for this service and version is changed.
     */
    synchronized String addListener(ServiceListener listener) {
        String id = SMSUtils.getUniqueID();
        listenerObjects.put(id, listener);
        return (id);
    }

    /**
     * Unregisters the listener from the service for the given listener ID. The
     * ID was issued when the listener was registered.
     */
    synchronized void removeListener(String listenerID) {
        listenerObjects.remove(listenerID);
    }

    // -----------------------------------------------------------
    // Plugin Methods
    // -----------------------------------------------------------
    Set getPluginInterfaceNames() {
        return (new HashSet(pluginInterfaces.keySet()));
    }

    PluginInterface getPluginInterface(String piName) {
        return ((PluginInterface) pluginInterfaces.get(piName));
    }

    // -----------------------------------------------------------
    // Internal protected methods
    // -----------------------------------------------------------
    void setServiceManager(ServiceManager sm) {
        this.sm = sm;
    }

    Document getDocument() {
        return (document);
    }

    Document getDocumentCopy() throws SMSException {
        String schema = xmlSchema;
        if ((schema == null) || (schema.length() == 0)) {
            schema = SMSSchema.getDummyXML(serviceName, version);
        }
        return (SMSSchema.getXMLDocument(schema, false));
    }

    protected void finalize() throws Throwable {
        smsEntry.removeServiceListener(this);
    }

    synchronized void updateAndNotifyListeners() {
        update();
        HashMap lo = (HashMap) listenerObjects.clone();
        Iterator lObjects = lo.values().iterator();
        while (lObjects.hasNext()) {
            ServiceListener listener = (ServiceListener) lObjects.next();
            listener.schemaChanged(serviceName, version);
        }
    }

    synchronized void update() {
        xmlSchema = smsEntry.getXMLSchema();
        if (xmlSchema == null) {
            // This could mean the service schema has been deleted
            if (debug.warningEnabled()) {
                debug.warning("ServiceSchemaManagerImpl:: schema is null "
                        + serviceName + "(" + version + ")");
            }
            return;
        }

        // Construct the XML document from the schema
        try {
            document = SMSSchema.getXMLDocument(SMSSchema
                    .getServiceSchemaInputStream(xmlSchema), false);
            schemaRoot = XMLUtils.getRootNode(document, SMSUtils.SCHEMA);
        } catch (Exception e) {
            // This should not happpen
            debug.error("ServiceSchemaManagerImpl: XML parser error: "
                    + serviceName + "(" + version + ")", e);
            return;
        }

        if (schemaRoot == null) {
            debug.warning("ServiceSchemaManagerImpl: " + serviceName
                    + "no schema found");
            return;
        }

        // Update instance variables
        i18nKey = XMLUtils.getNodeAttributeValue(schemaRoot, SMSUtils.I18N_KEY);
        i18nFileName = XMLUtils.getNodeAttributeValue(schemaRoot,
                SMSUtils.PROPERTIES_FILENAME);
        i18nJarURL = XMLUtils.getNodeAttributeValue(schemaRoot,
                SMSUtils.RESOURCE_BUNDLE_URL);
        serviceHierarchy = XMLUtils.getNodeAttributeValue(schemaRoot,
                SMSUtils.SERVICE_HIERARCHY);
        viewBeanURL = XMLUtils.getNodeAttributeValue(schemaRoot,
                SMSUtils.PROPERTIES_VIEW_BEAN_URL);
        String revNum = XMLUtils.getNodeAttributeValue(schemaRoot,
                SMSUtils.REVISION_NUMBER);
        try {
            if (revNum != null) {
                revisionNumber = Integer.parseInt(revNum);
            } else {
                revisionNumber = DEFAULT_REVISION;
            }
        } catch (Exception e) {
            // could be no revision number or number format exception
            if (debug.warningEnabled()) {
                debug.warning("ServiceSchemaManagerImpl ==> " + serviceName
                        + ": Invalid revision revision number: " + revNum, e);
            }
            revisionNumber = REVISION_ERROR;
        }

        // Update sub-schema caches, if any
        updateSchema(SchemaType.GLOBAL, SMSUtils.GLOBAL_SCHEMA);
        updateSchema(SchemaType.ORGANIZATION, SMSUtils.ORG_SCHEMA);
        updateSchema(SchemaType.DYNAMIC, SMSUtils.DYNAMIC_SCHEMA);
        updateSchema(SchemaType.USER, SMSUtils.USER_SCHEMA);
        updateSchema(SchemaType.POLICY, SMSUtils.POLICY_SCHEMA);
        updateSchema(SchemaType.GROUP, SMSUtils.GROUP_SCHEMA);
        updateSchema(SchemaType.DOMAIN, SMSUtils.DOMAIN_SCHEMA);
        updateGenericSchema(SMSUtils.GENERIC_SCHEMA);

        // Update plugin intefaces, if any
        Iterator pins = XMLUtils.getChildNodes(schemaRoot,
                SMSUtils.PLUGIN_INTERFACE).iterator();
        while (pins.hasNext()) {
            PluginInterface pi = new PluginInterface(this, (Node) pins.next());
            pluginInterfaces.put(pi.getName(), pi);
        }
    }

    void updateGenericSchema(String schemaName) {
        for (Iterator nodes = XMLUtils.getChildNodes(schemaRoot, schemaName)
                .iterator(); nodes.hasNext();) {
            Node childNode = (Node) nodes.next();
            String stype = XMLUtils.getNodeAttributeValue(childNode,
                    SMSUtils.ATTRIBUTE_TYPE);
            if (stype == null) {
                continue;
            }
            SchemaType type = new SchemaType(stype.toUpperCase());
            ServiceSchemaImpl ss = (ServiceSchemaImpl) subSchemas.get(type);
            if (ss != null) {
                ss.update(childNode);
            } else {
                subSchemas.put(type, new ServiceSchemaImpl(this, childNode));
            }
        }
    }

    void updateSchema(SchemaType type, String schemaName) {
        Node childNode = XMLUtils.getChildNode(schemaRoot, schemaName);
        if (childNode == null) {
            subSchemas.remove(type);
        } else {
            ServiceSchemaImpl ss = (ServiceSchemaImpl) subSchemas.get(type);
            if (ss != null) {
                ss.update(childNode);
            } else {
                subSchemas.put(type, new ServiceSchemaImpl(this, childNode));
            }
        }
    }

    // Static method to get an instance of this class
    static ServiceSchemaManagerImpl getInstance(SSOToken t, String serviceName,
            String version) throws SMSException, SSOException {
        String cacheName = ServiceManager.getCacheIndex(serviceName, version);
        ServiceSchemaManagerImpl ssmi = 
            (ServiceSchemaManagerImpl) schemaManagers.get(cacheName);
        if (ssmi != null && !ssmi.smsEntry.isValid()) {
            // CachedSMSEntry is not valid. Re-create this object
            ssmi = null;
        }
        if (ssmi != null) {
            // Check if the entry needs to be updated
            if (!SMSEntry.cacheSMSEntries) {
                // Read the entry, since it should not be cached
                ssmi.update();
            }
            return (ssmi);
        }

        synchronized (schemaMgrMutex) {
            // Check again, since it is now in synchronized block
            ssmi = (ServiceSchemaManagerImpl) schemaManagers.get(cacheName);
            if (ssmi == null || !ssmi.smsEntry.isValid()) {
                // Instantiate and add to cache
                ssmi = new ServiceSchemaManagerImpl(t, serviceName, version);
                Map sudoSchemaManagers = new HashMap(schemaManagers);
                sudoSchemaManagers.put(cacheName, ssmi);
                schemaManagers = sudoSchemaManagers;
            }
        }
        return (ssmi);
    }

    // Clears the cache
    static void clearCache() {
        schemaManagers = new HashMap();
    }

    // Debug & I18n
    private static Debug debug = SMSEntry.debug;

    // Pointers to ServiceSchemaManager instances
    private static Map schemaManagers = new HashMap();

    private static final String schemaMgrMutex = "SchemaManagerMutext";

    private static final int DEFAULT_REVISION = 10;

    private static final int REVISION_ERROR = -1;
    
    public String toXML(AMEncryption encryptObj)
        throws SMSException {
        Document doc = getDocumentCopy();
        ServiceManager.checkAndEncryptPasswordSyntax(doc, false, encryptObj);
        return SMSSchema.nodeToString(
            XMLUtils.getRootNode(doc, SMSUtils.SERVICE));
    }
}
