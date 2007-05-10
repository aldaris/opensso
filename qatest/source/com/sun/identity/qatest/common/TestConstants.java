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
 * $Id: TestConstants.java,v 1.2 2007-05-10 17:21:21 rmisra Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.qatest.common;

public interface TestConstants {

    /**
     * Property name for AMConfig.properties file
     */
    String TEST_PROPERTY_AMCONFIG = "AMConfig";

    /**
     * Property name for logging level.
     */
    String KEY_ATT_LOG_LEVEL = "log_level";

    /**
     * Property key for <code>amadmin</code> user.
     */
    String KEY_ATT_AMADMIN_USER = "amadmin_username";

    /**
     * Property key for <code>amadmin</code> user password.
     */
    String KEY_ATT_AMADMIN_PASSWORD = "amadmin_password";

    /**
     * Property key for <code>com.iplanet.am.defaultOrg</code>.
     */
    String KEY_AMC_BASEDN = "com.iplanet.am.defaultOrg";

    /**
     * Property key for <code>planet.am.server.protocol</code>.
     */
    String KEY_AMC_PROTOCOL = "com.iplanet.am.server.protocol";

    /**
     * Property key for <code>com.iplanet.am.server.host</code>.
     */
    String KEY_AMC_HOST = "com.iplanet.am.server.host";

    /**
     * Property key for <code>com.iplanet.am.server.port</code>.
     */
    String KEY_AMC_PORT = "com.iplanet.am.server.port";

    /**
     * Property key for
     * <code>com.iplanet.am.services.deploymentDescriptor</code>.
     */
    String KEY_AMC_URI = "com.iplanet.am.services.deploymentDescriptor";

    /**
     * Property key for <code>com.iplanet.am.naming.url</code>.
     */
    String KEY_AMC_NAMING_URL = "com.iplanet.am.naming.url";

    /**
     * Property key for <code>com.iplanet.am.service.password</code>.
     */
    String KEY_AMC_SERVICE_PASSWORD = "com.iplanet.am.service.password";

    /**
     * Property key for <code>realm</code>.
     */
    String KEY_ATT_REALM = "realm";

    /**
     * Property key for <code>testservername</code>.
     */
    String KEY_ATT_SERVER_NAME = "testservername";

    /**
     * Property key for <code>cookiedomain</code>.
     */
    String KEY_ATT_COOKIE_DOMAIN = "cookiedomain";

    /**
     * Property key for <code>namingservice</code>.
     */
    String KEY_ATT_NAMING_SVC = "namingservice";

    /**
     * Property key for <code>config_dir</code>.
     */
    String KEY_ATT_CONFIG_DIR = "config_dir";

    /**
     * Property key for <code>datastore</code>.
     */
    String KEY_ATT_CONFIG_DATASTORE = "datastore";

    /**
     * Property key for <code>directory_server</code>.
     */
    String KEY_ATT_DIRECTORY_SERVER = "directory_server";

    /**
     * Property key for <code>directory_port</code>.
     */
    String KEY_ATT_DIRECTORY_PORT = "directory_port";

    /**
     * Property key for <code>config_root_suffix</code>.
     */
    String KEY_ATT_CONFIG_ROOT_SUFFIX = "config_root_suffix";

    /**
     * Property key for <code>sm_root_suffix</code>.
     */
    String KEY_ATT_SM_ROOT_SUFFIX = "sm_root_suffix";

    /**
     * Property key for <code>ds_dirmgrdn</code>.
     */
    String KEY_ATT_DS_DIRMGRDN = "ds_dirmgrdn";

    /**
     * Property key for <code>ds_dirmgrpasswd</code>.
     */
    String KEY_ATT_DS_DIRMGRPASSWD = "ds_dirmgrpasswd";

    /**
     * Property key for <code>load_ums</code>.
     */
    String KEY_ATT_LOAD_UMS = "load_ums";

    /**
     * Property key for <code>defaultorg</code>.
     */
    String KEY_ATT_DEFAULTORG = "defaultorg";

    /**
     * Property key for <code>productSetupResult</code>.
     */
    String KEY_ATT_PRODUCT_SETUP_RESULT = "productSetupResult";

    /**
     * Property key for <code>metaalias</code>.
     */
    String KEY_ATT_METAALIAS = "metaalias";

    /**
     * Property key for <code>entity_name</code>.
     */
    String KEY_ATT_ENTITY_NAME = "entity_name";

    /**
     * Property key for <code>cot</code>.
     */
    String KEY_ATT_COT = "cot";

    /**
     * Property key for <code>certalias</code>.
     */
    String KEY_ATT_CERTALIAS = "certalias";

    /**
     * Property key for <code>protocol</code>.
     */
    String KEY_ATT_PROTOCOL = "protocol";

    /**
     * Property key for <code>host</code>.
     */
    String KEY_ATT_HOST = "host";

    /**
     * Property key for <code>port</code>.
     */
    String KEY_ATT_PORT = "port";

    /**
     * Property key for <code>deployment_uri</code>.
     */
    String KEY_ATT_DEPLOYMENT_URI = "deployment_uri";
} 
