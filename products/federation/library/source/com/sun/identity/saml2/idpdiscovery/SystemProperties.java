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
 * $Id: SystemProperties.java,v 1.1 2006-10-30 23:16:18 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.idpdiscovery;

import java.io.*;
import java.util.Properties;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

/* iPlanet-PUBLIC-CLASS */

/** This class provides functionality that allows single-point-of-access
 * to all related system properties.
 * 
 * The class tries to find a file <code>IDPDiscoveryConfig.properties</code> in
 * the CLASSPATH accessible to this code.
 *
 * If multiple servers are running, each may have their own configuration file.
 * The naming convention for such scenarios is 
 * <code>IDPDiscoveryConfig_serverName</code>.
 */
public class SystemProperties {
    private static ResourceBundle properties = null;
    public static String iasGXId = null;

    /* Load the properties file for config information before anything else
     * starts.
     */
    static {
        try { 
            String serverName = System.getProperty("server.name");
            String fname = null;
            if (serverName != null) {
                fname = "libIDPDiscoveryConfig_" + serverName;
            } else {
                fname = "libIDPDiscoveryConfig";
            }
            properties = ResourceBundle.getBundle(fname);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Returns system property of a given key.
     *
     * @param key the key whose value one is looking for.
     * @return the value if the key exists; otherwise returns <code>null</code>.
    */
    public static String get(String key) {
        try {
            return properties.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     * Returns system property of a given key.
     *
     * @param key the key whose value one is looking for.
     * @param def the default value. This value is returned if the key does not
     *        exist.
     * @return system property of <code>key</code>.
     */
    public static String get(String key, String def) {
        try {
            return properties.getString(key);
        } catch (MissingResourceException e) {
            return def;
        }
    }

    /** This method lets you get all the properties defined and their 
     * values.
     * @return Properties object with all the key value pairs.
     */
    public static Properties getAll() {
        Enumeration enumerator = properties.getKeys();
        Properties returnProperties = new Properties();
        while (enumerator.hasMoreElements()) {
            try {
                String key =  (String) enumerator.nextElement();
                String value = (String) properties.getString(key);
                returnProperties.setProperty(key, value);
            } catch (MissingResourceException e) {
                CookieUtils.debug.error("SystemPropertiesManager.getAll:"
                    + e.getMessage()); 
            }
        }

        return returnProperties;
    }

    /** This method lets you query for all the platform properties defined 
     * and their values. Returns a Properties object with all the key value 
     * pairs.
     * @return the platform properties
     */
    public static Properties getPlatform() {
        return getAll();
    }
}
