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
 * $Id: MonitoringUtils.java,v 1.2 2008-06-25 05:52:54 qcheng Exp $
 *
 */

package com.sun.identity.monitoring;

import java.util.HashSet;

import com.sun.cmm.cim.OperationalStatus;
import com.sun.mfwk.instrum.me.CMM_RemoteServiceAccessPointInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_ConnectionPoolSettingInstrum;
import com.sun.mfwk.instrum.me.settings.CMM_SessionPoolSettingInstrum;
import com.sun.mfwk.instrum.me.settings.CMM_SWRCacheSettingInstrum;
import com.sun.mfwk.instrum.me.settings.CMM_ThreadPoolSettingInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ConnectionPoolStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_RemoteServiceAccessPointStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_SessionPoolStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_SWRCacheStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ThreadPoolStatsInstrum;

import com.sun.identity.monitoring.Monitoring;
import com.sun.identity.shared.debug.Debug;


/**
 * This class provides utilities for the monitoring initialization
 * and instrumentation of the AM services.
 */

public class MonitoringUtils {

    /**
     *  MonitoringUtils constructor
     */
    private MonitoringUtils()
    {
    }

    /**
     * This method initializes the numerical entries of the
     * CMM_ServiceAccessURIStatsInstrum passed to zero.
     * @param svcUriStats The ServiceAccessURIStats instance to init.
     * @param svc The service the ServiceAccessURIStats are for.
     */
    protected static void initSvcUriStats (
        CMM_ServiceAccessURIStatsInstrum svcUriStats,
        String svc) throws MfManagedElementInstrumException
    {
        if (svcUriStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initSvcUriStats: no svc uri stats for " +
                    svc);
            }
        } else {
            svcUriStats.setAbortedConnectionsCount(0);
            svcUriStats.setAbortedRequestsCount(0);
            svcUriStats.setConnectionsCount(0);
            svcUriStats.setConnectionsTime(0);
            svcUriStats.setFailedConnectionsCount(0);
            svcUriStats.setFailedRequestsCount(0);
            svcUriStats.setInBytesCount(0);
            svcUriStats.setOutBytesCount(0);
            svcUriStats.setRequestsCount(0);
            svcUriStats.setRequestsTime(0);
            svcUriStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_RemoteServiceAccessPointStatsInstrum passed to zero.
     * @param rsapStats The RemoteServiceAccessPointStats instance to init.
     * @param rsap The RemoteServiceAccessPoint the Stats are for.
     */
    protected static void initRsapStats (
        CMM_RemoteServiceAccessPointStatsInstrum rsapStats,
        String rsap) throws MfManagedElementInstrumException
    {
        if (rsapStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initRsapStats: no RSAP stats for " +
                    rsap);
            }
        } else {
            rsapStats.setAbortedConnectionsCount(0);
            rsapStats.setAbortedRequestsCount(0);
            rsapStats.setConnectionsCount(0);
            rsapStats.setConnectionsTime(0);
            rsapStats.setFailedConnectionsCount(0);
            rsapStats.setFailedRequestsCount(0);
            rsapStats.setInBytesCount(0);
            rsapStats.setOutBytesCount(0);
            rsapStats.setRequestsCount(0);
            rsapStats.setRequestsTime(0);
            rsapStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_ServiceStatsInstrum passed to zero.
     * @param svcStats The ServiceStats instance to init.
     * @param svc The service the ServiceStats are for.
     */
    protected static void initSvcStats (
        CMM_ServiceStatsInstrum svcStats,
        String svc) throws MfManagedElementInstrumException
    {
        if (svcStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initSvcStats: no service stats for " +
                    svc);
            }
        } else {
            svcStats.setAbortedRequests(0);
            svcStats.setFailedRequests(0);
            svcStats.setInRequests(0);
            svcStats.setInRequestsInBytes(0);
            svcStats.setOutRequests(0);
            svcStats.setOutRequestsInBytes(0);
            svcStats.setResidentTime(0);
            svcStats.setServiceTime(0);
            svcStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_ConnectionPoolStatsInstrum passed to zero.
     * @param connPoolStats The ConnectionPoolStats instance to init.
     * @param svc The service the ConnectionPoolStats are for.
     */
    protected static void initConnPoolStats (
        CMM_ConnectionPoolStatsInstrum connPoolStats,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (connPoolStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initConnPoolStats: no stats for " +
                    svc);
            }
        } else {
            connPoolStats.setNumConnUsedCurrent(0);
            connPoolStats.setNumConnReleased(0);
            connPoolStats.setNumConnTimedOut(0);
            connPoolStats.setNumConnCreated(0);
            connPoolStats.setNumConnDestroyed(0);
            connPoolStats.setAverageConnWaitTime(0);
            connPoolStats.setConnRequestWaitTimeCurrent(0);
            connPoolStats.setNumConnAcquired(0);
            connPoolStats.setNumConnFailedValidation(0);
            connPoolStats.setNumConnFreeCurrent(0);
            connPoolStats.setWaitQueueLength(0);

            connPoolStats.setFreePoolSize(0);
            connPoolStats.setPoolSize(0);
            connPoolStats.setWaitingTime(0);
            connPoolStats.setBufferSize(0);
            connPoolStats.setEntriesCount(0);
            connPoolStats.setFailedOperations(0);
            connPoolStats.setOperationsCount(0);
            connPoolStats.setErrorCount(0);
            connPoolStats.setLowerLimit(0);
            connPoolStats.setOtherLowerLimit(0);
            connPoolStats.setOtherUpperLimit(0);
            connPoolStats.setUpperLimit(0);
            connPoolStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_ConnectionPoolSettingInstrum passed to zero.
     * @param connPoolSetting The ConnectionPoolSetting instance to init.
     * @param svc The service the ConnectionPoolSetting is for.
     */
    protected static void initConnPoolSetting (
        CMM_ConnectionPoolSettingInstrum connPoolSetting,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (connPoolSetting == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initConnPoolSetting:" +
                    " no connection pool setting for " + svc);
            }
        } else {
            connPoolSetting.setMaxPendingCount(0);
            connPoolSetting.setQueueSizeInBytes(0);
            connPoolSetting.setReceiveBufferSizeInBytes(0);
            connPoolSetting.setSendBufferSizeInBytes(0);

            connPoolSetting.setLowerAllocationLimit(0);
            connPoolSetting.setLowerInputLimit(0);
            connPoolSetting.setLowerOutputLimit(0);
            connPoolSetting.setOtherLowerAllocationLimit(0);
            connPoolSetting.setOtherLowerInputLimit(0);
            connPoolSetting.setOtherLowerOutputLimit(0);
            connPoolSetting.setOtherUpperAllocationLimit(0);
            connPoolSetting.setOtherUpperInputLimit(0);
            connPoolSetting.setOtherUpperOutputLimit(0);
            connPoolSetting.setUpperAllocationLimit(0);
            connPoolSetting.setUpperInputLimit(0);
            connPoolSetting.setUpperOutputLimit(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_SWRCacheStatsInstrum passed to zero.
     * @param swrCacheStats The SWRCacheStats instance to init.
     * @param svc The service the SWRCacheStats are for.
     */
    protected static void initSWRCacheStats (
        CMM_SWRCacheStatsInstrum swrCacheStats,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (swrCacheStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initSWRCacheStats: no SWRCache stats for "
                    + svc);
            }
        } else {
            swrCacheStats.setCacheHitRate(0);
            swrCacheStats.setCacheHits(0);
            swrCacheStats.setCacheMisses(0);
            swrCacheStats.setCreateTime(0);
            swrCacheStats.setLastAccessTime(0);
            swrCacheStats.setMissRate(0);
            swrCacheStats.setReadRate(0);
            swrCacheStats.setReads(0);
            swrCacheStats.setWriteRate(0);
            swrCacheStats.setWrites(0);

            swrCacheStats.setBufferSize(0);
            swrCacheStats.setEntriesCount(0);
            swrCacheStats.setErrorCount(0);
            swrCacheStats.setLowerLimit(0);
            swrCacheStats.setOtherLowerLimit(0);
            swrCacheStats.setOtherUpperLimit(0);
            swrCacheStats.setUpperLimit(0);
            swrCacheStats.setFailedOperations(0);
            swrCacheStats.setOperationsCount(0);
            swrCacheStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_SWRCacheSettingInstrum passed to zero.
     * @param swrCacheSetting The SWRCacheSetting instance to init.
     * @param svc The service the SWRCacheSetting is for.
     */
    protected static void initSWRCacheSetting (
        CMM_SWRCacheSettingInstrum swrCacheSetting,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (swrCacheSetting == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initSWRCacheSetting:" +
                    " no SWRCache setting for " + svc);
            }
        } else {
            //  commented out ones need to be set by calling routine
            swrCacheSetting.setLowerAllocationLimit(0);
            swrCacheSetting.setLowerInputLimit(0);
            swrCacheSetting.setLowerOutputLimit(0);
            swrCacheSetting.setOtherLowerAllocationLimit(0);
            swrCacheSetting.setOtherLowerInputLimit(0);
            swrCacheSetting.setOtherLowerOutputLimit(0);
            swrCacheSetting.setOtherUpperAllocationLimit(0);
            swrCacheSetting.setOtherUpperInputLimit(0);
            swrCacheSetting.setOtherUpperOutputLimit(0);
            swrCacheSetting.setUpperAllocationLimit(0);
            swrCacheSetting.setUpperInputLimit(0);
            swrCacheSetting.setUpperOutputLimit(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_SessionPoolStatsInstrum passed to zero.
     * @param sessPoolStats The SessionPoolStats instance to init.
     * @param svc The service the SessionPool is for.
     */
    protected static void initSessionPoolStats (
        CMM_SessionPoolStatsInstrum sessPoolStats,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (sessPoolStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initSessionPoolStats:" +
                    " no session pool for " + svc);
            }
        } else {
            sessPoolStats.setActiveSessions(0);
            sessPoolStats.setSessionTime(0);

            sessPoolStats.setFreePoolSize(0);
            sessPoolStats.setPoolSize(0);
            sessPoolStats.setWaitingTime(0);
            sessPoolStats.setBufferSize(0);
            sessPoolStats.setEntriesCount(0);
            sessPoolStats.setFailedOperations(0);
            sessPoolStats.setOperationsCount(0);
            sessPoolStats.setErrorCount(0);
            sessPoolStats.setLowerLimit(0);
            sessPoolStats.setOtherLowerLimit(0);
            sessPoolStats.setOtherUpperLimit(0);
            sessPoolStats.setUpperLimit(0);
            sessPoolStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_SessionPoolSettingInstrum passed to zero.
     * @param sessPoolSetting The SessionPoolSetting instance to init.
     * @param svc The service the SessionPool is for.
     */
    protected static void initSessionPoolSetting (
        CMM_SessionPoolSettingInstrum sessPoolSetting,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (sessPoolSetting == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initSessionPoolSetting:" +
                    " no session pool setting for " + svc);
            }
        } else {
            sessPoolSetting.setActiveSessionsLowerBound(0);
            sessPoolSetting.setActiveSessionsUpperBound(0);

            sessPoolSetting.setFreePoolSizeLowerBound(0);
            sessPoolSetting.setFreePoolSizeUpperBound(0);
            sessPoolSetting.setPoolSizeLowerBound(0);
            sessPoolSetting.setPoolSizeUpperBound(0);
            sessPoolSetting.setLowerAllocationLimit(0);
            sessPoolSetting.setLowerInputLimit(0);
            sessPoolSetting.setLowerOutputLimit(0);
            sessPoolSetting.setOtherLowerAllocationLimit(0);
            sessPoolSetting.setOtherLowerInputLimit(0);
            sessPoolSetting.setOtherLowerOutputLimit(0);
            sessPoolSetting.setOtherUpperAllocationLimit(0);
            sessPoolSetting.setOtherUpperInputLimit(0);
            sessPoolSetting.setOtherUpperOutputLimit(0);
            sessPoolSetting.setUpperAllocationLimit(0);
            sessPoolSetting.setUpperInputLimit(0);
            sessPoolSetting.setUpperOutputLimit(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_ThreadPoolStatsInstrum passed to zero.
     * @param threadPoolStats The ThreadPoolStats instance to init.
     * @param svc The service the ThreadPool is for.
     */
    protected static void initThreadPoolStats (
        CMM_ThreadPoolStatsInstrum threadPoolStats,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (threadPoolStats == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initThreadPoolStats: no thread pool for "
                    + svc);
            }
        } else {
            threadPoolStats.setAverageTimeInQueue(0);
            threadPoolStats.setAverageTimeInQueue(0);
            threadPoolStats.setAverageWorkCompletionTime(0);
            threadPoolStats.setAverageWorkCompletionTime(0);
            threadPoolStats.setCurrentNumberOfThreads(0);
            threadPoolStats.setCurrentNumberOfThreadsLowerBound(0);
            threadPoolStats.setCurrentNumberOfThreadsUpperBound(0);
            threadPoolStats.setNumberOfAvailableThreads(0);
            threadPoolStats.setNumberOfBusyThreads(0);
            threadPoolStats.setTotalWorkItemsAdded(0);

            threadPoolStats.setFreePoolSize(0);
            threadPoolStats.setPoolSize(0);
            threadPoolStats.setWaitingTime(0);
            threadPoolStats.setBufferSize(0);
            threadPoolStats.setEntriesCount(0);
            threadPoolStats.setFailedOperations(0);
            threadPoolStats.setOperationsCount(0);
            threadPoolStats.setErrorCount(0);
            threadPoolStats.setLowerLimit(0);
            threadPoolStats.setOtherLowerLimit(0);
            threadPoolStats.setOtherUpperLimit(0);
            threadPoolStats.setUpperLimit(0);
            threadPoolStats.setSampleInterval(0);
        }
    }


    /**
     * This method initializes the numerical entries of the
     * CMM_ThreadPoolSettingInstrum passed to zero.
     * @param threadPoolSetting The ThreadPoolSetting instance to init.
     * @param svc The service the ThreadPool is for.
     */
    protected static void initThreadPoolSetting (
        CMM_ThreadPoolSettingInstrum threadPoolSetting,
        String svc) throws MfManagedElementInstrumException
    {
        /*
         *  only the numerical entries are initialized.  the commented
         *  out lines are reminders about the non-numerical entries.
         */
        if (threadPoolSetting == null) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:initThreadPoolSetting:" +
                    " no thread pool setting for " + svc);
            }
        } else {
            threadPoolSetting.setLowerAllocationLimit(0);
            threadPoolSetting.setLowerInputLimit(0);
            threadPoolSetting.setLowerOutputLimit(0);
            threadPoolSetting.setFreePoolSizeLowerBound(0);
            threadPoolSetting.setOtherLowerAllocationLimit(0);
            threadPoolSetting.setOtherLowerInputLimit(0);
            threadPoolSetting.setOtherLowerOutputLimit(0);
            threadPoolSetting.setOtherUpperAllocationLimit(0);
            threadPoolSetting.setOtherUpperInputLimit(0);
            threadPoolSetting.setOtherUpperOutputLimit(0);
            threadPoolSetting.setUpperAllocationLimit(0);
            threadPoolSetting.setUpperInputLimit(0);
            threadPoolSetting.setUpperOutputLimit(0);
        }
    }

    /**
     * This method sets the operational state of the
     * CMM_RemoteServiceAccessPointInstrum passed to the status passed.
     * @param element The CMM_RemoteServiceAccessPointInstrum instance
     *   to init.
     * @param status The OperationalStatus to set
     * @param svc The service this element is for.
     */
    protected static void setRsapOperationalStatus (
        CMM_RemoteServiceAccessPointInstrum element,
        OperationalStatus status,
        String svc)
    {
        if ((element == null) || (status == null)) {
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message(
                    "MonitoringUtils:setOperationalStatus:" +
                    " no element or no status for " + svc);
            }
        } else {
            HashSet tmpStatus = new HashSet();
            tmpStatus.add(status);
            try {
                element.setOperationalStatus(tmpStatus);
            } catch (MfManagedElementInstrumException mmeie) {
                Monitoring.debug.error(
                    "setOperationalStatus:setDBStatus:error = " +
                        mmeie.getMessage());
            }
        }
    }
}

