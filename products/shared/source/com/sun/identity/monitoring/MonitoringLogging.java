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
 * $Id: MonitoringLogging.java,v 1.2 2008-06-25 05:52:53 qcheng Exp $
 *
 */

package com.sun.identity.monitoring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.cmm.cim.OperationalStatus;

import com.sun.mfwk.instrum.me.CMM_AMMessageLogInstrum;
import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_RemoteServiceAccessPointInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.statistics.CMM_RemoteServiceAccessPointStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
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
 * specifically associated with the Logging service for the JES Monitoring
 * Framework.  It is invoked during container initialization by the main
 * Monitoring.java module.
 */
public class MonitoringLogging {

    private static CMM_ServiceInstrum amSvcLogging;

    private static CMM_ServiceInstrum amSvcLoggingPlugin;
    private static CMM_ServiceInstrum amLoggingPluginFlatfile;
    private static CMM_ServiceInstrum amLoggingPluginRdbms;

    private static CMM_RemoteServiceAccessPointInstrum
        amLoggingRsapRdbmsOracleDest;
    private static CMM_RemoteServiceAccessPointInstrum
        amLoggingRsapRdbmsMysqlDest;
    private static CMM_RemoteServiceAccessPointInstrum amLoggingRsapCertdbDest;


    //  Stats

    private static CMM_ServiceStatsInstrum amLoggingSvcStats;
    private static CMM_ServiceAccessURIStatsInstrum amLoggingSvcUriStats;

    private static CMM_RemoteServiceAccessPointStatsInstrum
        amLoggingRsapRdbmsOracleStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amLoggingRsapRdbmsMysqlStats;
    private static CMM_RemoteServiceAccessPointStatsInstrum
        amLoggingRsapCertdbStats;

    //  ServiceAccessURIs
    private static CMM_ServiceAccessURIInstrum amLoggingSvcUri;
    private static CMM_ServiceAccessURIInstrum amLoggingJaxrpcUri;

    private static CMM_AMMessageLogInstrum amLoggingMsgLog;

