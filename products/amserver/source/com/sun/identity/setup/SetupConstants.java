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
 * $Id: SetupConstants.java,v 1.2 2006-11-01 05:12:04 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.setup;

/**
 * This defines the constants used in setup package.
 */
public interface SetupConstants {
    /**
     * Setup Debug name.
     */
    String DEBUG_NAME = "amSetupServlet";
    
    /**
     * Default Platform Locale.
     */
    String DEFAULT_PLATFORM_LOCALE = "en_US";

    /**
     * Flag to overwrite <code>AMConfig.properties</code>
     */
    String AMC_OVERRIDE_PROPERTY = "com.sun.identity.overrideAMC";

    /**
     * Configurator plugins properties file name.
     */
    String PROPERTY_CONFIGURATOR_PLUGINS = "configuratorPlugins";

    /**
     * Configurator plugins class name.
     */
    String KEY_CONFIGURATOR_PLUGINS = "configurator.plugins";
    
    /**
     * <code>AMConfig.properties</code> file name.
     */
    String AMCONFIG_PROPERTIES = "AMConfig.properties";
    
    /**
     * Properties file name that contains the names of all services that need
     * to be registered by the configurator.
     */
    String PROPERTY_FILENAME = "serviceNames";
    
    /**
     * Property key in <code>PROPERTY_FILENAMEM</code> file that has all
     * services that need to be registered by the configurator.
     */
    String SERVICE_NAMES = "serviceNames";
    
    /**
     * Configuration Variable for product name.
     */
    String CONFIG_VAR_PRODUCT_NAME = "IS_PRODNAME";
    
    /**
     * Configuration Variable for lagency console deployment URI.
     */
    String CONFIG_VAR_OLD_CONSOLE_URI  = "OLDCON_DEPLOY_URI";

    /**
     * Configuration Variable for console deployment URI.
     */
    String CONFIG_VAR_CONSOLE_URI  = "CONSOLE_URI";
    
    /**
     * Configuration Variable for server protocol.
     */
    String CONFIG_VAR_SERVER_PROTO = "SERVER_PROTO";
    
    /**
     * Configuration Variable for server host.
     */
    String CONFIG_VAR_SERVER_HOST = "SERVER_HOST";
    
    /**
     * Configuration Variable for server port.
     */
    String CONFIG_VAR_SERVER_PORT = "SERVER_PORT";
    
    /**
     * Configuration Variable for server deployment URI.
     */
    String CONFIG_VAR_SERVER_URI  = "SERVER_URI";
    
    /**
     * Configuration Variable for installation base directory.
     */
    String CONFIG_VAR_BASE_DIR  = "BASE_DIR";
    
    /**
     * Configuration Variable for root suffix.
     */
    String CONFIG_VAR_ROOT_SUFFIX = "ROOT_SUFFIX";
    
    /**
     * Configuration Variable for root suffix with carat suffix.
     */
    String CONFIG_VAR_ROOT_SUFFIX_HAT = "ROOT_SUFFIX_HAT";
}
