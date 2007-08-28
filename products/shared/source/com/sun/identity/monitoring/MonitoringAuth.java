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
 * $Id: MonitoringAuth.java,v 1.1 2007-08-28 20:28:59 bigfatrat Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.monitoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.cmm.cim.OperationalStatus;

import com.sun.mfwk.instrum.me.CIM_ManagedElementInstrum;
import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_ConnectionPoolInstrum;
import com.sun.mfwk.instrum.me.CMM_RemoteServiceAccessPointInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.CMM_SWRProtocolEndPointInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_ConnectionPoolSettingInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ConnectionPoolStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_RemoteServiceAccessPointStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.MfTransactionInstrum;
import com.sun.mfwk.instrum.me.statistics.MfTransactionInstrumConstants;
import com.sun.mfwk.instrum.relations.MfRelationInstrum;
import com.sun.mfwk.instrum.relations.MfRelationInstrumException;
import com.sun.mfwk.instrum.server.MfManagedElementInfo;
import com.sun.mfwk.instrum.server.MfManagedElementServer;
import com.sun.mfwk.instrum.server.MfManagedElementServerException;
import com.sun.mfwk.instrum.server.MfManagedElementType;
import com.sun.mfwk.instrum.server.MfRelationInfo;
import com.sun.mfwk.instrum.server.MfRelationType;

import com.sun.identity.monitoring.MonitoringConstants;
import com.sun.identity.monitoring.MonitoringUtils;
import com.sun.identity.shared.debug.Debug;


/**
 * This class provides the initialization of the FAM-related managed elements
 * specifically associated with the Authentication service for the Proctor
 * Monitoring Framework.  It is invoked during container initialization by
 * the main Monitoring.java module.
 */
public class MonitoringAuth {

    private static CMM_ServiceInstrum amSvcAuth;
    private static CMM_ServiceInstrum amSvcAuthPlugin;

    private static CMM_ServiceInstrum amAuthPluginAD;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapAdDest;

    private static CMM_ServiceInstrum amAuthPluginAnonymous;

    private static CMM_ServiceInstrum amAuthPluginCert;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapCertDest;

    private static CMM_ServiceInstrum amAuthPluginHttpBasic;

    private static CMM_ServiceInstrum amAuthPluginJdbc;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapJdbcDest;

    private static CMM_ServiceInstrum amAuthPluginLdap;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapLdapDest;

    private static CMM_ServiceInstrum amAuthPluginMembership;
    private static CMM_RemoteServiceAccessPointInstrum
        amAuthRsapMembershipDest;

    private static CMM_ServiceInstrum amAuthPluginMsisdn;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapMsisdnDest;

    private static CMM_ServiceInstrum amAuthPluginNT;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapNtDest;

    private static CMM_ServiceInstrum amAuthPluginRadius;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapRadiusDest;

    private static CMM_ServiceInstrum amAuthPluginSafeword;
    private static CMM_RemoteServiceAccessPointInstrum amAuthRsapSafewordDest;

    private static CMM_ServiceInstrum amAuthPluginSaml;

    private static CMM_ServiceInstrum amAuthPluginSecurid;
    private static CMM_SWRProtocolEndPointInstrum amAuthHapSecuridDest;

    private static CMM_ServiceInstrum amAuthPluginUnix;
    private static CMM_SWRProtocolEndPointInstrum amAuthHapUnixDest;

    private static CMM_ServiceInstrum amAuthPluginWindowsDesktopSso;
    private static CMM_RemoteServiceAccessPointInstrum
        amAuthRsapWindowsDesktopSsoDest;

    //  Stats

    private static CMM_ServiceStatsInstrum amAuthSvcStats;

    private static CMM_ServiceAccessURIStatsInstrum amAuthLoginURIStats;
    private static CMM_ServiceAccessURIStatsInstrum amAuthLogoutURIStats;
    private static CMM_ServiceAccessURIStatsInstrum amAuthSvcURIStats;

    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapMsisdnStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapNtStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapJdbcStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapRadiusStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapAdStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapCertStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapSafewordStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapLdapStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amAuthRsapWindowsDesktopSsoStats;

