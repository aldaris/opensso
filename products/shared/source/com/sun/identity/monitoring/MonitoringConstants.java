/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: MonitoringConstants.java,v 1.3 2008-07-23 17:58:16 veiming Exp $
 *
 */

package com.sun.identity.monitoring;

/**
 * This interface contains the names provided to the JES Monitoring
 * Framework during the initialization of the AM Server's managed
 * elements.
 */
public interface MonitoringConstants {
    /**
     *  strings for JES containers AM uses
     *
     *  to be used to see in
     *  AMConfig.properties:com.iplanet.am.admin.cli.certdb.dir
     *  which container is configured.  also for naming the 
     *  container element.
     */
    public String JES_WEBSERVER_PKG_NAME = "SUNWwbsvr";
    public String JES_WIN_WEBSERVER_PKG_NAME = "webserver";
    public String JES_APPSERVER_PKG_NAME = "SUNWappserver";
    public String JES_WIN_APPSERVER_PKG_NAME = "Applicationserver";
    public String GENERIC_APPSERVER_PKG_NAME = "Other Application Server";


    /**
     *  property string for AM product code name for the
     *  PRODUCT_CODE_NAME_CTX_KEY
     */
    public String AM_PRODUCT_CODE_NAME = "am";

    /**
     *  property string for PRODUCT_PREFIX_CTX_KEY should be
     *  this plus the ServerID for this instance of AM
     */
    public String AM_PRODUCT_PREFIX_CTX_KEY_NAME = "amServer";

    /**
     *  property string for PRIVATE_CONNECTOR_SERVER_URL_KEY in
     *  AMConfig.properties.
     *  default the value to "service:jmx:rmi://" if
     *  not defined.
     */

    public String AM_PVT_CONN_SVR_URL_KEY_NAME =
        "com.sun.identity.monitoring.local.conn.server.url";


    /**
     *  default value for PRIVATE_CONNECTOR_SERVER_URL_KEY if
     *  value for com.sun.identity.monitoring.local.conn.server.url
     *  not defined in AMConfig.properties.
     */

    public String AM_PVT_CONN_SVR_URL_KEY_DFLT_VALUE = "service:jmx:rmi://";

    /**
     *  property string for PRODUCT_COLLECTIONID_CTX_KEY should be
     *  gotten from Constants.AM_INSTALL_DIR (com.iplanet.am.installdir)
     */


    /**
     *
     *  property string for the official OpenSSO Enterprise product name for the
     *  PRODUCT_NAME_CTX_KEY
     */
    public String AM_PRODUCT_NAME = "OpenSSO Enterprise";

    /**
     *
     *  property string for the AM description for the
     *  CMM_APPLICATION_SYSTEM
     */
    public String AM_PRODUCT_DESCRIPTION = "OpenSSO Enterprise";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_APPLICATION_SYSTEM
     */
    public String AM_SERVER_NAME = "amserver";

    /**
     *
     *  property string for the AM server stat name for the
     *  CMM_APPLICATION_SYSTEM_STATS
     */
    public String AM_SERVER_STATS_NAME = "amserver statistics";

    /**
     *
     *  property string for the AM server setting name for the
     *  CMM_APPLICATION_SYSTEM_SETTING
     */
    public String AM_SERVER_SETTING_NAME = "amserver settings";

    /**
     *
     *  property string for the AM owner name for the
     *  CMM_APPLICATION_SYSTEM
     */
    public String AM_OWNER_NAME = "amadmin";

    /**
     *
     *  General AM names
     */

    //  Service Access URI (for client/remote sdk)

    public String AM_URI_JAXRPC_NAME = "jaxrpc URI";
    public String AM_URI_JAXRPC_STR =
        "%protocol://%host:%port/amserver/JAXRPC";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the Authentication service
     */
    public String AM_AUTHSVC_NAME = "authentication";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Authentication Plugins service
     */
    public String AM_AUTHSVC_PLUGINSVC_NAME = "authentication plugins";

    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Authentication Plugins (auth modules)
     */
    public String AM_AUTH_PLUGIN_AD_NAME = "active directory";
    public String AM_AUTH_SP_AD_NAME = "active directory server";
    public String AM_AUTH_PLUGIN_ANONYMOUS_NAME = "anonymous";
    public String AM_AUTH_PLUGIN_CERT_NAME = "certificate";
    public String AM_AUTH_SP_CERTDB_NAME = "cert DB";
    public String AM_AUTH_PLUGIN_HTTPBASIC_NAME = "httpbasic";
    public String AM_AUTH_PLUGIN_JDBC_NAME = "jdbc";
    public String AM_AUTH_SP_JDBC_NAME = "jdbc server";
    public String AM_AUTH_PLUGIN_LDAP_NAME = "ldap";
    public String AM_AUTH_SP_LDAP_NAME = "ldap server";
    public String AM_AUTH_PLUGIN_MEMBERSHIP_NAME = "membership";
    public String AM_AUTH_PLUGIN_MSISDN_NAME = "msisdn";
    public String AM_AUTH_SP_MSISDN_NAME = "msisdn server";
    public String AM_AUTH_PLUGIN_NT_NAME = "nt";
    public String AM_AUTH_SP_NT_NAME = "nt server";
    public String AM_AUTH_PLUGIN_RADIUS_NAME = "radius";
    public String AM_AUTH_SP_RADIUS_NAME = "radius server";
    public String AM_AUTH_PLUGIN_SAFEWORD_NAME = "safeword";
    public String AM_AUTH_SP_SAFEWORD_NAME = "safeword server";
    public String AM_AUTH_PLUGIN_SAML_NAME = "saml";
    public String AM_AUTH_PLUGIN_SECURID_NAME = "securid";
    public String AM_AUTH_SP_SECURID_NAME = "securid server";
    public String AM_AUTH_SPEP_SECURID_NAME = "securid server";
    public String AM_AUTH_PLUGIN_UNIX_NAME = "unix";
    public String AM_AUTH_SP_UNIX_NAME = "unix helper";
    public String AM_AUTH_SPEP_UNIX_NAME = "unix helper";
    public String AM_AUTH_PLUGIN_WINDOWSDESKTOPSSO_NAME = "windowsdesktopsso";
    public String AM_AUTH_SP_WINDOWSDESKTOPSSO_NAME = "kerberos server";

