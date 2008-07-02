/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the terms
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
 * $Id: DSConstants.java,v 1.1 2008-07-02 18:50:40 kanduls Exp $
 */

package com.sun.identity.tune.constants;


/**
 * Directory Server constants.
 * 
 */
public interface DSConstants extends AMTuneConstants {
    static String DS_INSTANCE_DIR = "DS_INSTANCE_DIR";
    static String DS_HOST = "DS_HOST";
    static String DS_PORT = "DS_PORT";
    static String ROOT_SUFFIX = "ROOT_SUFFIX";
    static String DIRMGR_UID = "DIRMGR_UID";
    static String DS_VERSION = "DS_VERSION";
    static String ENABLE_MODE = "ENABLE_MODE";
    static String NM_SUFFIX = "NM_SUFFIX";
    static String ORG_OBJECT_CLASS = "ORG_OBJECT_CLASS";
    static String NM_ORG_ROOT_SUFFIX = "NM_ORG_ROOT_SUFFIX";
    static String ORG_ROOT_SUFFIX = "ORG_ROOT_SUFFIX";
    static String PERL_BIN_DIR = "PERL_BIN_DIR";
    static String DS_TOOLS_DIR = "DS_TOOLS_DIR";
    static String SM_DS_HOST = "SM_DS_HOST";
    static String SM_DS_PORT = "SM_DS_PORT";
    static String SM_ROOT_SUFFIX = "SM_ROOT_SUFFIX";
    static String SM_DS_DIRMGR_UID = "SM_DS_DIRMGR_UID";
    static String SM_DS_INSTANCE_DIR = "SM_DS_INSTANCE_DIR";
    static String SM_DS_VERSION = "SM_DS_VERSION";
    static String SM_DS_TOOLS_DIR = "SM_DS_TOOLS_DIR";
    static String IS_UM_SM_DATASTORE_SAME = "IS_UM_SM_DATASTORE_SAME";
    static String TUNE_UM_ONLY = "TUNE_UM_ONLY";
    static String NSSLAPD_DB_HOME_DIRECTORY = "nsslapd-db-home-directory";
    static String NSSLAPD_DIRECTORY = "nsslapd-directory";
    static String NSSLAPD_SUFFIX = "nsslapd-suffix";
    static String CONFIG_DN = "cn=config";
    static String MAPPING_CONF_DN = "cn=mapping tree," + CONFIG_DN;
    static String NSSLAPD_PARENT_SUFFIX = "nsslapd-parent-suffix";
    static String LDBM_DATABASE_DN = "cn=ldbm database,cn=plugins," + CONFIG_DN;
    static String DB_PLUGIN_CONF_DN = CONFIG_DN + "," + LDBM_DATABASE_DN;
    static String NSSLAPD_THREADNO = "nsslapd-threadnumber";
    static String NSSLAPD_ACCESSLOG_LOGGING_ENABLED = 
            "nsslapd-accesslog-logging-enabled";
    static String IPLANET_AM_AUTH_LDAP_USER_SEARCH_ATTRIBUTES = 
            "iplanet-am-auth-ldap-user-search-attributes";
    static String AUTH_LDAP_SERVICE_DN = 
            "ou=default,ou=OrganizationConfig,ou=1.0," +
            "ou=iPlanetAMAuthLDAPService,ou=services,";
    static String NSSLAPD_DBCACHESIZE = "nsslapd-dbcachesize";
    static String NSSLAPD_CACHEMEMSIZE = "nsslapd-cachememsize";
    static String NSSLAPD_SIZELIMIT = "nsslapd-sizelimit";
    static String NSSLAPD_TIMELIMIT = "nsslapd-timelimit";
    static String NSSLAPD_LOOKTHROUGHLIMIT = "nsslapd-lookthroughlimit";
    static String NSSLAPD_REQUIRED_INDEX = "nsslapd-require-index";
    static String NSSLAPD_DB_TRANSACTION_BATCH_VAL = 
            "nsslapd-db-transaction-batch-val";
    static String NSSLAPD_DB_LOGBUF_SIZE = "nsslapd-db-logbuf-size";
    static String REF_INTIGRITY_DN = "cn=referential integrity postoperation," +
                "cn=plugins,cn=config";
    static String NSSLAPD_PLUGINARG = "nsslapd-pluginarg0";
    static String OBJ_CLASS_FILTER = "(objectclass=*)" ;
    static String NSSLAPD_BACKEND = "nsslapd-backend";
    static String SUNKEY_VALUE = "sunkeyvalue";
    static String DB_BACKUP_DIR_PREFIX = "amtune";
    static String LDAP_WORKER_THREADS = "24";
    static String DS5_VERSION = "5.";
    static String DS63_VERSION = "6.3";
    static String DS62_VERSION = "6.2";
    static String OPEN_DS = "openDS";
    
            
}
