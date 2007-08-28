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
 * $Id: MonitoringPolicy.java,v 1.1 2007-08-28 20:29:00 bigfatrat Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.monitoring;

import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.cmm.cim.OperationalStatus;
import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_ConnectionPoolInstrum;
import com.sun.mfwk.instrum.me.CMM_RemoteServiceAccessPointInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.CMM_SWRCacheInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_ConnectionPoolSettingInstrum;
import com.sun.mfwk.instrum.me.settings.CMM_SWRCacheSettingInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ConnectionPoolStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_RemoteServiceAccessPointStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_SWRCacheStatsInstrum;
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
 * specifically associated with the Policy service for the JES Monitoring
 * Framework.  It is invoked during container initialization by the main
 * Monitoring.java module.
 */
public class MonitoringPolicy {

    private static CMM_ServiceInstrum amSvcPolicy;

    private static CMM_ServiceInstrum amSVCPolicyPlugin;
    private static CMM_ServiceInstrum amPolicyPluginSubject;

    private static CMM_RemoteServiceAccessPointInstrum
        amPolicyRsapSubjectLdapDest;


    //  Stats

    private static CMM_ServiceStatsInstrum amPolicySvcStats;

    private static CMM_ConnectionPoolInstrum amPolicyConnPoolElement;
    private static CMM_ConnectionPoolStatsInstrum amPolicyConnPoolStats;
    private static CMM_ConnectionPoolSettingInstrum amPolicyConnPoolSetting;

    private static CMM_SWRCacheInstrum amPolicyCacheElement;
    private static CMM_SWRCacheStatsInstrum amPolicyCacheStats;
    private static CMM_SWRCacheSettingInstrum amPolicyCacheSetting;

    private static CMM_ServiceAccessURIStatsInstrum amPolicySvcUriStats;

    private static CMM_RemoteServiceAccessPointStatsInstrum
        amPolicyRsapSubjectLdapStats;