    //  not "public"
    public String AM_AUTH_PLUGIN_APPLICATION_NAME = "application";
    //  maybe not used
    public String AM_AUTH_PLUGIN_CUSTOM_NAME = "custom";

    public String[] AUTH_PLUGIN_MODULES = {
        AM_AUTH_PLUGIN_AD_NAME,
        AM_AUTH_PLUGIN_ANONYMOUS_NAME,
        AM_AUTH_PLUGIN_CERT_NAME,
        AM_AUTH_PLUGIN_HTTPBASIC_NAME,
        AM_AUTH_PLUGIN_JDBC_NAME,
        AM_AUTH_PLUGIN_LDAP_NAME,
        AM_AUTH_PLUGIN_MEMBERSHIP_NAME,
        AM_AUTH_PLUGIN_MSISDN_NAME,
        AM_AUTH_PLUGIN_NT_NAME,
        AM_AUTH_PLUGIN_RADIUS_NAME,
        AM_AUTH_PLUGIN_SAFEWORD_NAME,
        AM_AUTH_PLUGIN_SAML_NAME,
        AM_AUTH_PLUGIN_SECURID_NAME,
        AM_AUTH_PLUGIN_UNIX_NAME,
        AM_AUTH_PLUGIN_WINDOWSDESKTOPSSO_NAME
    };

    //  Auth service access URIs

    public String AM_AUTH_URI_AUTHSERVICE_NAME =
        "auth service URI";
    public String AM_AUTH_URI_AUTHLOGIN_NAME = "auth Login URI";
    public String AM_AUTH_URI_AUTHLOGOUT_NAME = "auth Logout URI";
    public String AM_AUTH_URI_AUTHLOGIN_STR =
        "%protocol://%host:%port/amserver/UI/Login";
    public String AM_AUTH_URI_AUTHLOGOUT_STR =
        "%protocol://%host:%port/amserver/UI/Logout";
    public String AM_AUTH_URI_AUTHSVC_STR =
        "%protocol://%host:%port/amserver/authservice";

    //  Auth service stats names

    public String AM_AUTH_SVC_STATS_RQT_NAME = "auth rqt";

    //  Auth service URI stats names

    public String AM_AUTH_LOGIN_URI_STATS_NAME = "auth login URI";
    public String AM_AUTH_LOGOUT_URI_STATS_NAME = "auth logout URI";
    public String AM_AUTH_SVC_URI_STATS_NAME = "auth service URI";

    //  Auth service Remote Service Access Point Stats names

    public String AM_AUTH_RSAP_MSISDN_STATS_NAME = "auth MSISDN server stats";
    public String AM_AUTH_RSAP_NT_STATS_NAME = "auth NT server stats";
    public String AM_AUTH_RSAP_JDBC_STATS_NAME = "auth JDBC server stats";
    public String AM_AUTH_RSAP_RADIUS_STATS_NAME = "auth RADIUS server stats";
    public String AM_AUTH_RSAP_AD_STATS_NAME =
        "auth Active Directory server stats";
    public String AM_AUTH_RSAP_CERT_STATS_NAME =
        "auth Certificate server stats";
    public String AM_AUTH_RSAP_WINDOWSDESKTOPSSO_STATS_NAME =
        "auth Windows Desktop SSO server stats";
    public String AM_AUTH_RSAP_SAFEWORD_STATS_NAME =
        "auth SafeWord server stats";
    public String AM_AUTH_RSAP_LDAP_STATS_NAME = "auth LDAP server stats";

    //  Auth service LDAP Connection Pool Stats names

    public String AM_AUTH_LDAP_CONN_POOL_NAME = "auth LDAP connection pool";
    public String AM_AUTH_LDAP_CONN_POOL_STATS_NAME =
        "auth LDAP connection pool stats";
    public String AM_AUTH_LDAP_CONN_POOL_SETTING_NAME =
        "auth LDAP connection pool setting";

    //  Realm-related element Map key names

    public String AM_REALM_LOGICAL_COMPONENT = "RealmLC";
    public String AM_REALM_LC_SETTING = "Setting";
    public String AM_REALM_LC_STATS = "Stats";


    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the Session service
     */
    public String AM_SESSSVC_NAME = "session";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Session Plugins service
     */
    public String AM_SESSSVC_PLUGINSVC_NAME = "session plugins";


    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Session Plugins
     */

    public String AM_SESS_PLUGIN_SSOTG_NAME = "SSOToken generator";

    public String AM_SESS_PLUGIN_JMQ_NAME = "JMQ";
    public String AM_SESS_SP_DB_NAME = "Berkeley DB";


    //  Session service access URIs

    public String AM_SESS_URI_SESSSERVICE_NAME = "session service URI";
    public String AM_SESS_URI_SESSSERVICE_STR =
        "%protocol://%host:%port/amserver/sessionservice";

    //  Session service stats names

