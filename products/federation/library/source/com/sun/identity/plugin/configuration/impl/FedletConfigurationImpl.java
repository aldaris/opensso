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
 * $Id: FedletConfigurationImpl.java,v 1.1 2008-03-26 04:30:24 qcheng Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.plugin.configuration.impl;

import com.sun.identity.shared.debug.Debug;
import com.sun.identity.plugin.configuration.ConfigurationException;
import com.sun.identity.plugin.configuration.ConfigurationInstance;
import com.sun.identity.plugin.configuration.ConfigurationListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <code>FedletConfigurationImpl</code> is the implementation for Fedlet
 * to retrieve configuration from flat files. 
 */
public class FedletConfigurationImpl implements ConfigurationInstance {

    private static String UTF_8 = "UTF-8";
    private static String EXTENDED_XML_SUFFIX = "-extended.xml";
    private static String COT_FILE_SUFFIX = ".cot";
    // fedlet home directory, this is the directory which contains metadata
    // and COT files
    private static String fedletHomeDir; 
    // property name to point to the fedlet home
    // if not defined, default to "$user_home/fedlet"
    private static String FEDLET_HOME_DIR = "com.sun.identity.fedlet.home"; 
    private String componentName = null;
    private static final String RESOURCE_BUNDLE = "fmConfigurationService";
    static Debug debug = Debug.getInstance("fedletConfiguration");;