    //  ServiceAccessURIs
    private static CMM_ServiceAccessURIInstrum amPolicySvcUri;
    private static CMM_ServiceAccessURIInstrum amPolicyJaxrpcUri;


    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the Policy Service's managed elements.  At
     * some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createPolicy (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        Monitoring.debug.message ("MonitoringPolicy:createPolicy");

        try {
            MfRelationInstrum mRI = null;

            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();

            /*
             *  Create CMM_Service managed element for Policy
             *  with CMM_HostedService (a containment) dependency
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_POLICYSVC_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcPolicy = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  ========
             *
             *  Create CMM_Service managed element for
             *  Policy Plugin service
             *  with CMM_ServiceComponent (a containment) dependency
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_POLICYSVC_PLUGINSVC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            /*
             *  is a separate MfRelationInstrum instance needed?
             *  or can the other one be reused?
             */

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcPolicy,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element for policy plugin svc

            amSVCPolicyPlugin = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  =================
             *
             *  Create CMM_Service managed elements for
             *  Policy Plugin (Subject, skip custom plugin)
             *  with CMM_ServiceComponent (a containment) dependency
             */

            //  this one is for the Subject

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_POLICY_PLUGIN_SUBJECT_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSVCPolicyPlugin,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amPolicyPluginSubject = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the LDAP server
             *
             *  there's only one for Subject
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(
                MonitoringConstants.AM_POLICY_SP_SUBJECT_LDAP_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);


            // get the destination ME

            amPolicyRsapSubjectLdapDest =
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

            mRI = mfMEServer.createRelation(amPolicyRsapSubjectLdapDest,
                    relInfo, amPolicyPluginSubject);


            /*
             *  ===================
             *
             *  now create the ServiceAccessUri's
             *  there are:
             *  %protocol://%host:%port/amserver/policyservice
             *  %protocol://%host:%port/amserver/jaxrpc
             *
             *
             *  %protocol://%host:%port/amserver/policyservice URI
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_POLICY_URI_POLICYSERVICE_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amPolicySvcUri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amPolicySvcUri.setLabeledURI(
                MonitoringConstants.AM_POLICY_URI_POLICYSERVICE_STR);

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
            mRI = mfMEServer.createRelation(amSvcPolicy, relInfo,
                    amPolicySvcUri);

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
            mRI = mfMEServer.createRelation(amSvcPolicy, relInfo,
                    Monitoring.getAMJAXRPCURI());

            /*
             *  ##########################################################
             *
             *  create the statistics elements
             *
             *  add connection pool stats for policy service
             *
             *  and policy cache
             *
             *  ##########################################################
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(MonitoringConstants.AM_POLICY_SVC_STATS_RQT_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(amSvcPolicy,
                    relInfo, meInfo);

            amPolicySvcStats = (CMM_ServiceStatsInstrum)mRI.getDestination();



            meInfo.setType(MfManagedElementType.CMM_CONNECTION_POOL);
            meInfo.setName(MonitoringConstants.AM_POLICY_CONN_POOL_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amPolicyConnPoolElement =
                (CMM_ConnectionPoolInstrum)mRI.getDestination();

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);

            //  think this is the right order for src and dest
            mRI = mfMEServer.createRelation(amSvcPolicy, relInfo,
                    amPolicyConnPoolElement);
        
            //  now the stats and setting for policy's connection pool

            meInfo.setType(MfManagedElementType.CMM_CONNECTION_POOL_STATS);
            meInfo.setName(
                MonitoringConstants.AM_POLICY_CONN_POOL_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amPolicyConnPoolElement, relInfo, meInfo);

            amPolicyConnPoolStats =
                (CMM_ConnectionPoolStatsInstrum)mRI.getDestination();


            meInfo.setType(MfManagedElementType.CMM_CONNECTION_POOL_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_POLICY_CONN_POOL_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amPolicyConnPoolElement, relInfo, meInfo);

            amPolicyConnPoolSetting =
                (CMM_ConnectionPoolSettingInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(MonitoringConstants.AM_POLICY_CACHE_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amPolicyCacheElement =
                (CMM_SWRCacheInstrum)mRI.getDestination();

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcPolicy, relInfo,
                    amPolicyCacheElement);
        
            //  now the stats and setting for policy's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(MonitoringConstants.AM_POLICY_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amPolicyCacheElement, relInfo, meInfo);

            amPolicyCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();


            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(MonitoringConstants.AM_POLICY_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amPolicyCacheElement, relInfo, meInfo);

            amPolicyCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  stats for amserver/policyservice URI

            meInfo.setType(
                MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_POLICY_SVC_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amPolicySvcUri, relInfo, meInfo);
        
            amPolicySvcUriStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  Policy Subject's LDAP Server Stats

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_POLICY_SP_SUBJECT_LDAP_STATS_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amPolicyRsapSubjectLdapDest, relInfo, meInfo);

            amPolicyRsapSubjectLdapStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();


            //  initialize the statistics elements
            initStats();
        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("MonitoringPolicy:createPolicy: " +
                mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("MonitoringPolicy:createPolicy: " +
                mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:createPolicy:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the Policy service's attributes
     * in its managed element (when that element gets created).
     */
    public static void initAttributes() {
    }

    public static CMM_ConnectionPoolStatsInstrum
        getConnectionPoolStatsInstrum() {
        return amPolicyConnPoolStats;
    }


    /**
     * This method initializes the Policy service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {
        MonitoringUtils.initSvcStats(amPolicySvcStats, "policy");

        MonitoringUtils.initConnPoolStats(amPolicyConnPoolStats, "policy");

        MonitoringUtils.initConnPoolSetting(amPolicyConnPoolSetting, "policy");

        MonitoringUtils.initSWRCacheStats(amPolicyCacheStats, "policy");

        MonitoringUtils.initSWRCacheSetting(amPolicyCacheSetting, "policy");
        if (amPolicyCacheSetting == null) {
            Monitoring.debug.message(
                "MonitoringPolicy:initStats - no cache setting yet.");
        }

        MonitoringUtils.initRsapStats(amPolicyRsapSubjectLdapStats,
            "policy subject LDAP");

        MonitoringUtils.initSvcUriStats(amPolicySvcUriStats,
            "policy");
    }

    /**
     * This method sets the Policy service's operational status
     * in its managed element.
     * @param status The operational status to set the Policy service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        if (!Monitoring.isLocal) {  // not local/servermode
            return;
        }

        HashSet polStatus = new HashSet();
        polStatus.add(status);

        if ((amSvcPolicy == null) || (amSVCPolicyPlugin == null)) {
            return;
        }

        try {
            amSvcPolicy.setOperationalStatus(polStatus);
            /*
             *  probably amSVCPolicyPluginSubject (plugin service), too,
             *  as they're not really independent.
             */
            amSVCPolicyPlugin.setOperationalStatus(polStatus);
            if (amPolicyPluginSubject != null) {
                    amPolicyPluginSubject.setOperationalStatus(polStatus);
            }
            if (amPolicySvcUri != null) {
                    amPolicySvcUri.setOperationalStatus(polStatus);
            }
            if (amPolicyConnPoolElement != null) {
                    amPolicyConnPoolElement.setOperationalStatus(polStatus);
            }
            if (amPolicyCacheElement != null) {
                    amPolicyCacheElement.setOperationalStatus(polStatus);
            }
            if (amPolicyRsapSubjectLdapDest != null) {
                    amPolicyRsapSubjectLdapDest.setOperationalStatus(polStatus);
            }
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringPolicy:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringPolicy:setStatus:error = " +
                mmeie.getMessage());
        }
    }


    /**
     * This method returns the handle to the Policy service's statistics
     * managed element.
     * @return The Policy service's statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getPolicySvcStats() {
        return amPolicySvcStats;
    }

    /**
     * This method increments the Policy service's in request count
     * statistic in its managed element, by one.
     */

    public static void incrementPolicyEvalsIn() {
        if (amPolicySvcStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicySvcStats.addInRequests(1);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:incrementPolicyEvalsIn:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method increments the Policy service's out request count
     * statistic in its managed element, by one.
     */

    public static void incrementPolicyEvalsOut() {
        if (amPolicySvcStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicySvcStats.addOutRequests(1);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:incrementPolicyEvalsOut:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method increments the Policy service's cache's hit
     * count statistic in its managed element, by one.
     */
    public static void incrementCacheHits() {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.incrementCacheHits();
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:incrementCacheHits:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method increments the Policy service's cache's miss
     * count statistic in its managed element, by one.
     */
    public static void incrementCacheMisses() {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.incrementCacheMisses();
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:incrementCacheMisses:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method increments the Policy service's cache's write
     * count statistic in its managed element, by one.
     */
    public static void incrementWrites() {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.incrementWrites();
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:incrementWrites:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method increments the Policy service's cache's read
     * count statistic in its managed element, by one.
     */
    public static void incrementReads() {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.incrementReads();
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:incrementReads:error = " +
                mmeie.getMessage());
        }
    }


    /**
     * This method sets the Policy service's cache's hit
     * count statistic in its managed element, to the passed value.
     * @param num The number of cache hits
     */
    public static void setCacheHits (long num) {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.setCacheHits(num);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:setCacheHits:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method sets the Policy service's cache's miss
     * count statistic in its managed element, to the passed value.
     * @param num The number of cache misses
     */
    public static void setCacheMisses (long num) {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.setCacheMisses(num);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:setCacheMisses:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method sets the Policy service's cache's write
     * count statistic in its managed element, to the passed value.
     * @param num The number of cache writes
     */
    public static void setWrites (long num) {

        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }

        try {
            amPolicyCacheStats.setWrites(num);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:setWrites:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method sets the Policy service's cache's read
     * count statistic in its managed element, to the passed value.
     * @param num The number of cache reads
     */
    public static void setReads (long num) {
        if (amPolicyCacheStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amPolicyCacheStats.setReads(num);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringPolicy:setReads:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the Policy service's
     * managed element.
     */
    protected static CMM_ServiceInstrum getSvcPolicy () {
        return amSvcPolicy;
    }
}