    public String AM_SESS_SVC_STATS_RQT_NAME = "session rqt";
    public String AM_SESS_SVC_DB_STATS_RQT_NAME = "session DB rqt";
    public String AM_SESS_SVC_URI_STATS_NAME = "session URI stats";
    public String AM_SESS_JAXRPC_URI_STATS_NAME = "session JAXRPC URI stats";
    public String AM_SESS_SVC_SETTING_NAME = "session setting";
    public String AM_SESS_CONN_POOL_STATS_RQT_NAME = "session connections";
    public String AM_SESS_CONN_POOL_NAME = "session connection pool";
    public String AM_SESS_CONN_POOL_STATS_NAME =
        "session connection pool stats";
    public String AM_SESS_CONN_POOL_SETTING_NAME =
        "session connection pool setting";
    public String AM_SESS_SESSION_POOL_NAME = "session session pool";
    public String AM_SESS_SESSION_POOL_STATS_NAME =
        "session session pool stats";
    public String AM_SESS_SESSION_POOL_SETTING_NAME =
        "session session pool setting";
    public String AM_SESS_THREAD_POOL_NAME = "session thread pool";
    public String AM_SESS_THREAD_POOL_STATS_NAME = "session thread pool stats";
    public String AM_SESS_THREAD_POOL_SETTING_NAME =
        "session thread pool setting";


    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the Profile service
     */
    public String AM_PROFSVC_NAME = "profile";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Profile Plugins service
     */
    public String AM_PROFSVC_PLUGINSVC_NAME = "profile plugins";

    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Profile Plugins
     */

    public String AM_PROF_PLUGIN_AMSDK_NAME = "AMSDK";
    public String AM_PROF_PLUGIN_IDREPO_NAME = "IdRepo";
    public String AM_PROF_SP_AMSDK_LDAP_NAME = "AMSDK's LDAP";
    public String AM_PROF_SP_IDREPO_LDAP_NAME = "IdRepo's LDAP";
    public String AM_PROF_SP_SENDMAIL_NAME = "profile SendMail server";


    //  Profile service access URIs

    public String AM_PROF_URI_PROFSERVICE_NAME = "profile service URI";
    public String AM_PROF_URI_PROFSERVICE_STR =
        "%protocol://%host:%port/amserver/profileservice";

    //  Profile service stats names

    public String AM_PROF_SVC_STATS_RQT_NAME = "profile rqt";
    public String AM_PROF_CONN_POOL_STATS_RQT_NAME =
        "amSDK profile connections";
    public String AM_PROF_CONN_POOL_NAME = "profile connection pool";
    public String AM_PROF_CONN_POOL_STATS_NAME =
        "profile connection pool stats";
    public String AM_PROF_CONN_POOL_SETTING_NAME =
        "profile connection pool setting";

    public String AM_PROF_CACHE_NAME = "profile cache";
    public String AM_PROF_CACHE_STATS_NAME = "profile cache stats";
    public String AM_PROF_CACHE_SETTING_NAME = "profile cache setting";

    public String AM_PROF_SVC_URI_STATS_NAME = "profile URI stats";

    public String AM_PROF_SP_AMSDK_LDAP_STATS_NAME = "AMSDK's LDAP stats";
    public String AM_PROF_SP_SENDMAIL_STATS_NAME =
        "profile SendMail server stats";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the IdRepo service
     */
    public String AM_IDREPOSVC_NAME = "idrepo";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the IdRepo Plugins service
     */
    public String AM_IDREPOSVC_PLUGINSVC_NAME = "idrepo plugins";

    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the IdRepo Plugins
     */

    public String AM_IDREPO_PLUGIN_LDAPV3_NAME = "LDAPv3";
    public String AM_IDREPO_PLUGIN_LDAPV3AD_NAME = "LDAPv3/AD";
    public String AM_IDREPO_PLUGIN_FLATFILE_NAME = "Flat File";
    public String AM_IDREPO_SP_LDAPV3_NAME = "LDAPv3";
    public String AM_IDREPO_SP_LDAPV3AD_NAME = "LDAPv3/AD";

    public String AM_IDREPO_SVC_STATS_RQT_NAME = "idrepo rqt";
    public String AM_IDREPO_PLUGIN_STATS_RQT_NAME = "idrepo plugin rqt";
    public String AM_IDREPO_PLUGIN_LDAPV3_STATS_RQT_NAME =
        "idrepo plugin ldapv3 rqt";
    public String AM_IDREPO_PLUGIN_LDAPV3AD_STATS_RQT_NAME =
        "idrepo plugin ldapv3/ad rqt";
    public String AM_IDREPO_PLUGIN_FLATFILE_STATS_RQT_NAME =
        "idrepo plugin flatfile rqt";
    public String AM_IDREPO_PLUGIN_LDAPV3_AUTH_STATS_RQT_NAME =
        "idrepo plugin ldapv3 auth rqt";
    public String AM_IDREPO_PLUGIN_LDAPV3AD_AUTH_STATS_RQT_NAME =
        "idrepo plugin ldapv3/ad auth rqt";
    public String AM_IDREPO_PLUGIN_FLATFILE_AUTH_STATS_RQT_NAME =
        "idrepo plugin flatfile auth rqt";

    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the IdRepo Plugins' Authentication Services
     */

    public String AM_IDREPO_PLUGIN_LDAPV3_AUTH_NAME = "LDAPv3 Auth";
    public String AM_IDREPO_PLUGIN_LDAPV3AD_AUTH_NAME = "LDAPv3/AD Auth";
    public String AM_IDREPO_PLUGIN_FLATFILE_AUTH_NAME = "FlatFile Auth";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the Policy service
     */
    public String AM_POLICYSVC_NAME = "policy";


    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Policy Plugins service
     */
    public String AM_POLICYSVC_PLUGINSVC_NAME = "policy plugins";

    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Policy Plugin
     */

    public String AM_POLICY_PLUGIN_SUBJECT_NAME = "subject";
    public String AM_POLICY_SP_SUBJECT_LDAP_NAME = "subject's LDAP";


    //  Policy service access URIs

    public String AM_POLICY_URI_POLICYSERVICE_NAME = "policy service URI";
    public String AM_POLICY_URI_POLICYSERVICE_STR =
        "%protocol://%host:%port/amserver/policyservice";

    //  Policy service stats names

    public String AM_POLICY_SVC_STATS_RQT_NAME = "policy rqt";
    public String AM_POLICY_CONN_POOL_NAME = "policy connection pool";
    public String AM_POLICY_CONN_POOL_STATS_NAME =
        "policy connection pool stats";
    public String AM_POLICY_CONN_POOL_SETTING_NAME =
        "policy connection pool setting";