    /**
     *  MonitoringLogging constructor
     */
    private MonitoringLogging()
    {
    }


    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the Logging Service's managed elements.  At
     * some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createLogging (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        try {
            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();
            MfRelationInstrum mRI = null;

            /*
             *  Create CMM_Service managed element for Logging
             *  with CMM_HostedService (a containment) dependency
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_LOGGINGSVC_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcLogging = (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  ========
             *
             *  Create CMM_Service managed element for
             *  Logging Plugin service
             *  with CMM_ServiceComponent (a containment) dependency
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_LOGGINGSVC_PLUGINSVC_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcLogging,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element for logging plugin svc

            amSvcLoggingPlugin = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  =================
             *
             *  Create CMM_Service managed elements for
             *  Logging Plugins (Flatfile, RDBMS, skip Custom plugin)
             *  with CMM_ServiceComponent (a containment) dependency
             */

            //  this one is for the Flatfiles

            Monitoring.debug.message (
                "MonitoringLogging:createLogging:Flatfile");

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_PLUGIN_FLATFILE_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcLoggingPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amLoggingPluginFlatfile =
                (CMM_ServiceInstrum)mRI.getDestination();


            //  this one is for RDBMS

            Monitoring.debug.message ("MonitoringLogging:createLogging:RDBMS");

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_LOGGING_PLUGIN_RDBMS_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSvcLoggingPlugin, relInfo, meInfo);

            //  Get created CMM_Service managed element

            amLoggingPluginRdbms = (CMM_ServiceInstrum)mRI.getDestination();


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the CertDB store
             *
             *  this is "attached" to the Logging service
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(MonitoringConstants.AM_LOGGING_SP_CERTDB_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);


            // get the destination ME

            amLoggingRsapCertdbDest =
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

            mRI = mfMEServer.createRelation(amLoggingRsapCertdbDest,
                    relInfo, amSvcLogging);


            /*
             *  Create CMM_RemoteServiceAccessPoint managed element
             *  for the RDBMS servers (Oracle, MySQL)
             *
             *  both are "attached" to the RDBMS plugin
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_SP_RDBMS_ORACLE_NAME);

            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);


            // get the destination ME

            amLoggingRsapRdbmsOracleDest =
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

            mRI = mfMEServer.createRelation(amLoggingRsapRdbmsOracleDest,
                    relInfo, amLoggingPluginRdbms);


            //  now for MySQL server

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_SP_RDBMS_MYSQL_NAME);


            // Creates the containment

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                relInfo, meInfo);


            // get the destination ME

            amLoggingRsapRdbmsMysqlDest =
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

            mRI = mfMEServer.createRelation(amLoggingRsapRdbmsMysqlDest,
                    relInfo, amLoggingPluginRdbms);


            /*
             *  ===================
             *
             *  now create the ServiceAccessUri's
             *  there are:
             *  %protocol://%host:%port/amserver/loggingservice
             *  %protocol://%host:%port/amserver/jaxrpc
             */

            //  %protocol://%host:%port/amserver/loggingservice URI

            Monitoring.debug.message (
                "MonitoringLogging:createLogging:Service Access URI");

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_URI_LOGGINGSERVICE_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amLoggingSvcUri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amLoggingSvcUri.setLabeledURI(
                MonitoringConstants.AM_LOGGING_URI_LOGGINGSERVICE_STR);

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
            mRI = mfMEServer.createRelation(amSvcLogging, relInfo,
                    amLoggingSvcUri);


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
            mRI = mfMEServer.createRelation(amSvcLogging, relInfo,
                    Monitoring.getAMJAXRPCURI());

            /*
             *  ##########################################################
             *
             *  create the statistics elements
             *
             *  add stats for Logging service
             *
             *  ##########################################################
             */

            Monitoring.debug.message ("MonitoringLogging:createLogging:stats");


            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(MonitoringConstants.AM_LOGGING_SVC_STATS_RQT_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(amSvcLogging,
                    relInfo, meInfo);

            amLoggingSvcStats = (CMM_ServiceStatsInstrum)mRI.getDestination();


            //  amserver/loggingservice URI stats

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(MonitoringConstants.AM_LOGGING_SVC_URI_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amLoggingSvcUri, relInfo, meInfo);
            
            amLoggingSvcUriStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();
        
            //  rdbms and certdb stats

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_ORACLE_RDBMS_STATS_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amLoggingRsapRdbmsOracleDest, relInfo, meInfo);

            amLoggingRsapRdbmsOracleStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();


            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_MYSQL_RDBMS_STATS_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amLoggingRsapRdbmsMysqlDest, relInfo, meInfo);

            amLoggingRsapRdbmsMysqlStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();


            /*
             *  should this element's operational status be "Dormant"
             *  if secure logging is not configured?
             */

            meInfo.setType(
                MfManagedElementType.CMM_REMOTE_SERVICE_ACCESS_POINT_STATS);
            meInfo.setName(
                MonitoringConstants.AM_LOGGING_CERTDB_STATS_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amLoggingRsapCertdbDest, relInfo, meInfo);

            amLoggingRsapCertdbStats =
                (CMM_RemoteServiceAccessPointStatsInstrum)mRI.getDestination();

            //  initialize the statistics elements

