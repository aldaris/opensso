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
 * $Id: MonitoringSAMLv1.java,v 1.2 2008-06-25 05:52:54 qcheng Exp $
 *
 */

package com.sun.identity.monitoring;

import java.util.HashSet;

import com.sun.cmm.cim.OperationalStatus;

import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.CMM_SWRCacheInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_SWRCacheSettingInstrum;
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
 * This class provides the initialization of the OpenFAM-related managed
 * elements * specifically associated with the SAML1.0 service for the
 * Proctor Monitoring Framework.  It is invoked during container
 * initialization by the main Monitoring.java module.
 */
public class MonitoringSAMLv1 {

    //  Services (servlets)
    private static CMM_ServiceInstrum amSvcSAML10;
    private static CMM_ServiceInstrum amSvcSAML10SAMLPostProfile;
    private static CMM_ServiceInstrum amSvcSAML10SAMLAware;
    private static CMM_ServiceInstrum amSvcSAML10SAMLSOAPReceiver;

    //  Service (servlets) Stats
    private static CMM_ServiceStatsInstrum amSAML10SAMLSvcStats;
    private static CMM_ServiceStatsInstrum amSAML10SAMLPostProfileStats;
    private static CMM_ServiceStatsInstrum amSAML10SAMLAwareStats;
    private static CMM_ServiceStatsInstrum amSAML10SAMLSOAPReceiverStats;

    //  ServiceAccessURIs
    private static CMM_ServiceAccessURIInstrum amSAML10PostProfileUri;
    private static CMM_ServiceAccessURIInstrum amSAML10SAMLAwareUri;
    private static CMM_ServiceAccessURIInstrum amSAML10SOAPReceiverUri;
    private static CMM_ServiceAccessURIInstrum amSAML10JaxrpcUri;

    //  ServiceAccessURIStats
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML10SAMLPOSTProfileUriStats;
    private static CMM_ServiceAccessURIStatsInstrum amSAML10SAMLAwareUriStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML10SAMLSOAPRcvrUriStats;
    private static CMM_ServiceAccessURIStatsInstrum amSAML10JaxrpcUriStats;

    //  SAML1.0 Caches - assertions and artifacts
    private static CMM_SWRCacheInstrum amSAML10AssertionCacheElement;
    private static CMM_SWRCacheInstrum amSAML10ArtifactCacheElement;

    //  Cache Stats
    private static CMM_SWRCacheStatsInstrum amSAML10AssertionCacheStats;
    private static CMM_SWRCacheStatsInstrum amSAML10ArtifactCacheStats;

    //  Cache Settings
    private static CMM_SWRCacheSettingInstrum amSAML10AssertionCacheSetting;
    private static CMM_SWRCacheSettingInstrum amSAML10ArtifactCacheSetting;