    public String AM_POLICY_CACHE_NAME = "policy cache";
    public String AM_POLICY_CACHE_STATS_NAME = "policy cache stats";
    public String AM_POLICY_CACHE_SETTING_NAME = "policy cache setting";

    public String AM_POLICY_SVC_URI_STATS_NAME = "policy URI stats";
    public String AM_POLICY_SP_SUBJECT_LDAP_STATS_NAME =
        "policy subject's LDAP stats";

    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the Logging service
     */
    public String AM_LOGGINGSVC_NAME = "logging";


    /**
     *
     *  property string for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Logging Plugins service
     */
    public String AM_LOGGINGSVC_PLUGINSVC_NAME = "logging plugins";

    /**
     *
     *  property strings for the AM server name for the
     *  CMM_Service with CMM_ServiceComponent dependency
     *  for the Logging Plugin
     */

    public String AM_LOGGING_PLUGIN_FLATFILE_NAME = "flatfile";
    public String AM_LOGGING_PLUGIN_RDBMS_NAME = "rdbms";

    public String AM_LOGGING_SP_RDBMS_ORACLE_NAME = "Oracle rdbms";
    public String AM_LOGGING_SP_RDBMS_MYSQL_NAME = "MySQL rdbms";
    public String AM_LOGGING_SP_CERTDB_NAME = "CertDB keystore";

    //  Policy service access URIs

    public String AM_LOGGING_URI_LOGGINGSERVICE_NAME = "logging service URI";
    public String AM_LOGGING_URI_LOGGINGSERVICE_STR =
        "%protocol://%host:%port/amserver/loggingservice";

    //  Profile service stats names

    public String AM_LOGGING_SVC_STATS_RQT_NAME = "logging rqt";
    public String AM_LOGGING_LOG_STATS_NAME = "logging stats";
    public String AM_LOGGING_SVC_SETTING_NAME = "logging setting";
    public String AM_LOGGING_SVC_URI_STATS_NAME = "logging service URI stats";
    public String AM_LOGGING_ORACLE_RDBMS_STATS_NAME =
        "logging service Oracle RDBMS stats";
    public String AM_LOGGING_MYSQL_RDBMS_STATS_NAME =
        "logging service MySQL RDBMS stats";
    public String AM_LOGGING_CERTDB_STATS_NAME =
        "logging service Certificate DB KeyStore stats";

    public String AM_LOGGING_MSG_LOG_NAME = "logging service message log";


    /**
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the SAML1.0 service
     */
    public String AM_SAML10SVC_NAME = "SAML1.0";

    //  SAML1.0 servlet names
    public String AM_SAML10_POSTPROFILE_NAME =
        "SAML1.0 SAMLPOSTProfile servlet";
    public String AM_SAML10_SAMLAWARE_NAME = "SAML1.0 SAMLAware servlet";
    public String AM_SAML10_SAMLSOAPRECEIVER_NAME =
        "SAML1.0 SAMLSOAPReceiver servlet";

    //  SAML1.0 service access URIs

    public String AM_SAML10_URI_POSTPROFILE_NAME =
        "SAML1.0 POSTProfileServlet URI";
    public String AM_SAML10_URI_SAMLAWARE_NAME =
        "SAML1.0 SAMLAwareServlet URI";
    public String AM_SAML10_URI_SAMLSOAPRECEIVER_NAME =
        "SAML1.0 SAMLSOAPReceiver URI";
    public String AM_SAML10_URI_POSTPROFILE_STR =
        "%protocol://%host:%port/openfam/SAMLPOSTProfileServlet";
    public String AM_SAML10_URI_SAMLAWARE_STR =
        "%protocol://%host:%port/openfam/SAMLAWareServlet";
    public String AM_SAML10_URI_SAMLSOAPRECEIVER_STR =
        "%protocol://%host:%port/openfam/SAMLSDOAPReceiver";

    //  SAML1.0 service stats names

    public String AM_SAML10_SVC_STATS_NAME = "SAML1.0 Service";
    public String AM_SAML10_SVC_STATS_POSTPROFILE_NAME =
        "SAML1.0 POST Profile servlet";
    public String AM_SAML10_SVC_STATS_SAMLAWARE_NAME =
        "SAML1.0 SAMLAware servlet";
    public String AM_SAML10_SVC_STATS_SAMLSOAPRCVR_NAME =
        "SAML1.0 SAMLSOAP receiver";
    public String AM_SAML10_JAXRPC_URI_STATS_NAME = "SAML1.0 JAXRPC URI stats";
    public String AM_SAML10_POSTPROFILE_SVC_URI_STATS_NAME =
        "SAML1.0 POST Profile URI stats";
    public String AM_SAML10_SAMLAWARE_SVC_URI_STATS_NAME =
        "SAML1.0 SAML Aware URI stats";
    public String AM_SAML10_SAMLSOAPRCVR_SVC_URI_STATS_NAME =
        "SAML1.0 SAMLSOAP Receiver URI stats";

    public String AM_SAML10_ASSERTION_CACHE_NAME = "SAML1.0 Assertion cache";
    public String AM_SAML10_ASSERTION_CACHE_STATS_NAME =
        "SAML1.0 Assertion cache stats";
    public String AM_SAML10_ASSERTION_CACHE_SETTING_NAME =
        "SAML1.0 Assertion cache setting";

    public String AM_SAML10_ARTIFACT_CACHE_NAME = "SAML1.0 Artifact cache";
    public String AM_SAML10_ARTIFACT_CACHE_STATS_NAME =
        "SAML1.0 Artifact cache stats";
    public String AM_SAML10_ARTIFACT_CACHE_SETTING_NAME =
        "SAML1.0 Artifact cache setting";


