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
 * $Id: MonitoringSession.java,v 1.2 2008-06-25 05:52:54 qcheng Exp $
 *
 */

package com.sun.identity.monitoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.cmm.CMM_ThreadPool;
import com.sun.cmm.cim.OperationalStatus;

import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_RemoteServiceAccessPointInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.CMM_SessionPoolInstrum;
import com.sun.mfwk.instrum.me.CMM_ThreadPoolInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_SessionPoolSettingInstrum;
import com.sun.mfwk.instrum.me.settings.CMM_ThreadPoolSettingInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_RemoteServiceAccessPointStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_SessionPoolStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ThreadPoolStatsInstrum;
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
 * This class provides the initialization of the AM-related managed elements
 * specifically associated with the Session service for the JES Monitoring
 * Framework.  It is invoked during container initialization by the main
 * Monitoring.java module.
 */
public class MonitoringSession {

    private static CMM_ServiceInstrum amSvcSess;

    private static CMM_ServiceInstrum amSvcSessPlugin;
    private static CMM_ServiceInstrum amSessPluginJmq;

    private static CMM_RemoteServiceAccessPointInstrum amSessRsapDbDest;

    //  Stats

    private static CMM_ServiceStatsInstrum amSessSvcStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum amSessRsapDbStats;

    private static CMM_ServiceAccessURIStatsInstrum amSessSvcUriStats;
    private static CMM_ServiceAccessURIStatsInstrum amSessJaxrpcUriStats;

    private static CMM_SessionPoolInstrum amSessSessionPoolElement;
    private static CMM_SessionPoolStatsInstrum amSessSessionPoolStats;
    private static CMM_SessionPoolSettingInstrum amSessSessionPoolSetting;

    private static CMM_ThreadPoolInstrum amSessThreadPoolElement;
    private static CMM_ThreadPoolStatsInstrum amSessThreadPoolStats;
    private static CMM_ThreadPoolSettingInstrum amSessThreadPoolSetting;

    //  ServiceAccessURIs
    private static CMM_ServiceAccessURIInstrum amSessSvcUri;
    private static CMM_ServiceAccessURIInstrum amSessJaxrpcUri;