    /**
     *  MonitoringSAMLv1 constructor
     */
    private MonitoringSAMLv1()
    {
    }

    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the SAML1.0 Service's managed elements.  At
     * some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createSAMLv1 (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        Monitoring.debug.message ("MonitoringSAMLv1:createSAMLv1");
        try {
            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();
            MfRelationInstrum mRI = null;

            /*
             *  Create CMM_Service managed element for SAML10
             *  with CMM_HostedService (a containment) dependency
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML10SVC_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcSAML10 = (CMM_ServiceInstrum)mRI.getDestination();

            //  now create the servlets' CMM_Service elements
            //  SAMLPOSTProfile servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML10_POSTPROFILE_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML10,
                      relInfo, meInfo);
        
            //  get created CMM_Service managed element

            amSvcSAML10SAMLPostProfile =
                (CMM_ServiceInstrum)mRI.getDestination();

            //  SAML Aware servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML10_SAMLAWARE_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML10,
                      relInfo, meInfo);

            amSvcSAML10SAMLAware = (CMM_ServiceInstrum)mRI.getDestination();

            //  SAML SOAP Receiver servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML10_SAMLSOAPRECEIVER_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML10,
                      relInfo, meInfo);

            amSvcSAML10SAMLSOAPReceiver =
                (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  ===================
             *
             *  now create the ServiceAccessUri's
             *  there are:
             *  %protocol://%host:%port/openfam/SAMLPOSTProfileServlet
             *  %protocol://%host:%port/openfam/SAMLAwareServlet
             *  %protocol://%host:%port/openfam/SAMLSOAPReceiver
             *
             *  map them onto their corresponding SAML1.0 servlets
             */

            //  %protocol://%host:%port/openfam/SAMLPOSTProfileServlet URI

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_SAML10_URI_POSTPROFILE_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amSAML10PostProfileUri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amSAML10PostProfileUri.setLabeledURI(
                MonitoringConstants.AM_SAML10_URI_POSTPROFILE_STR);

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
            mRI = mfMEServer.createRelation(amSvcSAML10SAMLPostProfile, relInfo,
                    amSAML10PostProfileUri);


            //  %protocol://%host:%port/openfam/SAMLAwareServlet

            meInfo.setName(MonitoringConstants.AM_SAML10_URI_SAMLAWARE_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amSAML10SAMLAwareUri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amSAML10SAMLAwareUri.setLabeledURI(
                MonitoringConstants.AM_SAML10_URI_SAMLAWARE_STR);

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSvcSAML10SAMLAware, relInfo,
                    amSAML10SAMLAwareUri);


            //  %protocol://%host:%port/openfam/SAMLSOAPReceiver

            meInfo.setName(
                MonitoringConstants.AM_SAML10_URI_SAMLSOAPRECEIVER_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amSAML10SOAPReceiverUri =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amSAML10SOAPReceiverUri.setLabeledURI(
                MonitoringConstants.AM_SAML10_URI_SAMLSOAPRECEIVER_STR);

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSvcSAML10SAMLSOAPReceiver,
                    relInfo, amSAML10SOAPReceiverUri);

            /*
             *  ###########################################################
             *
             *  create the statistics elements
             *
             *  add in/out stats for SAML1.0 service
             *    amSAML10SAMLSvcStats
             *
             *  URI stats for
             *    SAMLPostProfile
             *    SAMLAware
             *    SAMLSOAPReceiver
             *
             *  ###########################################################
             */

            //  SAML1.0 Service stats
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(MonitoringConstants.AM_SAML10_SVC_STATS_NAME);
  
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML10,
                    relInfo, meInfo);
  
            amSAML10SAMLSvcStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            //  SAML URI stats

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML10_SVC_STATS_POSTPROFILE_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML10PostProfileUri, relInfo, meInfo);

            amSAML10SAMLPOSTProfileUriStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();


            meInfo.setName(
                MonitoringConstants.AM_SAML10_SVC_STATS_SAMLAWARE_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML10SAMLAwareUri, relInfo, meInfo);

            amSAML10SAMLAwareUriStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();


            meInfo.setName(
                MonitoringConstants.AM_SAML10_SVC_STATS_SAMLSOAPRCVR_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML10SOAPReceiverUri, relInfo, meInfo);

            amSAML10SAMLSOAPRcvrUriStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  SAML JAXRPC URI stats

//          meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
//          meInfo.setName(
//              MonitoringConstants.AM_SAML10_JAXRPC_URI_STATS_NAME);
//
//          relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
//          mRI = mfMEServer.createRelationToNewManagedElement(
//              amSAML10JaxrpcUri, relInfo, meInfo);
//
//          amSAML10JaxrpcUriStats = 
//              (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();
        
            /*
             *  stats and settings for
             *  SAML1.0 service's Assertion and Artifact caches
             */