    /**
     *  property string for the AM server name for the
     *  CMM_Service with CMM_HostedService dependency
     *  for the SAML2 service
     */
    public String AM_SAML2SVC_NAME = "SAML2";

    //  SAML2 servlet names
    //  SP servlets
    public String AM_SAML2_SP_IDPMniInit_NAME =
        "SAML2 SP idpMNIRequestInit servlet";
    public String AM_SAML2_SP_IDPMniRedirect_NAME =
        "SAML2 SP idpMNIRedirect servlet";
    public String AM_SAML2_SP_SPMniInit_NAME =
        "SAML2 SP spMNIRequestInit servlet";
    public String AM_SAML2_SP_SPMniRedirect_NAME =
        "SAML2 SP spMNIRedirect servlet";
    public String AM_SAML2_SP_WSFederation_NAME =
        "SAML2 SP WSFederation servlet";
    public String AM_SAML2_SP_spssoinit_NAME =
        "SAML2 SP spSSOInit servlet";
    public String AM_SAML2_SP_Consumer_NAME =
        "SAML2 SP spAssertionConsumer servlet";
    public String AM_SAML2_SP_SPMniSoap_NAME =
        "SAML2 SP spMNISOAP servlet";
    public String AM_SAML2_SP_IDPMniSoap_NAME =
        "SAML2 SP idpMNISOAP servlet";
    public String AM_SAML2_SP_SPSloInit_NAME =
        "SAML2 SP spSingleLogoutInit servlet";
    public String AM_SAML2_SP_SPSloRedirect_NAME =
        "SAML2 SP spSingleLogoutRedirect servlet";

    //  IDP servlets
    public String AM_SAML2_IDP_IDPSloSoap_NAME =
        "SAML2 IDP IDPSingleLogoutServiceSOAP servlet";
    public String AM_SAML2_IDP_WSFederation_NAME =
        "SAML2 IDP WSFederation servlet";
    public String AM_SAML2_IDP_idpSSOFederate_NAME =
        "SAML2 IDP idpSSOFederate servlet";
    public String AM_SAML2_IDP_IDPSloInit_NAME =
        "SAML2 IDP idpSingleLogoutInit servlet";
    public String AM_SAML2_IDP_IDPSloRedirect_NAME =
        "SAML2 IDP idpSingleLogoutRedirect servlet";
    public String AM_SAML2_IDP_idpssoinit_NAME =
        "SAML2 IDP idpssoinit servlet";
    public String AM_SAML2_IDP_Consumer_NAME =
        "SAML2 IDP spAssertionConsumer servlet";
    public String AM_SAML2_IDP_SPMniRedirect_NAME =
        "SAML2 IDP spMNIRedirect servlet";
    public String AM_SAML2_IDP_IDPMniRedirect_NAME =
        "SAML2 IDP idpMNIRedirect servlet";
    public String AM_SAML2_IDP_SPMniSoap_NAME =
        "SAML2 IDP spMNISOAP servlet";
    public String AM_SAML2_IDP_IDPMniSoap_NAME =
        "SAML2 IDP idpMNISOAP servlet";
    public String AM_SAML2_IDP_SPMniInit_NAME =
        "SAML2 IDP spMNIRequestInit servlet";
    public String AM_SAML2_IDP_IDPMniInit_NAME =
        "SAML2 IDP idpMNIRequestInit servlet";
    public String AM_SAML2_IDP_idpArtifactResolution_NAME =
        "SAML2 IDP idpArtifactResolution servlet";

    //  SAML2 service access URIs
    //  SP servlet URIs
    public String AM_SAML2_URI_SP_IDPMniInit_NAME =
        "SAML2 SP idpMNIRequestInit servlet";
    public String AM_SAML2_URI_SP_IDPMniInit_STR =
        "%protocol://%host:%port/openfam/IDPMniInit/*";
    public String AM_SAML2_URI_SP_IDPMniRedirect_NAME =
        "SAML2 SP idpMNIRedirect servlet";
    public String AM_SAML2_URI_SP_IDPMniRedirect_STR =
        "%protocol://%host:%port/openfam/IDPMniRedirect/*";
    public String AM_SAML2_URI_SP_SPMniInit_NAME =
        "SAML2 SP spMNIRequestInit servlet";
    public String AM_SAML2_URI_SP_SPMniInit_STR =
        "%protocol://%host:%port/openfam/SPMniInit/*";
    public String AM_SAML2_URI_SP_SPMniRedirect_NAME =
        "SAML2 SP spMNIRedirect servlet";
    public String AM_SAML2_URI_SP_SPMniRedirect_STR =
        "%protocol://%host:%port/openfam/SPMniRedirect/*";
    public String AM_SAML2_URI_SP_WSFederation_NAME =
        "SAML2 SP WSFederation servlet";
    public String AM_SAML2_URI_SP_WSFederation_STR =
        "%protocol://%host:%port/openfam/WSFederationServlet/*";
    public String AM_SAML2_URI_SP_spssoinit_NAME =
        "SAML2 SP spSSOInit servlet";
    public String AM_SAML2_URI_SP_spssoinit_STR =
        "%protocol://%host:%port/openfam/spssoinit";
    public String AM_SAML2_URI_SP_Consumer_NAME =
        "SAML2 SP spAssertionConsumer servlet";
    public String AM_SAML2_URI_SP_Consumer_STR =
        "%protocol://%host:%port/openfam/Consumer/*";
    public String AM_SAML2_URI_SP_SPMniSoap_NAME =
        "SAML2 SP spMNISOAP servlet";
    public String AM_SAML2_URI_SP_SPMniSoap_STR =
        "%protocol://%host:%port/openfam/SPMniSoap/*";
    public String AM_SAML2_URI_SP_IDPMniSoap_NAME =
        "SAML2 SP idpMNISOAP servlet";
    public String AM_SAML2_URI_SP_IDPMniSoap_STR =
        "%protocol://%host:%port/openfam/IDPMniSoap/*";
    public String AM_SAML2_URI_SP_SPSloInit_NAME =
        "SAML2 SP spSingleLogoutInit servlet";
    public String AM_SAML2_URI_SP_SPSloInit_STR =
        "%protocol://%host:%port/openfam/SPSloInit/*";
    public String AM_SAML2_URI_SP_SPSloRedirect_NAME =
        "SAML2 SP spSingleLogoutRedirect servlet";
    public String AM_SAML2_URI_SP_SPSloRedirect_STR =
        "%protocol://%host:%port/openfam/SPSloRedirect/*";