    /**
     * Initializer.
     * @param componentName Name of the components, e.g. SAML1, SAML2, ID-FF
     * @param session FM Session object.
     * @exception ConfigurationException if could not initialize the instance.
     */
    public void init(String componentName, Object session) 
        throws ConfigurationException {

        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.init: component=" +
                componentName);
        }
        this.componentName = componentName;       
        fedletHomeDir = System.getProperty(FEDLET_HOME_DIR);
        if ((fedletHomeDir == null) || (fedletHomeDir.trim().length() == 0)) {
            fedletHomeDir = System.getProperty("user.home") +
                File.separator + "fedlet";
        }
        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.init: fedlet home=" + 
                fedletHomeDir);
        }
    }

    /**
     * Returns Configurations.
     * @param realm the name of organization at which the configuration resides.
     * @param configName configuration instance name. e.g. "/sp".
     *     The configName could be null or empty string, which means the default
     *     configuration for this components. 
     * @return Map of key/value pairs, key is the attribute name, value is
     *     a Set of attribute values or null if service configuration doesn't
     *     doesn't exist.
     * @exception ConfigurationException if an error occurred while getting
     *     service configuration.
     */
    public Map getConfiguration(String realm, String configName)
        throws ConfigurationException {

        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.getConfiguration: " +
                "componentName = " + componentName + ", realm = " + realm +
                ", configName = " + configName);
        }

        try {
            // only need to support SAML2/LIBCOT for now
            if ("SAML2".equals(componentName)) {
                return handleSAML2Metadata(configName);
            } else if ("LIBCOT".equals(componentName)) {
                return handleCOT(configName);
            } else {
                return null;
            }
        } catch (FileNotFoundException fnf) {
            debug.warning("FedletConfigurationImpl.getConfiguration:", fnf);
            // configuration not found
            return null;
        } catch (IOException ioe) {
            debug.error("FedletConfigurationImpl.getConfiguration:", ioe);
            String[] data = { componentName, realm };
            throw new ConfigurationException(RESOURCE_BUNDLE,
                "failedGetConfig", data);
        }
    }

    /**
     * Retrieves SAMLv2 standard and extended metadata from falt files.
     * Standard metadata stored in a file named <configName>.xml
     * Extended metadata stored in a file named <configName>-extended.xml
     */
    private Map handleSAML2Metadata(String configName) 
        throws FileNotFoundException, IOException {
        // standard metadata files ends with .xml
        String metaFile = fedletHomeDir + File.separator + encode(configName)
            + ".xml";
        String metaXML = openFile(metaFile);
        Map map = new HashMap();
        Set set = new HashSet();
        set.add(metaXML);
        map.put("sun-fm-saml2-metadata", set);
        // get extended metadata files
        String extFile = fedletHomeDir + File.separator + encode(configName)
            + EXTENDED_XML_SUFFIX; 
        String extXML = openFile(extFile); 
        set = new HashSet();
        set.add(extXML);
        map.put("sun-fm-saml2-entityconfig", set);
        return map;
    }

    /**
     * Returns COT attribute value pair. Key is the attribute name,
     * value is a Set of values for the attribute.
     * The COT is stored in a falt file named "<configName>.cot" which contains
     * list of properties, format like this :
     * attribute_name=value1,value2,value3...
     */
    private Map handleCOT(String configName) 
    throws FileNotFoundException, IOException {
        String cotFile = fedletHomeDir + File.separator + encode(configName)
            + COT_FILE_SUFFIX;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(cotFile);
            Properties props = new Properties();
            props.load(fis); 
            // convert each value string to a Set.
            Map attrMap = new HashMap();
            if (props != null) {
                Enumeration keys = props.propertyNames();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    String vals = props.getProperty(key);
                    if ((vals != null) && (vals.length() > 0)) {
                        attrMap.put(key, toValSet(key, vals));
                    }
                }
            }
            return attrMap;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ioe) {
                }
            } 
        }
    }

    /**
     * Converts a string of values from the attributes properties file 
     * to a Set, decoding special characters in each value.
     */
    protected Set toValSet(String attrName, String vals) {
        Set valset = new HashSet();
        char[] valchars = vals.toCharArray();
        int i, j;

        for (i = 0, j = 0; j < valchars.length; j++) {
            char c = valchars[j];
            if (c == ',') {
                if (i == j) {
                    i = j +1;
                } else { // separator found
                    String val = new String(valchars, i, j-i).trim();
                    if (val.length() > 0) {
                        val = decodeVal(val);
                    }
                    valset.add(val);
                    i = j +1;
                }
            }
        }
        if (j == valchars.length && i < j) {
            String val = new String(valchars, i, j-i).trim();
            if (val.length() > 0) {
                val = decodeVal(val);
            }
            valset.add(val);
        }
        return valset;
    }


    /** 
     * Decodes a value, %2C to comma and %25 to percent.
     */
    protected String decodeVal(String v) {
        char[] chars = v.toCharArray();
        StringBuffer sb = new StringBuffer(chars.length);
        int i = 0, lastIdx = 0;
        for (i = 0; i < chars.length; i++) {
            if (chars[i] == '%' && i+2 < chars.length && chars[i+1] == '2') {
                if (lastIdx != i) {
                    sb.append(chars, lastIdx, i-lastIdx);
                }
                if (chars[i+2] == 'C') {
                    sb.append(',');
                }
                else if (chars[i+2] == '5') {
                    sb.append('%');
                }
                else {
                    sb.append(chars, i, 3);
                }
                i += 2;
                lastIdx = i+1;
            }
        }
        if (lastIdx != i) {
            sb.append(chars, lastIdx, i-lastIdx);
        }
        return sb.toString();
    }

    /**
     * Returns the contains of a file as String.
     */
    private String openFile(String file) 
    throws FileNotFoundException, IOException  {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file)); 
            StringBuffer sb = new StringBuffer(5000);
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
            return sb.toString();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                }
            }
        }
    }

    /**
     * Sets Configurations.
     * @param realm the name of organization at which the configuration resides.
     * @param configName configuration instance name. e.g. "/sp"
     *     The configName could be null or empty string, which means the default
     *     configuration for this components.
     * @param avPairs Map of key/value pairs to be set in the service
     *     configuration, key is the attribute name, value is
     *     a Set of attribute values. 
     * @exception ConfigurationException if could not set service configuration
     *     or service configuration doesn't exist.
     */
    public void setConfiguration(String realm,
        String configName, Map avPairs)
        throws ConfigurationException {

        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.setConfiguration: " +
                "componentName = " + componentName + ", realm = " + realm +
                ", configName = " + configName + ", avPairs = " + avPairs);
        }

        String[] data = { componentName, realm };
        throw new ConfigurationException(RESOURCE_BUNDLE, 
            "failedSetConfig", data);
    }

    /**
     * Creates Configurations.
     * @param realm the name of organization at which the configuration resides.
     * @param configName service configuration name. e.g. "/sp"
     *     The configName could be null or empty string, which means the
     *     default configuration for this components.
     * @param avPairs Map of key/value pairs to be set in the service
     *     configuration, key is the attribute name, value is
     *     a Set of attribute values. 
     * @exception ConfigurationException if could not create service 
     *     configuration.
     */
    public void createConfiguration(String realm, String configName,
        Map avPairs)
        throws ConfigurationException {

        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.createConfiguration: " +
                "componentName = " + componentName + ", realm = " + realm +
                ", configName = " + configName + ", avPairs = " + avPairs);
        }

        String[] data = { componentName, realm };
        throw new ConfigurationException(RESOURCE_BUNDLE,
            "failedCreateConfig", data);
    }

    /**
     * Deletes Configuration.
     * @param realm the name of organization at which the configuration resides.
     * @param configName service configuration name. e.g. "/sp"
     *     The configName could be null or empty string, which means the default
     *     configuration for this components.
     * @param attributes A set of attributes to be deleted from the Service
     *     configuration. If the value is null or empty, deletes all service 
     *     configuration.
     * @exception ConfigurationException if could not delete service 
     *     configuration.
     */
    public void deleteConfiguration(String realm, 
        String configName, Set attributes)
        throws ConfigurationException {

        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.deleteConfiguration: " +
                "componentName = " + componentName + ", realm = " + realm +
                ", configName = " + configName + ", attributes = " +
                attributes);
        }

        String[] data = { componentName, realm };
        throw new ConfigurationException(RESOURCE_BUNDLE, 
            "failedDeleteConfig", data);
    }

    /**
     * Returns all service config name for this components.
     * @param realm the name of organization at which the configuration resides.
     * @return Set of service configuration names. Return null if there 
     *     is no service configuration for this component, return empty set
     *     if there is only default configuration instance.
     * @exception ConfigurationException if could not get all service 
     *     configuration names.
     */
    public Set getAllConfigurationNames(String realm) 
        throws ConfigurationException {

        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.getAllConfigurationNames"+
                ": realm = " + realm + ", componentName = " + componentName);
        }
        if ("SAML2".equals(componentName)) {
            return getSAML2Entities();
        } else if ("LIBCOT".equals(componentName)) {
            return getCOTNames();
        } else {
            return Collections.EMPTY_SET;
        }
    }

    private Set getSAML2Entities() throws ConfigurationException {
        File homeDir = new File(fedletHomeDir);
        String[] files = homeDir.list();
        Set retSet = new HashSet();
        if ((files != null) && (files.length != 0)) {
            for (int i = 0; i < files.length; i++) {
                String name = files[i];
                if (name.endsWith(EXTENDED_XML_SUFFIX)) {
                    retSet.add(decode(name.substring(0, 
                        name.length() - EXTENDED_XML_SUFFIX.length())));
                }
            }
        }
        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.getSAML2Entities"+
                " componentName = " + componentName + ", entities=" + retSet);
        }
        return retSet;
    } 

    private Set getCOTNames() {
        File homeDir = new File(fedletHomeDir);
        String[] files = homeDir.list();
        Set retSet = new HashSet();
        if ((files != null) && (files.length != 0)) {
            for (int i = 0; i < files.length; i++) {
                String name = files[i];
                if (name.endsWith(COT_FILE_SUFFIX)) {
                    retSet.add(decode(name.substring(0, 
                        name.length() - COT_FILE_SUFFIX.length())));
                }
            }
        }
        if (debug.messageEnabled()) {
            debug.message("FedletConfigurationImpl.getSAML2Entities"+
                " componentName = " + componentName + ", COTs=" + retSet);
        }
        return retSet;
    }

    /**
     * Registers for changes to the component's configuration. The object will
     * be called when configuration for this component is changed.
     * @return the registered id for this listener instance.
     * @exception ConfigurationException if could not register the listener.
     */
    public String addListener(ConfigurationListener listener)
        throws ConfigurationException {
        return "NO_OP";
    }

    /**
     * Unregisters the listener from the component for the given
     * listener ID. The ID was issued when the listener was registered.
     * @param listenerID the returned id when the listener was registered.
     * @exception ConfigurationException if could not register the listener.
     */
    public void removeListener(String listenerID)
        throws ConfigurationException {
    }

    /**
     * Encodes configuration name. 
     * @param configName Configuration name to be encoded.
     * @return encoded configuration name.
     */
    public String encode(String configName) {
        try {
            return URLEncoder.encode(configName, UTF_8);
        } catch (UnsupportedEncodingException ex) {
            debug.error("FedletConfigurationImpl.encode", ex);
            return configName;
        }
    }

    /**
     * Decodes configuration name.
     * @param configName Configuration name to be decoded.
     * @return decoded configuration name.
     */
    public String decode(String configName) {
        try {
            return URLDecoder.decode(configName, UTF_8);
        } catch (UnsupportedEncodingException ex) {
            debug.error("FedletConfigurationImpl.decode", ex);
            return configName;
        }
    }
}