            //  Assertion Cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(MonitoringConstants.AM_SAML10_ASSERTION_CACHE_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amSAML10AssertionCacheElement =
                (CMM_SWRCacheInstrum)mRI.getDestination();

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML10, relInfo,
                    amSAML10AssertionCacheElement);
        
            //  now the stats and setting for Assertion's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML10_ASSERTION_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML10AssertionCacheElement, relInfo, meInfo);

            amSAML10AssertionCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();


            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_SAML10_ASSERTION_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML10AssertionCacheElement, relInfo, meInfo);

            amSAML10AssertionCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();


            //  Artifact Cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(MonitoringConstants.AM_SAML10_ARTIFACT_CACHE_NAME);

            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            amSAML10ArtifactCacheElement =
                (CMM_SWRCacheInstrum)mRI.getDestination();

            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML10, relInfo,
                    amSAML10ArtifactCacheElement);
        
            //  now the stats and setting for Artifact's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML10_ARTIFACT_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML10ArtifactCacheElement, relInfo, meInfo);

            amSAML10ArtifactCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();


            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_SAML10_ARTIFACT_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML10ArtifactCacheElement, relInfo, meInfo);

            amSAML10ArtifactCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();


            // intialize the statistics elements

            initStats();

        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("createSAMLv1: " +
                mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("createSAMLv1: " +
                mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("createSAMLv1:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the SAML1.0 service's attributes
     * in its managed element (when that element gets created).
     */
    public static void initAttributes() {
    }

    /**
     * This method initializes the SAML1.0 service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {
        MonitoringUtils.initSWRCacheStats(amSAML10ArtifactCacheStats,
            "SAML 1.0");
        MonitoringUtils.initSWRCacheStats(amSAML10AssertionCacheStats,
            "SAML 1.0");
        MonitoringUtils.initSvcStats(amSAML10SAMLSOAPReceiverStats,
            "SAML 1.0");
        MonitoringUtils.initSvcStats(amSAML10SAMLAwareStats, "SAML 1.0");
        MonitoringUtils.initSvcStats(amSAML10SAMLPostProfileStats,
            "SAML 1.0");
    }

    /*
     *  don't know if there should be individual methods
     *  for each status type, or make the SAML1.0 service
     *  import com.sun.cmm.cim.OperationalStatus.  probably
     *  the latter...
     */
    /**
     * This method sets the SAML1.0 service's operational status
     * in its managed element.
     * @param status The operational status to set the SAML1.0 service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        HashSet samlStatus = new HashSet();
        samlStatus.add(status);

        if (amSvcSAML10 == null) {
            return;
        }

        try {
            amSvcSAML10.setOperationalStatus(samlStatus);
            if (amSAML10PostProfileUri != null) {
                amSAML10PostProfileUri.setOperationalStatus(samlStatus);
            }
            if (amSAML10SAMLAwareUri != null) {
                amSAML10SAMLAwareUri.setOperationalStatus(samlStatus);
            }
            if (amSAML10SOAPReceiverUri != null) {
                amSAML10SOAPReceiverUri.setOperationalStatus(samlStatus);
            }
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringSAMLv1:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringSAMLv1:setStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the SAML1.0 service's statistics
     * managed element.
     * @return the SAML1.0 service's statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getSAMLv1SvcStats() {
        return amSAML10SAMLSvcStats;
    }

    /**
     * This method returns the handle to the SAML1.0 service's
     *  PostProfile statistics managed element.
     * @return the SAML1.0 service's Post Profile statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getSAMLv1PostProfileStats() {
        return amSAML10SAMLPostProfileStats;
    }

    /**
     * This method returns the handle to the SAML1.0 service's
     *  SAML Aware statistics managed element.
     * @return the SAML1.0 service's SAML Aware statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getSAMLv1SAMLAwareStats() {
        return amSAML10SAMLAwareStats;
    }

    /**
     * This method returns the handle to the SAML1.0 service's
     *  SAML Aware statistics managed element.
     * @return the SAML1.0 service's SOAP Receiver statistics element handle.
     */
    public static CMM_ServiceStatsInstrum getSAMLv1SOAPRcvrStats() {
        return amSAML10SAMLSOAPReceiverStats;
    }


    /**
     * This method returns the handle to the SAML1.0 service's
     * managed element.
     * @return the SAML1.0 service's element handle.
     */
    protected static CMM_ServiceInstrum getSvcSAMLv1 () {
        return amSvcSAML10;
    }
}