    //  IDP servlets
    public String AM_SAML2_URI_IDP_IDPSloSoap_NAME =
        "SAML2 IDP IDPSingleLogoutServiceSOAP servlet";
    public String AM_SAML2_URI_IDP_IDPSloSoap_STR =
        "%protocol://%host:%port/openfam/IDPSloSoap/*";
    public String AM_SAML2_URI_IDP_WSFederation_NAME =
        "SAML2 IDP WSFederation servlet";
    public String AM_SAML2_URI_IDP_WSFederation_STR =
        "%protocol://%host:%port/openfam/WSFederationServlet/*";
    public String AM_SAML2_URI_IDP_idpSSOFederate_NAME =
        "SAML2 IDP idpSSOFederate servlet";
    public String AM_SAML2_URI_IDP_idpSSOFederate_STR =
        "%protocol://%host:%port/openfam/idpSSOFederate/*";
    public String AM_SAML2_URI_IDP_IDPSloInit_NAME =
        "SAML2 IDP idpSingleLogoutInit servlet";
    public String AM_SAML2_URI_IDP_IDPSloInit_STR =
        "%protocol://%host:%port/openfam/IDPSloInit/*";
    public String AM_SAML2_URI_IDP_IDPSloRedirect_NAME =
        "SAML2 IDP idpSingleLogoutRedirect servlet";
    public String AM_SAML2_URI_IDP_IDPSloRedirect_STR =
        "%protocol://%host:%port/openfam/IDPSloRedirect/*";
    public String AM_SAML2_URI_IDP_idpssoinit_NAME =
        "SAML2 IDP idpssoinit servlet";
    public String AM_SAML2_URI_IDP_idpssoinit_STR =
        "%protocol://%host:%port/openfam/idpssoinit";
    public String AM_SAML2_URI_IDP_Consumer_NAME =
        "SAML2 IDP spAssertionConsumer servlet";
    public String AM_SAML2_URI_IDP_Consumer_STR =
        "%protocol://%host:%port/openfam/Consumer/*";
    public String AM_SAML2_URI_IDP_SPMniRedirect_NAME =
        "SAML2 IDP spMNIRedirect servlet";
    public String AM_SAML2_URI_IDP_SPMniRedirect_STR =
        "%protocol://%host:%port/openfam/SPMniRedirect/*";
    public String AM_SAML2_URI_IDP_IDPMniRedirect_NAME =
        "SAML2 IDP idpMNIRedirect servlet";
    public String AM_SAML2_URI_IDP_IDPMniRedirect_STR =
        "%protocol://%host:%port/openfam/IDPMniRedirect/*";
    public String AM_SAML2_URI_IDP_SPMniSoap_NAME =
        "SAML2 IDP spMNISOAP servlet";
    public String AM_SAML2_URI_IDP_SPMniSoap_STR =
        "%protocol://%host:%port/openfam/SPMniSoap/*";
    public String AM_SAML2_URI_IDP_IDPMniSoap_NAME =
        "SAML2 IDP idpMNISOAP servlet";
    public String AM_SAML2_URI_IDP_IDPMniSoap_STR =
        "%protocol://%host:%port/openfam/IDPMniSoap/*";
    public String AM_SAML2_URI_IDP_SPMniInit_NAME =
        "SAML2 IDP spMNIRequestInit servlet";
    public String AM_SAML2_URI_IDP_SPMniInit_STR =
        "%protocol://%host:%port/openfam/SPMniInit/*";
    public String AM_SAML2_URI_IDP_IDPMniInit_NAME =
        "SAML2 IDP idpMNIRequestInit servlet";
    public String AM_SAML2_URI_IDP_IDPMniInit_STR =
        "%protocol://%host:%port/openfam/IDPMniInit/*";
    public String AM_SAML2_URI_IDP_idpArtifactResolution_NAME =
        "SAML2 IDP idpArtifactResolution servlet";
    public String AM_SAML2_URI_IDP_idpArtifactResolution_STR =
        "%protocol://%host:%port/openfam/idpArtifactResolution/*";

    //  SAML2 service stats names
    public String AM_SAML2_SVC_STATS_NAME = "SAML2 Service";

    //  SAML2 SP servlet stats names
    public String AM_SAML2_SVC_STATS_SP_IDPMniInit_NAME =
        "SAML2 SP idpMNIRequestInit";
    public String AM_SAML2_SVC_STATS_SP_IDPMniRedirect_NAME =
        "SAML2 SP idpMNIRedirect";
    public String AM_SAML2_SVC_STATS_SP_SPMniInit_NAME =
        "SAML2 SP spMNIRequestInit";
    public String AM_SAML2_SVC_STATS_SP_SPMniRedirect_NAME =
        "SAML2 SP spMNIRedirect";
    public String AM_SAML2_SVC_STATS_SP_WSFederation_NAME =
        "SAML2 SP WSFederation";
    public String AM_SAML2_SVC_STATS_SP_spssoinit_NAME =
        "SAML2 SP spSSOInit";
    public String AM_SAML2_SVC_STATS_SP_Consumer_NAME =
        "SAML2 SP spAssertionConsumer";
    public String AM_SAML2_SVC_STATS_SP_SPMniSoap_NAME =
        "SAML2 SP spMNISOAP";
    public String AM_SAML2_SVC_STATS_SP_IDPMniSoap_NAME =
        "SAML2 SP idpMNISOAP";
    public String AM_SAML2_SVC_STATS_SP_SPSloInit_NAME =
        "SAML2 SP spSingleLogoutInit";
    public String AM_SAML2_SVC_STATS_SP_SPSloRedirect_NAME =
        "SAML2 SP spSingleLogoutRedirect";