    private static CMM_ConnectionPoolInstrum amAuthLdapConnPoolElement;
    private static CMM_ConnectionPoolStatsInstrum amAuthLdapConnPoolStats;
    private static CMM_ConnectionPoolSettingInstrum amAuthLdapConnPoolSetting;

    //  ServiceAccessURIs
    private static CMM_ServiceAccessURIInstrum am_auth_svc_uri;
    private static CMM_ServiceAccessURIInstrum am_auth_login_uri;
    private static CMM_ServiceAccessURIInstrum am_auth_logout_uri;


    /**
     *  MonitoringAuth constructor
     */
    private MonitoringAuth()
    {
    }

    public static CMM_ConnectionPoolStatsInstrum
        getConnectionPoolStatsInstrum() {
        return amAuthLdapConnPoolStats;
    }

    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the Authentication Service's managed elements.
     * At some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createAuth (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        Monitoring.debug.message ("MonitoringAuth:createAuth");
        try {
            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();

            /*
             *  Create CMM_Service managed element for Authentication
             *  with CMM_HostedService (a containment) dependency
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */


            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTHSVC_NAME);
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            MfRelationInstrum mRI =
                mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcAuth = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_Service managed element for
             *  Authentication Plugin service
             *  with CMM_ServiceComponent (a containment) dependency
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTHSVC_PLUGINSVC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            /*
             *  is a separate MfRelationInstrum instance needed?
             *  or can the other one be reused?
             */

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcAuth,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element for auth plugin svc

            amSvcAuthPlugin = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_Service managed elements for
             *  Authentication Plugins (auth modules)
             *  with CMM_ServiceComponent (a containment) dependency
             *
             */

            //  this one is for Active Directory

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_AD_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginAD = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the Active Directory (LDAP) server
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_AD_NAME);

