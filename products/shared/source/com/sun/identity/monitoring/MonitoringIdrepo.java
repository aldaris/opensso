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
 * $Id: MonitoringIdrepo.java,v 1.1 2007-08-28 20:28:59 bigfatrat Exp $
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

import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_RemoteServiceAccessPointInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
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
 * This class provides the initialization of the AM-related managed elements
 * specifically associated with the Identity Repository service for the
 * JES Monitoring Framework.  It is invoked during container initialization
 * by the main Monitoring.java module.
 */
public class MonitoringIdrepo {

    private static CMM_ServiceInstrum amSvcIdrepo;

    private static CMM_ServiceInstrum amIdrepoPlugin;
    private static CMM_ServiceInstrum amIdrepoPluginLdapV3;
    private static CMM_ServiceInstrum amIdrepoPluginLdapV3AD;
    private static CMM_ServiceInstrum amIdrepoPluginFlatFile;
    private static CMM_ServiceInstrum amIdrepoPluginLdapV3Auth;
    private static CMM_ServiceInstrum amIdrepoPluginLdapV3ADAuth;
    private static CMM_ServiceInstrum amIdrepoPluginFlatFileAuth;

    private static CMM_RemoteServiceAccessPointInstrum amIdrepoRsapLdapV3Dest;
    private static CMM_RemoteServiceAccessPointInstrum amIdrepoRsapLdapV3ADDest;

    //  Stats

    private static CMM_ServiceStatsInstrum amIdrepoSvcStats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginStats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginLdapV3Stats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginLdapV3ADStats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginFlatFileStats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginLdapV3AuthStats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginLdapV3ADAuthStats;
    private static CMM_ServiceStatsInstrum amIdrepoPluginFlatFileAuthStats;

    //  ServiceAccessURIs
    private static CMM_ServiceAccessURIInstrum amIdrepoJaxrpcUri;