    //  IDP servlets
    public String AM_SAML2_SVC_STATS_IDP_IDPSloSoap_NAME =
        "SAML2 IDP IDPSingleLogoutServiceSOAP";
    public String AM_SAML2_SVC_STATS_IDP_WSFederation_NAME =
        "SAML2 IDP WSFederation";
    public String AM_SAML2_SVC_STATS_IDP_idpSSOFederate_NAME =
        "SAML2 IDP idpSSOFederate";
    public String AM_SAML2_SVC_STATS_IDP_IDPSloInit_NAME =
        "SAML2 IDP idpSingleLogoutInit";
    public String AM_SAML2_SVC_STATS_IDP_IDPSloRedirect_NAME =
        "SAML2 IDP idpSingleLogoutRedirect";
    public String AM_SAML2_SVC_STATS_IDP_idpssoinit_NAME =
        "SAML2 IDP idpssoinit";
    public String AM_SAML2_SVC_STATS_IDP_Consumer_NAME =
        "SAML2 IDP spAssertionConsumer";
    public String AM_SAML2_SVC_STATS_IDP_SPMniRedirect_NAME =
        "SAML2 IDP spMNIRedirect";
    public String AM_SAML2_SVC_STATS_IDP_IDPMniRedirect_NAME =
        "SAML2 IDP idpMNIRedirect";
    public String AM_SAML2_SVC_STATS_IDP_SPMniSoap_NAME =
        "SAML2 IDP spMNISOAP";
    public String AM_SAML2_SVC_STATS_IDP_IDPMniSoap_NAME =
        "SAML2 IDP idpMNISOAP";
    public String AM_SAML2_SVC_STATS_IDP_SPMniInit_NAME =
        "SAML2 IDP spMNIRequestInit";
    public String AM_SAML2_SVC_STATS_IDP_IDPMniInit_NAME =
        "SAML2 IDP idpMNIRequestInit";
    public String AM_SAML2_SVC_STATS_IDP_idpArtifactResolution_NAME =
        "SAML2 IDP idpArtifactResolution";

    //  SAML2 SP Cache Names
    public String AM_SAML2_SP_REQUESTHASH_CACHE_NAME =
        "SAML2 SP Request Hash Cache";
    public String AM_SAML2_SP_MNIREQUESTHASH_CACHE_NAME =
        "SAML2 SP mni Request Hash Cache";
    public String AM_SAML2_SP_RELAYSTATEHASH_CACHE_NAME =
        "SAML2 SP relay State Hash Cache";
    public String AM_SAML2_SP_FEDSESSLISTBYNAME_CACHE_NAME =
        "SAML2 SP federation Session Lists By Name ID Info Key Cache";
    public String AM_SAML2_SP_LOGOUTRQTIDS_CACHE_NAME =
        "SAML2 SP logout Request IDs Cache";
    public String AM_SAML2_SP_RESPONSEHASH_CACHE_NAME =
        "SAML2 SP response Hash Cache";
    public String AM_SAML2_SP_AUTHCTXOBJHASH_CACHE_NAME =
        "SAML2 SP authentication Context Object Hash Cache";
    public String AM_SAML2_SP_AUTHCONTEXTHASH_CACHE_NAME =
        "SAML2 SP authentication Context Hash Cache";
    public String AM_SAML2_SP_SPACCOUNTMAPPER_CACHE_NAME =
        "SAML2 SP sp Account Mapper Cache";
    //  SAML2 IDP Cache Names
    public String AM_SAML2_IDP_AUTHNREQUEST_CACHE_NAME =
        "SAML2 IDP authentication Request Cache";
    public String AM_SAML2_IDP_RELAYSTATE_CACHE_NAME =
        "SAML2 IDP relay State Cache";
    public String AM_SAML2_IDP_IDPSESSBYINDX_CACHE_NAME =
        "SAML2 IDP idp Sessions By Indices Cache";
    public String AM_SAML2_IDP_RSPSBYARTIFACTS_CACHE_NAME =
        "SAML2 IDP responses By Artifacts Cache";
    public String AM_SAML2_IDP_MNIREQUESTHASH_CACHE_NAME =
        "SAML2 IDP mni Request Hash Cache";
    public String AM_SAML2_IDP_DPATTRMAPPER_CACHE_NAME =
        "SAML2 IDP dp Attribute Mapper Cache";
    public String AM_SAML2_IDP_IDPACCTMAPPER_CACHE_NAME =
        "SAML2 IDP idp Account Mapper Cache";
    public String AM_SAML2_IDP_IDPAUTHNCTXMAPPER_CACHE_NAME =
        "SAML2 IDP idp Authentication Mapper Cache";
    public String AM_SAML2_IDP_RESPONSE_CACHE_NAME =
        "SAML2 IDP response Cache";
    public String AM_SAML2_IDP_AUTHNCONTEXT_CACHE_NAME =
        "SAML2 IDP authtication Context Cache";
    public String AM_SAML2_IDP_OLDIDPSESSION_CACHE_NAME =
        "SAML2 IDP old IDP Session Cache";