            //  Create the containment relation with application
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amAuthRsapAdDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapAdDest, relInfo,
                    amAuthPluginAD);

            //  this one is for Anonymous

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_PLUGIN_ANONYMOUS_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginAnonymous = (CMM_ServiceInstrum)mRI.getDestination();


            //  this one is for Certificate

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_CERT_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginCert =
                (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the Certificate DB
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_CERTDB_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amAppli, relInfo, meInfo);

            // get the destination ME

            amAuthRsapCertDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             * /

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapCertDest, relInfo,
                    amAuthPluginCert);


            //  this one is for HTTPBasic

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_HTTPBASIC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginHttpBasic = (CMM_ServiceInstrum)mRI.getDestination();


            //  HTTPBasic auth module doesn't have a remote service point

            //  this one is for JDBC

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_JDBC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginJdbc =
                (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the JDBC
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_JDBC_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amAppli, relInfo, meInfo);

            amAuthRsapJdbcDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapJdbcDest, relInfo,
                    amAuthPluginJdbc);

            //  this one is for LDAP

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_LDAP_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginLdap = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the LDAP server
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_LDAP_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(
                    amAppli, relInfo, meInfo);

            amAuthRsapLdapDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);

            mRI = mfMEServer.createRelation(amAuthRsapLdapDest, relInfo,
                    amAuthPluginLdap);


            //  this one is for Membership

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_MEMBERSHIP_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginMembership =
                (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the Membership module.  Also uses LDAP.
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_LDAP_NAME);

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */

            /*
             *  note that it points to the amAuthRsapLdapDest... that
             *  may change if an "external" LDAP server is configurable
             *  for the membership module
             */

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapLdapDest, relInfo,
                    amAuthPluginMembership);

            //  this one is for MSISDN

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_MSISDN_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginMsisdn = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the MSISDN (LDAP) server
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_MSISDN_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);


            amAuthRsapMsisdnDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapMsisdnDest, relInfo,
                    amAuthPluginMsisdn);

            //  this one is for NT

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_NT_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginNT = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the Windows NT server
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_NT_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amAuthRsapNtDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);

            mRI = mfMEServer.createRelation(amAuthRsapNtDest, relInfo,
                    amAuthPluginNT);


            //  this one is for RADIUS

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_RADIUS_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginRadius = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the RADIUS server (actually, there can be 2)
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_RADIUS_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amAuthRsapRadiusDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapRadiusDest, relInfo,
                    amAuthPluginRadius);


            //  this one is for SafeWord

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_SAFEWORD_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginSafeword = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the SafeWord server
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SP_SAFEWORD_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);


            amAuthRsapSafewordDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(amAuthRsapSafewordDest,
                    relInfo, amAuthPluginSafeword);

            //  this one is for SAML

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_SAML_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginSaml = (CMM_ServiceInstrum)mRI.getDestination();

            //  SAML auth module doesn't have a remote service point

            //  this one is for SecurID

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_SECURID_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginSecurid = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_SWRProtocolEndPoint managed element
             *  for the SecurID (ACE) server
             */

            meInfo.setType(
                MfManagedElementType.CMM_SWR_PROTOCOL_END_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SPEP_SECURID_NAME);

            /*
             *  An Protocol EndPoint is a resource and in that case the
             *  containment relation is a CMM_ResourceOfSystem
             */

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amAuthHapSecuridDest =
                (CMM_SWRProtocolEndPointInstrum)mRI.getDestination();

            /*
             *  the relationship:
             *  a service and a resource : CMM_ServiceResource
             */

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(
                    (CIM_ManagedElementInstrum)amAuthPluginSecurid,
                    relInfo,
                    (CIM_ManagedElementInstrum)amAuthHapSecuridDest);

            //  this one is for Unix

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_AUTH_PLUGIN_UNIX_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginUnix = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_SWRProtocolEndPoint managed element
             *  for the Unix "helper".
             */

            meInfo.setType(
                MfManagedElementType.CMM_SWR_PROTOCOL_END_POINT);
            meInfo.setName(MonitoringConstants.AM_AUTH_SPEP_UNIX_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amAuthHapUnixDest =
                (CMM_SWRProtocolEndPointInstrum)mRI.getDestination();

            /*
             *  the relationship
             *  a service and a resource : CMM_ServiceResource
             */

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);

            mRI = mfMEServer.createRelation(
                    (CIM_ManagedElementInstrum)amAuthPluginUnix,
                    relInfo, (CIM_ManagedElementInstrum)amAuthHapUnixDest);

            //  this one is for WindowsDesktopSSO

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_PLUGIN_WINDOWSDESKTOPSSO_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcAuthPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amAuthPluginWindowsDesktopSso =
                (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the Kerberos server
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_SP_WINDOWSDESKTOPSSO_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amAuthRsapWindowsDesktopSsoDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  CMM_ServiceSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_SAP_DEPENDENCY);
            mRI = mfMEServer.createRelation(
                    amAuthRsapWindowsDesktopSsoDest,
                    relInfo, amAuthPluginWindowsDesktopSso);


            /*
             *  ===================
             *
             *  now create the ServiceAccessUri's
             *  there are:
             *  %protocol://%host:%port/amserver/authservice
             *  %protocol://%host:%port/amserver/UI/Login
             *  %protocol://%host:%port/amserver/UI/Logout
             *  %protocol://%host:%port/amserver/jaxrpc
             *
             *  the jaxrpc uri is the "general" one for the SDK.
             */

            //  %protocol://%host:%port/amserver/authservice URI

            Monitoring.debug.message ("MonitoringAuth:createAuth:svcAccURIs");

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_AUTH_URI_AUTHSERVICE_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            am_auth_svc_uri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            am_auth_svc_uri.setLabeledURI(
                MonitoringConstants.AM_AUTH_URI_AUTHSVC_STR);

            /*
             *  ###########################################################
             *
             *  CMM_ServiceAccessSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSvcAuth, relInfo,
                    am_auth_svc_uri);

            //  %protocol://%host:%port/amserver/UI/Login URI

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_AUTH_URI_AUTHLOGIN_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            am_auth_login_uri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            am_auth_login_uri.setLabeledURI(
                MonitoringConstants.AM_AUTH_URI_AUTHLOGIN_STR);

            /*
             *  ###########################################################
             *
             *  CMM_ServiceAccessSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSvcAuth, relInfo,
                    am_auth_login_uri);

            //  %protocol://%host:%port/amserver/UI/Logout URI

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_AUTH_URI_AUTHLOGOUT_NAME);

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            am_auth_logout_uri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            am_auth_logout_uri.setLabeledURI(
                MonitoringConstants.AM_AUTH_URI_AUTHLOGOUT_STR);

            /*
             *  ###########################################################
             *
             *  CMM_ServiceAccessSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSvcAuth, relInfo,
                    am_auth_logout_uri);

            /*
             *  ###########################################################
             *
             *  CMM_ServiceAccessSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSvcAuth, relInfo,
                    Monitoring.getAMJAXRPCURI());

            /*
             *  ###########################################################
             *
             *  add in/out stats for auth service
             *
             *  ###########################################################
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(MonitoringConstants.AM_AUTH_SVC_STATS_RQT_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(amSvcAuth,
                    relInfo, meInfo);

            amAuthSvcStats = (CMM_ServiceStatsInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  add stats for auth URIs
             *
             *  ###########################################################
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_AUTH_LOGIN_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                am_auth_login_uri, relInfo, meInfo);
        
            amAuthLoginURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_AUTH_LOGOUT_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                am_auth_logout_uri, relInfo, meInfo);
        
            amAuthLogoutURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_AUTH_SVC_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                am_auth_svc_uri, relInfo, meInfo);
        
            amAuthSvcURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  stats for RemoteServiceAccessPoints

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_MSISDN_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapMsisdnDest, relInfo, meInfo);

            amAuthRsapMsisdnStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_NT_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapNtDest, relInfo, meInfo);

            amAuthRsapNtStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_JDBC_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapJdbcDest, relInfo, meInfo);

            amAuthRsapJdbcStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_RADIUS_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapRadiusDest, relInfo, meInfo);

            amAuthRsapRadiusStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_AD_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapAdDest, relInfo, meInfo);

            amAuthRsapAdStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_CERT_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapCertDest, relInfo, meInfo);

            amAuthRsapCertStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_WINDOWSDESKTOPSSO_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapWindowsDesktopSsoDest, relInfo, meInfo);

            amAuthRsapWindowsDesktopSsoStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_SAFEWORD_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapSafewordDest, relInfo, meInfo);

            amAuthRsapSafewordStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_RSAP_LDAP_STATS_NAME);
        
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthRsapLdapDest, relInfo, meInfo);

            amAuthRsapLdapStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            //  stats for Auth's LDAP Connection Pool

            meInfo.setType(MfManagedElementType.CMM_CONNECTION_POOL);
            meInfo.setName(MonitoringConstants.AM_AUTH_LDAP_CONN_POOL_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);
            
            amAuthLdapConnPoolElement =
                (CMM_ConnectionPoolInstrum)mRI.getDestination();
            
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);

            //  think this is the right order for src and dest
            mRI = mfMEServer.createRelation(amSvcAuth, relInfo,
                amAuthLdapConnPoolElement);

            //  now the stats and setting for auth's ldap connection pool

            meInfo.setType(MfManagedElementType.CMM_CONNECTION_POOL_STATS);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_LDAP_CONN_POOL_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthLdapConnPoolElement, relInfo, meInfo);

            amAuthLdapConnPoolStats =
                (CMM_ConnectionPoolStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_CONNECTION_POOL_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_AUTH_LDAP_CONN_POOL_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amAuthLdapConnPoolElement, relInfo, meInfo);

            amAuthLdapConnPoolSetting =
                (CMM_ConnectionPoolSettingInstrum)mRI.getDestination();

            initStats();

        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("createAuth: " + mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("createAuth: " + mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("createAuth:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the Authentication service's attributes
     * in its managed element (when that element gets created).
     */
    public static void initAttributes() {
    }

    /**
     * This method initializes the Authentication service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {
        amAuthSvcStats.setInRequestsInBytes(1024);
        amAuthSvcStats.setOutRequestsInBytes(0);
        amAuthSvcStats.setSampleInterval(0);

        MfTransactionInstrum trans = amAuthSvcStats.getTransaction();
        if (trans.start() == MfTransactionInstrumConstants.NOT_OK) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringAuth:initStats:trans:code = " +
                    trans.getErrorCode() + ", errmsg = " +
                    trans.getErrorMessage(trans.getErrorCode()));
            }
        }
        Monitoring.debug.message("MonitoringAuth:initStats:trans started");
        if (trans.stop(MfTransactionInstrumConstants.STATUS_GOOD) ==
            MfTransactionInstrumConstants.NOT_OK)
        {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringAuth:initStats:trans stop:code = " +
                    trans.getErrorCode() + ", errmsg = " +
                    trans.getErrorMessage(trans.getErrorCode()));
            }
        }
        Monitoring.debug.message("MonitoringAuth:initStats:trans ended");

        MonitoringUtils.initConnPoolStats(amAuthLdapConnPoolStats,
            "authentication");
        MonitoringUtils.initConnPoolSetting(amAuthLdapConnPoolSetting,
            "authentication");

        MonitoringUtils.initRsapStats(amAuthRsapMsisdnStats,
            "MSISDN authentication");
        MonitoringUtils.initRsapStats(amAuthRsapNtStats, "NT authentication");
        MonitoringUtils.initRsapStats(amAuthRsapJdbcStats,
            "JDBC authentication");
        MonitoringUtils.initRsapStats(amAuthRsapRadiusStats,
            "RADIUS authentication");
        MonitoringUtils.initRsapStats(amAuthRsapAdStats, "AD authentication");
        MonitoringUtils.initRsapStats(amAuthRsapCertStats,
            "Certificate authentication");
        MonitoringUtils.initRsapStats(amAuthRsapWindowsDesktopSsoStats,
            "Windows desktop SSO authentication");
        MonitoringUtils.initRsapStats(amAuthRsapSafewordStats,
            "SafeWord authentication");
        MonitoringUtils.initRsapStats(amAuthRsapLdapStats,
            "LDAP authentication");

        MonitoringUtils.initSvcUriStats(amAuthLoginURIStats,
            "auth login");
        MonitoringUtils.initSvcUriStats(amAuthLogoutURIStats,
            "auth logout");
        MonitoringUtils.initSvcUriStats(amAuthSvcURIStats, "authentication");

    }

    /**
     * This method sets the Authentication service's operational status
     * in its managed element.
     * @param status The operational status to set the Authentication
     *               service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        HashSet authStatus = new HashSet();
        authStatus.add(status);

        if ((amSvcAuth == null) || (amSvcAuthPlugin == null)) {
            return;
        }
        try {
            amSvcAuth.setOperationalStatus(authStatus);
            /*
             *  probably amSvcAuthPlugin (plugin service), too,
             *  as they're not really independent.
             */
            amSvcAuthPlugin.setOperationalStatus(authStatus);
            if (amAuthPluginAD != null) {
                amAuthPluginAD.setOperationalStatus(authStatus);
            }
            if (amAuthPluginAnonymous != null) {
                amAuthPluginAnonymous.setOperationalStatus(authStatus);
            }
            if (amAuthPluginCert != null) {
                amAuthPluginCert.setOperationalStatus(authStatus);
            }
            if (amAuthPluginHttpBasic != null) {
                amAuthPluginHttpBasic.setOperationalStatus(authStatus);
            }
            if (amAuthPluginJdbc != null) {
                amAuthPluginJdbc.setOperationalStatus(authStatus);
            }
            if (amAuthPluginLdap != null) {
                amAuthPluginLdap.setOperationalStatus(authStatus);
            }
            if (amAuthPluginMembership != null) {
                amAuthPluginMembership.setOperationalStatus(authStatus);
            }
            if (amAuthPluginMsisdn != null) {
                amAuthPluginMsisdn.setOperationalStatus(authStatus);
            }
            if (amAuthPluginNT != null) {
                amAuthPluginNT.setOperationalStatus(authStatus);
            }
            if (amAuthPluginRadius != null) {
                amAuthPluginRadius.setOperationalStatus(authStatus);
            }
            if (amAuthPluginSafeword != null) {
                amAuthPluginSafeword.setOperationalStatus(authStatus);
            }
            if (amAuthPluginSaml != null) {
                amAuthPluginSaml.setOperationalStatus(authStatus);
            }
            if (amAuthPluginSecurid != null) {
                amAuthPluginSecurid.setOperationalStatus(authStatus);
            }
            if (amAuthPluginUnix != null) {
                amAuthPluginUnix.setOperationalStatus(authStatus);
            }
            if (amAuthPluginWindowsDesktopSso != null) {
                amAuthPluginWindowsDesktopSso.setOperationalStatus(authStatus);
            }
            if (am_auth_logout_uri != null) {
                am_auth_logout_uri.setOperationalStatus(authStatus);
            }
            if (am_auth_login_uri != null) {
                am_auth_login_uri.setOperationalStatus(authStatus);
            }
            if (am_auth_svc_uri != null) {
                am_auth_svc_uri.setOperationalStatus(authStatus);
            }
            if (amAuthLdapConnPoolElement != null) {
                amAuthLdapConnPoolElement.setOperationalStatus(authStatus);
            }
            if (amAuthHapUnixDest != null) {
                amAuthHapUnixDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapAdDest != null) {
                amAuthRsapAdDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapRadiusDest != null) {
                amAuthRsapRadiusDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapSafewordDest != null) {
                amAuthRsapSafewordDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapCertDest != null) {
                amAuthRsapCertDest.setOperationalStatus(authStatus);
            }
            if (amAuthHapSecuridDest != null) {
                amAuthHapSecuridDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapWindowsDesktopSsoDest != null) {
                amAuthRsapWindowsDesktopSsoDest.setOperationalStatus(
                    authStatus);
            }
            if (amAuthRsapLdapDest != null) {
                amAuthRsapLdapDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapJdbcDest != null) {
                amAuthRsapJdbcDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapNtDest != null) {
                amAuthRsapNtDest.setOperationalStatus(authStatus);
            }
            if (amAuthRsapMsisdnDest != null) {
                amAuthRsapMsisdnDest.setOperationalStatus(authStatus);
            }
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringAuth:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringAuth:setStatus: " +
                mmeie.getMessage());
        }
    }

    /**
     *
     *  auth service is the only user of this method
     */

    /**
     * This method returns the handle to the Authentication service's
     *  statistics managed element.
     * @return the Authentication service's statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getAuthSvcStats() {
        return amAuthSvcStats;
    }

    /**
     * This method returns the handle to the Authentication service's
     * managed element.
     * @return the Authentication service's element handle.
     */
    protected static CMM_ServiceInstrum getSvcAuth () {
        return amSvcAuth;
    }

    /**
     * This method increments auth module-specific statistics
     */

    public static void moduleStats (String authModule, String method) {
        if (Monitoring.debug.messageEnabled()) {
            Monitoring.debug.error("MonitoringAuth:moduleStats:authModule = " +
                authModule + ", method = " + method);
        }
        CMM_RemoteServiceAccessPointStatsInstrum stats = null;

        if (authModule.equalsIgnoreCase("AD")) {
            stats = amAuthRsapAdStats;
        } else if (authModule.equalsIgnoreCase("Anonymous")) {
        } else if (authModule.equalsIgnoreCase("Application")) {
        } else if (authModule.equalsIgnoreCase("Cert")) {
            stats = amAuthRsapCertStats;
        } else if (authModule.equalsIgnoreCase("DataStore")) {
        } else if (authModule.equalsIgnoreCase("HTTPBasic")) {
        } else if (authModule.equalsIgnoreCase("JDBC")) {
            stats = amAuthRsapJdbcStats;
        } else if (authModule.equalsIgnoreCase("LDAP")) {
            stats = amAuthRsapLdapStats;
        } else if (authModule.equalsIgnoreCase("Membership")) {
        } else if (authModule.equalsIgnoreCase("MSISDN")) {
            stats = amAuthRsapMsisdnStats;
        } else if (authModule.equalsIgnoreCase("NT")) {
            stats = amAuthRsapNtStats;
        } else if (authModule.equalsIgnoreCase("RADIUS")) {
            stats = amAuthRsapRadiusStats;
        } else if (authModule.equalsIgnoreCase("SafeWord")) {
            stats = amAuthRsapSafewordStats;
        } else if (authModule.equalsIgnoreCase("SAML")) {
        } else if (authModule.equalsIgnoreCase("SecurID")) {
        } else if (authModule.equalsIgnoreCase("Unix")) {
        } else if (authModule.equalsIgnoreCase("WindowsDesktopSSO")) {
            stats = amAuthRsapWindowsDesktopSsoStats;
        } else {
            Monitoring.debug.error(
                "MonitoringAuth:moduleStats:don't know this module: " +
                authModule);
        }

        if (stats != null) {
            try {
                if (method.equalsIgnoreCase("login")) {
                    stats.addRequestsCount(1);
                } else if (method.equalsIgnoreCase("commit")) {
                    stats.addConnectionsCount(1);
                } else if (method.equalsIgnoreCase("abort")) {
                    stats.addAbortedConnectionsCount(1);
                } else {
                    Monitoring.debug.error(
                        "MonitoringAuth:moduleStats:don't know this method: "+
                        method);
                }
            } catch (MfManagedElementInstrumException meie) {
                Monitoring.debug.error(
                    "MonitoringAuth:moduleStats:error updating stats: " +
                    meie.getMessage());
            }
        }
    }

    /**
     * This method increments the Authentication Login URI counter
     * by one, indicating one more authentication process request.
     */
    public static void incrementAuthLoginURI(boolean successful) {
        if (Monitoring.debug.messageEnabled()) {
            Monitoring.debug.message("incrementAuthLoginURI: successful = " +
                successful + ", amAuthLoginURIStats = " +
                amAuthLoginURIStats);
        }
        if (amAuthLoginURIStats == null) {
            return;
        }
        try {
            if (successful) {
                amAuthLoginURIStats.addRequestsCount(1);
            } else {
                amAuthLoginURIStats.addFailedRequestsCount(1);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringAuth:incrementAuthLoginURI:successful = " +
                successful + ", error = " + mmeie.getMessage());
        }
    }

    /**
     * This method increments the Authentication Logout URI counter
     * by one, indicating one more logout process request.
     */
    public static void incrementAuthLogoutURI(boolean successful) {
        if (Monitoring.debug.messageEnabled()) {
            Monitoring.debug.message("incrementAuthLogoutURI: successful = " +
                successful + ", amAuthLogoutURIStats = " +
                amAuthLogoutURIStats);
        }
        if (amAuthLogoutURIStats == null) {
            return;
        }
        try {
            if (successful) {
                amAuthLogoutURIStats.addRequestsCount(1);
            } else {
                amAuthLogoutURIStats.addFailedRequestsCount(1);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringAuth:incrementAuthLogoutURI:successful = " +
                successful + ", error = " + mmeie.getMessage());
        }
    }
}