    /**
     *  MonitoringIdrepo constructor
     */
    private MonitoringIdrepo()
    {
    }

    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the Idrepo Service's managed elements.  At
     * some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createIdrepo (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        Monitoring.debug.message ("MonitoringIdrepo.createIdrepo");

        try {
            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();
            MfRelationInstrum mRI = null;

            /*
             *  Create CMM_Service managed element for Idrepo
             *  with CMM_HostedService (a containment) dependency
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */


            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_IDREPOSVC_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcIdrepo = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *
             *  Create CMM_Service managed element for the
             *  IdRepo Plugin service
             *  with CMM_ServiceComponent (a containment) dependency
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_IDREPOSVC_PLUGINSVC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcIdrepo,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element for idrepo plugin svc

            amIdrepoPlugin = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  Create CMM_Service managed elements for
             *  IdRepo Plugins (LDAPv3, LDAPv3/AD, FlatFile,
             *  skip the Custom plugin) with CMM_ServiceComponent
             *  (a containment) dependency.  And their associated
             *  data store authentication service.
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amIdrepoPlugin,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amIdrepoPluginLdapV3 = (CMM_ServiceInstrum)mRI.getDestination();

            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3_AUTH_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amIdrepoPluginLdapV3,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amIdrepoPluginLdapV3Auth =
                (CMM_ServiceInstrum)mRI.getDestination();


            //  LDAPv3/AD

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3AD_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amIdrepoPlugin,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amIdrepoPluginLdapV3AD = (CMM_ServiceInstrum)mRI.getDestination();

            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3AD_AUTH_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amIdrepoPluginLdapV3AD,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amIdrepoPluginLdapV3ADAuth =
                (CMM_ServiceInstrum)mRI.getDestination();


            //  FlatFile

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_IDREPO_PLUGIN_FLATFILE_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amIdrepoPlugin,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amIdrepoPluginFlatFile = (CMM_ServiceInstrum)mRI.getDestination();

            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3AD_AUTH_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amIdrepoPluginFlatFile,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amIdrepoPluginFlatFileAuth =
                (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the LDAP servers (LDAPv3 and LDAPv3/AD)
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_IDREPO_SP_LDAPV3_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);


            // get the destination ME

            amIdrepoRsapLdapV3Dest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();


            //  ldapv3/AD

            meInfo.setName(MonitoringConstants.AM_IDREPO_SP_LDAPV3AD_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);

            // get the destination ME

            amIdrepoRsapLdapV3ADDest =
                (CMM_RemoteServiceAccessPointInstrum)mRI.getDestination();

            /*
             *  IdRepo service is only available (remotely) through the
             *  API; it doesn't have a separate URI, as some of the
             *  other services.  (no separate ServiceAccessUri for
             *  IdRepo.)
             */

            //  %protocol://%host:%port/amserver/jaxrpc URI

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
            mRI = mfMEServer.createRelation(amSvcIdrepo, relInfo,
                    Monitoring.getAMJAXRPCURI());

            /*
             *  ##########################################################
             *
             *  create the statistics elements
             *
             *  ##########################################################
             */

            // idrepo svc stats
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(MonitoringConstants.AM_IDREPO_SVC_STATS_RQT_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(amSvcIdrepo,
                    relInfo, meInfo);

            amIdrepoSvcStats = (CMM_ServiceStatsInstrum)mRI.getDestination();

            // idrepo plugin stats
            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI =
                mfMEServer.createRelationToNewManagedElement(amIdrepoPlugin,
                    relInfo, meInfo);

            amIdrepoPluginStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            // idrepo plugins stats

            // ldapv3
            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amIdrepoPluginLdapV3, relInfo, meInfo);

            amIdrepoPluginLdapV3Stats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            // ldapv3/ad
            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_LDAPV3AD_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amIdrepoPluginLdapV3AD, relInfo, meInfo);

            amIdrepoPluginLdapV3ADStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            // flatfile
            meInfo.setName(
                MonitoringConstants.AM_IDREPO_PLUGIN_FLATFILE_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amIdrepoPluginFlatFile, relInfo, meInfo);

            amIdrepoPluginFlatFileStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            // data store auth service stats

            // ldapv3 auth
            meInfo.setName(MonitoringConstants.
                AM_IDREPO_PLUGIN_LDAPV3_AUTH_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amIdrepoPluginLdapV3Auth, relInfo, meInfo);

            amIdrepoPluginLdapV3AuthStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            // ldapv3/ad auth
            meInfo.setName(MonitoringConstants.
                AM_IDREPO_PLUGIN_LDAPV3AD_AUTH_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amIdrepoPluginLdapV3ADAuth, relInfo, meInfo);

            amIdrepoPluginLdapV3ADAuthStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            // flat file auth
            meInfo.setName(MonitoringConstants.
                AM_IDREPO_PLUGIN_FLATFILE_AUTH_STATS_RQT_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amIdrepoPluginFlatFileAuth, relInfo, meInfo);

            amIdrepoPluginFlatFileAuthStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            /*
             *  stats for the URIs
             *  what to do about the API's URI?
             */

//            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
//            meInfo.setName(MonitoringConstants.AM_IDREPO_JAXRPC_URI_STATS_NAME);
//
//            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
//            mRI = mfMEServer.createRelationToNewManagedElement(
//                amIdrepoSvcUri, relInfo, meInfo);
//
//            amIdrepoSvcUriStats =
//                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            initStats();
        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("MonitoringIdrepo.createIdrepo: " +
                mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("MonitoringIdrepo.createIdrepo: " +
                mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringIdrepo.createIdrepo:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the IdRepo service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {

        MonitoringUtils.initSvcStats(amIdrepoSvcStats, "idrepo");
        MonitoringUtils.initSvcStats(amIdrepoPluginStats, "idrepo plugins");
        MonitoringUtils.initSvcStats(amIdrepoPluginLdapV3Stats,
            "idrepo plugin for LDAPv3");
        MonitoringUtils.initSvcStats(amIdrepoPluginLdapV3ADStats,
            "idrepo plugin for LDAPv3/AD");
        MonitoringUtils.initSvcStats(amIdrepoPluginFlatFileStats,
            "idrepo plugin for FlatFile");
        MonitoringUtils.initSvcStats(amIdrepoPluginLdapV3AuthStats,
            "idrepo plugin for LDAPv3 Auth");
        MonitoringUtils.initSvcStats(amIdrepoPluginLdapV3ADAuthStats,
            "idrepo plugin for LDAPv3/AD Auth");
        MonitoringUtils.initSvcStats(amIdrepoPluginFlatFileAuthStats,
            "idrepo plugin for FlatFile Auth");
    }

    /*
     *  don't know if there should be individual methods
     *  for each status type, or make the idrepo service
     *  import com.sun.cmm.cim.OperationalStatus.  probably
     *  the latter...
     */
    /**
     * This method sets the IdRepo service's operational status
     * in its managed element.
     * @param status The operational status to set the IdRepo service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        HashSet idrepoStatus = new HashSet();
        idrepoStatus.add(status);

        if ((amSvcIdrepo == null) || (amIdrepoPlugin == null)) {
            return;
        }

        try {
            amSvcIdrepo.setOperationalStatus(idrepoStatus);
            /*
             *  probably amSvcIdrepoPlugin (plugin service), too,
             *  as they're not really independent.
             */
            amIdrepoPlugin.setOperationalStatus(idrepoStatus);

            // the datastore plugins are operational when configured

            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringIdrepo:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringIdrepo:setStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the Idrepo service's statistics
     * managed element.
     * @return the Idrepo service's statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getIdrepoSvcStats() {
        return amIdrepoSvcStats;
    }

    /**
     * This method returns the handle to the Idrepo service's
     * managed element.
     * @return the Idrepo service's managed element handle.
     */
    protected static CMM_ServiceInstrum getSvcIdrepo () {
        return amSvcIdrepo;
    }
}

