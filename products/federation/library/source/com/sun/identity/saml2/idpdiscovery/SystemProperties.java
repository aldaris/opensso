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
 * $Id: SystemProperties.java,v 1.3 2007-10-29 16:56:10 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.idpdiscovery;

import com.sun.identity.shared.configuration.SystemPropertiesManager;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

/** 
 * This class provides functionality that allows single-point-of-access
 * to all related system properties.
 * 
 * The class tries to retrieve IDP discovery related properties in services,
 * if not exists, find a file <code>IDPDiscoveryConfig.properties</code> in
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
     * Returns system property of a given key. The method will get the property
     * using SystemPropertiesManager first (server mode), if not found,  
     * get it from the locale file (IDP discovery WAR only mode). 
     *
     * @param key the key whose value to be returned.
     * @return the value if the key exists; otherwise returns <code>null</code>.
    */
    public static String get(String key) {
        try {
            String val = SystemPropertiesManager.get(key);
            if (val == null) {
                return properties.getString(key);
            } else {
                return val;
            }
        } catch (MissingResourceException e) {
            return null;
        }
    }
}