    //  SAML2 SP Cache Stats Names
    public String AM_SAML2_SP_REQUESTHASH_CACHE_STATS_NAME =
        "SAML2 SP Request Hash Cache";
    public String AM_SAML2_SP_MNIREQUESTHASH_CACHE_STATS_NAME =
        "SAML2 SP mni Request Hash Cache";
    public String AM_SAML2_SP_RELAYSTATEHASH_CACHE_STATS_NAME =
        "SAML2 SP relay State Hash Cache";
    public String AM_SAML2_SP_FEDSESSLISTBYNAME_CACHE_STATS_NAME =
        "SAML2 SP federation Session Lists By Name ID Info Key Cache";
    public String AM_SAML2_SP_LOGOUTRQTIDS_CACHE_STATS_NAME =
        "SAML2 SP logout Request IDs Cache";
    public String AM_SAML2_SP_RESPONSEHASH_CACHE_STATS_NAME =
        "SAML2 SP response Hash Cache";
    public String AM_SAML2_SP_AUTHCTXOBJHASH_CACHE_STATS_NAME =
        "SAML2 SP authentication Context Object Hash Cache";
    public String AM_SAML2_SP_AUTHCONTEXTHASH_CACHE_STATS_NAME =
        "SAML2 SP authentication Context Hash Cache";
    public String AM_SAML2_SP_SPACCOUNTMAPPER_CACHE_STATS_NAME =
        "SAML2 SP sp Account Mapper Cache";
    //  SAML2 IDP Cache Stats Names
    public String AM_SAML2_IDP_AUTHNREQUEST_CACHE_STATS_NAME =
        "SAML2 IDP authentication Request Cache";
    public String AM_SAML2_IDP_RELAYSTATE_CACHE_STATS_NAME =
        "SAML2 IDP relay State Cache";
    public String AM_SAML2_IDP_IDPSESSBYINDX_CACHE_STATS_NAME =
        "SAML2 IDP idp Sessions By Indices Cache";
    public String AM_SAML2_IDP_RSPSBYARTIFACTS_CACHE_STATS_NAME =
        "SAML2 IDP responses By Artifacts Cache";
    public String AM_SAML2_IDP_MNIREQUESTHASH_CACHE_STATS_NAME =
        "SAML2 IDP mni Request Hash Cache";
    public String AM_SAML2_IDP_DPATTRMAPPER_CACHE_STATS_NAME =
        "SAML2 IDP dp Attribute Mapper Cache";
    public String AM_SAML2_IDP_IDPACCTMAPPER_CACHE_STATS_NAME =
        "SAML2 IDP idp Account Mapper Cache";
    public String AM_SAML2_IDP_IDPAUTHNCTXMAPPER_CACHE_STATS_NAME =
        "SAML2 IDP idp Authentication Mapper Cache";
    public String AM_SAML2_IDP_RESPONSE_CACHE_STATS_NAME =
        "SAML2 IDP response Cache";
    public String AM_SAML2_IDP_AUTHNCONTEXT_CACHE_STATS_NAME =
        "SAML2 IDP authtication Context Cache";
    public String AM_SAML2_IDP_OLDIDPSESSION_CACHE_STATS_NAME =
        "SAML2 IDP old IDP Session Cache";

    //  SAML2 SP Cache Settings Names
    public String AM_SAML2_SP_REQUESTHASH_CACHE_SETTING_NAME =
        "SAML2 SP Request Hash Cache";
    public String AM_SAML2_SP_MNIREQUESTHASH_CACHE_SETTING_NAME =
        "SAML2 SP mni Request Hash Cache";
    public String AM_SAML2_SP_RELAYSTATEHASH_CACHE_SETTING_NAME =
        "SAML2 SP relay State Hash Cache";
    public String AM_SAML2_SP_FEDSESSLISTBYNAME_CACHE_SETTING_NAME =
         "SAML2 SP federation Session Lists By Name ID Info Key Cache";
    public String AM_SAML2_SP_LOGOUTRQTIDS_CACHE_SETTING_NAME =
        "SAML2 SP logout Request IDs Cache";
    public String AM_SAML2_SP_RESPONSEHASH_CACHE_SETTING_NAME =
        "SAML2 SP response Hash Cache";
    public String AM_SAML2_SP_AUTHCTXOBJHASH_CACHE_SETTING_NAME =
        "SAML2 SP authentication Context Object Hash Cache";
    public String AM_SAML2_SP_AUTHCONTEXTHASH_CACHE_SETTING_NAME =
        "SAML2 SP authentication Context Hash Cache";
    public String AM_SAML2_SP_SPACCOUNTMAPPER_CACHE_SETTING_NAME =
        "SAML2 SP sp Account Mapper Cache";
    //  SAML2 IDP Cache Settings Names
    public String AM_SAML2_IDP_AUTHNREQUEST_CACHE_SETTING_NAME =
        "SAML2 IDP authentication Request Cache";
    public String AM_SAML2_IDP_RELAYSTATE_CACHE_SETTING_NAME =
        "SAML2 IDP relay State Cache";
    public String AM_SAML2_IDP_IDPSESSBYINDX_CACHE_SETTING_NAME =
        "SAML2 IDP idp Sessions By Indices Cache";
    public String AM_SAML2_IDP_RSPSBYARTIFACTS_CACHE_SETTING_NAME =
        "SAML2 IDP responses By Artifacts Cache";
    public String AM_SAML2_IDP_MNIREQUESTHASH_CACHE_SETTING_NAME =
        "SAML2 IDP mni Request Hash Cache";
    public String AM_SAML2_IDP_DPATTRMAPPER_CACHE_SETTING_NAME =
        "SAML2 IDP dp Attribute Mapper Cache";
    public String AM_SAML2_IDP_IDPACCTMAPPER_CACHE_SETTING_NAME =
        "SAML2 IDP idp Account Mapper Cache";
    public String AM_SAML2_IDP_IDPAUTHNCTXMAPPER_CACHE_SETTING_NAME =
        "SAML2 IDP idp Authentication Mapper Cache";
    public String AM_SAML2_IDP_RESPONSE_CACHE_SETTING_NAME =
        "SAML2 IDP response Cache";
    public String AM_SAML2_IDP_AUTHNCONTEXT_CACHE_SETTING_NAME =
        "SAML2 IDP authtication Context Cache";
    public String AM_SAML2_IDP_OLDIDPSESSION_CACHE_SETTING_NAME =
        "SAML2 IDP old IDP Session Cache";

}