            Monitoring.debug.message (
                "MonitoringLogging:createLogging:init stats");
            initStats();

        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("createLogging: " +
                mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("createLogging: " +
                mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("createLogging:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the Logging service's attributes
     * in its managed element (when that element gets created).
     */
    public static void initAttributes() {
    }

    /**
     * This method initializes the Logging service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {

        /*
         *  CMM_ServiceAccessUriStats - for
         *  %protocol://%host:%port/amserver/loggingservice
         */

        MonitoringUtils.initSvcUriStats(amLoggingSvcUriStats,
            "logging");

        //  CMM_ServiceStats for logging

        MonitoringUtils.initSvcStats(amLoggingSvcStats, "logging");

        //  CMM_RemoteServiceAccessPointStats for logging

        Monitoring.debug.message (
            "MonitoringLogging:createLogging:initStats for Rsaps");

        MonitoringUtils.initRsapStats(amLoggingRsapRdbmsOracleStats,
            "logging Oracle rdbms");
        MonitoringUtils.setRsapOperationalStatus(
            amLoggingRsapRdbmsOracleDest,
            OperationalStatus.OK, "logging Oracle rdbms");

        MonitoringUtils.initRsapStats(amLoggingRsapRdbmsMysqlStats,
            "logging MySQL rdbms");
        MonitoringUtils.setRsapOperationalStatus(
            amLoggingRsapRdbmsMysqlDest,
            OperationalStatus.OK, "logging MySQL rdbms");

        MonitoringUtils.initRsapStats(amLoggingRsapCertdbStats,
            "logging CertDB");
        MonitoringUtils.setRsapOperationalStatus(
            amLoggingRsapCertdbDest,
            OperationalStatus.OK, "logging CertDB");
    }

    /*
     *  there's only addCurrentNumberOfRecords for incrementing
     *  the number of records.  could have one for addFileSize(),
     *  setRotated (for flatfile only).  problem is that unless
     *  there are separate stats for each log file/table, these
     *  stats (and methods) can only apply to the collective
     *  numbers.
     */

    /**
     * This method sets the Logging service's operational status
     * in its managed element.
     * @param status The operational status to set the Logging service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        if (!Monitoring.isLocal) {  // not local/servermode
            return;
        }

        HashSet loggingStatus = new HashSet();
        loggingStatus.add(status);

        if ((amSvcLogging == null) || (amSvcLoggingPlugin == null)) {
            return;
        }

        try {
            amSvcLogging.setOperationalStatus(loggingStatus);
            /*
             *  probably amSvcLoggingPlugin (plugin service), too,
             *  as they're not really independent.
             */
            amSvcLoggingPlugin.setOperationalStatus(loggingStatus);
            if (amLoggingPluginFlatfile != null) {
                amLoggingPluginFlatfile.setOperationalStatus(loggingStatus);
            }
            if (amLoggingPluginRdbms != null) {
                amLoggingPluginRdbms.setOperationalStatus(loggingStatus);
            }
            if (amLoggingSvcUri != null) {
                amLoggingSvcUri.setOperationalStatus(loggingStatus);
            }
            /*
             *  might have to check that secure logging is enabled
             *  before setting this one to "In Service" from "Dormant"
             */
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringLogging:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringLogging:setStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method sets the Logging service's CertDB Keystore's
     * operational status in its managed element.
     * @param status The operational status to set the Logging service's
     * CertDB Keystore to.
     */
    public static void setCertDBStatus(OperationalStatus status) 
    {
        if (!Monitoring.isLocal) {  // not local/servermode
            return;
        }

        HashSet loggingStatus = new HashSet();
        loggingStatus.add(status);

        if (amLoggingRsapCertdbDest == null) {
            return;
        }

        try {
            amLoggingRsapCertdbDest.setOperationalStatus(loggingStatus);
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringLogging:setCertDBStatus = " + status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringLogging:setCertDBStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method sets the Logging service's DB Loggers'
     * operational status in their managed element.
     * @param driver The classpath for the JDBC driver
     * @param status The operational status to set the Logging service's
     * DB to.
     */
    public static void setDBStatus(String driver, OperationalStatus status) 
    {
        if (!Monitoring.isLocal) {  // not local/servermode
            return;
        }
        if ((driver == null) || driver.length() == 0) {
            return;
        }

        /*
         *  see if the driver is Oracle or MySQL... these are the
         *  only ones we know about.
         */

        boolean isMySQL = true;
        if (driver.toLowerCase().indexOf("oracle") != -1) {
            isMySQL = false;
        }

        HashSet dblogStatus = new HashSet();
        dblogStatus.add(status);

        /*
         *  if we're trying to set status on an element that's
         *  not there, return.
         */

        if (!isMySQL && amLoggingRsapRdbmsOracleDest == null) {
            return;
        }
        if (isMySQL && amLoggingRsapRdbmsMysqlDest == null) {
            return;
        }

        CMM_RemoteServiceAccessPointInstrum tmpRsap =
            amLoggingRsapRdbmsMysqlDest;
        if (!isMySQL) {
            tmpRsap = amLoggingRsapRdbmsOracleDest;
        }

        try {
            if (status == OperationalStatus.OK) {
                if (!isMySQL) {
                    amLoggingRsapRdbmsOracleDest.setOperationalStatus(
                            dblogStatus);
                } else {
                    amLoggingRsapRdbmsMysqlDest.setOperationalStatus(
                            dblogStatus);
                }
            } else {
                tmpRsap.setOperationalStatus(dblogStatus);
            }
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringLogging:setDBStatus = " + status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringLogging:setDBStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the Logging service's statistics
     * managed element.
     * @return the Logging service's statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getLoggingSvcStats() {
        return amLoggingSvcStats;
    }


    /**
     * This method increments the Logging service's request count
     * statistic in its managed element, by one.
     */
    public static void incrementLogRecords() {
        if (amLoggingSvcStats == null) {
            //  not initialized yet
            return;
        }
        try {
            amLoggingSvcStats.addInRequests(1);
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringLogging:incrementLogRecords:error = " +
                mmeie.getMessage());
        }
    }


    /**
     * This method increments the Logging service's connection request count
     * statistic for the DB remote service access points.
     * @param driver The classpath for the JDBC driver
     * @param successful Whether successful or failed connection
     */
    public static void incrementDBConnections(String driver,
        boolean successful)
    {
        if ((driver == null) || driver.length() == 0) {
            return;
        }

        /*
         *  see if the driver is Oracle or MySQL... these are the
         *  only ones we know about.
         */

        boolean isMySQL = true;
        if (driver.toLowerCase().indexOf("oracle") != -1) {
            isMySQL = false;
        }
        if (!isMySQL && amLoggingRsapRdbmsOracleStats == null) {
            return;
        }
        if (isMySQL && amLoggingRsapRdbmsMysqlStats == null) {
            return;
        }

        CMM_RemoteServiceAccessPointStatsInstrum tmpRsap =
            amLoggingRsapRdbmsMysqlStats;
        if (!isMySQL) {
            tmpRsap = amLoggingRsapRdbmsOracleStats;
        }

        try {
            if (successful) {
                tmpRsap.addConnectionsCount(1);
            } else {
                tmpRsap.addFailedConnectionsCount(1);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringLogging:incrementDBConnections:driver = " +
                driver + ", successful = " + successful + ", error = " +
                mmeie.getMessage());
        }
    }


    /**
     * This method increments the Logging service's request count
     * statistic for the DB remote service access points.
     * @param driver The classpath for the JDBC driver
     * @param successful Whether successful or failed request
     * @param count Number of records requested to be written
     */
    public static void incrementDBRequests(String driver,
        boolean successful, long count)
    {
        if ((driver == null) || driver.length() == 0) {
            return;
        }

        /*
         *  see if the driver is Oracle or MySQL... these are the
         *  only ones we know about.
         */

        boolean isMySQL = true;
        if (driver.toLowerCase().indexOf("oracle") != -1) {
            isMySQL = false;
        }
        if (!isMySQL && amLoggingRsapRdbmsOracleStats == null) {
            return;
        }
        if (isMySQL && amLoggingRsapRdbmsMysqlStats == null) {
            return;
        }

        CMM_RemoteServiceAccessPointStatsInstrum tmpRsap =
            amLoggingRsapRdbmsMysqlStats;
        if (!isMySQL) {
            tmpRsap = amLoggingRsapRdbmsOracleStats;
        }

        try {
            if (successful) {
                tmpRsap.addRequestsCount(count);
            } else {
                tmpRsap.addFailedRequestsCount(count);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error(
                "MonitoringLogging:incrementDBRequests:driver = " +
                driver + ", successful = " + successful + ", count = " +
                count + ", error = " + mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the Logging service's
     * managed element.
     */
    protected static CMM_ServiceInstrum getSvcLogging () {
        return amSvcLogging;
    }

 }