    /**
     *  MonitoringSession constructor
     */
    private MonitoringSession()
    {
    }

    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the Session Service's managed elements.  At
     * some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createSession (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        Monitoring.debug.message ("MonitoringSession:createSession");
        try {
            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();
            MfRelationInstrum mRI = null;

            /*
             *  Create CMM_Service managed element for Session
             *  with CMM_HostedService (a containment) dependency
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SESSSVC_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcSess = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  ========
             *
             *  Create CMM_Service managed element for
             *  Session Plugin service
             *  with CMM_ServiceComponent (a containment) dependency
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SESSSVC_PLUGINSVC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);


            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSess,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element for sess plugin svc

            amSvcSessPlugin = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  =================
             *
             *  Create CMM_Service managed element for
             *  Session Plugin JMQ
             *  with CMM_ServiceComponent (a containment) dependency
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SESS_PLUGIN_JMQ_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcSessPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSessPluginJmq = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the Berkeley DBServer
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_SESS_SP_DB_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amAppli, relInfo, meInfo);

            // get the destination ME

            amSessRsapDbDest =
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
            mRI = mfMEServer.createRelation(amSessRsapDbDest, relInfo,
                    amSessPluginJmq);


            /*
             *  ===================
             *
             *  now create the ServiceAccessUri's
             *  there are:
             *  %protocol://%host:%port/amserver/sessionservice
             *  %protocol://%host:%port/amserver/jaxrpc
             *
             *  not quite sure about the jaxrpc uri, as that one
             *  is the "general" one for the SDK.
             */

            //  %protocol://%host:%port/amserver/sessionservice URI

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_SESS_URI_SESSSERVICE_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amSessSvcUri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amSessSvcUri.setLabeledURI(
                MonitoringConstants.AM_SESS_URI_SESSSERVICE_STR);

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
            mRI = mfMEServer.createRelation(amSvcSess, relInfo,
                    amSessSvcUri);

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
            mRI = mfMEServer.createRelation(amSvcSess, relInfo,
                    Monitoring.getAMJAXRPCURI());

            amSessJaxrpcUri = 
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            /*
             *  ###########################################################
             *
             *  create the statistics elements
             *
             *  add in/out stats for session service
             *
             *  ###########################################################
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(MonitoringConstants.AM_SESS_SVC_STATS_RQT_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSess,
                    relInfo, meInfo);

            amSessSvcStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  session DB stats

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(MonitoringConstants.AM_SESS_SVC_DB_STATS_RQT_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessRsapDbDest, relInfo, meInfo);

            amSessRsapDbStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            //  session URI stats

            meInfo.setType(
                MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_SESS_SVC_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessSvcUri, relInfo, meInfo);
        
            amSessSvcUriStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  session JAXRPC URI stats

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_SESS_JAXRPC_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessJaxrpcUri, relInfo, meInfo);

            amSessJaxrpcUriStats = 
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();
        
            //  Session service's Session Pool element and stats

            meInfo.setType(MfManagedElementType.CMM_SESSION_POOL);
            meInfo.setName(MonitoringConstants.AM_SESS_SESSION_POOL_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amSessSessionPoolElement =
                (CMM_SessionPoolInstrum)mRI.getDestination();

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSess, relInfo,
                    amSessSessionPoolElement);
        
            //  now the stats and setting for session's session pool

            meInfo.setType(MfManagedElementType.CMM_SESSION_POOL_STATS);
            meInfo.setName(MonitoringConstants.AM_SESS_SESSION_POOL_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessSessionPoolElement, relInfo, meInfo);

            amSessSessionPoolStats =
                (CMM_SessionPoolStatsInstrum)mRI.getDestination();


            meInfo.setType(MfManagedElementType.CMM_SESSION_POOL_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_SESS_SESSION_POOL_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessSessionPoolElement, relInfo, meInfo);

            amSessSessionPoolSetting =
                (CMM_SessionPoolSettingInstrum)mRI.getDestination();


            /*
             *  session service's threadpool
             *
             *  first, create the threadpool element, with
             *  containment under the AmServer (amAppli),
             *  then the association to session service (amSvcSess)
             */

            meInfo.setType(MfManagedElementType.CMM_THREAD_POOL);
            meInfo.setName(MonitoringConstants.AM_SESS_THREAD_POOL_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amSessThreadPoolElement =
                (CMM_ThreadPoolInstrum)mRI.getDestination();

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSess, relInfo,
                    amSessThreadPoolElement);
        
            //  now the stats and setting for session's thread pool

            meInfo.setType(MfManagedElementType.CMM_THREAD_POOL_STATS);
            meInfo.setName(MonitoringConstants.AM_SESS_THREAD_POOL_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessThreadPoolElement, relInfo, meInfo);

            amSessThreadPoolStats =
                (CMM_ThreadPoolStatsInstrum)mRI.getDestination();


            meInfo.setType(MfManagedElementType.CMM_THREAD_POOL_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_SESS_THREAD_POOL_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSessThreadPoolElement, relInfo, meInfo);

            amSessThreadPoolSetting =
                (CMM_ThreadPoolSettingInstrum)mRI.getDestination();

            Monitoring.debug.message ("MonitoringSession:createSess:stats");
            initStats();

        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("createSession: " +
                mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("createSession: " +
                mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("createSession:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the Session service's attributes
     * in its managed element (when that element gets created).
     */
    public static void initAttributes() {
    }

    /**
     * This method initializes the Session service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {
        /*
         *  not quite sure which pair to use for sessions:
         *    created/destroyed
         *    used/released
         *
         *  think we'll use created/destroyed for now, since
         *  SessionService.java creates another session, rather
         *  than getting one from an existing pool.
         */

        if (amSessSessionPoolStats != null) {
            amSessSessionPoolStats.setActiveSessions(0);
            // gotta see what entries to init
        }

        MonitoringUtils.initSvcStats(amSessSvcStats, "session");

        if (amSessSvcStats != null) {
            amSessSvcStats.setInRequestsInBytes(0);
            amSessSvcStats.setAbortedRequests(0);
            amSessSvcStats.setFailedRequests(0);
            amSessSvcStats.setOutRequests(0);

            MfTransactionInstrum trans = amSessSvcStats.getTransaction();
            if (trans.start() == MfTransactionInstrumConstants.NOT_OK) {
                if (Monitoring.debug.warningEnabled()) {
                    Monitoring.debug.warning(
                        "MonitoringSession:initStat:trans:code = " +
                        trans.getErrorCode() + ", errmsg = " +
                        trans.getErrorMessage(trans.getErrorCode()));
                }
            }
            Monitoring.debug.message(
                "MonitoringSession:initStat:trans started");
            if (trans.stop(MfTransactionInstrumConstants.STATUS_GOOD) ==
                MfTransactionInstrumConstants.NOT_OK)
            {
                if (Monitoring.debug.warningEnabled()) {
                    Monitoring.debug.warning(
                        "MonitoringSession:initStat:trans stop:code = " +
                        trans.getErrorCode() + ", errmsg = " +
                        trans.getErrorMessage(trans.getErrorCode()));
                }
            }
            Monitoring.debug.message("MonitoringSession:initStat:trans ended");
        }

        MonitoringUtils.initSessionPoolStats(amSessSessionPoolStats,
            "session");

        MonitoringUtils.initSessionPoolSetting(amSessSessionPoolSetting,
            "session");

        MonitoringUtils.initThreadPoolStats(amSessThreadPoolStats,
            "session");

        MonitoringUtils.initThreadPoolSetting(amSessThreadPoolSetting,
            "session");

        MonitoringUtils.initRsapStats(amSessRsapDbStats, "session DB server");

        MonitoringUtils.initSvcUriStats(amSessSvcUriStats, "session");
    }

    /*
     *  don't know if there should be individual methods
     *  for each status type, or make the session service
     *  import com.sun.cmm.cim.OperationalStatus.  probably
     *  the latter...
     */
    /**
     * This method sets the Session service's operational status
     * in its managed element.
     * @param status The operational status to set the Session service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        HashSet sessStatus = new HashSet();
        sessStatus.add(status);

        if ((amSvcSess == null) || (amSvcSessPlugin == null)) {
            return;
        }

        try {
            amSvcSess.setOperationalStatus(sessStatus);
            /*
             *  probably amSvcSessPlugin (plugin service), too,
             *  as they're not really independent.
             */
            amSvcSessPlugin.setOperationalStatus(sessStatus);
            if (amSessPluginJmq != null) {
                amSessPluginJmq.setOperationalStatus(sessStatus);
            }
            if (amSessThreadPoolElement != null) {
                amSessThreadPoolElement.setOperationalStatus(sessStatus);
            }
            if (amSessSessionPoolElement != null) {
                amSessSessionPoolElement.setOperationalStatus(sessStatus);
            }
            if (amSessSvcUri != null) {
                amSessSvcUri.setOperationalStatus(sessStatus);
            }
            if (amSessRsapDbDest != null) {
                amSessRsapDbDest.setOperationalStatus(sessStatus);
            }
            if (amSessJaxrpcUri != null) {
                amSessJaxrpcUri.setOperationalStatus(sessStatus);
            }
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringSession:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringSession:setStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the Session service's statistics
     * managed element.
     * @return the Session service's statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getSessSvcStats() {
        return amSessSvcStats;
    }

    /**
     * This method returns the handle to the Session service's Session
     * Pool statistics managed element.
     * @return the Session service's Session Pool statistics element handle.
     */
    public static CMM_SessionPoolStatsInstrum
        getSessSessionPoolStats()
    {
        return amSessSessionPoolStats;
    }

    /**
     * This method increments the Session service's Session Pool
     * connections created statistic in its managed element, by one.
     */
    public static void incrementSessions() {
        if (amSessSessionPoolStats == null) {
            return;
        }
        try {
            //  not sure this is the right one to increment
            amSessSessionPoolStats.addActiveSessions(1);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringSession:incrementSessions:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method decrements the Session service's Session Pool
     * connections created statistic in its managed element, by one.
     */
    public static void decrementSessions() {
        if (amSessSessionPoolStats == null) {
            return;
        }
        try {
            //  not sure this is the right one to decrement
            amSessSessionPoolStats.substractActiveSessions(1);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringSession:decrementSessions:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the Session service's
     * managed element.
     * @return the Session service's element handle.
     */
    protected static CMM_ServiceInstrum getSvcSess () {
        return amSvcSess;
    }
}

